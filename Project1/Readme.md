# Project 1 – CompuVote

## Documentation

See <a href="documentation/Documentation.md" target="_blank">Documentation.md</a> for the documentation resoources available for the project

## Shana notes

1. Note that we talked to Shana about our method of using `Assertions.assertAll` and other forms of multiple tests, and she approved it so long that we take order of execution into account and have a method of uniquely identifying failing test cases in the same method.

2. Note that our use of `Fraction` in `OpenPartyListSystem` was discussed with Shana. She specifically said the following in regards to the usage: "You go with what you know Nikunj.  If you are working through the algorithm and this is what needs to be done, then do it.  Work through the algorithms and the examples given and your work needs to align with their outcomes." As our work lines aligns with the examples and aligns conceptually, we have found it fit to use `Fraction` for `OpenPartyListSystem`.

More resources that indicate the use of `Fraction` and decimal remaining ballots being acceptable:

1. See page 5 of https://www.legco.gov.hk/yr97-98/english/sec/library/in1_plc.pdf

2. https://www.accuratedemocracy.com/e_shares.htm

3. https://en.idi.org.il/articles/3302

4. https://www.utas.edu.au/library/companion_to_tasmanian_history/H/Hare-Clark%20system.htm

## Running the program

Note: The audit output files appear in `Project1/audits`, and the report output files appear in `Project1/reports`.

### IntelliJ IDEA

Note: Ensure that your IntelliJ IDEA is up to date before running the below:

Note 2: If IntelliJ is showing errors, then sometimes IntelliJ has not loaded everything properly. If this happens, quit and reopen IntelliJ. Then, reopen the `repo-Team19` directory if it did not automatically open.

1. Navigate to the directory in which you would like to clone this project

2. Run `git clone https://github.umn.edu/umn-csci-5801-S21-002/repo-Team19`

3. Open IntelliJ IDEA

4. If a different project is open, go to File → Open and select the `repo-Team19` directory you cloned, and go to step 6

5. If you are in the IntelliJ view with no projects open, click the Open button and select the `repo-Team19` directory you cloned

6. To run the program, navigate to `Project1 → src → main → org.team19 → VotingSystemRunner`, and click the green play button to the left of `main`. This by default reads from standard input. If you wish to stop this, click the red square stop button at the upper-right of the window.

7. To run the program with command-line arguments, click `VotingSystemRunner` at the top to the left of the green play button (configuration generated from 6), click Edit Configurations..., add space-delimited arguments to the Program Arguments field, click OK, and run the program like in step 6

8. To run tests, navigate to `Project1 → src → test → org.team19` and open the file corresponding to the class in which the tests you wish to run reside.

9. Click the green play button to the left of the test method you wish to run and click the option starting with Run

### Eclipse

Note: Ensure that your Eclipse is up to date before running the below:

1. Navigate to the directory in which you would like to clone this project

2. Run `git clone https://github.umn.edu/umn-csci-5801-S21-002/repo-Team19`

3. Open Eclipse

4. Go to File → Open Projects from File System..., click Directory..., select the `repo-Team19` directory you cloned, and click Finish

5. To run the program, navigate to `Project1 → src → main → org.team19 → VotingSystemRunner.java`, scroll to the `main` method, right-click within it, hover over Run As, and click Java Application. This by default reads from standard input. If you wish to stop this, click the red square stop button to the right of the console tab that opened.

6. To run the program with command-line arguments, right-click within the `main` method, hover over Run As, and click Run Configurations..., click the Arguments tab, add space-delimited arguments to the Program arguments field, and click Run

7. To run tests, navigate to `Project1 → src → test → org.team19` and open the file corresponding to the class in which the tests you wish to run reside.

8. Right-click within the test method you wish to run, hover over Run As, and click JUnit Test

## Command line on Ubuntu and macOS

1. Navigate to the directory in which you would like to clone this project

2. Run `git clone https://github.umn.edu/umn-csci-5801-S21-002/repo-Team19`

3. Run `java -classpath out/production/repo-Team19 org.team19.VotingSystemRunner` to run the program with standard input as the file input

4. Run `java -classpath out/production/repo-Team19 org.team19.VotingSystemRunner <pathToElectionFile>` replacing `<pathToElectionFile>` with the file you wish to run the program with to test a specific election file

5. Shana mentioned that we do not have to provide instructions for running JUnit tests, so I will allow the reader to have the pleasure of figuring 
