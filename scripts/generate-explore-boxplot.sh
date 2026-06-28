#!/usr/bin/env bash
set -e

./gradlew test --tests "ExploreStrategyComparisonTest" --rerun

python3 python/PlotExploreComparisonGraph.py