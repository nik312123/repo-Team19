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
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class VotingSystemRunnerTest {
    
    //Creates a null device output stream to consume and ignore all output
    private static final OutputStream NULL_OUTPUT = OutputStream.nullOutputStream();
    
    //The character for separating directories in the filesystem
    private static final char FILE_SEP = File.separatorChar;
    
    //True if the tests for 100,000 ballots in 8 minutes should be run (will pass automatically if this is false)
    private static final boolean RUN_TIME_TESTS = false;
    
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
                            "Project2/testing/test-resources/votingSystemRunnerTest/test.txt".replace('/', fileSep)
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
                                "Project2/testing/test-resources/votingSystemRunnerTest/b.txt".replace('/', fileSep)
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
                                "Project2/testing/test-resources/votingSystemRunnerTest".replace('/', fileSep)
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
                        "Project2/testing/test-resources/votingSystemRunnerTest/test.txt".replace('/', fileSep)
                    )
                ),
                //Testing nonexistent file path
                () -> Assertions.assertDoesNotThrow(() ->
                    getFileOutputStream.invoke(
                        VotingSystem.class,
                        "Project2/testing/test-resources/votingSystemRunnerTest/b.txt".replace('/', fileSep)
                    )
                ),
                //Check that the nonexistent file was created for the output stream from the previous assertion
                () -> Assertions.assertTrue(
                    new File("Project2/testing/test-resources/votingSystemRunnerTest/b.txt".replace('/', fileSep)).exists()
                ),
                //Testing existing path but to directory
                () -> Assertions.assertEquals(
                    FileNotFoundException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getFileOutputStream.invoke(
                            VotingSystem.class,
                            "Project2/testing/test-resources/votingSystemRunnerTest".replace('/', fileSep)
                        )
                    ).getCause().getClass()
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getFile method from VotingSystemRunner");
        }
        finally {
            //Run garbage collector manually to properly allow deletion of the file on Windows due to Java bug
            System.gc();
            
            //Remove the created test file after the test is completed to reset to the initial state of files
            //noinspection ResultOfMethodCallIgnored
            new File("Project2/testing/test-resources/votingSystemRunnerTest/b.txt".replace('/', fileSep)).delete();
        }
    }
    
    /**
     * Runs {@link VotingSystemRunner#main(String...)} using the given audit and report file paths and compares the audit and report files
     * generated to the files containing the expected audit and repor output
     *
     * @param auditOutputPath       The path to the audit file to create
     * @param reportOutputPath      The path to the report file to create
     * @param expectedAuditPath     The path to the file with the expected audit output
     * @param expectedReportPath    The path to the file with the expected report output
     * @param inputPaths            The paths to the input CSV files on which to run an election
     * @param beforeParsingModifier The way in which the {@link VotingSystem} should be modified before parsing, which can be null if not used
     */
    private static void runAuditReportSystemTest(final String auditOutputPath, final String reportOutputPath, final String expectedAuditPath,
        final String expectedReportPath, final String[] inputPaths, final Consumer<VotingSystem> beforeParsingModifier) {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        FileOutputStream auditOutput = null;
        FileOutputStream reportOutput = null;
        
        try {
            auditOutput = new FileOutputStream(auditOutputPath);
            reportOutput = new FileOutputStream(reportOutputPath);
        }
        catch(FileNotFoundException e) {
            Assertions.fail(String.format(
                "Unable to create %s or %s",
                auditOutputPath.substring(auditOutputPath.lastIndexOf(File.separatorChar)),
                reportOutputPath.substring(reportOutputPath.lastIndexOf(File.separatorChar))
            ));
        }
        
        //Sets audit and report outputs
        VotingSystemRunner.auditOutputPotentialSource = auditOutput;
        VotingSystemRunner.reportOutputPotentialSource = reportOutput;
        VotingSystemRunner.votingSystemModifierBeforeParsing = beforeParsingModifier;
        
        //Runs main algorithm
        try {
            VotingSystemRunner.main(inputPaths);
            
            //Comparing expected output vs actual output of audit
            Assertions.assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
                new FileInputStream(expectedAuditPath),
                new FileInputStream(auditOutputPath)
            ));
            
            //Comparing expected output vs actual output of report
            Assertions.assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
                new FileInputStream(expectedReportPath),
                new FileInputStream(reportOutputPath)
            ));
            
            //Run garbage collector manually to properly allow deletion of the file on Windows due to Java bug
            System.gc();
            
            //Deletes temp files if test passes
            //noinspection ResultOfMethodCallIgnored
            new File(auditOutputPath).delete();
            //noinspection ResultOfMethodCallIgnored
            new File(reportOutputPath).delete();
        }
        finally {
            VotingSystemRunner.auditOutputPotentialSource = null;
            VotingSystemRunner.reportOutputPotentialSource = null;
            VotingSystemRunner.votingSystemModifierBeforeParsing = null;
            
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    /**
     * Disables ballot validation for a provided {@link VotingSystem}, assuming it is an instance of {@link InstantRunoffSystem}
     *
     * @param votingSystem The {@link VotingSystem} on which to disable ballot invalidation
     */
    private static void disableInvalidateBallots(final VotingSystem votingSystem) {
        ((InstantRunoffSystem) votingSystem).invalidateBallots = false;
    }
    
    @Test
    void testIrMajority() {
        runAuditReportSystemTest(
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_majority_audit_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_majority_report_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_majority_audit_expected.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_majority_report_expected.txt".replace('/', FILE_SEP),
            new String[] {"Project2/testing/test-resources/votingSystemRunnerTest/ir_test_majority.csv".replace('/', FILE_SEP)},
            VotingSystemRunnerTest::disableInvalidateBallots
        );
    }
    
    @Test
    void testIrPopularity() {
        runAuditReportSystemTest(
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_popularity_audit_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_popularity_report_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_popularity_audit_expected.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_popularity_report_expected.txt".replace('/', FILE_SEP),
            new String[] {"Project2/testing/test-resources/votingSystemRunnerTest/ir_test_popularity.csv".replace('/', FILE_SEP)},
            VotingSystemRunnerTest::disableInvalidateBallots
        );
    }
    
    @Test
    void testIrSingleCandidate() {
        runAuditReportSystemTest(
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_single_candidate_audit_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_single_candidate_report_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_single_candidate_audit_expected.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_single_candidate_report_expected.txt".replace('/', FILE_SEP),
            new String[] {"Project2/testing/test-resources/votingSystemRunnerTest/ir_test_single_candidate.csv".replace('/', FILE_SEP)},
            null
        );
    }
    
    @Test
    void testIrMultipleFiles() {
        runAuditReportSystemTest(
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_multiple_files_audit_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_multiple_files_report_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_multiple_files_audit_expected.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_multiple_files_report_expected.txt".replace('/', FILE_SEP),
            new String[] {
                "Project2/testing/test-resources/votingSystemRunnerTest/ir_multi_part_1.csv".replace('/', FILE_SEP),
                "Project2/testing/test-resources/votingSystemRunnerTest/ir_multi_part_2.csv".replace('/', FILE_SEP),
                "Project2/testing/test-resources/votingSystemRunnerTest/ir_multi_part_3.csv".replace('/', FILE_SEP)
            },
            null
        );
    }
    
    @Test
    void testIrBallotInvalidation() {
        runAuditReportSystemTest(
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_ballot_invalidation_audit_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_ballot_invalidation_report_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_ballot_invalidation_audit_expected.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_ballot_invalidation_report_expected.txt".replace('/', FILE_SEP),
            new String[] {"Project2/testing/test-resources/votingSystemRunnerTest/ir_test_ballot_invalidation.csv".replace('/', FILE_SEP)},
            null
        );
    }
    
    @Test
    void testIrZeroBallots() {
        runAuditReportSystemTest(
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_zero_ballots_audit_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_zero_ballots_report_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_zero_ballots_audit_expected.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_ir_zero_ballots_report_expected.txt".replace('/', FILE_SEP),
            new String[] {"Project2/testing/test-resources/votingSystemRunnerTest/ir_test_zero_ballots.csv".replace('/', FILE_SEP)},
            votingSystem -> ((InstantRunoffSystem) votingSystem).rand = new Random(10L)
        );
    }
    
    @Test
    void testOplTypical() {
        runAuditReportSystemTest(
            "Project2/testing/test-resources/votingSystemRunnerTest/test_opl_typical_audit_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_opl_typical_report_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_opl_typical_audit_expected.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_opl_typical_report_expected.txt".replace('/', FILE_SEP),
            new String[] {"Project2/testing/test-resources/votingSystemRunnerTest/opl_test_typical.csv".replace('/', FILE_SEP)},
            null
        );
    }
    
    @Test
    void testOplMoreSeatsThanCandidates() {
        runAuditReportSystemTest(
            "Project2/testing/test-resources/votingSystemRunnerTest/test_opl_more_seats_than_candidates_audit_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_opl_more_seats_than_candidates_report_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_opl_more_seats_than_candidates_audit_expected.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_opl_more_seats_than_candidates_report_expected.txt".replace('/', FILE_SEP),
            new String[] {"Project2/testing/test-resources/votingSystemRunnerTest/opl_test_more_seats_than_candidates.csv".replace('/', FILE_SEP)},
            null
        );
    }
    
    @Test
    void testOplMultipleFiles() {
        runAuditReportSystemTest(
            "Project2/testing/test-resources/votingSystemRunnerTest/test_opl_multiple_files_audit_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_opl_multiple_files_report_actual.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_opl_multiple_files_audit_expected.txt".replace('/', FILE_SEP),
            "Project2/testing/test-resources/votingSystemRunnerTest/test_opl_multiple_files_report_expected.txt".replace('/', FILE_SEP),
            new String[] {
                "Project2/testing/test-resources/votingSystemRunnerTest/opl_multi_part_1.csv".replace('/', FILE_SEP),
                "Project2/testing/test-resources/votingSystemRunnerTest/opl_multi_part_2.csv".replace('/', FILE_SEP),
                "Project2/testing/test-resources/votingSystemRunnerTest/opl_multi_part_3.csv".replace('/', FILE_SEP)
            },
            null
        );
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
    private static String generateIrStairBallotLine(final int candidateSize, final int groupNum, final int numCommasBefore) {
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
        for(int i = 1; i < candidateAndBallotSize; i++) {
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
            for(int i = 0; i < nextSize; i++) {
                outputWriter.println(generateIrStairBallotLine(candidateAndBallotSize, groupNum, 0));
            }
            nextSize >>= 1;
            groupNum++;
        }
        
        //Writes any missing ballots as just having the next single candidate as the ranking
        for(int i = numAdded + 1; i <= candidateAndBallotSize; i++) {
            outputWriter.println(generateIrStairBallotLine(candidateAndBallotSize, groupNum, 0));
            groupNum++;
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
        for(int i = 1; i < candidateAndBallotSize; i++) {
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
        final int rightSize = leftSize + candidateAndBallotSize % 2;
        
        //Fills the "left" side of the ballots (1 to n/2)
        int nextSize = leftSize >> 1;
        int groupNum = 1;
        int numAdded = 0;
        while(nextSize != 0) {
            numAdded += nextSize;
            for(int i = 0; i < nextSize; i++) {
                outputWriter.println(generateIrStairBallotLine(candidateAndBallotSize, groupNum, 0));
            }
            nextSize >>= 1;
            groupNum++;
        }
        
        //Writes any missing ballots as just having the next single candidate as the ranking for the left side
        for(int i = numAdded + 1; i <= leftSize; i++) {
            outputWriter.println(generateIrStairBallotLine(candidateAndBallotSize, groupNum, 0));
            groupNum++;
        }
        
        //Fills the "right" side of the ballots (n/2 + 1 to n)
        nextSize = rightSize >> 1;
        numAdded = 0;
        final int leftGroupNum = groupNum - 1;
        while(nextSize != 0) {
            numAdded += nextSize;
            for(int i = 0; i < nextSize; i++) {
                outputWriter.println(generateIrStairBallotLine(candidateAndBallotSize, groupNum, leftGroupNum));
            }
            nextSize >>= 1;
            groupNum++;
        }
        
        //Writes any missing ballots as just having the next single candidate as the ranking for the right side
        for(int i = numAdded + 1; i <= rightSize; i++) {
            outputWriter.println(generateIrStairBallotLine(candidateAndBallotSize, groupNum, leftGroupNum));
            groupNum++;
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
        for(int i = 1; i < candidateAndBallotSize; i++) {
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
        for(int i = 1; i <= candidateAndBallotSize; i++) {
            outputWriter.println(generateOplTestBallotLine(rand.nextInt(candidateAndBallotSize) + 1, commaString));
        }
        outputWriter.close();
    }
    
    /**
     * Tests a timed test method that involves generating a file and comparing the runtime of CompuVote on the generated file to an expected
     * maximum runtime
     *
     * @param generateFileMethod   The method used to generate the file used in testing with the first argument required to be the
     *                             {@link OutputStream} to which the contents of the generated file are written
     * @param generationParameters The parameters after the aforementioned {@link OutputStream} to pass to the generateFileMethod
     * @param filePath             The output path to which the file contents created by generateFileMethod will be written
     * @param votingSystemModifier The {@link Consumer} used to modify the {@link VotingSystem}, which can be null if nonused
     * @param testName             The name of the particular test calling this function
     * @param timeLimitSeconds     The time limit that will cause the test to fail if exceeded
     */
    void runTimedTest(final Method generateFileMethod, final Collection<Object> generationParameters, final String filePath,
        final Consumer<VotingSystem> votingSystemModifier, final String testName, final int timeLimitSeconds) {
        //If RUN_TIME_TESTS is false, then timed tests should not run and instead automatically pass
        if(!RUN_TIME_TESTS) {
            System.out.printf("The timed test for %s did not run because RUN_TIME_TESTS is false\n", testName);
            return;
        }
        
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        //The file upon which this test will be run
        final File testFile = new File(filePath);
        
        try {
            //Create the output stream to which the file will be generated
            final FileOutputStream testFileLocation = new FileOutputStream(testFile);
            
            //Create the parameters for the file generation method
            final Object[] finalGenerationParameters = new Object[generationParameters.size() + 1];
            finalGenerationParameters[0] = testFileLocation;
            int i = 1;
            for(final Object generationParameter : generationParameters) {
                finalGenerationParameters[i] = generationParameter;
                i++;
            }
            
            //Generate the test file
            try {
                generateFileMethod.invoke(this, finalGenerationParameters);
            }
            catch(IllegalAccessException e) {
                Assertions.fail(String.format("Unable to properly access %s: %s", generateFileMethod.getName(), e.getMessage()));
            }
            //Throw the underlying exception from the generation method if possible
            catch(InvocationTargetException e) {
                try {
                    testFileLocation.close();
                }
                catch(IOException ignored) {}
                
                //Retrieves the throwable underlying the InvocationTargetException if applicable
                final Throwable relevantThrowable = e.getCause() == null ? e : e.getCause();
                Assertions.fail(String.format("Error in running the generation function for %s: %s", testName, relevantThrowable.getMessage()));
            }
            testFileLocation.close();
            
            //Setting the output sources to the null output stream as the generated files are massive
            VotingSystemRunner.auditOutputPotentialSource = NULL_OUTPUT;
            VotingSystemRunner.reportOutputPotentialSource = NULL_OUTPUT;
            
            //Set the consumer used to modify the voting system
            VotingSystemRunner.votingSystemModifierBeforeParsing = votingSystemModifier;
            
            try {
                //Time the running of CompuVote with the current file
                final long initTime = System.nanoTime();
                VotingSystemRunner.main(testFile.toString());
                final long finalTime = System.nanoTime();
                
                //Get the runtime in seconds, and if it exceeds the time limit, then fail
                final double runtime = (double) (finalTime - initTime) / 1000000000;
                if(runtime > timeLimitSeconds) {
                    Assertions.fail(
                        String.format("%s took %.2f seconds but a maximum of %d seconds was expected", testName, runtime, timeLimitSeconds)
                    );
                }
                else {
                    originalSystemOut.printf("%s runtime: %f\n", testName, runtime);
                }
            }
            finally {
                //Setting the output sources back to null so they are not changed for other tests
                VotingSystemRunner.auditOutputPotentialSource = null;
                VotingSystemRunner.reportOutputPotentialSource = null;
                
                //Set the consumer used to modify the voting system back to null so they are not changed for other tests
                VotingSystemRunner.votingSystemModifierBeforeParsing = null;
            }
        }
        catch(FileNotFoundException e) {
            Assertions.fail(String.format("Unable to create the %s test file", testName));
        }
        catch(IOException e) {
            Assertions.fail(String.format("Unable to close the %s test file", testName));
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
            
            /*
             * Call the JVM garbage collector manually to prevent the issue of large memory build-up that can be caused by the running of this test
             * with the other timed tests and to properly allow deletion of the file on Windows
             */
            System.gc();
            
            //Delete the giant generated file
            //noinspection ResultOfMethodCallIgnored
            testFile.delete();
        }
    }
    
    //Tests that a 100,000-line IR election file in the format generated by generateIrTimeTestFileStairs runs under 8 minutes
    @Test
    void testIrStairsTime() {
        try {
            runTimedTest(
                VotingSystemRunnerTest.class.getDeclaredMethod("generateIrTimeTestFileStairs", OutputStream.class, int.class),
                Collections.singletonList(100000),
                "Project2/testing/test-resources/votingSystemRunnerTest/ir_stairs_test.txt".replace('/', File.separatorChar),
                VotingSystemRunnerTest::disableInvalidateBallots,
                "testIrStairsTime",
                8 * 60
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve generateIrTimeTestFileStairs");
        }
    }
    
    //Tests that a 100,000-line IR election file in the format generated by generateIrTimeTestFileDoubleStairs runs under 8 minutes
    @Test
    void testIrDoubleStairsTime() {
        try {
            runTimedTest(
                VotingSystemRunnerTest.class.getDeclaredMethod("generateIrTimeTestFileDoubleStairs", OutputStream.class, int.class),
                Collections.singletonList(100000),
                "Project2/testing/test-resources/votingSystemRunnerTest/ir_double_stairs_test.txt".replace('/', File.separatorChar),
                VotingSystemRunnerTest::disableInvalidateBallots,
                "testIrDoubleStairsTime",
                8 * 60
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve generateIrTimeTestFileDoubleStairs");
        }
    }
    
    /**
     * Returns a {@link Collection} that starts at 1, is continually multiplied by 4, and ends at the number that is just greater than or equal to
     * the number of ballots
     *
     * @return An {@link Collection} that starts at 1, is continually multiplied by 4, and ends at the number that is just greater than or equal to
     * the number of ballots
     */
    private static Deque<Arguments> provideOplTimeGroupSizes() {
        //The number of ballots being tested
        final int ballotsSize = 100000;
        
        //The list of arguments from 4^0, 4^1, ..., 4^n where 4^n >= ballotsSize and 4^(n - 1) < ballotsSize
        final Deque<Arguments> result = new ArrayDeque<>();
        
        //Adds the above-specified numbers from 4^0 to 4^(n-1) to result
        int currentCandidatePartySize = 1;
        for(; currentCandidatePartySize < ballotsSize; currentCandidatePartySize <<= 2) {
            result.add(Arguments.of(currentCandidatePartySize));
        }
        
        //Adds 4^n to the result
        result.add(Arguments.of(currentCandidatePartySize));
        
        return result;
    }
    
    /*
     * Tests that a 100,000-line OPL election file in the format generated by generateOplTimeTestFile with tests for various candidates per party
     * sizes each runs under 8 minutes
     */
    @ParameterizedTest(name = "{0} candidates per party")
    @MethodSource("org.team19.VotingSystemRunnerTest#provideOplTimeGroupSizes")
    void testOplTime(final int currentCandidatePartySize) {
        try {
            runTimedTest(
                VotingSystemRunnerTest.class.getDeclaredMethod("generateOplTimeTestFile", OutputStream.class, int.class, int.class),
                List.of(100000, currentCandidatePartySize),
                String.format(
                    "Project2/testing/test-resources/votingSystemRunnerTest/opl_test%d.txt",
                    currentCandidatePartySize
                ).replace('/', File.separatorChar),
                null,
                String.format("testOplTime%d", currentCandidatePartySize),
                8 * 60
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve generateOplTimeTestFile");
        }
    }
}
