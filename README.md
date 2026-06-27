## [Wiki](https://github.com/Izzywa/Software-design-and-programming-Coursework/wiki/Home)

## Team SDK

- This repository is configured to build with Java 21 by default. To avoid SDK mismatches in a group project, please standardize on minimum Java 21.

- Recommended setup for team members:
  - Install a local JDK 21 (Temurin/Adoptium is recommended) and ensure `java -version` shows a Java 21 runtime.

- Quick install examples:

  ### macOS (Homebrew + Temurin 21):

  ```bash
  brew install --cask temurin
  java -version # verify shows 21
  ```

  ### Windows (Adoptium installer):
  - Download and install Temurin JDK 21 from https://adoptium.net
  - Verify `java -version` in PowerShell or cmd.

  ### Codio:
   ```bash
    sdk install java 21.0.4-tem 
    ```
  - When prompted, select to use Java 21 as the default.
  ```baash
  Do you want java 21.0.4-tem to be set as default? (Y/n): 
  ```

### Local check: run the provided helper to verify you have Java 21 before building:

Make the script executable if needed:

```bash
chmod +x ./scripts/check-java.sh
```
In the terminal, run:

```bash
./scripts/check-java.sh
```

## Handy repo-wide shortcuts

These Gradle tasks are committed to the repository, so everyone who clones it can use the same short commands from the repo root:

```bash
./gradlew txt   # run the text interface
./gradlew gui   # run the GUI interface
./gradlew clean build
```

arguments could be added to the end of these commands as needed, for example:

```bash
./gradlew txt --args="-n 100" # run in headless mode with 100 iterations
./gradlew gui --args="-s 1050" # run the GUI with a custom seed
```

## Verify build success
Run in terminal:
```bash
./gradlew clean build
```
note: _there will be some warnings from problems in the code base but the terminal should show output like_
```bash
BUILD SUCCESSFUL in 5s
9 actionable tasks: 9 executed
```

# Tests
The test classes are located in `temple/src/test/java/` and are configured to run with JUnit 5. You can run all tests using Gradle with the following command:

```bash
./gradlew test
```

# Generating JavaDoc
Run the following command in terminal
```bash
./gradlew javadoc
```

You can view the documentation in the following file `temple/build/docs/javadoc/index.html`

# Strategies considered
Currently, the plot for the different strategies performance need to be generated manually. Run the following command in the terminal

```bash
 python3 python/PlotExploreComparisonGraph.py 
```
OR
```bash
 python python/PlotExploreComparisonGraph.py 
```
