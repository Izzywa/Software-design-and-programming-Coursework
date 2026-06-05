## Changes from the original CODIO code base
View the wiki [Wiki](https://github.com/Izzywa/Software-design-and-programming-Coursework/wiki/04062026_InitialSetup)

## Team SDK

- This repository is configured to build with Java 21 by default. To avoid SDK mismatches in a group project, please standardize on minimum Java 21.

- Recommended setup for team members:
  - Use the Gradle toolchain (already configured) so Gradle requests a JDK 21 automatically when needed.
  - Alternatively install a local JDK 21 (Temurin/Adoptium is recommended) and ensure `java -version` shows a Java 21 runtime.

- Quick install examples:

  macOS (Homebrew + Temurin 21):

  ```bash
  brew install --cask temurin
  java -version # verify shows 21
  ```

  Windows (Adoptium installer):
  - Download and install Temurin JDK 21 from https://adoptium.net
  - Verify `java -version` in PowerShell or cmd.


### Local check: run the provided helper to verify you have Java 21 before building:

Make the script executable if needed:

```bash
chmod +x ./scripts/check-java.sh
```
In the terminal, run:

```bash
./scripts/check-java.sh
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
