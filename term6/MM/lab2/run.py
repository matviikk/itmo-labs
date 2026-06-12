import subprocess
from pathlib import Path
import numpy as np
from PIL import Image
from scipy.ndimage import gaussian_filter
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parent
BIN = ROOT.parent / "tools/realesrgan/realesrgan-ncnn-vulkan"
MODELS = ROOT.parent / "tools/realesrgan/models"
HR, LR = 1024, 256
IMAGES = {
    "cartoon1": "A", "cartoon2": "A",
    "photo1": "B", "painting1": "B",
}
for d in ("hr", "lr", "sr"):
    (ROOT / d).mkdir(exist_ok=True)


def prep(name):
    im = Image.open(ROOT / "in" / f"{name}.jpg").convert("RGBA")
    bg = Image.new("RGBA", im.size, (255, 255, 255, 255))
    im = Image.alpha_composite(bg, im).convert("RGB")
    w, h = im.size
    m = min(w, h)
    im = im.crop(((w - m) // 2, (h - m) // 2, (w + m) // 2, (h + m) // 2))
    hr = im.resize((HR, HR), Image.LANCZOS)
    hr.save(ROOT / "hr" / f"{name}.png")
    hr.resize((LR, LR), Image.BOX).save(ROOT / "lr" / f"{name}.png")
    return hr


def esrgan(name, model):
    out = ROOT / "sr" / f"{name}_{model}.png"
    if not out.exists():
        subprocess.run([str(BIN), "-i", str(ROOT / "lr" / f"{name}.png"), "-o", str(out),
                        "-n", model, "-m", str(MODELS), "-s", "4"],
                       check=True, capture_output=True)
    return Image.open(out).convert("RGB").resize((HR, HR), Image.LANCZOS)


def detail_crop(hr, size=160):
    g = np.asarray(hr.convert("L"), float)
    lap = np.abs(np.gradient(g)[0]) + np.abs(np.gradient(g)[1])
    step = 64
    best, by, bx = -1, 0, 0
    for y in range(0, HR - size, step):
        for x in range(0, HR - size, step):
            v = lap[y:y + size, x:x + size].sum()
            if v > best:
                best, by, bx = v, y, x
    return by, bx


def psnr(a, b):
    e = np.mean((np.asarray(a, float) - np.asarray(b, float)) ** 2)
    return round(20 * np.log10(255) - 10 * np.log10(e), 2)


def ssim(a, b):
    a = np.asarray(a.convert("L"), float)
    b = np.asarray(b.convert("L"), float)
    mu_a = gaussian_filter(a, 1.5); mu_b = gaussian_filter(b, 1.5)
    va = gaussian_filter(a * a, 1.5) - mu_a ** 2
    vb = gaussian_filter(b * b, 1.5) - mu_b ** 2
    vab = gaussian_filter(a * b, 1.5) - mu_a * mu_b
    c1, c2 = (0.01 * 255) ** 2, (0.03 * 255) ** 2
    s = ((2 * mu_a * mu_b + c1) * (2 * vab + c2)) / ((mu_a ** 2 + mu_b ** 2 + c1) * (va + vb + c2))
    return round(float(s.mean()), 4)


rows = []
for name, kind in IMAGES.items():
    hr = prep(name)
    lr_up = Image.open(ROOT / "lr" / f"{name}.png").resize((HR, HR), Image.BICUBIC)
    gen = esrgan(name, "realesrgan-x4plus")
    ani = esrgan(name, "realesrgan-x4plus-anime")
    variants = {"bicubic": lr_up, "ESRGAN-general": gen, "ESRGAN-anime": ani}
    m = {k: (psnr(hr, v), ssim(hr, v)) for k, v in variants.items()}
    rows.append((name, kind, m))
    print(name, kind, m)

    best = "ESRGAN-anime" if kind == "A" else "ESRGAN-general"
    show = [("HR", hr), ("bicubic", lr_up), (best, variants[best])]
    cy, cx = detail_crop(hr)
    fig, ax = plt.subplots(2, 3, figsize=(12, 8))
    fig.suptitle(f"{name} (type {kind}) — ×4 upsampling", fontweight="bold")
    for i, (t, im) in enumerate(show):
        tag = "" if t == "HR" else f"\nPSNR={m[t][0]} SSIM={m[t][1]}"
        ax[0, i].imshow(im); ax[0, i].set_title(t + tag, fontsize=10)
        ax[1, i].imshow(np.asarray(im)[cy:cy + 160, cx:cx + 160])
        ax[0, i].axis("off"); ax[1, i].axis("off")
    plt.tight_layout(); plt.savefig(ROOT / f"compare_{name}.png", dpi=130, bbox_inches="tight")
    plt.close()

with open(ROOT / "metrics.csv", "w") as f:
    f.write("image,type,method,psnr,ssim\n")
    for name, kind, m in rows:
        for k, (p, s) in m.items():
            f.write(f"{name},{kind},{k},{p},{s}\n")
print("done")
