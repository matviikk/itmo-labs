from __future__ import annotations

import os
from typing import Tuple

import numpy as np
import torch


def load_idx_images(path: str) -> torch.Tensor:
    with open(path, "rb") as f:
        magic = int.from_bytes(f.read(4), "big")
        if magic != 2051:
            raise ValueError(f"Bad IDX image magic: {magic} (expected 2051)")
        n = int.from_bytes(f.read(4), "big")
        rows = int.from_bytes(f.read(4), "big")
        cols = int.from_bytes(f.read(4), "big")
        data = np.frombuffer(f.read(), dtype=np.uint8)
    images = data.reshape(n, 1, rows, cols).astype(np.float32) / 255.0
    return torch.from_numpy(images)


def load_idx_labels(path: str) -> torch.Tensor:
    with open(path, "rb") as f:
        magic = int.from_bytes(f.read(4), "big")
        if magic != 2049:
            raise ValueError(f"Bad IDX label magic: {magic} (expected 2049)")
        n = int.from_bytes(f.read(4), "big")
        data = np.frombuffer(f.read(), dtype=np.uint8)
    labels = data.reshape(n).astype(np.int64)
    return torch.from_numpy(labels)


def load_mnist_from_folder(root: str) -> Tuple[torch.Tensor, torch.Tensor, torch.Tensor, torch.Tensor]:
    train_images = load_idx_images(os.path.join(root, "train-images.idx3-ubyte"))
    train_labels = load_idx_labels(os.path.join(root, "train-labels.idx1-ubyte"))
    test_images = load_idx_images(os.path.join(root, "t10k-images.idx3-ubyte"))
    test_labels = load_idx_labels(os.path.join(root, "t10k-labels.idx1-ubyte"))
    return train_images, train_labels, test_images, test_labels

