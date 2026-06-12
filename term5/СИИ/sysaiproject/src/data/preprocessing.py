import numpy as np
import pandas as pd
import polars as pl
import torch
from pathlib import Path
from typing import Optional


class SimpleTorchLoader:
    def __init__(
        self,
        frame: pd.DataFrame,
        target,
        features,
        val_size=0.2,
        test_size=0.1,
        seed=52,
        standardize=True,
    ):
        self.frame = frame.copy()
        self.target = target
        self.features = features
        self.val_size = val_size
        self.test_size = test_size
        self.seed = seed
        self.standardize = standardize

    def split(self):
        X_all = self.frame[self.features].to_numpy(dtype=np.float32)
        y_all = self.frame[self.target].to_numpy(dtype=np.float32)

        N = X_all.shape[0]
        rng = np.random.default_rng(self.seed)
        indices = np.arange(N)
        rng.shuffle(indices)

        n_test = int(N * self.test_size)
        n_val = int(N * self.val_size)

        idx_test = indices[:n_test]
        idx_val = indices[n_test:n_test + n_val]
        idx_train = indices[n_test + n_val:]

        X_train, y_train = X_all[idx_train], y_all[idx_train]
        X_val, y_val = X_all[idx_val], y_all[idx_val]
        X_test, y_test = X_all[idx_test], y_all[idx_test]

        if self.standardize:
            mean = X_train.mean(axis=0, keepdims=True)
            std = X_train.std(axis=0, keepdims=True)
            std[std < 1e-6] = 1.0
            X_train = (X_train - mean) / std
            X_val = (X_val - mean) / std
            X_test = (X_test - mean) / std

        to_tensor = lambda arr: torch.from_numpy(arr.astype(np.float32))

        return {
            "X_train": to_tensor(X_train),
            "y_train": to_tensor(y_train).view(-1, 1),
            "X_val": to_tensor(X_val) if n_val > 0 else None,
            "y_val": to_tensor(y_val).view(-1, 1) if n_val > 0 else None,
            "X_test": to_tensor(X_test) if n_test > 0 else None,
            "y_test": to_tensor(y_test).view(-1, 1) if n_test > 0 else None,
        }


def _resolve_dataset_path(filename: str) -> Path:
    """Ищем файл только по явному пути или внутри каталога datasets/."""
    user_path = Path(filename)
    if user_path.exists():
        return user_path
    base = Path(__file__).resolve().parents[2] / "datasets"
    candidate = base / filename
    if candidate.exists():
        return candidate
    raise FileNotFoundError(f"Не найден файл {filename} (пробовали {user_path} и {candidate})")


def load_titanic_preprocessed():
    df = pd.read_csv(_resolve_dataset_path("titanic_preprocessed.csv"))
    _ = pl.DataFrame(df.head())
    target = "Survived"
    features = [col for col in df.columns if col != target]
    return df, features, target


def load_california_preprocessed():
    df = pd.read_csv(_resolve_dataset_path("california_preprocessed.csv"))
    _ = pl.DataFrame(df.head())
    target = "Median_House_Value"
    features = [col for col in df.columns if col != target]
    return df, features, target


def _train_test_split(x: np.ndarray, y: np.ndarray, test_size: float = 0.2, random_state: int = 42):
    rng = np.random.default_rng(seed=random_state)
    idx = np.arange(x.shape[0])
    rng.shuffle(idx)
    n_test = int(len(idx) * test_size)
    test_idx = idx[:n_test]
    train_idx = idx[n_test:]
    return x[train_idx], x[test_idx], y[train_idx], y[test_idx]


def preprocess_titanic_data(path: str, device: str = "cpu", test_size: float = 0.2, random_state: int = 42):
    data_path = _resolve_dataset_path(path if path else "titanic_preprocessed.csv")
    df = pd.read_csv(data_path)
    target = "Survived"
    feature_names = [c for c in df.columns if c != target]

    X = df[feature_names].to_numpy(dtype=np.float32)
    y = df[target].to_numpy(dtype=np.float32)

    X_train, X_test, y_train, y_test = _train_test_split(X, y, test_size=test_size, random_state=random_state)

    # стандартизация признаков на обучении
    mean = X_train.mean(axis=0, keepdims=True)
    std = X_train.std(axis=0, keepdims=True)
    std[std < 1e-6] = 1.0
    X_train = (X_train - mean) / std
    X_test = (X_test - mean) / std

    to_tensor = lambda arr: torch.from_numpy(arr).to(device=device, dtype=torch.float32)
    return (
        to_tensor(X_train),
        to_tensor(X_test),
        to_tensor(y_train),
        to_tensor(y_test),
        feature_names,
    )


def preprocess_california_data(path: str, device: str = "cpu", test_size: float = 0.2, random_state: int = 42):
    data_path = _resolve_dataset_path(path if path else "California_Houses.csv")
    df = pd.read_csv(data_path)
    target = df["Median_House_Value"].astype(np.float32).to_numpy()
    features = df.drop(columns=["Median_House_Value"]).astype(np.float32)

    feature_names = list(features.columns)
    X = features.to_numpy(dtype=np.float32)
    y = target

    X_train, X_test, y_train, y_test = _train_test_split(X, y, test_size=test_size, random_state=random_state)

    # стандартизуем признаки для регрессии
    mean = X_train.mean(axis=0, keepdims=True)
    std = X_train.std(axis=0, keepdims=True)
    std[std < 1e-6] = 1.0
    X_train = (X_train - mean) / std
    X_test = (X_test - mean) / std

    to_tensor = lambda arr: torch.from_numpy(arr).to(device=device, dtype=torch.float32)
    return (
        to_tensor(X_train),
        to_tensor(X_test),
        torch.from_numpy(y_train).to(device=device, dtype=torch.float32),
        torch.from_numpy(y_test).to(device=device, dtype=torch.float32),
        feature_names,
    )


def preprocess_fashion_mnist(path: str, device: str = "cpu", test_size: float = 0.2, random_state: int = 42, limit: Optional[int] = 5000):
    data_path = _resolve_dataset_path(path if path else "fashionmnist/fashion-mnist_train.csv")
    df = pd.read_csv(data_path)
    label_col = "label"
    feature_cols = [c for c in df.columns if c != label_col]
    if limit and limit < len(df):
        df = df.sample(n=limit, random_state=random_state).reset_index(drop=True)
    X = (df[feature_cols].to_numpy(dtype=np.float32) / 255.0)
    y = df[label_col].to_numpy(dtype=np.int64)
    X_train, X_test, y_train, y_test = _train_test_split(X, y, test_size=test_size, random_state=random_state)
    to_tensor = lambda arr: torch.from_numpy(arr).to(device=device, dtype=torch.float32)
    return to_tensor(X_train), to_tensor(X_test), torch.from_numpy(y_train).to(device=device), torch.from_numpy(y_test).to(device=device)


def preprocess_insurance(path: str, device: str = "cpu", test_size: float = 0.2, random_state: int = 42):
    data_path = _resolve_dataset_path(path if path else "insurance.csv")
    df = pd.read_csv(data_path)
    target_col = "charges"
    y = df[target_col].astype(np.float32).to_numpy()
    features = df.drop(columns=[target_col])
    cat_cols = ["sex", "smoker", "region"]
    features = pd.get_dummies(features, columns=cat_cols, drop_first=True)
    feature_names = list(features.columns)
    X = features.to_numpy(dtype=np.float32)
    X_train, X_test, y_train, y_test = _train_test_split(X, y, test_size=test_size, random_state=random_state)

    mean = X_train.mean(axis=0, keepdims=True)
    std = X_train.std(axis=0, keepdims=True)
    std[std < 1e-6] = 1.0
    X_train = (X_train - mean) / std
    X_test = (X_test - mean) / std

    to_tensor = lambda arr: torch.from_numpy(arr).to(device=device, dtype=torch.float32)
    return (
        to_tensor(X_train),
        to_tensor(X_test),
        torch.from_numpy(y_train).to(device=device, dtype=torch.float32),
        torch.from_numpy(y_test).to(device=device, dtype=torch.float32),
        feature_names,
    )
