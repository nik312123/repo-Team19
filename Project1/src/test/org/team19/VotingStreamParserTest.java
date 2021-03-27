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
            () -> VotingStreamParser.throwParseException("Sample message", 92)
        );
        
        Assertions.assertAll(
            /*
             * Check that the ParseException's error message matches the format "Error on line [lineNumber]: [message]", replacing [lineNumber] and
             * [message] with the corresponding parameters to throwParseException
             */
            () -> Assertions.assertEquals("Error on line 92: Sample message", parseException.getMessage()),
            
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
        
        try {
            final FileInputStream inputStream = new FileInputStream("Project1/testing/test-resources/votingStreamParserTest/file_ends_early.csv");
            Assertions.assertThrows(ParseException.class, () -> VotingStreamParser.parse(inputStream, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP));
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
        
        try {
            final FileInputStream inputStream = new FileInputStream("Project1/testing/test-resources/votingStreamParserTest/invalid_header.csv");
            Assertions.assertThrows(ParseException.class, () -> VotingStreamParser.parse(inputStream, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP));
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
        
        try {
            final FileInputStream inputStream = new FileInputStream(
                "Project1/testing/test-resources/votingStreamParserTest/invalid_candidates_header.csv"
            );
            Assertions.assertThrows(ParseException.class, () -> VotingStreamParser.parse(inputStream, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP));
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
        
        try {
            final FileInputStream inputStream = new FileInputStream(
                "Project1/testing/test-resources/votingStreamParserTest/invalid_candidates.csv"
            );
            Assertions.assertThrows(ParseException.class, () -> VotingStreamParser.parse(inputStream, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP));
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
        
        try {
            final FileInputStream inputStream = new FileInputStream(
                "Project1/testing/test-resources/votingStreamParserTest/invalid_ballot_header.csv"
            );
            Assertions.assertThrows(ParseException.class, () -> VotingStreamParser.parse(inputStream, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP));
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
        
        try {
            final FileInputStream inputStream = new FileInputStream(
                "Project1/testing/test-resources/votingStreamParserTest/invalid_ballot_line.csv"
            );
            Assertions.assertThrows(ParseException.class, () -> VotingStreamParser.parse(inputStream, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP));
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
        
        try {
            final FileInputStream inputStream = new FileInputStream(
                "Project1/testing/test-resources/votingStreamParserTest/correct_ir.csv"
            );
            
            //Get the InstantRunoffSystem if the assertion holds that no exception is thrown in parsing the file
            final InstantRunoffSystem instantRunoffSystem = (InstantRunoffSystem) Assertions.assertDoesNotThrow(() ->
                VotingStreamParser.parse(inputStream, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP)
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
        
        try {
            final FileInputStream inputStream = new FileInputStream(
                "Project1/testing/test-resources/votingStreamParserTest/correct_opl.csv"
            );
            
            //Get the OpenPartyListSystem if the assertion holds that no exception is thrown in parsing the file
            final OpenPartyListSystem openPartyListSystem = (OpenPartyListSystem) Assertions.assertDoesNotThrow(() ->
                VotingStreamParser.parse(inputStream, NULL_OUTPUT, NULL_OUTPUT, HEADER_SYSTEM_MAP)
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
    
}
