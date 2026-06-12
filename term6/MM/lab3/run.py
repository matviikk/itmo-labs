import subprocess
from pathlib import Path
import numpy as np
from PIL import Image
from scipy.ndimage import gaussian_filter
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parent
FRAMES = sorted((ROOT / "frames").glob("*.png"))
BIN = next((ROOT.parent / "tools/rife").rglob("rife-ncnn-vulkan"))
MODEL = next(p for p in sorted(BIN.parent.glob("rife-v4*"), reverse=True) if p.is_dir())


def arr(p):
    return np.asarray(Image.open(p).convert("RGB"), float)


def psnr(a, b):
    e = np.mean((a - b) ** 2)
    return round(20 * np.log10(255) - 10 * np.log10(e), 2)


def ssim(a, b):
    a = np.asarray(Image.fromarray(a.astype("uint8")).convert("L"), float)
    b = np.asarray(Image.fromarray(b.astype("uint8")).convert("L"), float)
    ma, mb = gaussian_filter(a, 1.5), gaussian_filter(b, 1.5)
    va = gaussian_filter(a * a, 1.5) - ma ** 2
    vb = gaussian_filter(b * b, 1.5) - mb ** 2
    vab = gaussian_filter(a * b, 1.5) - ma * mb
    c1, c2 = (0.01 * 255) ** 2, (0.03 * 255) ** 2
    s = ((2 * ma * mb + c1) * (2 * vab + c2)) / ((ma ** 2 + mb ** 2 + c1) * (va + vb + c2))
    return round(float(s.mean()), 4)


g = np.stack([np.asarray(Image.open(f).convert("L"), float) for f in FRAMES])
d2 = [np.abs(g[i] - g[i + 2]).mean() for i in range(len(g) - 2)]
i = max(range(8, len(d2) - 8), key=lambda k: d2[k])
A, GT, B = FRAMES[i], FRAMES[i + 1], FRAMES[i + 2]
print("triplet", A.name, GT.name, B.name)

mid = ROOT / "rife_mid.png"
subprocess.run([str(BIN), "-0", str(A), "-1", str(B), "-o", str(mid), "-m", str(MODEL)],
               check=True, capture_output=True)

a, gt, b = arr(A), arr(GT), arr(B)
rife = arr(mid)
blend = (a + b) / 2
m = {"blend": (psnr(gt, blend), ssim(gt, blend)), "RIFE": (psnr(gt, rife), ssim(gt, rife))}
print(m)

gg = np.abs(np.gradient(np.asarray(Image.fromarray(gt.astype("uint8")).convert("L"), float))[0])
h, w = gg.shape
cs = 200
by = max(0, min(int(np.argmax(gg.sum(1))) - cs // 2, h - cs))
bx = max(0, min(int(np.argmax(gg.sum(0))) - cs // 2, w - cs))
sl = np.s_[by:by + cs, bx:bx + cs]

fig, ax = plt.subplots(2, 4, figsize=(16, 6))
fig.suptitle("Frame interpolation on real video: neural (RIFE) vs naive blend", fontweight="bold")
top = [("frame A", a), ("real middle (GT)", gt),
       (f"blend\nPSNR={m['blend'][0]} SSIM={m['blend'][1]}", blend),
       (f"RIFE\nPSNR={m['RIFE'][0]} SSIM={m['RIFE'][1]}", rife)]
for j, (t, im) in enumerate(top):
    ax[0, j].imshow(im.astype("uint8")); ax[0, j].set_title(t, fontsize=10); ax[0, j].axis("off")
    ax[1, j].imshow(im[sl].astype("uint8")); ax[1, j].axis("off")
ax[1, 0].set_title("crop", fontsize=9)
plt.tight_layout(); plt.savefig(ROOT / "compare_interp.png", dpi=130, bbox_inches="tight")
plt.close()

out = ROOT / "frames2x"
out.mkdir(exist_ok=True)
subprocess.run([str(BIN), "-i", str(ROOT / "frames"), "-o", str(out), "-m", str(MODEL)],
               check=True, capture_output=True)
subprocess.run(["ffmpeg", "-v", "error", "-y", "-framerate", "60",
                "-i", str(out / "%08d.png"), "-pix_fmt", "yuv420p",
                str(ROOT / "interpolated_60fps.mp4")], check=True)
print("frames2x:", len(list(out.glob("*.png"))), "done")
