from pathlib import Path
import numpy as np
import cv2
from PIL import Image, ImageSequence
from scipy.ndimage import gaussian_filter
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parent
SRC = ROOT / "media"
SIZE = 512
SEQS = {"animA1": ("A", "animA1.gif"), "animA2": ("A", "animA2.gif"),
        "realB1": ("B", "realB1.mp4"), "realB2": ("B", "realB2.mp4")}
FB = dict(pyr_scale=0.5, levels=5, winsize=15, iterations=3, poly_n=5, poly_sigma=1.2, flags=0)
for d in ("comparison", "flow", "diffmaps", "crops", "metrics"):
    (ROOT / d).mkdir(parents=True, exist_ok=True)


def frames(p):
    cap = cv2.VideoCapture(str(p)); fr = []
    while True:
        ok, f = cap.read()
        if not ok:
            break
        fr.append(cv2.cvtColor(f, cv2.COLOR_BGR2RGB))
    cap.release()
    if len(fr) >= 3:
        return fr
    return [np.array(x.convert("RGB")) for x in ImageSequence.Iterator(Image.open(p))]


def square(a):
    h, w = a.shape[:2]
    m = min(h, w)
    a = a[(h - m) // 2:(h + m) // 2, (w - m) // 2:(w + m) // 2]
    return cv2.resize(a, (SIZE, SIZE), interpolation=cv2.INTER_AREA)


def triplet(p):
    fr = [square(x) for x in frames(p)]
    g = [cv2.cvtColor(x, cv2.COLOR_RGB2GRAY).astype(float) for x in fr]
    d = [np.abs(g[i] - g[i + 2]).mean() for i in range(len(fr) - 2)]
    order = sorted(range(len(d)), key=lambda k: d[k])
    i = order[int(0.75 * (len(order) - 1))]
    return fr[i], fr[i + 1], fr[i + 2]


def blend(a, b):
    return np.clip(0.5 * a.astype(np.float32) + 0.5 * b.astype(np.float32), 0, 255).astype(np.uint8)


def flow_interp(a, b):
    ga = cv2.cvtColor(a, cv2.COLOR_RGB2GRAY)
    gb = cv2.cvtColor(b, cv2.COLOR_RGB2GRAY)
    fwd = cv2.calcOpticalFlowFarneback(ga, gb, None, **FB)
    bwd = cv2.calcOpticalFlowFarneback(gb, ga, None, **FB)
    h, w = a.shape[:2]
    gx, gy = np.meshgrid(np.arange(w, dtype=np.float32), np.arange(h, dtype=np.float32))
    wa = cv2.remap(a, gx - 0.5 * fwd[..., 0], gy - 0.5 * fwd[..., 1], cv2.INTER_LINEAR, borderMode=cv2.BORDER_REFLECT)
    wb = cv2.remap(b, gx - 0.5 * bwd[..., 0], gy - 0.5 * bwd[..., 1], cv2.INTER_LINEAR, borderMode=cv2.BORDER_REFLECT)
    return blend(wa, wb), fwd


def flow_hsv(fl):
    mag, ang = cv2.cartToPolar(fl[..., 0], fl[..., 1])
    hsv = np.zeros((*fl.shape[:2], 3), np.uint8)
    hsv[..., 0] = ang * 90 / np.pi
    hsv[..., 1] = 255
    hsv[..., 2] = cv2.normalize(mag, None, 0, 255, cv2.NORM_MINMAX)
    return cv2.cvtColor(hsv, cv2.COLOR_HSV2RGB)


def flow_arrows(base, fl, step=32):
    img = base.copy(); h, w = fl.shape[:2]
    for y in range(step // 2, h, step):
        for x in range(step // 2, w, step):
            fx, fy = fl[y, x]
            cv2.arrowedLine(img, (x, y), (int(np.clip(x + fx * 3, 0, w - 1)), int(np.clip(y + fy * 3, 0, h - 1))),
                            (255, 50, 50), 1, tipLength=0.35)
    return img


def diffmap(gt, p):
    d = np.abs(gt.astype(float) - p.astype(float)).mean(2)
    d8 = cv2.normalize(d, None, 0, 255, cv2.NORM_MINMAX).astype(np.uint8)
    return cv2.cvtColor(cv2.applyColorMap(d8, cv2.COLORMAP_JET), cv2.COLOR_BGR2RGB)


def psnr(a, b):
    return round(20 * np.log10(255) - 10 * np.log10(np.mean((a.astype(float) - b.astype(float)) ** 2)), 2)


def ssim(a, b):
    a = np.asarray(Image.fromarray(a).convert("L"), float)
    b = np.asarray(Image.fromarray(b).convert("L"), float)
    ma, mb = gaussian_filter(a, 1.5), gaussian_filter(b, 1.5)
    va = gaussian_filter(a * a, 1.5) - ma ** 2
    vb = gaussian_filter(b * b, 1.5) - mb ** 2
    vab = gaussian_filter(a * b, 1.5) - ma * mb
    c1, c2 = (0.01 * 255) ** 2, (0.03 * 255) ** 2
    s = ((2 * ma * mb + c1) * (2 * vab + c2)) / ((ma ** 2 + mb ** 2 + c1) * (va + vb + c2))
    return round(float(s.mean()), 4)


rows = []
for name, (kind, fn) in SEQS.items():
    a, gt, b = triplet(SRC / fn)
    bl = blend(a, b)
    fl, flow = flow_interp(a, b)
    pb, sb = psnr(gt, bl), ssim(gt, bl)
    pf, sf = psnr(gt, fl), ssim(gt, fl)
    rows.append([name, kind, "BLEND", pb, sb])
    rows.append([name, kind, "FLOW", pf, sf])
    print(name, kind, "BLEND", pb, sb, "FLOW", pf, sf)

    hsv = flow_hsv(flow)
    fig, ax = plt.subplots(2, 3, figsize=(15, 10))
    fig.suptitle(f"{name} (type {kind})")
    for axi, (im, t) in zip(ax.flatten(), [(a, "A (N)"), (b, "B (N+2)"), (gt, "GT (N+1)"),
                                           (bl, f"BLEND P={pb} S={sb}"), (fl, f"FLOW P={pf} S={sf}"), (hsv, "HSV flow")]):
        axi.imshow(im); axi.set_title(t, fontsize=9); axi.axis("off")
    plt.tight_layout(); plt.savefig(ROOT / f"comparison/compare_{name}.png", dpi=120, bbox_inches="tight"); plt.close()

    fig, ax = plt.subplots(1, 2, figsize=(12, 5))
    fig.suptitle(f"optical flow, {name}")
    ax[0].imshow(hsv); ax[0].set_title("HSV"); ax[0].axis("off")
    ax[1].imshow(flow_arrows(a, flow)); ax[1].set_title("arrows"); ax[1].axis("off")
    plt.tight_layout(); plt.savefig(ROOT / f"flow/flow_{name}.png", dpi=120, bbox_inches="tight"); plt.close()

    fig, ax = plt.subplots(1, 3, figsize=(14, 4))
    fig.suptitle(f"|GT - interp|, {name}")
    for axi, (im, t) in zip(ax, [(gt, "GT"), (diffmap(gt, bl), "BLEND"), (diffmap(gt, fl), "FLOW")]):
        axi.imshow(im); axi.set_title(t); axi.axis("off")
    plt.tight_layout(); plt.savefig(ROOT / f"diffmaps/diff_{name}.png", dpi=130, bbox_inches="tight"); plt.close()

    for rn, (cyf, cxf) in {"center": (.5, .5), "topleft": (.25, .25), "bottomright": (.75, .75)}.items():
        y = max(0, min(int(cyf * SIZE - 64), SIZE - 128))
        x = max(0, min(int(cxf * SIZE - 64), SIZE - 128))
        sl = np.s_[y:y + 128, x:x + 128]
        fig, ax = plt.subplots(1, 3, figsize=(10, 4))
        fig.suptitle(f"{name} crop {rn}")
        for axi, (im, t) in zip(ax, [(gt[sl], "GT"), (bl[sl], f"BLEND P={pb}"), (fl[sl], f"FLOW P={pf}")]):
            axi.imshow(im); axi.set_title(t, fontsize=9); axi.axis("off")
        plt.tight_layout(); plt.savefig(ROOT / f"crops/crop_{name}_{rn}.png", dpi=130, bbox_inches="tight"); plt.close()

import csv
with open(ROOT / "metrics/all_metrics.csv", "w", newline="") as fp:
    w = csv.writer(fp); w.writerow(["sequence", "type", "method", "psnr", "ssim"]); w.writerows(rows)

for kind in "AB":
    sub = [r for r in rows if r[1] == kind]
    seqs = sorted({r[0] for r in sub})
    x = np.arange(len(seqs)); wd = 0.35
    fig, ax = plt.subplots(1, 2, figsize=(12, 5))
    fig.suptitle(f"type {kind}")
    for axi, idx, yl in [(ax[0], 3, "PSNR, dB"), (ax[1], 4, "SSIM")]:
        for j, mn in enumerate(("BLEND", "FLOW")):
            vals = [next(r[idx] for r in sub if r[0] == s and r[2] == mn) for s in seqs]
            bars = axi.bar(x + j * wd, vals, wd, label=mn)
            axi.bar_label(bars, fmt="%.2f", fontsize=8, padding=2)
        axi.set_xticks(x + wd / 2); axi.set_xticklabels(seqs, fontsize=8); axi.set_ylabel(yl); axi.legend(); axi.grid(alpha=.3, axis="y")
    plt.tight_layout(); plt.savefig(ROOT / f"metrics/plot_type{kind}.png", dpi=120, bbox_inches="tight"); plt.close()
print("done")
