from __future__ import annotations

import argparse
import csv
import re
import sys
from dataclasses import dataclass, field
from pathlib import Path
from statistics import mean, median
from typing import Iterable

SLA_MS = 730
REQ_PER_MIN_PER_USER = 20

try:
    import matplotlib

    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
except ImportError:
    plt = None


@dataclass
class Sample:
    ts_ms: int
    elapsed_ms: int
    label: str
    response_code: str
    success: bool


@dataclass
class GroupStats:
    name: str
    samples: list[Sample] = field(default_factory=list)

    @property
    def n(self) -> int:
        return len(self.samples)

    @property
    def n_ok(self) -> int:
        return sum(1 for s in self.samples if s.success)

    @property
    def n_503(self) -> int:
        return sum(1 for s in self.samples if s.response_code == "503")

    @property
    def n_403(self) -> int:
        return sum(1 for s in self.samples if s.response_code == "403")

    @property
    def n_other_err(self) -> int:
        return self.n - self.n_ok - self.n_503 - self.n_403

    @property
    def duration_s(self) -> float:
        if self.n < 2:
            return 0.0
        ts = [s.ts_ms for s in self.samples]
        return (max(ts) - min(ts)) / 1000.0

    @property
    def rps(self) -> float:
        return self.n / self.duration_s if self.duration_s > 0 else 0.0

    @property
    def rpm(self) -> float:
        return self.rps * 60.0

    def pct(self, p: float) -> float:
        if not self.samples:
            return 0.0
        vals = sorted(s.elapsed_ms for s in self.samples)
        k = max(0, min(len(vals) - 1, int(round(p / 100.0 * (len(vals) - 1)))))
        return float(vals[k])

    @property
    def mean_ms(self) -> float:
        return mean(s.elapsed_ms for s in self.samples) if self.samples else 0.0

    @property
    def median_ms(self) -> float:
        return float(median(s.elapsed_ms for s in self.samples)) if self.samples else 0.0

    @property
    def max_ms(self) -> int:
        return max((s.elapsed_ms for s in self.samples), default=0)


def read_jtl(path: Path) -> list[Sample]:
    out: list[Sample] = []
    with path.open(newline="") as fh:
        reader = csv.DictReader(fh)
        for row in reader:
            try:
                out.append(
                    Sample(
                        ts_ms=int(row["timeStamp"]),
                        elapsed_ms=int(row["elapsed"]),
                        label=row.get("label", ""),
                        response_code=str(row.get("responseCode", "")),
                        success=row.get("success", "").lower() == "true",
                    )
                )
            except (KeyError, ValueError):
                continue
    return out


def fmt_row(g: GroupStats) -> str:
    return (
        f"{g.name:<24} n={g.n:>5}  rpm={g.rpm:>6.1f}  "
        f"mean={g.mean_ms:>6.0f}ms  p50={g.median_ms:>6.0f}ms  "
        f"p95={g.pct(95):>6.0f}ms  p99={g.pct(99):>6.0f}ms  max={g.max_ms:>5}ms  "
        f"ok={g.n_ok}  503={g.n_503}  403={g.n_403}  err={g.n_other_err}"
    )


def analyze_load(paths: list[Path]) -> None:
    groups: list[GroupStats] = []
    for p in paths:
        samples = read_jtl(p)
        m = re.search(r"config(\d+)", p.stem)
        name = f"config{m.group(1)}" if m else p.stem
        g = GroupStats(name=name, samples=samples)
        groups.append(g)
        print(fmt_row(g))

    print()
    print(f"SLA: среднее и 95-й перцентиль времени отклика должны быть <= {SLA_MS} мс.")
    for g in groups:
        ok = (g.mean_ms <= SLA_MS) and (g.pct(95) <= SLA_MS) and (g.n_503 == 0)
        verdict = "PASS" if ok else "FAIL"
        print(f"  {g.name}: {verdict}  (mean={g.mean_ms:.0f}, p95={g.pct(95):.0f}, 503={g.n_503})")

    if plt is None:
        print("\nmatplotlib не установлен; графики пропущены", file=sys.stderr)
        return

    out_dir = Path("results")
    out_dir.mkdir(exist_ok=True)

    fig, ax = plt.subplots(figsize=(10, 5))
    for g in groups:
        if not g.samples:
            continue
        t0 = min(s.ts_ms for s in g.samples)
        xs = [(s.ts_ms - t0) / 1000.0 for s in g.samples]
        ys = [s.elapsed_ms for s in g.samples]
        ax.scatter(xs, ys, s=6, alpha=0.45, label=g.name)
    ax.axhline(SLA_MS, color="red", linestyle="--", linewidth=1, label=f"SLA = {SLA_MS} мс")
    ax.set_xlabel("Время от старта, с")
    ax.set_ylabel("Время отклика, мс")
    ax.set_title("Нагрузочное тестирование: время отклика во времени")
    ax.legend()
    ax.grid(True, alpha=0.3)
    fig.tight_layout()
    fig.savefig(out_dir / "load_response_time.png", dpi=140)
    plt.close(fig)

    fig, ax = plt.subplots(figsize=(8, 5))
    names = [g.name for g in groups]
    achieved = [g.rpm for g in groups]
    target = 13 * REQ_PER_MIN_PER_USER
    x = range(len(names))
    ax.bar(x, achieved, color="#4a90d9", label="Достигнуто, req/min")
    ax.axhline(target, color="green", linestyle="--", label=f"Цель = {target} req/min")
    ax.set_xticks(list(x))
    ax.set_xticklabels(names)
    ax.set_ylabel("Пропускная способность, req/min")
    ax.set_title("Пропускная способность по конфигурациям")
    ax.legend()
    ax.grid(True, axis="y", alpha=0.3)
    fig.tight_layout()
    fig.savefig(out_dir / "load_throughput.png", dpi=140)
    plt.close(fig)

    fig, ax = plt.subplots(figsize=(8, 5))
    width = 0.35
    means = [g.mean_ms for g in groups]
    p95s = [g.pct(95) for g in groups]
    xs = [i for i in range(len(names))]
    ax.bar([i - width / 2 for i in xs], means, width, label="Среднее", color="#4a90d9")
    ax.bar([i + width / 2 for i in xs], p95s, width, label="95-й перцентиль", color="#f5a623")
    ax.axhline(SLA_MS, color="red", linestyle="--", label=f"SLA = {SLA_MS} мс")
    ax.set_xticks(xs)
    ax.set_xticklabels(names)
    ax.set_ylabel("Время отклика, мс")
    ax.set_title("Время отклика по конфигурациям")
    ax.legend()
    ax.grid(True, axis="y", alpha=0.3)
    fig.tight_layout()
    fig.savefig(out_dir / "load_response_summary.png", dpi=140)
    plt.close(fig)

    print(f"\nплоты записаны в {out_dir}/load_*.png")


USERS_RE = re.compile(r"users\s*=\s*(\d+)", re.IGNORECASE)


def analyze_stress(path: Path) -> None:
    samples = read_jtl(path)
    if not samples:
        print(f"в {path} нет сэмплов", file=sys.stderr)
        return

    by_step: dict[int, GroupStats] = {}
    for s in samples:
        m = USERS_RE.search(s.label)
        if not m:
            continue
        users = int(m.group(1))
        by_step.setdefault(users, GroupStats(name=f"users={users}")).samples.append(s)

    steps = sorted(by_step.keys())
    if not steps:
        print("не удалось извлечь число пользователей из label сэмплеров", file=sys.stderr)
        return

    print(f"{'step':<14}{'mean ms':>10}{'p95 ms':>10}{'rpm':>10}{'503':>6}{'403':>6}{'err':>6}")
    crossing: int | None = None
    rows = []
    for u in steps:
        g = by_step[u]
        rows.append(g)
        print(
            f"users={u:<8}{g.mean_ms:>10.0f}{g.pct(95):>10.0f}"
            f"{g.rpm:>10.1f}{g.n_503:>6}{g.n_403:>6}{g.n_other_err:>6}"
        )
        if crossing is None and (g.mean_ms > SLA_MS or g.pct(95) > SLA_MS or g.n_503 > 0):
            crossing = u

    print()
    if crossing is not None:
        print(f"Конфигурация перестаёт удовлетворять SLA ({SLA_MS} мс) при N = {crossing} пользователей "
              f"(~{crossing * REQ_PER_MIN_PER_USER} req/min).")
    else:
        print(f"SLA ({SLA_MS} мс) не превышен ни на одном из шагов — расширьте диапазон --steps.")

    if plt is None:
        print("\nmatplotlib не установлен; графики пропущены", file=sys.stderr)
        return

    out_dir = Path("results")
    out_dir.mkdir(exist_ok=True)

    fig, ax = plt.subplots(figsize=(9, 5))
    xs = steps
    ax.plot(xs, [by_step[u].mean_ms for u in xs], marker="o", label="Среднее", color="#4a90d9")
    ax.plot(xs, [by_step[u].pct(95) for u in xs], marker="s", label="95-й перцентиль", color="#f5a623")
    ax.plot(xs, [by_step[u].max_ms for u in xs], marker="^", linestyle=":", label="Максимум",
            color="#9b9b9b", alpha=0.6)
    ax.axhline(SLA_MS, color="red", linestyle="--", linewidth=1, label=f"SLA = {SLA_MS} мс")
    if crossing is not None:
        ax.axvline(crossing, color="red", linestyle=":", linewidth=1, alpha=0.6)
        ax.annotate(
            f"N = {crossing}",
            xy=(crossing, SLA_MS),
            xytext=(crossing + 1, SLA_MS * 1.1),
            color="red",
        )
    ax.set_xlabel("Параллельные пользователи")
    ax.set_ylabel("Время отклика, мс")
    ax.set_title("Стресс-тест: время отклика от нагрузки")
    ax.legend()
    ax.grid(True, alpha=0.3)
    fig.tight_layout()
    fig.savefig(out_dir / "stress_response_vs_users.png", dpi=140)
    plt.close(fig)

    fig, ax = plt.subplots(figsize=(9, 5))
    xs_rpm = [u * REQ_PER_MIN_PER_USER for u in steps]
    ax.plot(xs_rpm, [by_step[u].mean_ms for u in steps], marker="o", label="Среднее", color="#4a90d9")
    ax.plot(xs_rpm, [by_step[u].pct(95) for u in steps], marker="s", label="95-й перцентиль", color="#f5a623")
    ax.axhline(SLA_MS, color="red", linestyle="--", label=f"SLA = {SLA_MS} мс")
    if crossing is not None:
        ax.axvline(crossing * REQ_PER_MIN_PER_USER, color="red", linestyle=":", alpha=0.6)
    ax.set_xlabel("Нагрузка, req/min")
    ax.set_ylabel("Время отклика, мс")
    ax.set_title("Стресс-тест: время отклика от нагрузки (req/min)")
    ax.legend()
    ax.grid(True, alpha=0.3)
    fig.tight_layout()
    fig.savefig(out_dir / "stress_response_vs_rpm.png", dpi=140)
    plt.close(fig)

    fig, ax = plt.subplots(figsize=(9, 5))
    ax.bar(
        [str(u) for u in steps],
        [by_step[u].n_503 for u in steps],
        label="HTTP 503",
        color="#d0021b",
    )
    ax.bar(
        [str(u) for u in steps],
        [by_step[u].n_other_err for u in steps],
        bottom=[by_step[u].n_503 for u in steps],
        label="Другие ошибки",
        color="#bd10e0",
    )
    ax.set_xlabel("Параллельные пользователи")
    ax.set_ylabel("Количество ошибок")
    ax.set_title("Стресс-тест: ошибки сервера по нагрузке")
    ax.legend()
    ax.grid(True, axis="y", alpha=0.3)
    fig.tight_layout()
    fig.savefig(out_dir / "stress_errors.png", dpi=140)
    plt.close(fig)

    print(f"\nплоты записаны в {out_dir}/stress_*.png")


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("paths", nargs="+", type=Path)
    ap.add_argument("--mode", choices=["load", "stress"], required=True)
    args = ap.parse_args()

    if args.mode == "load":
        analyze_load(args.paths)
    else:
        if len(args.paths) != 1:
            print("режим stress ожидает ровно один JTL", file=sys.stderr)
            return 2
        analyze_stress(args.paths[0])
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
