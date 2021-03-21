/*
 * File name:
 * VotingSystemRunnerTest.java
 *
 * Author:
 * Nikunj Chawla and Aaron Kandikatla
 *
 * Purpose:
 * Tests the VotingSystemRunner class
 */

package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//TODO: Add the final keyword where applicable (see CheckStyle)
//TODO: Add tests for the case of ties by setting the rand field to a Random with a specific seed so tests don't change
//TODO: Modify generated filenames to use the same name format as in Project1/testing/test-resources/votingStreamParserTest

final class VotingSystemRunnerTest {
    
    //Creates a null device output stream to consume and ignore all output
    private static final OutputStream NULL_OUTPUT = OutputStream.nullOutputStream();
    
    //The character for separating directories in the filesystem
    private static final char FILE_SEP = File.separatorChar;
    
    private VotingSystemRunnerTest() {}
    
    @Test
    void testGetFullFilePath() {
        try {
            final String projectRootDir = new File(".").getCanonicalPath();
            final String userHomeDir = System.getProperty("user.home");
            
            final Method getFullFilePath = VotingSystemRunner.class.getDeclaredMethod("getFullFilePath", String.class);
            getFullFilePath.setAccessible(true);
            
            final char fileSep = File.separatorChar;
            
            Assertions.assertAll(
                //Testing path relative to project directory
                () -> Assertions.assertEquals(
                    String.format("%s/test.txt".replace('/', fileSep), projectRootDir), getFullFilePath.invoke(VotingSystemRunner.class, "test.txt")
                ),
                //Testing path relative to home directory
                () -> Assertions.assertEquals(
                    String.format("%s/test.txt".replace('/', fileSep), userHomeDir),
                    getFullFilePath.invoke(VotingSystemRunner.class, "~/test.txt".replace('/', fileSep))
                ),
                //Testing absolute path
                () -> Assertions.assertEquals(
                    String.format("%s/Desktop/test.txt".replace('/', fileSep), userHomeDir),
                    getFullFilePath.invoke(VotingSystemRunner.class, String.format("%s/Desktop/test.txt".replace('/', fileSep), userHomeDir))
                )
            );
        }
        catch(IOException e) {
            Assertions.fail("Unable to retrieve the path for the project root directory");
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getFullFilePath method from VotingSystemRunner");
        }
    }
    
    @Test
    void testGetFileInputStream() {
        final Method getFileInputStream;
        try {
            getFileInputStream = VotingSystemRunner.class.getDeclaredMethod("getFileInputStream", String.class);
            getFileInputStream.setAccessible(true);
            
            final Method getFullFilePath = VotingSystemRunner.class.getDeclaredMethod("getFullFilePath", String.class);
            getFullFilePath.setAccessible(true);
            
            final char fileSep = File.separatorChar;
            
            Assertions.assertAll(
                //Testing valid file path
                () -> Assertions.assertDoesNotThrow(() ->
                    getFileInputStream.invoke(
                        VotingSystem.class,
                        getFullFilePath.invoke(
                            VotingSystemRunner.class,
                            "Project1/testing/test-resources/votingSystemRunnerTest/test.txt".replace('/', fileSep)
                        )
                    )
                ),
                //Testing invalid file path
                () -> Assertions.assertEquals(
                    FileNotFoundException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getFileInputStream.invoke(
                            VotingSystem.class,
                            getFullFilePath.invoke(
                                VotingSystemRunner.class,
                                "Project1/testing/test-resources/votingSystemRunnerTest/b.txt".replace('/', fileSep)
                            )
                        )
                    ).getCause().getClass()
                ),
                //Testing valid path but to directory
                () -> Assertions.assertEquals(
                    FileNotFoundException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getFileInputStream.invoke(
                            VotingSystem.class,
                            getFullFilePath.invoke(
                                VotingSystemRunner.class,
                                "Project1/testing/test-resources/votingSystemRunnerTest".replace('/', fileSep)
                            )
                        )
                    ).getCause().getClass()
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getFile method from VotingSystemRunner");
        }
    }
    
    @Test
    void testGenerateTimestampedFileName() {
        final Method generateTimestampedFileName;
        try {
            generateTimestampedFileName =
                VotingSystemRunner.class.getDeclaredMethod("generateTimestampedFileName", String.class, LocalDateTime.class);
            generateTimestampedFileName.setAccessible(true);
            
            //Testing various prefixes and time stamps for generateTimestampedFileName
            Assertions.assertAll(
                //Testing arbitrary date
                () -> Assertions.assertEquals("report_2016-08-14_14-55-47.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "report",
                    LocalDateTime.of(2016, 8, 14, 14, 55, 47)
                )),
                //Testing for proper zero padding
                () -> Assertions.assertEquals("potato_2021-09-09_09-09-09.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "potato",
                    LocalDateTime.of(2021, 9, 9, 9, 9, 9)
                )),
                //Edge case: Very low timestamp
                () -> Assertions.assertEquals("entrée_0-01-01_00-00-00.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "entrée",
                    LocalDateTime.of(0, 1, 1, 0, 0, 0)
                )),
                //Edge case: Very high timestamp
                () -> Assertions.assertEquals("vote_9999-12-31_23-59-59.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "vote",
                    LocalDateTime.of(9999, 12, 31, 23, 59, 59)
                ))
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the generateTimestampedFileName method from VotingSystemRunner");
        }
    }
    
    @Test
    void testGetFileOutputStream() {
        final Method getFileOutputStream;
        
        final char fileSep = File.separatorChar;
        
        try {
            //Retrieve getFileOutputStream using reflection due to it being private, and use reflection to make it accessible
            getFileOutputStream = VotingSystemRunner.class.getDeclaredMethod("getFileOutputStream", String.class);
            getFileOutputStream.setAccessible(true);
            
            Assertions.assertAll(
                //Testing existing file path
                () -> Assertions.assertDoesNotThrow(() ->
                    getFileOutputStream.invoke(
                        VotingSystem.class,
                        "Project1/testing/test-resources/votingSystemRunnerTest/test.txt".replace('/', fileSep)
                    )
                ),
                //Testing nonexistent file path
                () -> Assertions.assertDoesNotThrow(() ->
                    getFileOutputStream.invoke(
                        VotingSystem.class,
                        "Project1/testing/test-resources/votingSystemRunnerTest/b.txt".replace('/', fileSep)
                    )
                ),
                //Check that the nonexistent file was created for the output stream from the previous assertion
                () -> Assertions.assertTrue(
                    new File("Project1/testing/test-resources/votingSystemRunnerTest/b.txt".replace('/', fileSep)).exists()
                ),
                //Testing existing path but to directory
                () -> Assertions.assertEquals(
                    FileNotFoundException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getFileOutputStream.invoke(
                            VotingSystem.class,
                            "Project1/testing/test-resources/votingSystemRunnerTest".replace('/', fileSep)
                        )
                    ).getCause().getClass()
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getFile method from VotingSystemRunner");
        }
        finally {
            //Remove the created test file after the test is completed to reset to the initial state of files
            //noinspection ResultOfMethodCallIgnored
            new File("Project1/testing/test-resources/votingSystemRunnerTest/b.txt".replace('/', fileSep)).delete();
        }
    }
    
    @Test
    void testIrMajority() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        //TODO: Change audit and report comparison file names to test-specific names
        String auditOutputPath = "Project1/testing/test-resources/votingSystemRunnerTest/auditCompare.txt".replace('/', FILE_SEP);
        String reportOutputPath = "Project1/testing/test-resources/votingSystemRunnerTest/reportCompare.txt".replace('/', FILE_SEP);
        
        //Path to expected audit output
        String expectedAudit = "Project1/testing/test-resources/votingSystemRunnerTest/testIrMajorityAudit.txt"
            .replace('/', FILE_SEP);
        //Path to expected report output
        String expectedReport = "Project1/testing/test-resources/votingSystemRunnerTest/testIrMajorityReport.txt"
            .replace('/', FILE_SEP);
        
        //Path to CSV file
        String inputCSV = "Project1/testing/test-resources/votingSystemRunnerTest/ir_testMajority.csv"
            .replace('/', FILE_SEP);
        
        FileOutputStream auditOutput = null;
        FileOutputStream reportOutput = null;
        
        try {
            auditOutput = new FileOutputStream(auditOutputPath);
            reportOutput = new FileOutputStream(reportOutputPath);
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        
        //Sets audit and report outputs
        VotingSystemRunner.auditOutputPotentialSource = auditOutput;
        VotingSystemRunner.reportOutputPotentialSource = reportOutput;
        
        //Runs main algorithm
        VotingSystemRunner.main(inputCSV);
        
        VotingSystemRunner.auditOutputPotentialSource = null;
        VotingSystemRunner.reportOutputPotentialSource = null;
        
        //Comparing expected output vs actual output of audit
        Assertions.assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream(expectedAudit),
            new FileInputStream(auditOutputPath))
        );
        
        //Comparing expected output vs actual output of report
        Assertions.assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream(expectedReport),
            new FileInputStream(reportOutputPath))
        );
        
        //TODO: Create a File and convert it to FileInputStream instead of creating File just for deletion (see 2.1 in  tinyurl.com/y7am7464)
        //Deletes temp files if test passes
        //noinspection ResultOfMethodCallIgnored
        new File(auditOutputPath).delete();
        //noinspection ResultOfMethodCallIgnored
        new File(reportOutputPath).delete();
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    @Test
    void testIrPopularity() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        //TODO: Change audit and report comparison file names to test-specific names
        String auditOutputPath = "Project1/testing/test-resources/votingSystemRunnerTest/auditCompare.txt".replace('/', FILE_SEP);
        String reportOutputPath = "Project1/testing/test-resources/votingSystemRunnerTest/reportCompare.txt".replace('/', FILE_SEP);
        
        //Path to expected audit output
        String expectedAudit =
            "Project1/testing/test-resources/votingSystemRunnerTest/testIrPopularityAudit.txt".replace('/', FILE_SEP);
        //Path to expected report output
        String expectedReport =
            "Project1/testing/test-resources/votingSystemRunnerTest/testIrPopularityReport.txt".replace('/', FILE_SEP);
        
        //Path to CSV file
        String inputCSV =
            "Project1/testing/test-resources/votingSystemRunnerTest/ir_testPopularity.csv".replace('/', FILE_SEP);
        
        FileOutputStream auditOutput = null;
        FileOutputStream reportOutput = null;
        
        try {
            auditOutput = new FileOutputStream(auditOutputPath);
            reportOutput = new FileOutputStream(reportOutputPath);
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        
        //Sets audit and report outputs
        VotingSystemRunner.auditOutputPotentialSource = auditOutput;
        VotingSystemRunner.reportOutputPotentialSource = reportOutput;
        
        //Runs main algorithm
        VotingSystemRunner.main(inputCSV);
        
        VotingSystemRunner.auditOutputPotentialSource = null;
        VotingSystemRunner.reportOutputPotentialSource = null;
        
        //Comparing expected output vs actual output of audit
        Assertions.assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream(expectedAudit),
            new FileInputStream(auditOutputPath))
        );
        
        //Comparing expected output vs actual output of report
        Assertions.assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream(expectedReport),
            new FileInputStream(reportOutputPath))
        );
        
        //TODO: Create a File and convert it to FileInputStream instead of creating File just for deletion (see 2.1 in  tinyurl.com/y7am7464)
        //Deletes temp files if test passes
        //noinspection ResultOfMethodCallIgnored
        new File(auditOutputPath).delete();
        //noinspection ResultOfMethodCallIgnored
        new File(reportOutputPath).delete();
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    @Test
    void testIrSingleCandidate() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        //TODO: Change audit and report comparison file names to test-specific names
        String auditOutputPath = "Project1/testing/test-resources/votingSystemRunnerTest/auditCompare.txt".replace('/', FILE_SEP);
        String reportOutputPath = "Project1/testing/test-resources/votingSystemRunnerTest/reportCompare.txt".replace('/', FILE_SEP);
        
        //Path to expected audit output
        String expectedAudit =
            "Project1/testing/test-resources/votingSystemRunnerTest/testIrSingleCandidateAudit.txt".replace('/', FILE_SEP);
        //Path to expected report output
        String expectedReport =
            "Project1/testing/test-resources/votingSystemRunnerTest/testIrSingleCandidateReport.txt".replace('/', FILE_SEP);
        
        //Path to CSV file
        String inputCSV =
            "Project1/testing/test-resources/votingSystemRunnerTest/ir_testSingleCandidate.csv".replace('/', FILE_SEP);
        
        FileOutputStream auditOutput = null;
        FileOutputStream reportOutput = null;
        
        try {
            auditOutput = new FileOutputStream(auditOutputPath);
            reportOutput = new FileOutputStream(reportOutputPath);
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        
        //Sets audit and report outputs
        VotingSystemRunner.auditOutputPotentialSource = auditOutput;
        VotingSystemRunner.reportOutputPotentialSource = reportOutput;
        
        //Runs main algorithm
        VotingSystemRunner.main(inputCSV);
        
        VotingSystemRunner.auditOutputPotentialSource = null;
        VotingSystemRunner.reportOutputPotentialSource = null;
        
        //Comparing expected output vs actual output of audit
        Assertions.assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream(expectedAudit),
            new FileInputStream(auditOutputPath))
        );
        
        //Comparing expected output vs actual output of report
        Assertions.assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream(expectedReport),
            new FileInputStream(reportOutputPath))
        );
        
        //TODO: Create a File and convert it to FileInputStream instead of creating File just for deletion (see 2.1 in  tinyurl.com/y7am7464)
        //Deletes temp files if test passes
        //noinspection ResultOfMethodCallIgnored
        new File(auditOutputPath).delete();
        //noinspection ResultOfMethodCallIgnored
        new File(reportOutputPath).delete();
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    @Test
    void testOplTypical() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        //TODO: Change audit and report comparison file names to test-specific names
        String auditOutputPath = "Project1/testing/test-resources/votingSystemRunnerTest/auditCompare.txt".replace('/', FILE_SEP);
        String reportOutputPath = "Project1/testing/test-resources/votingSystemRunnerTest/reportCompare.txt".replace('/', FILE_SEP);
        
        //Path to expected audit output
        String expectedAudit =
            "Project1/testing/test-resources/votingSystemRunnerTest/testOplTypicalAudit.txt".replace('/', FILE_SEP);
        //Path to expected report output
        String expectedReport =
            "Project1/testing/test-resources/votingSystemRunnerTest/testOplTypicalReport.txt".replace('/', FILE_SEP);
        
        //Path to CSV file
        String inputCSV =
            "Project1/testing/test-resources/votingSystemRunnerTest/opl_testTypical.csv".replace('/', FILE_SEP);
        
        FileOutputStream auditOutput = null;
        FileOutputStream reportOutput = null;
        
        try {
            auditOutput = new FileOutputStream(auditOutputPath);
            reportOutput = new FileOutputStream(reportOutputPath);
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        
        //Sets audit and report outputs
        VotingSystemRunner.auditOutputPotentialSource = auditOutput;
        VotingSystemRunner.reportOutputPotentialSource = reportOutput;
        
        //Runs main algorithm
        VotingSystemRunner.main(inputCSV);
        
        VotingSystemRunner.auditOutputPotentialSource = null;
        VotingSystemRunner.reportOutputPotentialSource = null;
        
        //Comparing expected output vs actual output of audit
        Assertions.assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream(expectedAudit),
            new FileInputStream(auditOutputPath))
        );
        
        //Comparing expected output vs actual output of report
        Assertions.assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream(expectedReport),
            new FileInputStream(reportOutputPath))
        );
        
        //TODO: Create a File and convert it to FileInputStream instead of creating File just for deletion (see 2.1 in  tinyurl.com/y7am7464)
        //Deletes temp files if test passes
        //noinspection ResultOfMethodCallIgnored
        new File(auditOutputPath).delete();
        //noinspection ResultOfMethodCallIgnored
        new File(reportOutputPath).delete();
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    @Test
    void testOplMoreSeatsThanCandidates() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        //TODO: Change audit and report comparison file names to test-specific names
        String auditOutputPath = "Project1/testing/test-resources/votingSystemRunnerTest/auditCompare.txt".replace('/', FILE_SEP);
        String reportOutputPath = "Project1/testing/test-resources/votingSystemRunnerTest/reportCompare.txt".replace('/', FILE_SEP);
        
        //Path to expected audit output
        String expectedAudit =
            "Project1/testing/test-resources/votingSystemRunnerTest/testOplMoreSeatsThanCandidatesAudit.txt".replace('/', FILE_SEP);
        //Path to expected report output
        String expectedReport =
            "Project1/testing/test-resources/votingSystemRunnerTest/testOplMoreSeatsThanCandidatesReport.txt".replace('/', FILE_SEP);
        
        //Path to CSV file
        String inputCSV =
            "Project1/testing/test-resources/votingSystemRunnerTest/opl_testMoreSeatsThanCandidates.csv".replace('/', FILE_SEP);
        
        FileOutputStream auditOutput = null;
        FileOutputStream reportOutput = null;
        
        try {
            auditOutput = new FileOutputStream(auditOutputPath);
            reportOutput = new FileOutputStream(reportOutputPath);
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        
        //Sets audit and report outputs
        VotingSystemRunner.auditOutputPotentialSource = auditOutput;
        VotingSystemRunner.reportOutputPotentialSource = reportOutput;
        
        //Runs main algorithm
        VotingSystemRunner.main(inputCSV);
        
        VotingSystemRunner.auditOutputPotentialSource = null;
        VotingSystemRunner.reportOutputPotentialSource = null;
        
        //Comparing expected output vs actual output of audit
        Assertions.assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream(expectedAudit),
            new FileInputStream(auditOutputPath))
        );
        
        //Comparing expected output vs actual output of report
        Assertions.assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream(expectedReport),
            new FileInputStream(reportOutputPath))
        );
        
        //TODO: Create a File and convert it to FileInputStream instead of creating File just for deletion (see 2.1 in  tinyurl.com/y7am7464)
        //Deletes temp files if test passes
        //noinspection ResultOfMethodCallIgnored
        new File(auditOutputPath).delete();
        //noinspection ResultOfMethodCallIgnored
        new File(reportOutputPath).delete();
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    /**
     * Generates an IR ballot line that goes groupNum, groupNum -1, groupNum -2, ..., 1 with numCommasBefore commas before the numbering and
     * (candidateSize - groupNum - numCommasBefore) commas after
     *
     * @param candidateSize   The number of candidates in the sample election
     * @param groupNum        The candidate number that should be the last ranked from candidates endNumExclusive + 1 to groupNum
     * @param numCommasBefore The exclusive ending candidate number for the ballot line
     * @return An IR ballot line that goes groupNum, groupNum -1, groupNum -2, ..., endNumExclusive + 1 and then commas for the remaining spots
     */
    private static String generateIrTestBallotLine(final int candidateSize, final int groupNum, final int numCommasBefore) {
        return ",".repeat(numCommasBefore)
            + IntStream.iterate(groupNum, v -> v - 1)
            .limit(groupNum)
            .mapToObj(Integer::toString)
            .collect(Collectors.joining(","))
            + ",".repeat(candidateSize - groupNum - numCommasBefore);
    }
    
    /**
     * Generates an IR file with the provided number of ballots and candidates, designed to make the IR process very slow with candidate 1 having
     * 50% of the ballots, candidate 2 having 25% of the ballots, etc., and having each ballot be redistributed to the candidate with the number
     * one less than its current number until we reach the point where we have candidate 1 having 50% of the ballots and ballot 2 having 50% of the
     * ballots
     *
     * @param outputStream           The outputStream to write the IR election file contents
     * @param candidateAndBallotSize The size of both the number of candidates and number of ballots
     */
    private static void generateIrTimeTestFileStairs(final OutputStream outputStream, final int candidateAndBallotSize) {
        final PrintWriter outputWriter = new PrintWriter(outputStream);
        
        //Writing the election file header
        outputWriter.println("IR");
        
        //Writing the number of candidates for the candidates header
        outputWriter.println(candidateAndBallotSize);
        
        //Write each candidate and its party, each having a unique party
        for(int i = 1; i < candidateAndBallotSize; ++i) {
            outputWriter.printf("C%d (P%d),", i, i);
        }
        outputWriter.printf("C%d (P%d)\n", candidateAndBallotSize, candidateAndBallotSize);
        
        //Writing the number of ballots for the ballots header
        outputWriter.println(candidateAndBallotSize);
        
        /*
         * Writing each ballot as mentioned in the JavaDoc such that candidate 1 having 50% of the ballots, candidate 2 having 50% of the ballots,
         * etc.
         */
        int nextSize = candidateAndBallotSize >> 1;
        int groupNum = 1;
        int numAdded = 0;
        while(nextSize != 0) {
            numAdded += nextSize;
            for(int i = 0; i < nextSize; ++i) {
                outputWriter.println(generateIrTestBallotLine(candidateAndBallotSize, groupNum, 0));
            }
            nextSize >>= 1;
            ++groupNum;
        }
        
        //Writes any missing ballots as just having the next single candidate as the ranking
        for(int i = numAdded + 1; i <= candidateAndBallotSize; ++i) {
            outputWriter.println(generateIrTestBallotLine(candidateAndBallotSize, groupNum, 0));
            ++groupNum;
        }
        outputWriter.close();
    }
    
    /**
     * Generates an IR file with the provided number of candidates and ballots, designed to make the IR process very slow with candidate 1 having
     * 25% of the ballots, candidate 2 having 12.5% of the ballots, etc., and then having candidate n/2 + 1 having 25% of the votes, candidate n/2
     * + 2 having 12.5% of the votes, etc., and having  each ballot be redistributed to the candidate with the number one  less than
     * its current number until we reach the point where we have candidate 1 having 50% of the ballots and ballot n/2 having 50% of the ballots
     *
     * @param outputStream           The outputStream to write the IR election file contents
     * @param candidateAndBallotSize The size of both the number of candidates and number of ballots
     */
    private static void generateIrTimeTestFileDoubleStairs(final OutputStream outputStream, final int candidateAndBallotSize) {
        final PrintWriter outputWriter = new PrintWriter(outputStream);
        
        //Writing the election file header
        outputWriter.println("IR");
        
        //Writing the number of candidates for the candidates header
        outputWriter.println(candidateAndBallotSize);
        
        //Write each candidate and its party, each having a unique party
        for(int i = 1; i < candidateAndBallotSize; ++i) {
            outputWriter.printf("C%d (P%d),", i, i);
        }
        outputWriter.printf("C%d (P%d)\n", candidateAndBallotSize, candidateAndBallotSize);
        
        //Writing the number of ballots for the ballots header
        outputWriter.println(candidateAndBallotSize);
        
        /*
         * Writing each ballot as mentioned in the JavaDoc such that candidate 1 having 25% of the ballots, candidate 2 having 12.5% of the
         * ballots, etc., and candidate n/2 + 1 having 25% of the ballots, candidate n/2 + 2 having 12.5% of the ballots, etc.
         */
        final int leftSize = candidateAndBallotSize >> 1;
        final int rightSize = leftSize + (candidateAndBallotSize % 2 == 0 ? 0 : 1);
        
        //Fills the "left" side of the ballots (1 to n/2)
        int nextSize = leftSize >> 1;
        int groupNum = 1;
        int numAdded = 0;
        while(nextSize != 0) {
            numAdded += nextSize;
            for(int i = 0; i < nextSize; ++i) {
                outputWriter.println(generateIrTestBallotLine(candidateAndBallotSize, groupNum, 0));
            }
            nextSize >>= 1;
            ++groupNum;
        }
        
        //Writes any missing ballots as just having the next single candidate as the ranking for the left side
        for(int i = numAdded + 1; i <= leftSize; ++i) {
            outputWriter.println(generateIrTestBallotLine(candidateAndBallotSize, groupNum, 0));
            ++groupNum;
        }
        
        //Fills the "right" side of the ballots (n/2 + 1 to n)
        nextSize = rightSize >> 1;
        numAdded = 0;
        final int leftGroupNum = groupNum - 1;
        while(nextSize != 0) {
            numAdded += nextSize;
            for(int i = 0; i < nextSize; ++i) {
                outputWriter.println(generateIrTestBallotLine(candidateAndBallotSize, groupNum, leftGroupNum));
            }
            nextSize >>= 1;
            ++groupNum;
        }
        
        //Writes any missing ballots as just having the next single candidate as the ranking for the right side
        for(int i = numAdded + 1; i <= rightSize; ++i) {
            outputWriter.println(generateIrTestBallotLine(candidateAndBallotSize, groupNum, leftGroupNum));
            ++groupNum;
        }
        outputWriter.close();
    }
    
    /**
     * Generates an OPL ballot line by inserting a 1 at the position for the candidate in the provided comma string
     *
     * @param candidateNum The candidate number that the voter selected for the ballot
     * @param commaString  The string that consists of commas to insert the 1 into
     * @return The OPL ballot line by inserting a 1 at the position for the candidate in the provided comma string
     */
    private static String generateOplTestBallotLine(final int candidateNum, final String commaString) {
        return new StringBuilder(commaString).insert(candidateNum - 1, 1).toString();
    }
    
    /**
     * Generates an OPL file with the provided number of candidates and ballots and the provided number of candidates per party
     *
     * @param outputStream           The outputStream to write the OPL election file contents
     * @param candidateAndBallotSize The size of both the number of candidates and number of ballots
     * @param candidatesPerParty     The number of candidates per party
     */
    private static void generateOplTimeTestFile(final OutputStream outputStream, final int candidateAndBallotSize, final int candidatesPerParty) {
        final PrintWriter outputWriter = new PrintWriter(outputStream);
        
        //Writing the election file header
        outputWriter.println("OPL");
        
        //Writing the number of candidates for the candidates header
        outputWriter.println(candidateAndBallotSize);
        
        //Write each candidate and its party, each having
        for(int i = 1; i < candidateAndBallotSize; ++i) {
            outputWriter.printf("[C%d, P%d],", i, (int) Math.ceil((double) i / candidatesPerParty));
        }
        outputWriter.printf("[C%d, P%d]\n", candidateAndBallotSize, (int) Math.ceil((double) candidateAndBallotSize / candidatesPerParty));
        
        //Writing the number of seats for the ballots header
        outputWriter.println(candidateAndBallotSize);
        
        //Writing the number of ballots for the ballots header
        outputWriter.println(candidateAndBallotSize);
        
        //The string of commas for each ballot line in which the 1 will be inserted
        final String commaString = ",".repeat(candidateAndBallotSize - 1);
        
        //The random object used for creating random ballots
        final Random rand = new Random();
        
        //Write ballots with random candidates selected
        for(int i = 1; i <= candidateAndBallotSize; ++i) {
            outputWriter.println(generateOplTestBallotLine(rand.nextInt(candidateAndBallotSize) + 1, commaString));
        }
        outputWriter.close();
    }
    
    //Tests that a 100,000-line IR election file in the format generated by generateIrTimeTestFileStairs runs under 8 minutes
    @Test
    void testIrStairsTime() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        //Store the running time limit
        final int timeLimitSeconds = 8 * 60;
        
        //The file upon which this test will be run
        final String testFilePath = "Project1/testing/test-resources/votingSystemRunnerTest/irStairsTest.txt".replace('/', File.separatorChar);
        
        try {
            //Generate the test file
            final FileOutputStream testFileLocation = new FileOutputStream(testFilePath);
            generateIrvTimeTestFileStairs(testFileLocation, 100000);
            
            //Setting the output sources to the null output stream as the generated files are massive
            VotingSystemRunner.auditOutputPotentialSource = NULL_OUTPUT;
            VotingSystemRunner.reportOutputPotentialSource = NULL_OUTPUT;
            
            //Time the running of CompuVote with the current file
            final long initTime = System.nanoTime();
            VotingSystemRunner.main(testFilePath);
            final long finalTime = System.nanoTime();
            
            //Setting the output sources back to null so they are not changed for other tests
            VotingSystemRunner.auditOutputPotentialSource = null;
            VotingSystemRunner.reportOutputPotentialSource = null;
            
            //Get the runtime in seconds, and if it exceeds the time limit, then fail
            final double runtime = (double) (finalTime - initTime) / 1000000000;
            if(runtime > timeLimitSeconds) {
                Assertions.fail(String.format("testIrStairs took %.2f seconds but a maximum of %d seconds was expected", runtime, timeLimitSeconds));
            }
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to create the IR stairs test file");
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
            
            //Delete the giant generated file
            //noinspection ResultOfMethodCallIgnored
            new File(testFilePath).delete();
        }
    }
    
    //Tests that a 100,000-line IR election file in the format generated by generateIrvTimeTestFileDoubleStairs runs under 8 minutes
    @Test
    void testIrDoubleStairsTime() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        //Store the running time limit
        final int timeLimitSeconds = 8 * 60;
        
        //The file upon which this test will be run
        final String testFilePath = "Project1/testing/test-resources/votingSystemRunnerTest/irDoubleStairsTest.txt".replace('/', File.separatorChar);
        
        try {
            //Generate the test file
            final FileOutputStream testFileLocation = new FileOutputStream(testFilePath);
            generateIrvTimeTestFileDoubleStairs(testFileLocation, 100000);
            
            //Setting the output sources to the null output stream as the generated files are massive
            VotingSystemRunner.auditOutputPotentialSource = NULL_OUTPUT;
            VotingSystemRunner.reportOutputPotentialSource = NULL_OUTPUT;
            
            //Time the running of CompuVote with the current file
            final long initTime = System.nanoTime();
            VotingSystemRunner.main(testFilePath);
            final long finalTime = System.nanoTime();
            
            //Setting the output sources back to null so they are not changed for other tests
            VotingSystemRunner.auditOutputPotentialSource = null;
            VotingSystemRunner.reportOutputPotentialSource = null;
            
            //Get the runtime in seconds, and if it exceeds the time limit, then fail
            final double runtime = (double) (finalTime - initTime) / 1000000000;
            if(runtime > timeLimitSeconds) {
                Assertions.fail(String.format(
                    "testIrDoubleStairs took %.2f seconds but a maximum of %d seconds was expected",
                    runtime,
                    timeLimitSeconds)
                );
            }
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to create the IR double stairs test file");
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
            
            //Delete the giant generated file
            //noinspection ResultOfMethodCallIgnored
            new File(testFilePath).delete();
        }
    }
    
    /**
     * Returns an iterator that starts at 1, is continually multiplied by 4, and ends at the number that is just greater than or equal to
     * maxSize
     *
     * @return An iterator that starts at 1, is continually multiplied by 4, and ends at the number that is just greater than or equal to
     * maxSize
     */
    private static Iterator<Arguments> provideOplTimeGroupSizes() {
        final int maxSize = 100000;
        
        return new Iterator<>() {
            /**
             * The current size to return going from 1, 4, 16, ..., until the number just greater that or equal to maxSize
             */
            private int currentCandidatePartySize = 1;
            
            /**
             * True if the next number is the last number, which is the case for the number that is just greater than or equal to maxSize
             */
            private boolean oneMoreLeft = false;
            
            /**
             * Returns true if there is another size to provide
             *
             * @return True if there is another size to provide
             */
            @Override
            public boolean hasNext() {
                return currentCandidatePartySize <= maxSize || oneMoreLeft;
            }
            
            /**
             * Returns the next size as an {@link Arguments}
             *
             * @return The next size as an {@link Arguments}
             * @throws NoSuchElementException Thrown after the sizes have been gone through
             */
            @Override
            public Arguments next() throws NoSuchElementException {
                if(hasNext()) {
                    int returnValue = currentCandidatePartySize;
                    currentCandidatePartySize <<= 2;
                    oneMoreLeft = returnValue < maxSize && currentCandidatePartySize >= maxSize;
                    return Arguments.of(returnValue);
                }
                throw new NoSuchElementException("The size limit has already been reached");
            }
        };
    }
    
    /*
     * Tests that a 100,000-line OPL election file in the format generated by generateOplTimeTestFile with tests for various candidates per party
     * sizes each runs under 8 minutes
     */
    @ParameterizedTest(name = "{0} candidates per party")
    @MethodSource("org.team19.VotingSystemRunnerTest#provideOplTimeGroupSizes")
    void testOplTime(final int currentCandidatePartySize) {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        //Store the running time limit
        final int timeLimitSeconds = 8 * 60;
        
        //The file upon which this test will be run
        final String testFilePath = String.format(
            "Project1/testing/test-resources/votingSystemRunnerTest/oplTest%d.txt",
            currentCandidatePartySize
        ).replace('/', File.separatorChar);
        
        try {
            //Generate the test file
            final FileOutputStream testFileLocation = new FileOutputStream(testFilePath);
            generateOplTimeTestFile(testFileLocation, 100000, currentCandidatePartySize);
            
            //Setting the output sources to the null output stream as the generated files are massive
            VotingSystemRunner.auditOutputPotentialSource = NULL_OUTPUT;
            VotingSystemRunner.reportOutputPotentialSource = NULL_OUTPUT;
            
            //Time the running of CompuVote with the current file
            final long initTime = System.nanoTime();
            VotingSystemRunner.main(testFilePath);
            final long finalTime = System.nanoTime();
            
            //Setting the output sources back to null so they are not changed for other tests
            VotingSystemRunner.auditOutputPotentialSource = null;
            VotingSystemRunner.reportOutputPotentialSource = null;
            
            //Get the runtime in seconds, and if it exceeds the time limit, then fail
            final double runtime = (double) (finalTime - initTime) / 1000000000;
            if(runtime > timeLimitSeconds) {
                Assertions.fail(String.format(
                    "testOplTime took %.2f seconds for %d candidates per party but a maximum of %d seconds was expected",
                    runtime,
                    currentCandidatePartySize,
                    timeLimitSeconds)
                );
            }
        }
        catch(FileNotFoundException e) {
            Assertions.fail(String.format("Unable to create the OPL test file for a size of %d", currentCandidatePartySize));
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
            
            //Delete the giant generated file
            //noinspection ResultOfMethodCallIgnored
            new File(testFilePath).delete();
        }
    }
}
