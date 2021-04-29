/*
 * File name:
 * VotingSystemParserTest.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Tests the VotingSystemParserTest class
 */

package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

final class VotingStreamParserTest {
    
    private final static OutputStream NULL_OUTPUT = OutputStream.nullOutputStream();
    
    private static final Map<String, Class<? extends VotingSystem>> HEADER_SYSTEM_MAP = Map.of(
        "IR", InstantRunoffSystem.class,
        "OPL", OpenPartyListSystem.class
    );
    
    private VotingStreamParserTest() {}
    
    @Test
    void testThrowParseException() {
        //Tests that throwParseException throws a ParseException
        final ParseException parseException = Assertions.assertThrows(
            ParseException.class,
            () -> VotingStreamParser.throwParseException("Sample message", "test", 92)
        );
        
        Assertions.assertAll(
            /*
             * Check that the ParseException's error message matches the format "Error on line [lineNumber]: [message]", replacing [lineNumber] and
             * [message] with the corresponding parameters to throwParseException
             */
            () -> Assertions.assertEquals("Error for input source test on line 92: Sample message", parseException.getMessage()),
            
            //Check that the error offset is equivalent to the provided line number
            () -> Assertions.assertEquals(92, parseException.getErrorOffset())
        );
    }
    
    //Testing the election file ending earlier than anticipated
    @Test
    void testParseFileEndsEarly() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        final String inputPath = "Project2/testing/test-resources/votingStreamParserTest/file_ends_early.csv";
        
        try {
            final FileInputStream inputStream = new FileInputStream(inputPath);
            Assertions.assertThrows(ParseException.class, () -> VotingStreamParser.parse(
                new InputStream[] {inputStream}, new String[] {inputPath}, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP)
            );
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to open file_ends_early.csv");
        }
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    //Testing the election file having an invalid header
    @Test
    void testParseFileInvalidHeader() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        final String inputPath = "Project2/testing/test-resources/votingStreamParserTest/invalid_header.csv";
        
        try {
            final FileInputStream inputStream = new FileInputStream(inputPath);
            Assertions.assertThrows(ParseException.class, () -> VotingStreamParser.parse(
                new InputStream[] {inputStream}, new String[] {inputPath}, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP)
            );
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to open file_ends_early.csv");
        }
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    //Testing the election file having an invalid candidates header
    @Test
    void testParseFileInvalidCandidatesHeader() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        final String inputPath = "Project2/testing/test-resources/votingStreamParserTest/invalid_candidates_header.csv";
        
        try {
            final FileInputStream inputStream = new FileInputStream(inputPath);
            Assertions.assertThrows(ParseException.class, () -> VotingStreamParser.parse(
                new InputStream[] {inputStream}, new String[] {inputPath}, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP)
            );
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to open file_ends_early.csv");
        }
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    //Testing the election file having an invalid candidates line
    @Test
    void testParseFileInvalidCandidates() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        final String inputPath = "Project2/testing/test-resources/votingStreamParserTest/invalid_candidates.csv";
        
        try {
            final FileInputStream inputStream = new FileInputStream(inputPath);
            Assertions.assertThrows(ParseException.class, () -> VotingStreamParser.parse(
                new InputStream[] {inputStream}, new String[] {inputPath}, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP)
            );
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to open file_ends_early.csv");
        }
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    //Testing the election file having an invalid ballots header
    @Test
    void testParseFileInvalidBallotHeader() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        final String inputPath = "Project2/testing/test-resources/votingStreamParserTest/invalid_ballot_header.csv";
        
        try {
            final FileInputStream inputStream = new FileInputStream(inputPath);
            Assertions.assertThrows(ParseException.class, () -> VotingStreamParser.parse(
                new InputStream[] {inputStream}, new String[] {inputPath}, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP)
            );
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to open file_ends_early.csv");
        }
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    //Testing the election file having an invalid ballot line
    @Test
    void testParseFileInvalidBallotLine() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        final String inputPath = "Project2/testing/test-resources/votingStreamParserTest/invalid_ballot_line.csv";
        
        try {
            final FileInputStream inputStream = new FileInputStream(inputPath);
            Assertions.assertThrows(ParseException.class, () -> VotingStreamParser.parse(
                new InputStream[] {inputStream}, new String[] {inputPath}, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP)
            );
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to open file_ends_early.csv");
        }
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    //Tests a valid IR file
    @Test
    void testParseFileValidIr() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        final String inputPath = "Project2/testing/test-resources/votingStreamParserTest/correct_ir.csv";
        
        try {
            final FileInputStream inputStream = new FileInputStream(inputPath);
            
            //Get the InstantRunoffSystem if the assertion holds that no exception is thrown in parsing the file
            final InstantRunoffSystem instantRunoffSystem = (InstantRunoffSystem) Assertions.assertDoesNotThrow(() ->
                VotingStreamParser.parse(
                    new InputStream[] {inputStream}, new String[] {inputPath},
                    NULL_OUTPUT,
                    NULL_OUTPUT,
                    HEADER_SYSTEM_MAP
                )
            );
            
            Assertions.assertAll(
                //Check that the number of candidates was correctly parsed
                () -> Assertions.assertEquals(instantRunoffSystem.getNumCandidates(), 4),
                //Check that the candidates themselves were correctly parsed
                () -> Assertions.assertEquals(instantRunoffSystem.getCandidates(), List.of(
                    new Candidate("Rosen", "D"),
                    new Candidate("Kleinberg", "R"),
                    new Candidate("Chou", "I"),
                    new Candidate("Royce", "L")
                )),
                //Check that the number of ballots was correctly parsed
                () -> Assertions.assertEquals(instantRunoffSystem.getNumBallots(), 6)
            );
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to open file_ends_early.csv");
        }
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    //Tests a valid OPL file
    @Test
    void testParseFileValidOpl() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        final String inputPath = "Project2/testing/test-resources/votingStreamParserTest/correct_opl.csv";
        
        try {
            final FileInputStream inputStream = new FileInputStream(inputPath);
            
            //Get the OpenPartyListSystem if the assertion holds that no exception is thrown in parsing the file
            final OpenPartyListSystem openPartyListSystem = (OpenPartyListSystem) Assertions.assertDoesNotThrow(() ->
                VotingStreamParser.parse(
                    new InputStream[] {inputStream}, new String[] {inputPath},
                    NULL_OUTPUT,
                    NULL_OUTPUT,
                    HEADER_SYSTEM_MAP
                )
            );
            
            Assertions.assertAll(
                //Check that the number of candidates was correctly parsed
                () -> Assertions.assertEquals(openPartyListSystem.getNumCandidates(), 6),
                //Check that the candidates themselves were correctly parsed
                () -> Assertions.assertEquals(openPartyListSystem.getCandidates(), List.of(
                    new Candidate("Pike", "D"),
                    new Candidate("Foster", "D"),
                    new Candidate("Deutsch", "R"),
                    new Candidate("Borg", "R"),
                    new Candidate("Jones", "R"),
                    new Candidate("Smith", "I")
                )),
                //Check that the number of seats was correctly parsed
                () -> Assertions.assertEquals(openPartyListSystem.getNumSeats(), 3),
                //Check that the number of ballots was correctly parsed
                () -> Assertions.assertEquals(openPartyListSystem.getNumBallots(), 9)
            );
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to open file_ends_early.csv");
        }
        
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    @Test
    void testParseMultipleIrFiles() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        final String inputPath1 = "Project2/testing/test-resources/votingStreamParserTest/ir_multi_part_1.csv";
        final String inputPath2 = "Project2/testing/test-resources/votingStreamParserTest/ir_multi_part_2.csv";
        final String inputPath3 = "Project2/testing/test-resources/votingStreamParserTest/ir_multi_part_3.csv";
        
        try {
            final FileInputStream inputStream1 = new FileInputStream(inputPath1);
            final FileInputStream inputStream2 = new FileInputStream(inputPath2);
            final FileInputStream inputStream3 = new FileInputStream(inputPath3);
            
            final InstantRunoffSystem instantRunoffSystem = (InstantRunoffSystem) Assertions.assertDoesNotThrow(() ->
                VotingStreamParser.parse(
                    new InputStream[] {inputStream1, inputStream2, inputStream3},
                    new String[] {inputPath1, inputPath2, inputPath3},
                    NULL_OUTPUT,
                    NULL_OUTPUT,
                    HEADER_SYSTEM_MAP
                )
            );
            
            final List<Candidate> expectedCandidates = List.of(
                new Candidate("Rosen", "D"),
                new Candidate("Kleinberg", "R"),
                new Candidate("Chou", "I"),
                new Candidate("Royce", "L")
            );
            
            Assertions.assertAll(
                //Check that the number of candidates was correctly parsed
                () -> Assertions.assertEquals(4, instantRunoffSystem.getNumCandidates()),
                //Check that the candidates themselves were correctly parsed
                () -> Assertions.assertEquals(expectedCandidates, instantRunoffSystem.getCandidates()),
                //Check that ballots from 3 different files are added up
                () -> Assertions.assertEquals(9, instantRunoffSystem.getNumBallots()),
                //Check that all candidates' ballots are parsed and distributed properly
                () -> Assertions.assertEquals(4, instantRunoffSystem.candidateBallotsMap.get(expectedCandidates.get(0)).size()),
                () -> Assertions.assertEquals(2, instantRunoffSystem.candidateBallotsMap.get(expectedCandidates.get(1)).size()),
                () -> Assertions.assertEquals(1, instantRunoffSystem.candidateBallotsMap.get(expectedCandidates.get(2)).size()),
                () -> Assertions.assertEquals(2, instantRunoffSystem.candidateBallotsMap.get(expectedCandidates.get(3)).size())
            );
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to open multiple csv files");
        }
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
    
    @Test
    void testParseMultipleOplFiles() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        final String inputPath1 = "Project2/testing/test-resources/votingStreamParserTest/opl_multi_part_1.csv";
        final String inputPath2 = "Project2/testing/test-resources/votingStreamParserTest/opl_multi_part_2.csv";
        final String inputPath3 = "Project2/testing/test-resources/votingStreamParserTest/opl_multi_part_3.csv";
        
        try {
            final FileInputStream inputStream1 = new FileInputStream(inputPath1);
            final FileInputStream inputStream2 = new FileInputStream(inputPath2);
            final FileInputStream inputStream3 = new FileInputStream(inputPath3);
            
            final OpenPartyListSystem openPartyListSystem = (OpenPartyListSystem) Assertions.assertDoesNotThrow(() ->
                VotingStreamParser.parse(
                    new InputStream[] {inputStream1, inputStream2, inputStream3},
                    new String[] {inputPath1, inputPath2, inputPath3},
                    NULL_OUTPUT,
                    NULL_OUTPUT,
                    HEADER_SYSTEM_MAP
                )
            );
            
            Assertions.assertAll(
                //Check that the number of candidates was correctly parsed
                () -> Assertions.assertEquals(6, openPartyListSystem.getNumCandidates()),
                //Check that the candidates themselves were correctly parsed
                () -> Assertions.assertEquals(openPartyListSystem.getCandidates(), List.of(
                    new Candidate("Pike", "D"),
                    new Candidate("Foster", "D"),
                    new Candidate("Deutsch", "R"),
                    new Candidate("Borg", "R"),
                    new Candidate("Jones", "R"),
                    new Candidate("Smith", "I")
                )),
                //Check that ballots from 3 different files are added up
                () -> Assertions.assertEquals(9, openPartyListSystem.getNumBallots()),
                //Check that the number of seats was correctly parsed
                () -> Assertions.assertEquals(3, openPartyListSystem.getNumSeats()),
                //Check that all parties' ballots are parsed and assigned properly
                () -> Assertions.assertEquals(3, openPartyListSystem.partyToPartyInformation.get("R").numBallots),
                () -> Assertions.assertEquals(5, openPartyListSystem.partyToPartyInformation.get("D").numBallots),
                () -> Assertions.assertEquals(1, openPartyListSystem.partyToPartyInformation.get("I").numBallots)
            );
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to open multiple csv files");
        }
        //Redirect STDOUT back to STDOUT
        System.setOut(originalSystemOut);
    }
}
