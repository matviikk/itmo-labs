from pathlib import Path
import numpy as np
import cv2
from PIL import Image
from scipy.ndimage import gaussian_filter
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parent
IN = ROOT.parent / "lab2/in"
OUT = ROOT
SIZE = 512
FACTORS = [2, 3, 4]
IMAGES = {"cartoon1": "A", "cartoon2": "A", "photo1": "B", "painting1": "B"}
METHODS = {"NEAREST": cv2.INTER_NEAREST, "LINEAR": cv2.INTER_LINEAR,
           "CUBIC": cv2.INTER_CUBIC, "LANCZOS4": cv2.INTER_LANCZOS4}
for d in ("grids", "crops", "metrics"):
    (OUT / d).mkdir(parents=True, exist_ok=True)


def load(name):
    im = Image.open(IN / f"{name}.jpg").convert("RGBA")
    bg = Image.new("RGBA", im.size, (255, 255, 255, 255))
    im = Image.alpha_composite(bg, im).convert("RGB")
    a = np.asarray(im)
    h, w = a.shape[:2]
    m = min(h, w)
    a = a[(h - m) // 2:(h + m) // 2, (w - m) // 2:(w + m) // 2]
    return cv2.resize(a, (SIZE, SIZE), interpolation=cv2.INTER_AREA)


def psnr(a, b):
    e = np.mean((a.astype(float) - b.astype(float)) ** 2)
    return round(20 * np.log10(255) - 10 * np.log10(e), 2)


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


imgs = {n: (k, load(n)) for n, k in IMAGES.items()}
rows = []
for name, (kind, hr) in imgs.items():
    for f in FACTORS:
        lr = cv2.resize(hr, (SIZE // f, SIZE // f), interpolation=cv2.INTER_AREA)
        for mn, mf in METHODS.items():
            sr = cv2.resize(lr, (SIZE, SIZE), interpolation=mf)
            rows.append([name, kind, f, mn, psnr(hr, sr), ssim(hr, sr)])

import csv
with open(OUT / "metrics/all_metrics.csv", "w", newline="") as fp:
    w = csv.writer(fp); w.writerow(["image", "type", "factor", "method", "psnr", "ssim"]); w.writerows(rows)


def pivot(kind, col):
    idx = {"psnr": 4, "ssim": 5}[col]
    sub = [r for r in rows if r[1] == kind]
    table = [["factor \\ method"] + list(METHODS)]
    for f in FACTORS:
        line = [f"x{f}"]
        for mn in METHODS:
            vals = [r[idx] for r in sub if r[2] == f and r[3] == mn]
            line.append(round(sum(vals) / len(vals), 2 if col == "psnr" else 4))
        table.append(line)
    return table


for name, (kind, hr) in imgs.items():
    lr = cv2.resize(hr, (SIZE // 4, SIZE // 4), interpolation=cv2.INTER_AREA)
    nn = cv2.resize(lr, (SIZE, SIZE), interpolation=cv2.INTER_NEAREST)
    fig, ax = plt.subplots(2, 3, figsize=(15, 10))
    fig.suptitle(f"{name} (type {kind}) x4")
    ax = ax.flatten()
    ax[0].imshow(hr); ax[0].set_title("HR")
    ax[1].imshow(nn); ax[1].set_title("LR x4 (NN preview)")
    for i, (mn, mf) in enumerate(METHODS.items()):
        sr = cv2.resize(lr, (SIZE, SIZE), interpolation=mf)
        r = next(r for r in rows if r[0] == name and r[2] == 4 and r[3] == mn)
        ax[i + 2].imshow(sr); ax[i + 2].set_title(f"{mn}\nPSNR={r[4]} SSIM={r[5]}")
    for a in ax:
        a.axis("off")
    plt.tight_layout(); plt.savefig(OUT / f"grids/grid_{name}.png", dpi=120, bbox_inches="tight"); plt.close()

CROP = 96
REGIONS = {"center": (.5, .5), "topleft": (.25, .25), "bottomright": (.75, .75)}
for name, (kind, hr) in imgs.items():
    lr = cv2.resize(hr, (SIZE // 4, SIZE // 4), interpolation=cv2.INTER_AREA)
    srs = {mn: cv2.resize(lr, (SIZE, SIZE), interpolation=mf) for mn, mf in METHODS.items()}
    for rn, (cy, cx) in REGIONS.items():
        y = max(0, min(int(cy * SIZE - CROP / 2), SIZE - CROP))
        x = max(0, min(int(cx * SIZE - CROP / 2), SIZE - CROP))
        fig, ax = plt.subplots(1, 5, figsize=(14, 3))
        fig.suptitle(f"{name} crop {rn} x4")
        ax[0].imshow(hr[y:y + CROP, x:x + CROP]); ax[0].set_title("HR")
        for i, mn in enumerate(METHODS):
            r = next(r for r in rows if r[0] == name and r[2] == 4 and r[3] == mn)
            ax[i + 1].imshow(srs[mn][y:y + CROP, x:x + CROP]); ax[i + 1].set_title(f"{mn}\nP={r[4]} S={r[5]}", fontsize=8)
        for a in ax:
            a.axis("off")
        plt.tight_layout(); plt.savefig(OUT / f"crops/crop_{name}_{rn}.png", dpi=130, bbox_inches="tight"); plt.close()

for kind in "AB":
    fig, ax = plt.subplots(1, 2, figsize=(12, 5))
    fig.suptitle(f"Quality vs factor, type {kind}")
    for axi, col, yl in [(ax[0], 4, "PSNR, dB"), (ax[1], 5, "SSIM")]:
        for mn in METHODS:
            ys = []
            for f in FACTORS:
                vals = [r[col] for r in rows if r[1] == kind and r[2] == f and r[3] == mn]
                ys.append(sum(vals) / len(vals))
            axi.plot(FACTORS, ys, marker="o", label=mn, lw=2)
        axi.set_xticks(FACTORS); axi.set_xlabel("factor"); axi.set_ylabel(yl); axi.legend(); axi.grid(alpha=.3)
    plt.tight_layout(); plt.savefig(OUT / f"metrics/plot_type{kind}.png", dpi=120, bbox_inches="tight"); plt.close()

for kind in "AB":
    print(f"type {kind} PSNR", pivot(kind, "psnr"))
    print(f"type {kind} SSIM", pivot(kind, "ssim"))
print("done")
