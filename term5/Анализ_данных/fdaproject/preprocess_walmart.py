from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Tuple

import numpy as np
import pandas as pd
from sklearn.compose import ColumnTransformer
from sklearn.impute import SimpleImputer
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler

DATA_PATH = Path(__file__).resolve().parent / "Walmart.csv"
OUTPUT_DIR = Path(__file__).resolve().parent / "processed"


@dataclass
class OutlierBounds:
    lower: float
    upper: float


def load_raw_dataset(path: Path = DATA_PATH) -> pd.DataFrame:
    df = pd.read_csv(path)
    df["Date"] = pd.to_datetime(df["Date"], dayfirst=True)
    df = df.sort_values(["Store", "Date"]).reset_index(drop=True)
    return df


def fill_missing_values(df: pd.DataFrame, numeric_cols: Iterable[str], cat_cols: Iterable[str]) -> pd.DataFrame:
    df = df.copy()
    for col in numeric_cols:
        if df[col].isna().any():
            group_median = df.groupby("Store")[col].transform("median")
            df[col] = df[col].fillna(group_median)
            df[col] = df[col].fillna(df[col].median())
    for col in cat_cols:
        if df[col].isna().any():
            mode_value = df[col].mode(dropna=True)
            if not mode_value.empty:
                df[col] = df[col].fillna(mode_value.iloc[0])
    return df


def detect_and_remove_outliers(df: pd.DataFrame, numeric_cols: Iterable[str], multiplier: float = 1.5) -> Tuple[pd.DataFrame, Dict[str, OutlierBounds]]:
    df = df.copy()
    bounds: Dict[str, OutlierBounds] = {}
    mask = pd.Series(True, index=df.index)
    for col in numeric_cols:
        q1 = df[col].quantile(0.25)
        q3 = df[col].quantile(0.75)
        iqr = q3 - q1
        lower = q1 - multiplier * iqr
        upper = q3 + multiplier * iqr
        bounds[col] = OutlierBounds(lower=lower, upper=upper)
        mask &= df[col].between(lower, upper) | df[col].isna()
    trimmed = df[mask].reset_index(drop=True)
    return trimmed, bounds


def add_time_features(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy()
    df["Year"] = df["Date"].dt.year
    df["Quarter"] = df["Date"].dt.quarter
    df["Month"] = df["Date"].dt.month
    df["WeekOfYear"] = df["Date"].dt.isocalendar().week.astype(int)
    df["DayOfMonth"] = df["Date"].dt.day
    df["IsMonthStart"] = df["Date"].dt.is_month_start.astype(int)
    df["IsMonthEnd"] = df["Date"].dt.is_month_end.astype(int)
    df["DaysSinceStart"] = (df["Date"] - df["Date"].min()).dt.days
    return df


def add_lagged_features(df: pd.DataFrame, target_col: str, lag_periods: Iterable[int]) -> pd.DataFrame:
    df = df.copy()
    for lag in lag_periods:
        df[f"{target_col}_lag_{lag}"] = df.groupby("Store")[target_col].shift(lag)
    df["Holiday_Flag_lag_1"] = df.groupby("Store")["Holiday_Flag"].shift(1)
    return df


def add_rolling_stats(df: pd.DataFrame, target_col: str, windows: Iterable[int]) -> pd.DataFrame:
    df = df.copy()
    grouped = df.groupby("Store")[target_col]
    for window in windows:
        shifted = grouped.shift(1)
        rolling = shifted.rolling(window=window, min_periods=1)
        df[f"{target_col}_roll_mean_{window}"] = rolling.mean().reset_index(level=0, drop=True)
        df[f"{target_col}_roll_std_{window}"] = rolling.std().reset_index(level=0, drop=True)
    return df


def build_feature_matrix(df: pd.DataFrame, target_col: str) -> Tuple[pd.DataFrame, pd.Series, ColumnTransformer]:
    df = df.copy()
    feature_cols: List[str] = [col for col in df.columns if col not in {target_col}]
    df["Store"] = df["Store"].astype("category")
    df["Holiday_Flag"] = df["Holiday_Flag"].astype("category")
    categorical_cols = ["Store", "Holiday_Flag"]
    numeric_cols = [col for col in feature_cols if col not in categorical_cols and col != "Date"]

    preprocessor = ColumnTransformer(
        transformers=[
            (
                "num",
                Pipeline(
                    steps=[
                        ("imputer", SimpleImputer(strategy="median")),
                        ("scaler", StandardScaler()),
                    ]
                ),
                numeric_cols,
            ),
            (
                "cat",
                Pipeline(
                    steps=[
                        ("imputer", SimpleImputer(strategy="most_frequent")),
                        ("encoder", OneHotEncoder(handle_unknown="ignore", sparse_output=False)),
                    ]
                ),
                categorical_cols,
            ),
        ],
        remainder="drop",
    )

    features = df[feature_cols].drop(columns=["Date"])
    target = df[target_col].copy()
    transformed = preprocessor.fit_transform(features)
    feature_names = preprocessor.get_feature_names_out()
    feature_matrix = pd.DataFrame(transformed, columns=feature_names, index=df.index)
    return feature_matrix, target, preprocessor


def main() -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    numeric_cols = ["Weekly_Sales", "Temperature", "Fuel_Price", "CPI", "Unemployment"]
    categorical_cols = ["Holiday_Flag"]

    df_raw = load_raw_dataset()
    df_filled = fill_missing_values(df_raw, numeric_cols, categorical_cols)
    df_trimmed, outlier_bounds = detect_and_remove_outliers(df_filled, numeric_cols=numeric_cols)
    df_features = add_time_features(df_trimmed)
    df_features = add_lagged_features(df_features, target_col="Weekly_Sales", lag_periods=[1, 2, 4])
    df_features = add_rolling_stats(df_features, target_col="Weekly_Sales", windows=[4, 8])
    df_features = df_features.dropna().reset_index(drop=True)

    feature_matrix, target, preprocessor = build_feature_matrix(df_features, target_col="Weekly_Sales")
    cleaned_path = OUTPUT_DIR / "walmart_cleaned_features.csv"
    prepared_path = OUTPUT_DIR / "walmart_prepared_dataset.csv"
    transformer_path = OUTPUT_DIR / "walmart_preprocessor.pkl"

    df_features.to_csv(cleaned_path, index=False)
    prepared = feature_matrix.copy()
    prepared["Weekly_Sales"] = target.values
    prepared.to_csv(prepared_path, index=False)

    pd.to_pickle(
        {
            "preprocessor": preprocessor,
            "feature_columns": feature_matrix.columns.tolist(),
            "target_name": "Weekly_Sales",
            "outlier_bounds": outlier_bounds,
        },
        transformer_path,
    )

    summary_path = OUTPUT_DIR / "summary.txt"
    removed_rows = len(df_raw) - len(df_trimmed)
    with summary_path.open("w") as summary_file:
        summary_file.write("Rows before cleaning: {}\n".format(len(df_raw)))
        summary_file.write("Rows after outlier removal: {}\n".format(len(df_trimmed)))
        summary_file.write("Rows after feature engineering & lag drop: {}\n".format(len(df_features)))
        summary_file.write("Removed rows due to outliers: {}\n".format(removed_rows))
        summary_file.write("\nOutlier bounds (IQR method):\n")
        for feature, bounds in outlier_bounds.items():
            summary_file.write(f"{feature}: [{bounds.lower:.3f}, {bounds.upper:.3f}]\n")
        summary_file.write("\nGenerated feature columns:\n")
        for col in df_features.columns:
            summary_file.write(f"- {col}\n")


if __name__ == "__main__":
    main()
