from __future__ import annotations

from dataclasses import dataclass
from typing import Optional

import numpy as np
from PIL import Image, ImageOps


def _otsu_threshold(img: np.ndarray) -> int:
    hist = np.bincount(img.reshape(-1), minlength=256).astype(np.float64)
    total = img.size
    if total == 0:
        return 127

    sum_total = float(np.dot(np.arange(256), hist))
    sum_b = 0.0
    w_b = 0.0
    max_var = -1.0
    threshold = 127

    for t in range(256):
        w_b += hist[t]
        if w_b == 0:
            continue
        w_f = total - w_b
        if w_f == 0:
            break

        sum_b += t * hist[t]
        m_b = sum_b / w_b
        m_f = (sum_total - sum_b) / w_f
        var_between = w_b * w_f * (m_b - m_f) ** 2
        if var_between > max_var:
            max_var = var_between
            threshold = t
    return int(threshold)


def _shift_to_center(img: np.ndarray, shift_y: int, shift_x: int) -> np.ndarray:
    h, w = img.shape
    out = np.zeros_like(img)

    src_y0 = max(0, -shift_y)
    src_y1 = min(h, h - shift_y) if shift_y >= 0 else h
    dst_y0 = max(0, shift_y)
    dst_y1 = dst_y0 + (src_y1 - src_y0)

    src_x0 = max(0, -shift_x)
    src_x1 = min(w, w - shift_x) if shift_x >= 0 else w
    dst_x0 = max(0, shift_x)
    dst_x1 = dst_x0 + (src_x1 - src_x0)

    if src_y1 > src_y0 and src_x1 > src_x0:
        out[dst_y0:dst_y1, dst_x0:dst_x1] = img[src_y0:src_y1, src_x0:src_x1]
    return out


def preprocess_drawn_digit(path: str, *, invert_if_background_white: bool = True) -> np.ndarray:

    pil = Image.open(path).convert("L")

    if invert_if_background_white:
        arr0 = np.array(pil, dtype=np.uint8)
        if float(arr0.mean()) > 127:
            pil = ImageOps.invert(pil)

    arr = np.array(pil, dtype=np.uint8)
    thr = _otsu_threshold(arr)
    mask = arr > thr
    if not mask.any():
        return np.zeros((28, 28), dtype=np.float32)

    ys, xs = np.where(mask)
    y0, y1 = int(ys.min()), int(ys.max()) + 1
    x0, x1 = int(xs.min()), int(xs.max()) + 1
    cropped = arr[y0:y1, x0:x1]

    h, w = cropped.shape
    side = max(h, w)
    square = np.zeros((side, side), dtype=np.uint8)
    oy = (side - h) // 2
    ox = (side - w) // 2
    square[oy : oy + h, ox : ox + w] = cropped

    digit20 = Image.fromarray(square).resize((20, 20), resample=Image.BILINEAR)
    digit20 = np.array(digit20, dtype=np.uint8)

    canvas = np.zeros((28, 28), dtype=np.uint8)
    canvas[4:24, 4:24] = digit20

    weights = canvas.astype(np.float64)
    s = weights.sum()
    if s > 0:
        ys2, xs2 = np.indices(canvas.shape)
        cy = float((ys2 * weights).sum() / s)
        cx = float((xs2 * weights).sum() / s)
        shift_y = int(np.round(14 - cy))
        shift_x = int(np.round(14 - cx))
        canvas = _shift_to_center(canvas, shift_y, shift_x)

    return (canvas.astype(np.float32) / 255.0).clip(0.0, 1.0)

