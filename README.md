## Changes from the original CODIO code base
View the [Wiki](https://github.com/Izzywa/Software-design-and-programming-Coursework/wiki/04062026_InitialSetup)

## Team SDK

- This repository is configured to build with Java 21 by default. To avoid SDK mismatches in a group project, please standardize on minimum Java 21.

- Recommended setup for team members:
  - Install a local JDK 21 (Temurin/Adoptium is recommended) and ensure `java -version` shows a Java 21 runtime.

- Quick install examples:

  macOS (Homebrew + Temurin 21):

  ```bash
  brew install --cask temurin
  java -version # verify shows 21
  ```

  Windows (Adoptium installer):
  - Download and install Temurin JDK 21 from https://adoptium.net
  - Verify `java -version` in PowerShell or cmd.

  Codio:
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
# Creating a new branch for your work
To create a new branch for your work, use the following command in your terminal:
```bash
git checkout -b your-branch-name
```
Replace `your-branch-name` with a descriptive name for your branch `Ticket-number/fix-issue`. 
This command will create a new branch and switch to it, allowing you to start working on your changes without affecting the main branch.

# Creating a pull request
The `main` branch is protected, so you will need to create a pull request to merge your changes into the main branch.
1. Push your branch to the remote repository:
```bash
git push origin your-branch-name
```
2. Go to the repository on GitHub and you should see a prompt to create a pull request for your recently pushed branch. Click on "Compare & pull request".
3. Provide a descriptive title and description for your pull request, explaining the changes you made and why they are necessary.
4. Click on "Create pull request" to submit your changes for review. Once your pull request is created, team members can review your changes, provide feedback, and approve the merge into the main branch. 
6. After approval, you can merge your pull request to integrate your changes into the main branch.