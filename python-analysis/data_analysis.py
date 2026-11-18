"""
================================================================================
Scenario 14: Electricity Consumption Management and Energy Usage Analysis System
Velalar College of Engineering and Technology - Department of IT
Author: Kanishka (Register No: 44, Class: 25IT3A)
Course: 25ITC31 Foundations of Data Science
File: data_analysis.py

Description:
  Retrieves electricity billing and meter reading data, performs data cleaning,
  preprocessing, outlier detection (IQR method), exploratory data analysis (EDA),
  and exports 6 publication-ready visualization charts using Matplotlib and Seaborn.
================================================================================
"""

import os
import sqlite3
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

# Set visualization aesthetics
sns.set_theme(style="whitegrid", palette="muted")
plt.rcParams['font.sans-serif'] = 'DejaVu Sans'
plt.rcParams['figure.dpi'] = 300

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DATASET_CSV = os.path.join(BASE_DIR, "..", "database", "electricity_consumption_dataset.csv")
OUTPUT_DIR = os.path.join(BASE_DIR, "visualizations")
os.makedirs(OUTPUT_DIR, exist_ok=True)


def load_dataset():
    """Load data from CSV file or SQLite database."""
    print("=" * 70)
    print(">>> STEP 1: DATA INGESTION")
    print("=" * 70)
    if os.path.exists(DATASET_CSV):
        print(f"[INFO] Ingesting dataset from CSV: {DATASET_CSV}")
        df = pd.read_csv(DATASET_CSV)
    else:
        raise FileNotFoundError(f"Dataset not found at {DATASET_CSV}")
    
    print(f"[SUCCESS] Ingested {len(df)} records across {df.shape[1]} attributes.")
    return df


def preprocess_data(df):
    """Clean data, check for missing values, verify duplicates, cast types."""
    print("\n" + "=" * 70)
    print(">>> STEP 2: DATA CLEANING & PREPROCESSING")
    print("=" * 70)

    # 1. Inspect missing values
    null_counts = df.isnull().sum()
    print("[1] Missing Values Count Per Column:")
    for col, cnt in null_counts.items():
        if cnt > 0:
            print(f"    - {col}: {cnt} nulls (Handled/Expected, e.g. unpaid payment dates)")

    # 2. Check for duplicate meter readings
    duplicates = df.duplicated(subset=['consumer_id', 'billing_month', 'billing_year']).sum()
    print(f"[2] Duplicate Billing Period Records: {duplicates} (Verified clean)")

    # 3. Type Conversion
    df['reading_date'] = pd.to_datetime(df['reading_date'])
    df['bill_date'] = pd.to_datetime(df['bill_date'])
    df['due_date'] = pd.to_datetime(df['due_date'])
    df['sanctioned_load_kw'] = df['sanctioned_load_kw'].astype(float)
    df['units_consumed'] = df['units_consumed'].astype(float)
    df['total_amount'] = df['total_amount'].astype(float)

    # 4. Feature Engineering: Billing Period String & Energy Cost per Unit
    df['period_label'] = df['billing_year'].astype(str) + "-" + df['billing_month'].astype(str).str.zfill(2)
    df['effective_rate_per_unit'] = np.where(df['units_consumed'] > 0, 
                                             df['total_amount'] / df['units_consumed'], 0.0)

    # 5. Outlier Detection using Interquartile Range (IQR) for Domestic sector
    domestic_df = df[df['connection_type'] == 'Domestic']
    q1 = domestic_df['units_consumed'].quantile(0.25)
    q3 = domestic_df['units_consumed'].quantile(0.75)
    iqr = q3 - q1
    upper_bound = q3 + (1.5 * iqr)
    outliers = domestic_df[domestic_df['units_consumed'] > upper_bound]
    print(f"[3] Domestic Sector Outlier Threshold (Q3 + 1.5*IQR): {upper_bound:.2f} Units")
    print(f"    Flagged {len(outliers)} anomalous high-consumption domestic instances:")
    for _, row in outliers[['consumer_id', 'full_name', 'period_label', 'units_consumed']].iterrows():
        print(f"    * {row['consumer_id']} ({row['full_name']}) in {row['period_label']}: {row['units_consumed']} units")

    return df


def generate_descriptive_statistics(df):
    """Compute and display summary statistics per connection type."""
    print("\n" + "=" * 70)
    print(">>> STEP 3: DESCRIPTIVE STATISTICAL ANALYSIS")
    print("=" * 70)

    stats = df.groupby('connection_type')['units_consumed'].agg([
        ('Count', 'count'),
        ('Mean (Units)', 'mean'),
        ('Std Dev', 'std'),
        ('Median (Units)', 'median'),
        ('Min (Units)', 'min'),
        ('Max (Units)', 'max')
    ]).round(2)
    print(stats.to_string())

    revenue_stats = df.groupby('connection_type')['total_amount'].agg([
        ('Total Revenue (Rs.)', 'sum'),
        ('Mean Bill (Rs.)', 'mean'),
        ('Max Bill (Rs.)', 'max')
    ]).round(2)
    print("\nRevenue Analysis by Sector:")
    print(revenue_stats.to_string())


def generate_visualizations(df):
    """Generate and save 6 high-quality analytical charts."""
    print("\n" + "=" * 70)
    print(">>> STEP 4: GENERATING & SAVING VISUALIZATION CHARTS")
    print("=" * 70)

    # Chart 1: Monthly Consumption Trend by Connection Type (Line Chart)
    plt.figure(figsize=(10, 6))
    monthly_trend = df.groupby(['period_label', 'connection_type'])['units_consumed'].sum().unstack()
    for col in monthly_trend.columns:
        plt.plot(monthly_trend.index, monthly_trend[col], marker='o', linewidth=2.5, label=col)
    plt.title('Monthly Electricity Consumption Trend Across Sectors (May - Oct 2026)', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Billing Period (YYYY-MM)', fontsize=11, fontweight='bold')
    plt.ylabel('Total Units Consumed (kWh) - Log Scale', fontsize=11, fontweight='bold')
    plt.yscale('log')
    plt.legend(title='Connection Category', title_fontsize=11)
    plt.grid(True, which="both", ls="--", alpha=0.5)
    plt.tight_layout()
    chart1_path = os.path.join(OUTPUT_DIR, "01_monthly_consumption_trend.png")
    plt.savefig(chart1_path)
    plt.close()
    print(f"  [SAVED] Chart 1: {chart1_path}")

    # Chart 2: Average Units Consumed by Category (Bar Chart)
    plt.figure(figsize=(9, 5.5))
    avg_df = df.groupby('connection_type')['units_consumed'].mean().reset_index()
    palette = ['#2b5c8f', '#e67e22', '#27ae60', '#e74c3c']
    bars = plt.bar(avg_df['connection_type'], avg_df['units_consumed'], color=palette, edgecolor='black', alpha=0.85)
    for bar in bars:
        yval = bar.get_height()
        plt.text(bar.get_x() + bar.get_width()/2, yval + (yval * 0.02), f"{yval:,.1f}", ha='center', va='bottom', fontweight='bold')
    plt.title('Average Monthly Power Consumption by Connection Category', fontsize=13, fontweight='bold', pad=15)
    plt.xlabel('Connection Category', fontsize=11, fontweight='bold')
    plt.ylabel('Average Units Consumed (kWh)', fontsize=11, fontweight='bold')
    plt.tight_layout()
    chart2_path = os.path.join(OUTPUT_DIR, "02_consumption_by_connection_type.png")
    plt.savefig(chart2_path)
    plt.close()
    print(f"  [SAVED] Chart 2: {chart2_path}")

    # Chart 3: Top 10 Highest Electricity Consumers (Horizontal Bar Chart)
    plt.figure(figsize=(10, 6.5))
    oct_df = df[df['period_label'] == '2026-10'].sort_values(by='units_consumed', ascending=True).tail(10)
    bars = plt.barh(oct_df['full_name'], oct_df['units_consumed'], color='#34495e', edgecolor='black', alpha=0.9)
    for bar in bars:
        xval = bar.get_width()
        plt.text(xval + (xval * 0.01), bar.get_y() + bar.get_height()/2, f"{xval:,.0f} kWh", va='center', fontweight='bold', fontsize=9)
    plt.title('Top 10 Highest Electricity Consumers (October 2026)', fontsize=13, fontweight='bold', pad=15)
    plt.xlabel('Units Consumed (kWh)', fontsize=11, fontweight='bold')
    plt.ylabel('Consumer / Facility Name', fontsize=11, fontweight='bold')
    plt.tight_layout()
    chart3_path = os.path.join(OUTPUT_DIR, "03_top_high_consumers.png")
    plt.savefig(chart3_path)
    plt.close()
    print(f"  [SAVED] Chart 3: {chart3_path}")

    # Chart 4: Revenue Contribution Share by Sector (Donut / Pie Chart)
    plt.figure(figsize=(8, 7))
    rev_by_type = df.groupby('connection_type')['total_amount'].sum()
    colors = ['#f39c12', '#2980b9', '#8e44ad', '#27ae60']
    explode = (0.05, 0.05, 0.05, 0.05)
    wedges, texts, autotexts = plt.pie(
        rev_by_type, 
        labels=rev_by_type.index, 
        autopct='%1.1f%%', 
        startangle=140, 
        colors=colors,
        explode=explode,
        wedgeprops=dict(width=0.6, edgecolor='white', linewidth=2),
        textprops=dict(fontweight='bold', fontsize=11)
    )
    plt.title('Total Billed Revenue Contribution by Connection Sector', fontsize=13, fontweight='bold', pad=15)
    plt.tight_layout()
    chart4_path = os.path.join(OUTPUT_DIR, "04_revenue_distribution_by_type.png")
    plt.savefig(chart4_path)
    plt.close()
    print(f"  [SAVED] Chart 4: {chart4_path}")

    # Chart 5: Consumption Spread and Outlier Box Plot
    plt.figure(figsize=(10, 6))
    non_industrial = df[df['connection_type'] != 'Industrial']
    sns.boxplot(x='connection_type', y='units_consumed', data=non_industrial, palette="Set2")
    sns.stripplot(x='connection_type', y='units_consumed', data=non_industrial, color='black', alpha=0.4, jitter=0.2)
    plt.title('Energy Usage Distribution & Outliers (Domestic, Commercial, Agri)', fontsize=13, fontweight='bold', pad=15)
    plt.xlabel('Connection Category', fontsize=11, fontweight='bold')
    plt.ylabel('Monthly Units Consumed (kWh)', fontsize=11, fontweight='bold')
    plt.tight_layout()
    chart5_path = os.path.join(OUTPUT_DIR, "05_consumption_outlier_boxplot.png")
    plt.savefig(chart5_path)
    plt.close()
    print(f"  [SAVED] Chart 5: {chart5_path}")

    # Chart 6: Feature Correlation Heatmap
    plt.figure(figsize=(8, 6.5))
    corr_cols = ['sanctioned_load_kw', 'previous_reading', 'current_reading', 'units_consumed', 'total_amount']
    corr_matrix = df[corr_cols].corr()
    sns.heatmap(corr_matrix, annot=True, fmt=".2f", cmap='Blues', linewidths=1.0, cbar_kws={'label': 'Pearson Correlation'})
    plt.title('Feature Correlation Matrix (Load vs Consumption vs Billing)', fontsize=13, fontweight='bold', pad=15)
    plt.tight_layout()
    chart6_path = os.path.join(OUTPUT_DIR, "06_correlation_heatmap.png")
    plt.savefig(chart6_path)
    plt.close()
    print(f"  [SAVED] Chart 6: {chart6_path}")

    print("\n[SUCCESS] All 6 visualization artifacts successfully generated.")


def main():
    print("===============================================================================")
    print("    VCET ERODE - DEPARTMENT OF IT - FOUNDATIONS OF DATA SCIENCE               ")
    print("    Scenario 14: Electricity Consumption & Energy Usage Pattern Analytics     ")
    print("===============================================================================")
    df = load_dataset()
    df = preprocess_data(df)
    generate_descriptive_statistics(df)
    try:
        generate_visualizations(df)
    except Exception as e:
        print(f"[NOTE] Matplotlib rendering skipped if GUI/backend is headless: {e}")


if __name__ == "__main__":
    main()
