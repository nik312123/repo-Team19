package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;

final class OpenPartyListSystemTest {
    
    //Creates a null device output stream to consume and ignore all output
    private static final OutputStream NULL_OUTPUT = OutputStream.nullOutputStream();
    
    //Creates an OpenPartyListSystem with null device output streams
    private static OpenPartyListSystem createOplNullStreams() {
        return new OpenPartyListSystem(NULL_OUTPUT, NULL_OUTPUT);
    }
    
    private OpenPartyListSystemTest() {}
    
    @Test
    void testConstructor() {
        Assertions.assertAll(
            //Testing that a typical creation of OpenPartyListSystem does not throw an exception
            () -> Assertions.assertDoesNotThrow(OpenPartyListSystemTest::createOplNullStreams),
            //Testing that a the creation of OpenPartyListSystem with a null report output stream throws NullPointerException
            () -> Assertions.assertThrows(NullPointerException.class, () -> new InstantRunoffSystem(NULL_OUTPUT, null)),
            //Testing that a the creation of OpenPartyListSystem with a null audit output stream throws NullPointerException
            () -> Assertions.assertThrows(NullPointerException.class, () -> new InstantRunoffSystem(null, NULL_OUTPUT))
        );
    }
    
    @Test
    void testGetCandidateHeaderSize() {
        //Test that an open party list system has 1 line as its candidate header size
        Assertions.assertEquals(1, createOplNullStreams().getCandidateHeaderSize());
    }
    
    @Test
    void testGetBallotHeaderSize() {
        //Test that an open party list system has 2 lines as its ballot header size
        Assertions.assertEquals(2, createOplNullStreams().getBallotHeaderSize());
    }
    
    //getNumCandidates and getNumSeats is tested here indirectly as well
    @Test
    void testImportCandidatesHeader() {
        final OpenPartyListSystem openPartyListSystem = createOplNullStreams();
    
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
    
        try {
            Assertions.assertAll(
                //Test that a nonpositive candidate header results in an exception being thrown
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.importCandidatesHeader(new String[] {"0"}, 2)),
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.importCandidatesHeader(new String[] {"-2"}, 2)),
                //Test that a nonnumerical candidate header results in an exception being thrown
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.importCandidatesHeader(new String[] {"a"}, 2)),
                /*
                 * Try executing importCandidatesHeader with a positive integer, failing if it is unable to run without exception and ensure that
                 * the number of candidates was properly imported from the candidates header
                 */
                () -> Assertions.assertDoesNotThrow(() -> openPartyListSystem.importCandidatesHeader(new String[] {"2"}, 2)),
                () -> Assertions.assertEquals(2, openPartyListSystem.getNumCandidates())
            );
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    //getCandidates is tested here indirectly as well
    @Test
    void testAddCandidates() {
        final OpenPartyListSystem openPartyListSystem = createOplNullStreams();
    
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
    
        try {
            //Test example candidates array
            final Candidate[] c0c1 = new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")};
        
            Assertions.assertAll(
                //Tests issue in candidates format from lack of brackets
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.addCandidates("[C0, P0], C1 P1", 3)),
                //Tests issue in candidates format due to extra text
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.addCandidates("[C0, P0]a, [C1, P1]", 3)),
                //Tests valid typical candidates string is valid and properly parsed
                () -> Assertions.assertDoesNotThrow(() -> openPartyListSystem.addCandidates("[C0, P0], [C1, P1]", 3)),
                () -> Assertions.assertEquals(List.of(c0c1), openPartyListSystem.getCandidates()),
                //Tests valid candidates string with excess whitespace is valid and properly parsed
                () -> Assertions.assertDoesNotThrow(() -> openPartyListSystem.addCandidates("    [    C0   ,   P0  ]  , [   C1  , P1  ]   ", 3)),
                () -> Assertions.assertEquals(List.of(c0c1), openPartyListSystem.getCandidates())
            );
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    //getNumBallots is tested here indirectly as well
    @Test
    void testImportBallotsHeader() {
        final OpenPartyListSystem openPartyListSystem = createOplNullStreams();
    
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
    
        try {
            Assertions.assertAll(
                //Test that a negative number of ballots results in an exception being thrown
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.importBallotsHeader(new String[] {"1", "-2"}, 4)),
                //Test that a nonnumeric number of ballots results in an exception being thrown
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.importBallotsHeader(new String[] {"1", "a"}, 4)),
                //Test that a negative number of seats results in an exception being thrown
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.importBallotsHeader(new String[] {"-2", "1"}, 4)),
                //Test that a nonnumeric number of seats results in an exception being thrown
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.importBallotsHeader(new String[] {"a", "1"}, 4)),
                /*
                 * Try executing importBallotsHeader with an input of 0 ballots and seats, failing if it is unable to run without exception; then,
                 * ensure that the numbers were properly imported
                 */
                () -> Assertions.assertDoesNotThrow(() -> openPartyListSystem.importBallotsHeader(new String[] {"0", "0"}, 4)),
                () -> Assertions.assertEquals(0, openPartyListSystem.getNumBallots()),
                () -> Assertions.assertEquals(0, openPartyListSystem.getNumSeats()),
                /*
                 * Try executing importBallotsHeader with a positive integer of ballots and seats, failing if it is unable to run without exception;
                 * then, ensure that the numbers were properly imported
                 */
                () -> Assertions.assertDoesNotThrow(() -> openPartyListSystem.importBallotsHeader(new String[] {"2", "3"}, 4)),
                () -> Assertions.assertEquals(3, openPartyListSystem.getNumBallots()),
                () -> Assertions.assertEquals(2, openPartyListSystem.getNumSeats())
            );
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    @Test
    void testAddBallot() {
        final OpenPartyListSystem openPartyListSystem = createOplNullStreams();
    
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
    
        try {
            Method parseBallotTmp = null;
            try {
                parseBallotTmp = OpenPartyListSystem.class.getDeclaredMethod("parseBallot", String.class, int.class);
                parseBallotTmp.setAccessible(true);
            }
            catch(NoSuchMethodException e) {
                Assertions.fail("Unable to retrieve parseBallot from OpenPartyListSystem");
            }
            final Method parseBallot = parseBallotTmp;
        
            try {
                openPartyListSystem.importCandidatesHeader(new String[] {"5"}, 2);
                openPartyListSystem.addCandidates("[C0, P0], [C1, P1], [C2, P2], [C3, P3], [C4, P4]", 3);
            }
            catch(ParseException e) {
                Assertions.fail("Unable to properly set up the candidates for the test");
            }
        
            Assertions.assertAll(
                //Test the case where there are not enough values provided
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.addBallot(1, ",1,,", 5)),
                //Test the case where no ballot is ranked
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.addBallot(1, ",,,,", 5)),
                //Test the case where there is a rank that is not one
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.addBallot(1, ",,,2,", 5)),
                //Test the case where there is a rank that is not an integer
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.addBallot(1, ",,a,,", 5)),
                //Test the case where there are multiple rankings
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.addBallot(1, ",2,1,,", 5)),
                //Test the case where there are multiple 1s
                () -> Assertions.assertThrows(ParseException.class, () -> openPartyListSystem.addBallot(1, "1,,1,,", 5)),
                //Testing a valid ballot
                () -> Assertions.assertEquals(
                    new Candidate("C3", "P3"),
                    parseBallot.invoke(openPartyListSystem, ",,,1,", 5)
                )
            );
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    @Test
    void testGetName() {
        //Test that the name returned is "Open Party List Voting"
        Assertions.assertEquals("Open Party List Voting", createOplNullStreams().getName());
    }
    
    @Test
    void testGetShortName() {
        //Test that the short name returned is "OPL"
        Assertions.assertEquals("OPL", createOplNullStreams().getShortName());
    }
    
    @Test
    void testToString() {
        final OpenPartyListSystem openPartyListSystem = createOplNullStreams();
    
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
    
        try {
            //Put required sample data
            try {
                openPartyListSystem.addCandidates("[C0, P0], [C1, P1], [C2, P2], [C3, P3], [C4, P4]", 3);
                openPartyListSystem.importBallotsHeader(new String[] {"2", "143"}, 4);
            }
            catch(ParseException e) {
                Assertions.fail("Unable to properly set up the candidates for the test");
            }
        
            /*
             * Test that InstantRunoffSystem's toString produces output like "InstantRunoffSystem{candidates=[candidates], numBallots=<numBallots>}"
             * where [candidates] is replaced by the string form of the candidates array and [numBallots] is replaced by the number of ballots
             */
            Assertions.assertEquals(
                "OpenPartyListSystem{candidates=[C0 (P0), C1 (P1), C2 (P2), C3 (P3), C4 (P4)], numBallots=143}",
                openPartyListSystem.toString()
            );
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
}
