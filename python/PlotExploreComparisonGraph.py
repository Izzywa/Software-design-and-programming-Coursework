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

df.boxplot(column="Bonus Factor", by="Strategy", grid=True, vert=False, figsize=(10, 5))
plt.xlabel("Bonus Factor")
plt.ylabel("Strategy")
plt.yticks(rotation=45)
plt.tight_layout()
plt.savefig("python/images/explore_strategy_boxplot.png")
