# Project 2 – CompuVote

## Documentation

See <a href="documentation/Documentation.md" target="_blank">Documentation.md</a> for the documentation resources available for the project (highly recommended).

## Shana notes

1. Note that we talked to Shana about our method of using `Assertions.assertAll` and other forms of multiple tests, and she approved it so long that we take order of execution into account and have a method of uniquely identifying failing test cases in the same method.

2. Note that our use of `Fraction` in `OpenPartyListSystem` was discussed with Shana. She specifically said the following regarding the usage: "You go with what you know Nikunj.  If you are working through the algorithm and this is what needs to be done, then do it. Work through the algorithms and the examples given and your work needs to align with their outcomes." As our work lines aligns with the examples and aligns conceptually, we have found it fit to use `Fraction` for `OpenPartyListSystem`.

More resources that indicate the use of `Fraction` and decimal remaining ballots being acceptable:

1. See page 5 of https://www.legco.gov.hk/yr97-98/english/sec/library/in1_plc.pdf

2. https://www.accuratedemocracy.com/e_shares.htm

3. https://en.idi.org.il/articles/3302

4. https://www.utas.edu.au/library/companion_to_tasmanian_history/H/Hare-Clark%20system.htm

## Running the program

Note: The audit output files appear in `Project2/audits`, and the report output files appear in `Project2/reports`.

### Step 1: Cloning the program

#### Windows 10

1\. Click the windows search in the taskbar

![misc/readme-resources/windows_search.png](misc/readme-resources/windows_search.png)

2\. Search "powershell" without quotations, and click the search result that shows "Windows Powershell" without quotes

![misc/readme-resources/powershell_search.png](misc/readme-resources/powershell_search.png)

3\. In PowerShell, use the `Set-Location` command to navigate into the directory in which you would like to clone the repository

![misc/readme-resources/powershell_set_location.png](misc/readme-resources/powershell_set_location.png)

4\. Run `git clone https://github.umn.edu/umn-csci-5801-S21-002/repo-Team19`, and type in your credentials into the pop-up box that appears if required

![misc/readme-resources/powershell_clone.png](misc/readme-resources/powershell_clone.png)

#### macOS

Note: If using SSH, then skip to step 4

1\. Open Finder by clicking the Finder application in the dock

![misc/readme-resources/open_finder.png](misc/readme-resources/open_finder.png)

2\. Use Command + Shift + G, and in the pop-up, enter "/Applications/Utilities" without quotes

![misc/readme-resources/finder_utilities.png](misc/readme-resources/finder_utilities.png)

3\. In the opened folder, double-click on Terminal.app

![misc/readme-resources/finder_terminal.png](misc/readme-resources/finder_terminal.png)

4\. In Terminal, use the `cd` command to navigate into the directory in which you would like to clone the repository

![misc/readme-resources/terminal_cd.png](misc/readme-resources/terminal_cd.png)

5\. Run `git clone https://github.umn.edu/umn-csci-5801-S21-002/repo-Team19`, and type in your credentials into the Terminal if required

![misc/readme-resources/terminal_clone.png](misc/readme-resources/terminal_clone.png)

#### Ubuntu

Note: If using SSH, then skip to step 3

1\. Click the three-by-three squares in the lower-left

![misc/readme-resources/ubuntu_show_applications.png](misc/readme-resources/ubuntu_show_applications.png)

2\. In the search box, type in "terminal" without quotes, and click the Terminal application

![misc/readme-resources/ubuntu_click_terminal.png](misc/readme-resources/ubuntu_click_terminal.png)

3\. In Terminal, use the `cd` command to navigate into the directory in which you would like to clone the repository

![misc/readme-resources/terminal_cd.png](misc/readme-resources/ubuntu_terminal_cd.png)

4\. Run `git clone https://github.umn.edu/umn-csci-5801-S21-002/repo-Team19`, and type in your credentials into the Terminal if required

![misc/readme-resources/ubuntu_terminal_clone.png](misc/readme-resources/ubuntu_terminal_clone.png)

### For step 2, there are several options available. Choose the one best for your use-case.

### Step 2 Option A: IntelliJ IDEA (most recommended)

Note: Ensure that your IntelliJ IDEA is up to date before running the below:

Note 2: If IntelliJ is showing errors, then sometimes IntelliJ has not loaded everything properly. If this happens, quit and reopen IntelliJ. Then, reopen the `repo-Team19` directory if it did not automatically open.

1. Open IntelliJ IDEA

2. If a different project is open, go to File → Open and select the `repo-Team19` directory you cloned, and go to step 6

![misc/readme-resources/intellij_open.png](misc/readme-resources/intellij_open.png)
![misc/readme-resources/intellij_select.png](misc/readme-resources/intellij_select.png)

3. If you are in the IntelliJ view with no projects open, click the Open button and select the `repo-Team19` directory you cloned

![misc/readme-resources/intellij_open.png](misc/readme-resources/intellij_projects_open.png)
![misc/readme-resources/intellij_select.png](misc/readme-resources/intellij_projects_select.png)

4. To run the program, open the project view, and navigate to `Project2 → src → main → org.team19 → VotingSystemRunner`. Then, click the green play button to the left of `main`. This by default reads from standard input. If you wish to stop this, click the red square stop button at the upper-right of the window.

![misc/readme-resources/intellij_project_view.png](misc/readme-resources/intellij_project_view.png)
![misc/readme-resources/intellij_voting_system_runner.png](misc/readme-resources/intellij_voting_system_runner.png)
![misc/readme-resources/intellij_run_main.png](misc/readme-resources/intellij_run_main.png)

5. To run the program with command-line arguments, click `VotingSystemRunner` at the top to the left of the green play button (configuration generated from 6), click Edit Configurations..., add space-delimited arguments to the Program Arguments field, click OK, and run the program like in step 6

![misc/readme-resources/intellij_run_main.png](misc/readme-resources/intellij_configuration.png)
![misc/readme-resources/intellij_args.png](misc/readme-resources/intellij_args.png)

6. To run tests, navigate to `Project2 → src → test → org.team19` and open the file corresponding to the class in which the tests you wish to run reside.

![misc/readme-resources/intellij_test_location.png](misc/readme-resources/intellij_test_location.png)

7. Click the green play button to the left of the test method you wish to run and click the option starting with Run

![misc/readme-resources/intellij_run_test.png](misc/readme-resources/intellij_run_test.png)

Note 3: You can run all tests for a class by right-clicking a test class and clicking the option with the green play button

Note 4: You can run all tests by right-clicking the test folder and clicking the option with the green play button

### Step 2 Option B: Eclipse

Note: Ensure that your Eclipse is up to date before running the below:

Note 2: If Eclipse is showing errors, then sometimes Eclipse has not loaded everything. If this happens, be patient.

1. Open Eclipse

2. Go to File → Open Projects from File System..., click Directory..., select the `repo-Team19` directory you cloned, and click Finish

![misc/readme-resources/eclipse_open.png](misc/readme-resources/eclipse_open.png)
![misc/readme-resources/eclipse_open_project.png](misc/readme-resources/eclipse_open_project.png)

3. To run the program, navigate to `Project2 → src → main → org.team19 → VotingSystemRunner.java`, scroll to the `main` method, right-click within it, hover over Run As, and click Java Application. This by default reads from standard input. If you wish to stop this, click the red square stop button to the right of the console tab that opened.

![misc/readme-resources/eclipse_voting_system_runner.png](misc/readme-resources/eclipse_voting_system_runner.png)
![misc/readme-resources/eclipse_run_main.png](misc/readme-resources/eclipse_run_main.png)

4. To run the program with command-line arguments, right-click within the `main` method, hover over Run As, and click Run Configurations..., click the Arguments tab, add space-delimited arguments to the Program arguments field, and click Run

![misc/readme-resources/eclipse_configuration.png](misc/readme-resources/eclipse_configuration.png)
![misc/readme-resources/eclipse_arguments.png](misc/readme-resources/eclipse_arguments.png)
![misc/readme-resources/eclipse_run_args.png](misc/readme-resources/eclipse_run_args.png)

5. To run tests, navigate to `Project2 → src → test → org.team19` and open the file corresponding to the class in which the tests you wish to run reside.

![misc/readme-resources/eclipse_test_location.png](misc/readme-resources/eclipse_test_location.png)

6. Right-click within the test method you wish to run, hover over Run As, and click JUnit Test

![misc/readme-resources/eclipse_run_test.png](misc/readme-resources/eclipse_run_test.png)

Note 3: You can run all tests for a class by right-clicking a test class, clicking Run As, and clicking JUnit Test

Note 4: You can run all tests by right-clicking the org.team19 package in the test folder, clicking Run As, and clicking JUnit Test

## Step 2 Option C: Command line on Ubuntu (least recommended)

Note: We only provide the command line option on Ubuntu as checking the macOS machines require being a graduate student. None of us are graduate students.

### Building the project

1. `cd` into the `repo-Team19` folder

2. Run `ant` to build the project (Note: `ant` 1.10.x or later is recommended for building the project)

### Running the project

- Run `java -classpath out/production/repo-Team19 org.team19.VotingSystemRunner` to run the program with standard input as the file input

- Run `java -classpath out/production/repo-Team19 org.team19.VotingSystemRunner <pathToElectionFile>` replacing `<pathToElectionFile>` with the file you wish to run the program with to test a specific election file

### Running tests (not recommended over the non-CLI options)

- To run all tests, run `java -jar lib/junit-platform-console-standalone-1.8.0-M1.jar -cp out/production/repo-Team19:out/test/repo-Team19 -p org.team19`

- To run tests for a class, run `java -jar lib/junit-platform-console-standalone-1.8.0-M1.jar -cp out/production/repo-Team19:out/test/repo-Team19 -c org.team19.[TestClassName]`, replacing the bracketed item with the corresponding test class (e.g. `org.team19.VotingSystemRunnerTest`)

- To run a test method, run `java -jar lib/junit-platform-console-standalone-1.8.0-M1.jar -cp out/production/repo-Team19:out/test/repo-Team19 -m org.team19.[TestClassName]#[methodName]`, replacing the bracketed item with the corresponding test class (e.g. `org.team19.VotingSystemRunnerTest#testGetFullFilePath`)

Note: If you change `RUN_TIME_TESTS` to `true`, then you must rebuild the project by rerunnning the `ant` command
