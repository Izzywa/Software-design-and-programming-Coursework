# read file from temple/benchmark/explore_strategy_comparison.csv and plot an errorbox graph using matplotlib
import pandas as pd
import matplotlib.pyplot as plt
import os
import pathlib as path

BASE_DIR = path.Path(__file__).resolve().parent.parent

filename = (
    BASE_DIR
    / "temple"
    / "src"
    / "test"
    / "resources"
    / "benchmark"
    / "escape_strategy_comparison.csv"
)
df = pd.read_csv(filename)

images_dir = BASE_DIR / "python" / "images"
if not images_dir.exists():
    os.makedirs(images_dir)

df.boxplot(column="Gold Collected", by="Strategy", grid=True, vert=False, figsize=(10, 5))
plt.title("Escape Strategy Comparison")
plt.xlabel("Gold Collected")
plt.ylabel("Strategy")
plt.yticks(rotation=45)
plt.tight_layout()
plt.savefig("python/images/escape_strategy_gold_boxplot.png")

df.boxplot(column="Time Taken", by="Strategy", grid=True, vert=False, figsize=(10, 5))
plt.title("Escape Strategy Comparison")
plt.xlabel("Time Taken (ms)")
plt.ylabel("Strategy")
plt.yticks(rotation=45)
plt.axvline(x=10000, color="red", linestyle="--", label="10 seconds")
plt.legend()    
plt.tight_layout()
plt.savefig("python/images/escape_strategy_time_boxplot.png")

df['gold/time given'] = df['Gold Collected'] / df['Time Given']
df.boxplot(column="gold/time given", by="Strategy", grid=True, vert=False, figsize=(10, 5))
plt.title("Escape Strategy Comparison")
plt.xlabel("Gold Collected / Time Given")
plt.ylabel("Strategy")
plt.yticks(rotation=45)
plt.tight_layout()
plt.savefig("python/images/escape_strategy_gold_time_boxplot.png")
