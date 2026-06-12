from pathlib import Path
import numpy as np
import soundfile as sf
from scipy.signal import welch, spectrogram
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parent.parent
OUT = Path(__file__).resolve().parent
FILES = {
    "original": ROOT / "Пример Маргарита1.mp3",
    "degraded": ROOT / "Пример Маргарита3 distort.mp3",
    "restored V2": ROOT / "Пример Маргарита3 restored_V2.mp3",
    "restored V3": ROOT / "Пример Маргарита3 restored_V3.mp3",
}
BANDS = [(0, 300), (300, 3400), (3400, 8000)]


def load(p):
    x, sr = sf.read(p)
    if x.ndim > 1:
        x = x.mean(1)
    return x.astype(float), sr


def floor_dbfs(x, sr):
    w = int(0.3 * sr)
    r = [np.sqrt(np.mean(x[i:i + w] ** 2)) for i in range(0, len(x) - w, w)]
    return 20 * np.log10(min(v for v in r if v > 0) + 1e-12)


def metrics(x, sr):
    f, p = welch(x, sr, nperseg=2048)
    pk = np.max(np.abs(x))
    rms = np.sqrt(np.mean(x ** 2))
    band = [round(p[(f >= lo) & (f < hi)].sum() / p.sum() * 100, 1) for lo, hi in BANDS]
    return dict(
        rms=round(rms, 4),
        peak_dbfs=round(20 * np.log10(pk), 1),
        zcr=round(np.mean(np.abs(np.diff(np.sign(x)))) / 2, 4),
        centroid=round((f * p).sum() / p.sum()),
        floor_dbfs=round(floor_dbfs(x, sr), 1),
        band=band,
    )


sig = {k: load(p) for k, p in FILES.items()}
for k, (x, sr) in sig.items():
    print(f"{k:12}", metrics(x, sr))

fig, ax = plt.subplots(4, 1, figsize=(11, 9), sharex=True)
for a, (k, (x, sr)) in zip(ax, sig.items()):
    f, t, S = spectrogram(x, sr, nperseg=1024, noverlap=768)
    a.pcolormesh(t, f, 10 * np.log10(S + 1e-10), shading="auto", cmap="magma", vmin=-100, vmax=-30)
    a.set_ylim(0, 8000); a.set_title(k); a.set_ylabel("Hz")
ax[-1].set_xlabel("s")
plt.tight_layout(); plt.savefig(OUT / "fig_spectrogram.png", dpi=130); plt.close()

plt.figure(figsize=(10, 5))
for k, (x, sr) in sig.items():
    f, p = welch(x, sr, nperseg=4096)
    plt.semilogy(f, p, label=k, lw=1.3)
for fc in (300, 3400):
    plt.axvline(fc, ls="--", color="gray", lw=1)
plt.xlim(0, 8000); plt.xlabel("Hz"); plt.ylabel("PSD"); plt.legend(); plt.grid(alpha=.3)
plt.title("PSD: degraded vs restored vs original")
plt.tight_layout(); plt.savefig(OUT / "fig_psd.png", dpi=130); plt.close()

xd, srd = sig["degraded"]
seg = slice(0, int(2.5 * srd))
fig, ax = plt.subplots(3, 1, figsize=(11, 6), sharex=True, sharey=True)
for a, k in zip(ax, ["degraded", "restored V2", "restored V3"]):
    x, sr = sig[k]
    a.plot(np.arange(len(x[seg])) / sr, x[seg], lw=0.4); a.set_title(k)
ax[-1].set_xlabel("s")
plt.tight_layout(); plt.savefig(OUT / "fig_waveform.png", dpi=130); plt.close()
print("figures:", OUT)
