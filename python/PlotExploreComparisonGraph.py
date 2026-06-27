# read file from temple/benchmark/explore_strategy_comparison.csv and plot an errorbox graph using matplotlib
import pandas as pd
import matplotlib.pyplot as plt
import os
import pathlib as path

BASE_DIR = path.Path(__file__).resolve().parent.parent

filename = BASE_DIR / "temple" / "benchmark" / "explore_strategy_comparison.csv"
df = pd.read_csv(filename)

images_dir = BASE_DIR / "python" / "images"
if not images_dir.exists():
    os.makedirs(images_dir)

# Group by Strategy and calculate mean and std for Bonus Factor
grouped = df.groupby("Strategy")["Bonus Factor"].agg(["mean", "std"]).reset_index()

plt.figure(figsize=(10, 5))
plt.errorbar(
    grouped["mean"],
    grouped["Strategy"],
    xerr=grouped["std"],
    fmt="o",
    capsize=5,
    label="Mean ± Std Dev",
)
plt.title("Explore Strategy Comparison")
plt.xlabel("Bonus Factor")
plt.ylabel("Strategy")
plt.yticks(rotation=45)
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("python/images/explore_strategy_comparison.png")
