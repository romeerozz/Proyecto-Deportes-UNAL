import csv
import os
import sys
from collections import defaultdict

import matplotlib.pyplot as plt


DEFAULT_INPUT = "data/results/index_benchmark_full.csv"
DEFAULT_OUTPUT_DIR = "data/graphics"


def read_csv(path):
    rows = []

    with open(path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)

        for row in reader:
            rows.append({
                "structure": row["structure"],
                "operation": row["operation"],
                "n": int(row["n"]),
                "trial": int(row["trial"]),
                "seed": int(row["seed"]),
                "count": int(row["count"]),
                "time_ns": int(row["time_ns"]),
                "ns_per_op": int(row["time_ns"]) / int(row["count"]),
            })

    return rows


def aggregate_by_average(rows):
    grouped = defaultdict(list)

    for row in rows:
        key = (row["structure"], row["operation"], row["n"])
        grouped[key].append(row["ns_per_op"])

    aggregated = []

    for (structure, operation, n), values in grouped.items():
        avg = sum(values) / len(values)
        aggregated.append({
            "structure": structure,
            "operation": operation,
            "n": n,
            "avg_ns_per_op": avg,
        })

    return aggregated


def plot_operation(aggregated, operation, output_dir):
    data = [row for row in aggregated if row["operation"] == operation]

    if not data:
        print(f"No hay datos para operación {operation}")
        return

    structures = sorted(set(row["structure"] for row in data))

    plt.figure()

    for structure in structures:
        points = [row for row in data if row["structure"] == structure]
        points.sort(key=lambda x: x["n"])

        x = [row["n"] for row in points]
        y = [row["avg_ns_per_op"] for row in points]

        plt.plot(x, y, marker="o", label=structure)

    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel("Tamaño de entrada n")
    plt.ylabel("Tiempo promedio por operación (ns/op)")
    plt.title(f"Benchmark {operation}")
    plt.legend()
    plt.grid(True, which="both", linestyle="--", linewidth=0.5)

    output_path = os.path.join(output_dir, f"{operation.lower()}_benchmark.png")
    plt.savefig(output_path, dpi=300, bbox_inches="tight")
    plt.close()

    print(f"Gráfica guardada: {output_path}")


def plot_all_operations(rows, output_dir):
    aggregated = aggregate_by_average(rows)

    operations = sorted(set(row["operation"] for row in rows))

    for operation in operations:
        plot_operation(aggregated, operation, output_dir)


def print_summary(rows):
    aggregated = aggregate_by_average(rows)
    aggregated.sort(key=lambda r: (r["operation"], r["n"], r["structure"]))

    print("\nResumen promedio ns/op:")
    print("operation,structure,n,avg_ns_per_op")

    for row in aggregated:
        print(
            f"{row['operation']},"
            f"{row['structure']},"
            f"{row['n']},"
            f"{row['avg_ns_per_op']:.2f}"
        )


def main():
    input_path = sys.argv[1] if len(sys.argv) > 1 else DEFAULT_INPUT
    output_dir = sys.argv[2] if len(sys.argv) > 2 else DEFAULT_OUTPUT_DIR

    if not os.path.exists(input_path):
        print(f"No existe el archivo CSV: {input_path}")
        return

    os.makedirs(output_dir, exist_ok=True)

    rows = read_csv(input_path)

    if not rows:
        print("El CSV está vacío.")
        return

    print_summary(rows)
    plot_all_operations(rows, output_dir)


if __name__ == "__main__":
    main()