import torch


def mse(y_hat: torch.Tensor, y: torch.Tensor) -> torch.Tensor:
    y = y.view(-1, 1)
    return ((y_hat - y) ** 2).mean()


def mae(y_hat: torch.Tensor, y: torch.Tensor) -> torch.Tensor:
    y = y.view(-1, 1)
    return (y_hat - y).abs().mean()


def rmse(y_hat: torch.Tensor, y: torch.Tensor) -> torch.Tensor:
    return torch.sqrt(mse(y_hat, y) + 1e-8)
