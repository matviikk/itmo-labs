import os
from typing import Optional

import matplotlib.pyplot as plt


def save_curves(history: dict, title_prefix: str, folder: str) -> None:
    os.makedirs(folder, exist_ok=True)
    epochs = range(1, len(next(iter(history.values()))) + 1)

    if "train_loss" in history:
        plt.figure()
        plt.plot(epochs, history["train_loss"], label="train")
        if "val_loss" in history and history["val_loss"]:
            plt.plot(epochs, history["val_loss"], label="val")
        plt.xlabel("epoch")
        plt.ylabel("loss")
        plt.title(f"{title_prefix}: loss")
        plt.legend()
        plt.tight_layout()
        plt.savefig(os.path.join(folder, f"{title_prefix}_loss.png"), dpi=200)
        plt.close()

    if "train_acc" in history:
        plt.figure()
        plt.plot(epochs, history["train_acc"], label="train acc")
        if "val_acc" in history and history["val_acc"]:
            plt.plot(epochs, history["val_acc"], label="val acc")
        plt.xlabel("epoch")
        plt.ylabel("accuracy")
        plt.title(f"{title_prefix}: accuracy")
        plt.legend()
        plt.tight_layout()
        plt.savefig(os.path.join(folder, f"{title_prefix}_acc.png"), dpi=200)
        plt.close()

    if "train_f1" in history:
        plt.figure()
        plt.plot(epochs, history["train_f1"], label="train f1")
        if "val_f1" in history and history["val_f1"]:
            plt.plot(epochs, history["val_f1"], label="val f1")
        plt.xlabel("epoch")
        plt.ylabel("f1")
        plt.title(f"{title_prefix}: f1")
        plt.legend()
        plt.tight_layout()
        plt.savefig(os.path.join(folder, f"{title_prefix}_f1.png"), dpi=200)
        plt.close()

    if "train_mse" in history:
        for metric in ("mse", "rmse", "mae"):
            train_key = f"train_{metric}"
            if train_key not in history:
                continue
            plt.figure()
            plt.plot(epochs, history[train_key], label=f"train {metric}")
            val_key = f"val_{metric}"
            if val_key in history and history[val_key]:
                plt.plot(epochs, history[val_key], label=f"val {metric}")
            plt.xlabel("epoch")
            plt.ylabel(metric.upper())
            plt.title(f"{title_prefix}: {metric.upper()}")
            plt.legend()
            plt.tight_layout()
            plt.savefig(os.path.join(folder, f"{title_prefix}_{metric}.png"), dpi=200)
            plt.close()


def plot_variant_curves(
    histories: list[tuple[str, dict]],
    metric_key: str,
    title: str,
    out_path: str,
    ylabel: Optional[str] = None,
):
    plt.figure()
    has_lines = False
    for label, hist in histories:
        values = hist.get(metric_key)
        if not values:
            continue
        epochs = range(1, len(values) + 1)
        plt.plot(epochs, values, label=label)
        has_lines = True

    if not has_lines:
        plt.close()
        return

    plt.xlabel("epoch")
    plt.ylabel(ylabel or metric_key)
    plt.title(title)
    plt.legend()
    plt.tight_layout()
    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    plt.savefig(out_path, dpi=200)
    plt.close()


def plot_logreg_training_metrics(history: dict, title: str, out_path: str):
    """График с четырьмя линиями: train BCE (data), val BCE (data), val acc, val F1."""
    epochs = range(1, len(history.get("train_loss_data", [])) + 1)
    if not epochs:
        return
    plt.figure(figsize=(8, 5))
    plt.plot(epochs, history.get("train_loss_data", []), label="train BCE")
    if history.get("val_loss_data"):
        plt.plot(epochs[: len(history["val_loss_data"])], history["val_loss_data"], label="val BCE")
    if history.get("val_acc"):
        plt.plot(epochs[: len(history["val_acc"])], history["val_acc"], label="val acc")
    if history.get("val_f1"):
        plt.plot(epochs[: len(history["val_f1"])], history["val_f1"], label="val F1")
    plt.xlabel("epoch")
    plt.ylabel("metric")
    plt.title(title)
    plt.legend()
    plt.tight_layout()
    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    plt.savefig(out_path, dpi=200)
    plt.close()


def plot_linreg_training_metrics(history: dict, title: str, out_path: str):
    """График с четырьмя линиями: train MSE, val MSE, val RMSE, val MAE."""
    epochs = range(1, len(history.get("train_mse", [])) + 1)
    if not epochs:
        return
    plt.figure(figsize=(8, 5))
    plt.plot(epochs, history.get("train_mse", []), label="train MSE")
    if history.get("val_mse"):
        plt.plot(epochs[: len(history["val_mse"])], history["val_mse"], label="val MSE")
    if history.get("val_rmse"):
        plt.plot(epochs[: len(history["val_rmse"])], history["val_rmse"], label="val RMSE")
    if history.get("val_mae"):
        plt.plot(epochs[: len(history["val_mae"])], history["val_mae"], label="val MAE")
    plt.xlabel("epoch")
    plt.ylabel("metric")
    plt.title(title)
    plt.legend()
    plt.tight_layout()
    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    plt.savefig(out_path, dpi=200)
    plt.close()


def plot_training_curves(history: dict, title: str, model_type: str = "classification", save_path: Optional[str] = None):
    """Универсальные графики обучения (loss + метрики)."""
    if not history:
        return

    if model_type == "classification":
        epochs = range(1, len(history["train_loss"]) + 1)
        fig, axes = plt.subplots(1, 2, figsize=(12, 5))
        axes[0].plot(epochs, history["train_loss"], label="train loss")
        val_loss = [m["loss"] for m in history.get("val_metrics", [])]
        if val_loss:
            axes[0].plot(epochs[: len(val_loss)], val_loss, label="val loss")
        axes[0].set_xlabel("epoch")
        axes[0].set_ylabel("loss")
        axes[0].set_title("Loss")
        axes[0].legend()

        # accuracy / f1
        acc = [m["accuracy"] for m in history.get("val_metrics", [])]
        f1 = [m["f1_score"] for m in history.get("val_metrics", [])]
        if acc or f1:
            axes[1].plot(epochs[: len(acc)], acc, label="val acc")
            axes[1].plot(epochs[: len(f1)], f1, label="val f1")
            axes[1].set_xlabel("epoch")
            axes[1].set_ylabel("metric")
            axes[1].set_title("Validation metrics")
            axes[1].legend()

        fig.suptitle(title)
        fig.tight_layout()
    else:
        # регрессия: строим MSE
        epochs = range(1, len(history["train_mse"]) + 1)
        plt.figure(figsize=(6, 4))
        plt.plot(epochs, history["train_mse"], label="train MSE")
        val_mse = history.get("val_mse", [])
        if val_mse:
            plt.plot(epochs[: len(val_mse)], val_mse, label="val MSE")
        plt.xlabel("epoch")
        plt.ylabel("MSE")
        plt.title(title)
        plt.legend()
        plt.tight_layout()

    if save_path:
        os.makedirs(os.path.dirname(save_path), exist_ok=True)
        plt.savefig(save_path, dpi=200)
        plt.close()
    else:
        plt.show()


def plot_confusion_matrix(y_true, y_pred, title: str = "Confusion Matrix", labels: Optional[list[str]] = None, save_path: Optional[str] = None):
    import numpy as np
    import seaborn as sns

    y_true = np.array(y_true).astype(int)
    y_pred = np.array(y_pred).astype(int)
    n_classes = max(y_true.max(), y_pred.max()) + 1
    if labels is None:
        labels = list(range(int(n_classes)))
    cm = np.zeros((n_classes, n_classes), dtype=int)
    for t, p in zip(y_true, y_pred):
        cm[int(t), int(p)] += 1

    plt.figure(figsize=(5, 4))
    ax = sns.heatmap(cm, annot=True, fmt="d", cmap="Blues", cbar=False, xticklabels=labels, yticklabels=labels)
    ax.set_xlabel("Predicted")
    ax.set_ylabel("True")
    ax.set_title(title)
    plt.tight_layout()
    if save_path:
        os.makedirs(os.path.dirname(save_path), exist_ok=True)
        plt.savefig(save_path, dpi=200)
        plt.close()
    else:
        plt.show()


def plot_feature_importance(weights, feature_names, title: str = "Feature Importance", save_path: Optional[str] = None):
    import numpy as np

    weights = np.array(weights).reshape(-1)
    abs_w = np.abs(weights)
    order = np.argsort(abs_w)[::-1]

    plt.figure(figsize=(10, 6))
    plt.bar(range(len(weights)), abs_w[order])
    plt.xticks(range(len(weights)), [feature_names[i] for i in order], rotation=45, ha="right")
    plt.ylabel("|weight|")
    plt.title(title)
    plt.tight_layout()
    if save_path:
        os.makedirs(os.path.dirname(save_path), exist_ok=True)
        plt.savefig(save_path, dpi=200)
        plt.close()
    else:
        plt.show()
