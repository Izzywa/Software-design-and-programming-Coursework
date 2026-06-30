## [Wiki](https://github.com/Izzywa/Software-design-and-programming-Coursework/wiki/Home)

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

