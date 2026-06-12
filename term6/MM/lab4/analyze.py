from pathlib import Path
import numpy as np
import soundfile as sf
from scipy.signal import welch, spectrogram
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parent.parent
OUT = Path(__file__).resolve().parent
ORIG = ROOT / "Пример Маргарита1.mp3"
DIST = ROOT / "Пример Маргарита3 distort.mp3"
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
        crest=round(20 * np.log10(pk / rms), 1),
        zcr=round(np.mean(np.abs(np.diff(np.sign(x)))) / 2, 4),
        centroid=round((f * p).sum() / p.sum()),
        floor_dbfs=round(floor_dbfs(x, sr), 1),
        nearclip=round(np.mean(np.abs(x) > 0.95 * pk) * 100, 3),
        band=band,
    )


def spec(ax, x, sr, title):
    f, t, S = spectrogram(x, sr, nperseg=1024, noverlap=768)
    ax.pcolormesh(t, f, 10 * np.log10(S + 1e-10), shading="auto", cmap="magma", vmin=-100, vmax=-30)
    ax.set_ylim(0, 8000)
    ax.set_title(title)
    ax.set_ylabel("Hz")


xo, sro = load(ORIG)
xd, srd = load(DIST)
mo, md = metrics(xo, sro), metrics(xd, srd)
print("original ", mo)
print("degraded ", md)

fig, ax = plt.subplots(2, 1, figsize=(11, 5), sharex=True)
ax[0].plot(np.arange(len(xo)) / sro, xo, lw=0.4); ax[0].set_title("Original — waveform"); ax[0].set_ylabel("amp")
ax[1].plot(np.arange(len(xd)) / srd, xd, lw=0.4, color="firebrick"); ax[1].set_title("Degraded — waveform"); ax[1].set_xlabel("s")
plt.tight_layout(); plt.savefig(OUT / "fig_waveform.png", dpi=130); plt.close()

fig, ax = plt.subplots(2, 1, figsize=(11, 6), sharex=True)
spec(ax[0], xo, sro, "Original — spectrogram")
spec(ax[1], xd, srd, "Degraded — spectrogram"); ax[1].set_xlabel("s")
plt.tight_layout(); plt.savefig(OUT / "fig_spectrogram.png", dpi=130); plt.close()

fo, po = welch(xo, sro, nperseg=4096)
fd, pd = welch(xd, srd, nperseg=4096)
plt.figure(figsize=(10, 5))
plt.semilogy(fo, po, label="original"); plt.semilogy(fd, pd, label="degraded", color="firebrick")
for fc in (300, 3400):
    plt.axvline(fc, ls="--", color="gray", lw=1)
plt.xlim(0, 8000); plt.xlabel("Hz"); plt.ylabel("PSD"); plt.legend(); plt.grid(alpha=.3)
plt.title("Power spectral density (Welch)")
plt.tight_layout(); plt.savefig(OUT / "fig_psd.png", dpi=130); plt.close()
print("figures:", OUT)
