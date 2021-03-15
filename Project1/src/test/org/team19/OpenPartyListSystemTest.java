/*
 * File name:
 * OpenPartyListSystemTest.java
 *
 * Author:
 * Nikunj Chawla and Aaron Kandikatla
 *
 * Purpose:
 * Tests the OpenPartyListSystem class
 */

package org.team19;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
    
    @Test
    void testGetRemainingBallots(){
        
        OpenPartyListSystem opl = new OpenPartyListSystem(OutputStream.nullOutputStream(),OutputStream.nullOutputStream());
        
        OpenPartyListSystem.PartyInformation testParty = new OpenPartyListSystem.PartyInformation();
        
        testParty.numSeats = 0;
        testParty.numBallots = 10;
        
        // Tests the case where a party has been allocated 0 seats.
        // No ballots taken out from initial allocation
        assertEquals("10", opl.getRemainingBallots(testParty));
        
        testParty.numSeats = 2;
        testParty.remainder = new Fraction(5,1);
        
        // Tests the case where a party has > 0 seats and remaining ballots is a whole number (numerator of 1)
        assertEquals("5", opl.getRemainingBallots(testParty));
        
        testParty.remainder = new Fraction(4,3);
        
        // Tests the case where a party has > 0 seats and remaining ballots is not a whole number
        assertEquals("1.3333",opl.getRemainingBallots(testParty));
        
    }
    
    @Test
    void testAllocateInitialSeatsTypical() {
        
        Class<?> partyInformation = null;
        try {
            partyInformation = Class.forName("org.team19.OpenPartyListSystem$PartyInformation");
            
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        assert partyInformation != null;
        
        Constructor<?> partyInformationConstructor = null;
        try {
            partyInformationConstructor = partyInformation.getDeclaredConstructor();
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert partyInformationConstructor != null;
        partyInformationConstructor.setAccessible(true);
        
        OpenPartyListSystem.PartyInformation partyD = null;
        OpenPartyListSystem.PartyInformation partyR = null;
        OpenPartyListSystem.PartyInformation partyI = null;
        
        try {
            try {
                partyD = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            partyR = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            partyI = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        Candidate foster_d = new Candidate("Foster", "D");
        Candidate pike_d = new Candidate("Pike", "D");
        
        Candidate deutsch_r = new Candidate("Deutsch", "R");
        Candidate jones_r = new Candidate("Jones", "R");
        Candidate borg_r = new Candidate("Borg", "R");
        
        Candidate smith_i = new Candidate("Smith", "I");
        
        assert partyD != null;
        
        partyD.numCandidates = 2;
        partyD.numBallots = 5;
        
        partyD.orderedCandidateBallots = new ArrayList<>();
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(foster_d, 3));
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(pike_d, 2));
        
        assert partyR != null;
        
        partyR.numCandidates = 3;
        partyR.numBallots = 3;
        
        partyR.orderedCandidateBallots = new ArrayList<>();
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(deutsch_r, 0));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(jones_r, 1));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(borg_r, 2));
        
        assert partyI != null;
        
        partyI.numCandidates = 1;
        partyI.numBallots = 1;
        
        partyI.orderedCandidateBallots = new ArrayList<>();
        partyI.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(smith_i, 1));
        
        OpenPartyListSystem opl = null;
        try {
            opl = new OpenPartyListSystem(
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit1.txt"),
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsReport1.txt")
            );
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        assert opl != null;
        opl.numSeats = 3;
        opl.numBallots = 9;
        
        opl.partiesToPartyInformation = new LinkedHashMap<>();
        opl.partiesToPartyInformation.put("D", partyD);
        opl.partiesToPartyInformation.put("R", partyR);
        opl.partiesToPartyInformation.put("I", partyI);
        
        opl.partyToCandidateCounts = new LinkedHashMap<>();
        
        opl.partyToCandidateCounts.put("D", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("D").put(foster_d, 3);
        opl.partyToCandidateCounts.get("D").put(pike_d, 2);
        
        opl.partyToCandidateCounts.put("R", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("R").put(deutsch_r, 0);
        opl.partyToCandidateCounts.get("R").put(jones_r, 1);
        opl.partyToCandidateCounts.get("R").put(borg_r, 2);
        
        opl.partyToCandidateCounts.put("I", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("I").put(smith_i, 1);
        
        Pair<Integer, Set<String>> returnValue = opl.allocateInitialSeats(new Fraction(opl.numBallots, opl.numSeats));
        
        // General test case with standard conditions
        // There should be only one seat remaining after initial allocation
        assertEquals(1 + " seat remaining", returnValue.getKey() + " seat remaining");
        // All 3 parties should still have enough candidates for more potential seats
        assertEquals(new HashSet<>(Arrays.asList("D", "R", "I")), returnValue.getValue());
        
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit1.txt".replace('/', FILE_SEP)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsReport1.txt".replace('/', FILE_SEP)).delete();
    }
    
    @Test
    void testAllocateInitialSeatsTypicalOutput(){
    
        Class<?> partyInformation = null;
        try {
            partyInformation = Class.forName("org.team19.OpenPartyListSystem$PartyInformation");
        
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    
        assert partyInformation != null;
    
        Constructor<?> partyInformationConstructor = null;
        try {
            partyInformationConstructor = partyInformation.getDeclaredConstructor();
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert partyInformationConstructor != null;
        partyInformationConstructor.setAccessible(true);
    
        OpenPartyListSystem.PartyInformation partyD = null;
        OpenPartyListSystem.PartyInformation partyR = null;
        OpenPartyListSystem.PartyInformation partyI = null;
    
        try {
            try {
                partyD = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            partyR = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            partyI = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
        
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    
        Candidate foster_d = new Candidate("Foster", "D");
        Candidate pike_d = new Candidate("Pike", "D");
    
        Candidate deutsch_r = new Candidate("Deutsch", "R");
        Candidate jones_r = new Candidate("Jones", "R");
        Candidate borg_r = new Candidate("Borg", "R");
    
        Candidate smith_i = new Candidate("Smith", "I");
    
        assert partyD != null;
    
        partyD.numCandidates = 2;
        partyD.numBallots = 5;
    
        partyD.orderedCandidateBallots = new ArrayList<>();
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(foster_d, 3));
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(pike_d, 2));
    
        assert partyR != null;
    
        partyR.numCandidates = 3;
        partyR.numBallots = 3;
    
        partyR.orderedCandidateBallots = new ArrayList<>();
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(deutsch_r, 0));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(jones_r, 1));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(borg_r, 2));
    
        assert partyI != null;
    
        partyI.numCandidates = 1;
        partyI.numBallots = 1;
    
        partyI.orderedCandidateBallots = new ArrayList<>();
        partyI.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(smith_i, 1));
    
        OpenPartyListSystem opl = null;
        try {
            opl = new OpenPartyListSystem(
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit1.txt"),
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsReport1.txt")
            );
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        assert opl != null;
        opl.numSeats = 3;
        opl.numBallots = 9;
    
        opl.partiesToPartyInformation = new LinkedHashMap<>();
        opl.partiesToPartyInformation.put("D", partyD);
        opl.partiesToPartyInformation.put("R", partyR);
        opl.partiesToPartyInformation.put("I", partyI);
    
        opl.partyToCandidateCounts = new LinkedHashMap<>();
    
        opl.partyToCandidateCounts.put("D", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("D").put(foster_d, 3);
        opl.partyToCandidateCounts.get("D").put(pike_d, 2);
    
        opl.partyToCandidateCounts.put("R", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("R").put(deutsch_r, 0);
        opl.partyToCandidateCounts.get("R").put(jones_r, 1);
        opl.partyToCandidateCounts.get("R").put(borg_r, 2);
    
        opl.partyToCandidateCounts.put("I", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("I").put(smith_i, 1);
    
        Pair<Integer, Set<String>> returnValue = opl.allocateInitialSeats(new Fraction(opl.numBallots, opl.numSeats));
        
        // Comparing expected output vs actual output
        assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsTypical.txt"),
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit1.txt"))
        );
    
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit1.txt".replace('/', FILE_SEP)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsReport1.txt".replace('/', FILE_SEP)).delete();
    }
    
    @Test
    void testAllocateInitialSeatsSingleCandidateHasAllVotes() {
        Class<?> partyInformation = null;
        try {
            partyInformation = Class.forName("org.team19.OpenPartyListSystem$PartyInformation");
            
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        assert partyInformation != null;
        
        Constructor<?> partyInformationConstructor = null;
        try {
            partyInformationConstructor = partyInformation.getDeclaredConstructor();
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert partyInformationConstructor != null;
        partyInformationConstructor.setAccessible(true);
        
        OpenPartyListSystem.PartyInformation partyD = null;
        OpenPartyListSystem.PartyInformation partyR = null;
        OpenPartyListSystem.PartyInformation partyI = null;
        
        try {
            try {
                partyD = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            partyR = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            partyI = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        Candidate foster_d = new Candidate("Foster", "D");
        Candidate pike_d = new Candidate("Pike", "D");
        
        Candidate deutsch_r = new Candidate("Deutsch", "R");
        Candidate jones_r = new Candidate("Jones", "R");
        Candidate borg_r = new Candidate("Borg", "R");
        
        Candidate smith_i = new Candidate("Smith", "I");
        
        assert partyD != null;
        
        partyD.numCandidates = 2;
        partyD.numBallots = 0;
        
        partyD.orderedCandidateBallots = new ArrayList<>();
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(foster_d, 0));
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(pike_d, 0));
        
        assert partyR != null;
        
        partyR.numCandidates = 3;
        partyR.numBallots = 0;
        
        partyR.orderedCandidateBallots = new ArrayList<>();
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(deutsch_r, 0));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(jones_r, 0));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(borg_r, 0));
        
        assert partyI != null;
        
        partyI.numCandidates = 1;
        partyI.numBallots = 100;
        
        partyI.orderedCandidateBallots = new ArrayList<>();
        partyI.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(smith_i, 100));
        
        OpenPartyListSystem opl = null;
        try {
            opl = new OpenPartyListSystem(
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit2.txt"),
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsReport2.txt")
            );
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        assert opl != null;
        opl.numSeats = 4;
        opl.numBallots = 100;
        
        opl.partiesToPartyInformation = new LinkedHashMap<>();
        opl.partiesToPartyInformation.put("D", partyD);
        opl.partiesToPartyInformation.put("R", partyR);
        opl.partiesToPartyInformation.put("I", partyI);
        
        opl.partyToCandidateCounts = new LinkedHashMap<>();
        
        opl.partyToCandidateCounts.put("D", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("D").put(foster_d, 0);
        opl.partyToCandidateCounts.get("D").put(pike_d, 0);
        
        opl.partyToCandidateCounts.put("R", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("R").put(deutsch_r, 0);
        opl.partyToCandidateCounts.get("R").put(jones_r, 0);
        opl.partyToCandidateCounts.get("R").put(borg_r, 0);
        
        opl.partyToCandidateCounts.put("I", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("I").put(smith_i, 100);
        
        Pair<Integer, Set<String>> returnValue = opl.allocateInitialSeats(new Fraction(opl.numBallots, opl.numSeats));
        
        // Test to ensure partyI only receives 1 seat b/c it only has one candidate
        // despite receiving all the votes
        assertEquals(1,partyI.numSeats);
        
        // General test case with standard conditions
        // There should be only three seat remaining after initial allocation
        assertEquals(3 + " seats remaining", returnValue.getKey() + " seats remaining");
        // Only 2 parties should have additional candidates after initial allocation.
        assertEquals(new HashSet<>(Arrays.asList("D", "R")), returnValue.getValue());
        
        
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit2.txt".replace('/',
            FILE_SEP)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsReport2.txt".replace('/', FILE_SEP)).delete();
    }
    
    @Test
    void testAllocateInitialSeatsSingleCandidateHasAllVotesOutput(){
        Class<?> partyInformation = null;
        try {
            partyInformation = Class.forName("org.team19.OpenPartyListSystem$PartyInformation");
        
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    
        assert partyInformation != null;
    
        Constructor<?> partyInformationConstructor = null;
        try {
            partyInformationConstructor = partyInformation.getDeclaredConstructor();
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert partyInformationConstructor != null;
        partyInformationConstructor.setAccessible(true);
    
        OpenPartyListSystem.PartyInformation partyD = null;
        OpenPartyListSystem.PartyInformation partyR = null;
        OpenPartyListSystem.PartyInformation partyI = null;
    
        try {
            try {
                partyD = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            partyR = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            partyI = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
        
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    
        Candidate foster_d = new Candidate("Foster", "D");
        Candidate pike_d = new Candidate("Pike", "D");
    
        Candidate deutsch_r = new Candidate("Deutsch", "R");
        Candidate jones_r = new Candidate("Jones", "R");
        Candidate borg_r = new Candidate("Borg", "R");
    
        Candidate smith_i = new Candidate("Smith", "I");
    
        assert partyD != null;
    
        partyD.numCandidates = 2;
        partyD.numBallots = 0;
    
        partyD.orderedCandidateBallots = new ArrayList<>();
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(foster_d, 0));
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(pike_d, 0));
    
        assert partyR != null;
    
        partyR.numCandidates = 3;
        partyR.numBallots = 0;
    
        partyR.orderedCandidateBallots = new ArrayList<>();
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(deutsch_r, 0));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(jones_r, 0));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(borg_r, 0));
    
        assert partyI != null;
    
        partyI.numCandidates = 1;
        partyI.numBallots = 100;
    
        partyI.orderedCandidateBallots = new ArrayList<>();
        partyI.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(smith_i, 100));
    
        OpenPartyListSystem opl = null;
        try {
            opl = new OpenPartyListSystem(
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit2.txt"),
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsReport2.txt")
            );
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        assert opl != null;
        opl.numSeats = 4;
        opl.numBallots = 100;
    
        opl.partiesToPartyInformation = new LinkedHashMap<>();
        opl.partiesToPartyInformation.put("D", partyD);
        opl.partiesToPartyInformation.put("R", partyR);
        opl.partiesToPartyInformation.put("I", partyI);
    
        opl.partyToCandidateCounts = new LinkedHashMap<>();
    
        opl.partyToCandidateCounts.put("D", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("D").put(foster_d, 0);
        opl.partyToCandidateCounts.get("D").put(pike_d, 0);
    
        opl.partyToCandidateCounts.put("R", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("R").put(deutsch_r, 0);
        opl.partyToCandidateCounts.get("R").put(jones_r, 0);
        opl.partyToCandidateCounts.get("R").put(borg_r, 0);
    
        opl.partyToCandidateCounts.put("I", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("I").put(smith_i, 100);
    
        Pair<Integer, Set<String>> returnValue = opl.allocateInitialSeats(new Fraction(opl.numBallots, opl.numSeats));
        
        // Comparing expected output vs actual output
        assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsSingleCandidateHasAllVotes.txt"),
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit2.txt"))
        );
    
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit2.txt".replace('/',
            FILE_SEP)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsReport2.txt".replace('/', FILE_SEP)).delete();
    }
    
    @Test
    void testAllocateInitialSeatsBallotsNotEvenlyDivisibleByQuota() {
        Class<?> partyInformation = null;
        try {
            partyInformation = Class.forName("org.team19.OpenPartyListSystem$PartyInformation");
            
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        assert partyInformation != null;
        
        Constructor<?> partyInformationConstructor = null;
        try {
            partyInformationConstructor = partyInformation.getDeclaredConstructor();
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert partyInformationConstructor != null;
        partyInformationConstructor.setAccessible(true);
        
        OpenPartyListSystem.PartyInformation partyD = null;
        OpenPartyListSystem.PartyInformation partyR = null;
        OpenPartyListSystem.PartyInformation partyI = null;
        
        try {
            try {
                partyD = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            partyR = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            partyI = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        Candidate foster_d = new Candidate("Foster", "D");
        Candidate pike_d = new Candidate("Pike", "D");
        
        Candidate deutsch_r = new Candidate("Deutsch", "R");
        Candidate jones_r = new Candidate("Jones", "R");
        Candidate borg_r = new Candidate("Borg", "R");
        
        Candidate smith_i = new Candidate("Smith", "I");
        
        assert partyD != null;
        
        partyD.numCandidates = 2;
        partyD.numBallots = 3;
        
        partyD.orderedCandidateBallots = new ArrayList<>();
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(foster_d, 1));
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(pike_d, 2));
        
        assert partyR != null;
        
        partyR.numCandidates = 3;
        partyR.numBallots = 5;
        
        partyR.orderedCandidateBallots = new ArrayList<>();
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(deutsch_r, 1));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(jones_r, 2));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(borg_r, 2));
        
        assert partyI != null;
        
        partyI.numCandidates = 1;
        partyI.numBallots = 5;
        
        partyI.orderedCandidateBallots = new ArrayList<>();
        partyI.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(smith_i, 5));
        
        OpenPartyListSystem opl = null;
        try {
            opl = new OpenPartyListSystem(
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit3.txt"),
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsReport3.txt")
            );
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        assert opl != null;
        opl.numSeats = 3;
        opl.numBallots = 13;
        
        opl.partiesToPartyInformation = new LinkedHashMap<>();
        opl.partiesToPartyInformation.put("D", partyD);
        opl.partiesToPartyInformation.put("R", partyR);
        opl.partiesToPartyInformation.put("I", partyI);
        
        opl.partyToCandidateCounts = new LinkedHashMap<>();
        
        opl.partyToCandidateCounts.put("D", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("D").put(foster_d, 1);
        opl.partyToCandidateCounts.get("D").put(pike_d, 2);
        
        opl.partyToCandidateCounts.put("R", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("R").put(deutsch_r, 1);
        opl.partyToCandidateCounts.get("R").put(jones_r, 2);
        opl.partyToCandidateCounts.get("R").put(borg_r, 2);
        
        opl.partyToCandidateCounts.put("I", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("I").put(smith_i, 5);
        
        Pair<Integer, Set<String>> returnValue = opl.allocateInitialSeats(new Fraction(opl.numBallots, opl.numSeats));
        
        
        // Comparing expected output vs actual output
        assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsBallotsNotEvenlyDivisibleByQuota.txt"),
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit3.txt"))
        );
        
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsAudit3.txt".replace('/',
            FILE_SEP)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateInitialSeatsReport3.txt".replace('/', FILE_SEP)).delete();
    }
    
    @Test
    void testAllocateRemainingSeatsTypical() {
        
        Class<?> partyInformation = null;
        try {
            partyInformation = Class.forName("org.team19.OpenPartyListSystem$PartyInformation");
            
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        assert partyInformation != null;
        
        Constructor<?> partyInformationConstructor = null;
        try {
            partyInformationConstructor = partyInformation.getDeclaredConstructor();
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert partyInformationConstructor != null;
        partyInformationConstructor.setAccessible(true);
        
        OpenPartyListSystem.PartyInformation partyD = null;
        OpenPartyListSystem.PartyInformation partyR = null;
        OpenPartyListSystem.PartyInformation partyI = null;
        
        try {
            try {
                partyD = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            partyR = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            partyI = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        Candidate foster_d = new Candidate("Foster", "D");
        Candidate pike_d = new Candidate("Pike", "D");
        
        Candidate deutsch_r = new Candidate("Deutsch", "R");
        Candidate jones_r = new Candidate("Jones", "R");
        Candidate borg_r = new Candidate("Borg", "R");
        
        Candidate smith_i = new Candidate("Smith", "I");
        
        assert partyD != null;
        
        partyD.numCandidates = 2;
        partyD.numBallots = 5;
        
        partyD.orderedCandidateBallots = new ArrayList<>();
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(foster_d, 3));
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(pike_d, 2));
        
        assert partyR != null;
        
        partyR.numCandidates = 3;
        partyR.numBallots = 3;
        
        partyR.orderedCandidateBallots = new ArrayList<>();
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(deutsch_r, 0));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(jones_r, 1));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(borg_r, 2));
        
        assert partyI != null;
        
        partyI.numCandidates = 1;
        partyI.numBallots = 1;
        
        partyI.orderedCandidateBallots = new ArrayList<>();
        partyI.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(smith_i, 1));
        
        OpenPartyListSystem opl = null;
        try {
            opl = new OpenPartyListSystem(
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsAudit1.txt"),
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsReport1.txt")
            );
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        assert opl != null;
        opl.numSeats = 3;
        opl.numBallots = 9;
        
        opl.partiesToPartyInformation = new LinkedHashMap<>();
        opl.partiesToPartyInformation.put("D", partyD);
        opl.partiesToPartyInformation.put("R", partyR);
        opl.partiesToPartyInformation.put("I", partyI);
        
        opl.partyToCandidateCounts = new LinkedHashMap<>();
        
        opl.partyToCandidateCounts.put("D", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("D").put(foster_d, 3);
        opl.partyToCandidateCounts.get("D").put(pike_d, 2);
        
        opl.partyToCandidateCounts.put("R", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("R").put(deutsch_r, 0);
        opl.partyToCandidateCounts.get("R").put(jones_r, 1);
        opl.partyToCandidateCounts.get("R").put(borg_r, 2);
        
        opl.partyToCandidateCounts.put("I", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("I").put(smith_i, 1);
        
        Pair<Integer, Set<String>> initialAllocationResults = opl.allocateInitialSeats(new Fraction(opl.numBallots, opl.numSeats));
        
        Integer numSeatsRemaining = initialAllocationResults.getFirst();
        Set<String> remainingParties = initialAllocationResults.getSecond();
        
        opl.allocateRemainingSeats(numSeatsRemaining, remainingParties);
        
        // Comparing expected output vs actual output
        assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsTypical.txt"),
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsAudit1.txt"))
        );
        
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsAudit1.txt".replace('/', FILE_SEP)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsReport1.txt".replace('/', FILE_SEP)).delete();
    }
    
    @Test
    void testAllocateRemainingSeatsSingleCandidateHasAllVotes() {
        Class<?> partyInformation = null;
        try {
            partyInformation = Class.forName("org.team19.OpenPartyListSystem$PartyInformation");
            
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        assert partyInformation != null;
        
        Constructor<?> partyInformationConstructor = null;
        try {
            partyInformationConstructor = partyInformation.getDeclaredConstructor();
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert partyInformationConstructor != null;
        partyInformationConstructor.setAccessible(true);
        
        OpenPartyListSystem.PartyInformation partyD = null;
        OpenPartyListSystem.PartyInformation partyR = null;
        OpenPartyListSystem.PartyInformation partyI = null;
        
        try {
            try {
                partyD = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            partyR = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            partyI = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        Candidate foster_d = new Candidate("Foster", "D");
        Candidate pike_d = new Candidate("Pike", "D");
        
        Candidate deutsch_r = new Candidate("Deutsch", "R");
        Candidate jones_r = new Candidate("Jones", "R");
        Candidate borg_r = new Candidate("Borg", "R");
        
        Candidate smith_i = new Candidate("Smith", "I");
        
        assert partyD != null;
        
        partyD.numCandidates = 2;
        partyD.numBallots = 0;
        
        partyD.orderedCandidateBallots = new ArrayList<>();
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(foster_d, 0));
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(pike_d, 0));
        
        assert partyR != null;
        
        partyR.numCandidates = 3;
        partyR.numBallots = 0;
        
        partyR.orderedCandidateBallots = new ArrayList<>();
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(deutsch_r, 0));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(jones_r, 0));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(borg_r, 0));
        
        assert partyI != null;
        
        partyI.numCandidates = 1;
        partyI.numBallots = 100;
        
        partyI.orderedCandidateBallots = new ArrayList<>();
        partyI.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(smith_i, 100));
        
        OpenPartyListSystem opl = null;
        try {
            opl = new OpenPartyListSystem(
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsAudit2.txt"),
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsReport2.txt")
            );
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        assert opl != null;
        opl.numSeats = 5;
        opl.numBallots = 100;
        
        opl.partiesToPartyInformation = new LinkedHashMap<>();
        opl.partiesToPartyInformation.put("D", partyD);
        opl.partiesToPartyInformation.put("R", partyR);
        opl.partiesToPartyInformation.put("I", partyI);
        
        opl.partyToCandidateCounts = new LinkedHashMap<>();
        
        opl.partyToCandidateCounts.put("D", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("D").put(foster_d, 0);
        opl.partyToCandidateCounts.get("D").put(pike_d, 0);
        
        opl.partyToCandidateCounts.put("R", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("R").put(deutsch_r, 0);
        opl.partyToCandidateCounts.get("R").put(jones_r, 0);
        opl.partyToCandidateCounts.get("R").put(borg_r, 0);
        
        opl.partyToCandidateCounts.put("I", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("I").put(smith_i, 100);
        
        Pair<Integer, Set<String>> initialAllocationResults = opl.allocateInitialSeats(new Fraction(opl.numBallots, opl.numSeats));
        
        Integer numSeatsRemaining = initialAllocationResults.getFirst();
        Set<String> remainingParties = initialAllocationResults.getSecond();
        
        opl.allocateRemainingSeats(numSeatsRemaining, remainingParties);
        
        // Test to ensure all remaining seats have been allocated
        assertEquals(opl.numSeats, partyD.numSeats + partyR.numSeats + partyI.numSeats);
        
        // Test to check that the parties with equal votes, received the same number of seats since after
        // initial allocation there are 4 seats remaining
        assertEquals(2, partyD.numSeats);
        assertEquals(2, partyR.numSeats);
        
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsAudit2.txt".replace('/', FILE_SEP)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsReport2.txt".replace('/', FILE_SEP)).delete();
        
    }
    
    @Test
    void testAllocateRemainingSeatsMoreSeatsThanCandidates() {
        
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        
        Class<?> partyInformation = null;
        try {
            partyInformation = Class.forName("org.team19.OpenPartyListSystem$PartyInformation");
            
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        assert partyInformation != null;
        
        Constructor<?> partyInformationConstructor = null;
        try {
            partyInformationConstructor = partyInformation.getDeclaredConstructor();
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert partyInformationConstructor != null;
        partyInformationConstructor.setAccessible(true);
        
        OpenPartyListSystem.PartyInformation partyD = null;
        OpenPartyListSystem.PartyInformation partyR = null;
        OpenPartyListSystem.PartyInformation partyI = null;
        
        try {
            try {
                partyD = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            partyR = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            partyI = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        Candidate foster_d = new Candidate("Foster", "D");
        Candidate pike_d = new Candidate("Pike", "D");
        
        Candidate deutsch_r = new Candidate("Deutsch", "R");
        Candidate jones_r = new Candidate("Jones", "R");
        Candidate borg_r = new Candidate("Borg", "R");
        
        Candidate smith_i = new Candidate("Smith", "I");
        
        assert partyD != null;
        
        partyD.numCandidates = 2;
        partyD.numBallots = 5;
        
        partyD.orderedCandidateBallots = new ArrayList<>();
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(foster_d, 3));
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(pike_d, 2));
        
        assert partyR != null;
        
        partyR.numCandidates = 3;
        partyR.numBallots = 3;
        
        partyR.orderedCandidateBallots = new ArrayList<>();
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(deutsch_r, 0));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(jones_r, 1));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(borg_r, 2));
        
        assert partyI != null;
        
        partyI.numCandidates = 1;
        partyI.numBallots = 1;
        
        partyI.orderedCandidateBallots = new ArrayList<>();
        partyI.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(smith_i, 1));
        
        OpenPartyListSystem opl = null;
        try {
            opl = new OpenPartyListSystem(
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsAudit3.txt"),
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsReport3.txt")
            );
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        assert opl != null;
        opl.numSeats = 10;
        opl.numBallots = 9;
        
        opl.partiesToPartyInformation = new LinkedHashMap<>();
        opl.partiesToPartyInformation.put("D", partyD);
        opl.partiesToPartyInformation.put("R", partyR);
        opl.partiesToPartyInformation.put("I", partyI);
        
        opl.partyToCandidateCounts = new LinkedHashMap<>();
        
        opl.partyToCandidateCounts.put("D", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("D").put(foster_d, 3);
        opl.partyToCandidateCounts.get("D").put(pike_d, 2);
        
        opl.partyToCandidateCounts.put("R", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("R").put(deutsch_r, 0);
        opl.partyToCandidateCounts.get("R").put(jones_r, 1);
        opl.partyToCandidateCounts.get("R").put(borg_r, 2);
        
        opl.partyToCandidateCounts.put("I", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("I").put(smith_i, 1);
        
        Pair<Integer, Set<String>> initialAllocationResults = opl.allocateInitialSeats(new Fraction(opl.numBallots, opl.numSeats));
        
        Integer numSeatsRemaining = initialAllocationResults.getFirst();
        Set<String> remainingParties = initialAllocationResults.getSecond();
        
        opl.allocateRemainingSeats(numSeatsRemaining, remainingParties);
        
        // Tests to check that every candidate has a seat
        assertEquals(partyR.numSeats, partyR.numCandidates);
        assertEquals(partyD.numSeats, partyD.numCandidates);
        assertEquals(partyI.numSeats, partyI.numCandidates);
        
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsAudit3.txt".replace('/', FILE_SEP)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsReport3.txt".replace('/', FILE_SEP)).delete();
        
        System.setOut(originalSystemOut);
    }
    
    @Test
    void allocateRemainingSeatsMoreSeatsThanCandidatesOutput(){
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
    
        Class<?> partyInformation = null;
        try {
            partyInformation = Class.forName("org.team19.OpenPartyListSystem$PartyInformation");
        
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    
        assert partyInformation != null;
    
        Constructor<?> partyInformationConstructor = null;
        try {
            partyInformationConstructor = partyInformation.getDeclaredConstructor();
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert partyInformationConstructor != null;
        partyInformationConstructor.setAccessible(true);
    
        OpenPartyListSystem.PartyInformation partyD = null;
        OpenPartyListSystem.PartyInformation partyR = null;
        OpenPartyListSystem.PartyInformation partyI = null;
    
        try {
            try {
                partyD = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            partyR = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            partyI = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
        
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    
        Candidate foster_d = new Candidate("Foster", "D");
        Candidate pike_d = new Candidate("Pike", "D");
    
        Candidate deutsch_r = new Candidate("Deutsch", "R");
        Candidate jones_r = new Candidate("Jones", "R");
        Candidate borg_r = new Candidate("Borg", "R");
    
        Candidate smith_i = new Candidate("Smith", "I");
    
        assert partyD != null;
    
        partyD.numCandidates = 2;
        partyD.numBallots = 5;
    
        partyD.orderedCandidateBallots = new ArrayList<>();
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(foster_d, 3));
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(pike_d, 2));
    
        assert partyR != null;
    
        partyR.numCandidates = 3;
        partyR.numBallots = 3;
    
        partyR.orderedCandidateBallots = new ArrayList<>();
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(deutsch_r, 0));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(jones_r, 1));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(borg_r, 2));
    
        assert partyI != null;
    
        partyI.numCandidates = 1;
        partyI.numBallots = 1;
    
        partyI.orderedCandidateBallots = new ArrayList<>();
        partyI.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(smith_i, 1));
    
        OpenPartyListSystem opl = null;
        try {
            opl = new OpenPartyListSystem(
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsAudit3.txt"),
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsReport3.txt")
            );
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        assert opl != null;
        opl.numSeats = 10;
        opl.numBallots = 9;
    
        opl.partiesToPartyInformation = new LinkedHashMap<>();
        opl.partiesToPartyInformation.put("D", partyD);
        opl.partiesToPartyInformation.put("R", partyR);
        opl.partiesToPartyInformation.put("I", partyI);
    
        opl.partyToCandidateCounts = new LinkedHashMap<>();
    
        opl.partyToCandidateCounts.put("D", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("D").put(foster_d, 3);
        opl.partyToCandidateCounts.get("D").put(pike_d, 2);
    
        opl.partyToCandidateCounts.put("R", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("R").put(deutsch_r, 0);
        opl.partyToCandidateCounts.get("R").put(jones_r, 1);
        opl.partyToCandidateCounts.get("R").put(borg_r, 2);
    
        opl.partyToCandidateCounts.put("I", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("I").put(smith_i, 1);
    
        Pair<Integer, Set<String>> initialAllocationResults = opl.allocateInitialSeats(new Fraction(opl.numBallots, opl.numSeats));
    
        Integer numSeatsRemaining = initialAllocationResults.getFirst();
        Set<String> remainingParties = initialAllocationResults.getSecond();
    
        opl.allocateRemainingSeats(numSeatsRemaining, remainingParties);
    
        // Comparing expected output vs actual output.
        assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsMoreSeatsThanCandidates.txt"),
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsAudit3.txt"))
        );
    
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsAudit3.txt".replace('/', FILE_SEP)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/allocateRemainingSeatsReport3.txt".replace('/', FILE_SEP)).delete();
    
        System.setOut(originalSystemOut);
    }
    
    @Test
    void testDistributeSeatsToCandidatesTypical() {
        
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        
        Class<?> partyInformation = null;
        try {
            partyInformation = Class.forName("org.team19.OpenPartyListSystem$PartyInformation");
            
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        assert partyInformation != null;
        
        Constructor<?> partyInformationConstructor = null;
        try {
            partyInformationConstructor = partyInformation.getDeclaredConstructor();
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert partyInformationConstructor != null;
        partyInformationConstructor.setAccessible(true);
        
        OpenPartyListSystem.PartyInformation partyD = null;
        OpenPartyListSystem.PartyInformation partyR = null;
        OpenPartyListSystem.PartyInformation partyI = null;
        
        try {
            try {
                partyD = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            partyR = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            partyI = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        Candidate foster_d = new Candidate("Foster", "D");
        Candidate pike_d = new Candidate("Pike", "D");
        
        Candidate deutsch_r = new Candidate("Deutsch", "R");
        Candidate jones_r = new Candidate("Jones", "R");
        Candidate borg_r = new Candidate("Borg", "R");
        
        Candidate smith_i = new Candidate("Smith", "I");
    
        assert partyD != null;
        
        partyD.numCandidates = 2;
        partyD.numBallots = 5;
        partyD.numSeats = 0;
        
        partyD.orderedCandidateBallots = new ArrayList<>();
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(pike_d, 3));
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(foster_d, 2));
        
        assert partyR != null;
        
        partyR.numCandidates = 3;
        partyR.numBallots = 3;
        partyR.numSeats = 0;
        
        partyR.orderedCandidateBallots = new ArrayList<>();
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(borg_r, 2));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(jones_r, 1));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(deutsch_r, 0));
        
        assert partyI != null;
        
        partyI.numCandidates = 1;
        partyI.numBallots = 1;
        partyI.numSeats = 0;
        
        partyI.orderedCandidateBallots = new ArrayList<>();
        partyI.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(smith_i, 1));
        
        OpenPartyListSystem opl = null;
        try {
            opl = new OpenPartyListSystem(
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/testDistributeSeatsToCandidatesAudit1.txt"),
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/testDistributeSeatsToCandidatesReport1.txt")
            );
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        assert opl != null;
        opl.numSeats = 3;
        opl.numBallots = 9;
        
        opl.partiesToPartyInformation = new LinkedHashMap<>();
        opl.partiesToPartyInformation.put("D", partyD);
        opl.partiesToPartyInformation.put("R", partyR);
        opl.partiesToPartyInformation.put("I", partyI);
        
        opl.partyToCandidateCounts = new LinkedHashMap<>();
        
        opl.partyToCandidateCounts.put("D", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("D").put(foster_d, 3);
        opl.partyToCandidateCounts.get("D").put(pike_d, 2);
        
        opl.partyToCandidateCounts.put("R", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("R").put(deutsch_r, 0);
        opl.partyToCandidateCounts.get("R").put(jones_r, 1);
        opl.partyToCandidateCounts.get("R").put(borg_r, 2);
        
        opl.partyToCandidateCounts.put("I", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("I").put(smith_i, 1);
        
        Pair<Integer, Set<String>> initialAllocationResults = opl.allocateInitialSeats(new Fraction(opl.numBallots, opl.numSeats));
        
        Integer numSeatsRemaining = initialAllocationResults.getFirst();
        Set<String> remainingParties = initialAllocationResults.getSecond();
        
        opl.allocateRemainingSeats(numSeatsRemaining, remainingParties);
        
        opl.distributeSeatsToCandidates();
        
        // Comparing expected output vs actual output for a typical seat distribution
        assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/testDistributeSeatsToCandidatesTypical.txt"),
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/testDistributeSeatsToCandidatesAudit1.txt"))
        );
        
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/testDistributeSeatsToCandidatesAudit1.txt".replace('/', FILE_SEP)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/testDistributeSeatsToCandidatesReport1.txt".replace('/', FILE_SEP))
            .delete();
        
        System.setOut(originalSystemOut);
        
    }
    
    @Test
    void testDistributeSeatsToCandidatesTieBreaks() {
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        
        Class<?> partyInformation = null;
        try {
            partyInformation = Class.forName("org.team19.OpenPartyListSystem$PartyInformation");
            
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        assert partyInformation != null;
        
        Constructor<?> partyInformationConstructor = null;
        try {
            partyInformationConstructor = partyInformation.getDeclaredConstructor();
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert partyInformationConstructor != null;
        partyInformationConstructor.setAccessible(true);
        
        OpenPartyListSystem.PartyInformation partyD = null;
        OpenPartyListSystem.PartyInformation partyR = null;
        OpenPartyListSystem.PartyInformation partyI = null;
        
        try {
            try {
                partyD = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            partyR = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            partyI = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        Candidate foster_d = new Candidate("Foster", "D");
        Candidate pike_d = new Candidate("Pike", "D");
        
        Candidate deutsch_r = new Candidate("Deutsch", "R");
        Candidate jones_r = new Candidate("Jones", "R");
        Candidate borg_r = new Candidate("Borg", "R");
        
        Candidate smith_i = new Candidate("Smith", "I");
        
        assert partyD != null;
        
        partyD.numCandidates = 2;
        partyD.numBallots = 5;
        partyD.numSeats = 0;
        
        partyD.orderedCandidateBallots = new ArrayList<>();
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(pike_d, 3));
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(foster_d, 2));
        
        assert partyR != null;
        
        partyR.numCandidates = 3;
        partyR.numBallots = 2;
        partyR.numSeats = 0;
        
        partyR.orderedCandidateBallots = new ArrayList<>();
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(borg_r, 1));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(jones_r, 1));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(deutsch_r, 0));
        
        assert partyI != null;
        
        partyI.numCandidates = 1;
        partyI.numBallots = 1;
        partyI.numSeats = 0;
        
        partyI.orderedCandidateBallots = new ArrayList<>();
        partyI.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(smith_i, 1));
        
        OpenPartyListSystem opl = null;
        try {
            opl = new OpenPartyListSystem(
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/testDistributeSeatsToCandidatesTieBreaksAudit1.txt"),
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/testDistributeSeatsToCandidatesTieBreaksReport1.txt")
            );
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        assert opl != null;
        opl.numSeats = 3;
        opl.numBallots = 8;
        
        opl.partiesToPartyInformation = new LinkedHashMap<>();
        opl.partiesToPartyInformation.put("D", partyD);
        opl.partiesToPartyInformation.put("R", partyR);
        opl.partiesToPartyInformation.put("I", partyI);
        
        opl.partyToCandidateCounts = new LinkedHashMap<>();
        
        opl.partyToCandidateCounts.put("D", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("D").put(foster_d, 3);
        opl.partyToCandidateCounts.get("D").put(pike_d, 2);
        
        opl.partyToCandidateCounts.put("R", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("R").put(borg_r, 1);
        opl.partyToCandidateCounts.get("R").put(jones_r, 1);
        opl.partyToCandidateCounts.get("R").put(deutsch_r, 0);
    
        opl.partyToCandidateCounts.put("I", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("I").put(smith_i, 1);
        
        Pair<Integer, Set<String>> initialAllocationResults = opl.allocateInitialSeats(new Fraction(opl.numBallots, opl.numSeats));
        
        Integer numSeatsRemaining = initialAllocationResults.getFirst();
        Set<String> remainingParties = initialAllocationResults.getSecond();
        
        opl.allocateRemainingSeats(numSeatsRemaining, remainingParties);
        
        opl.printFinalSeatAllocations();
        
        ArrayList<Candidate> candidatesWithSeats = opl.distributeSeatsToCandidates();
        
        System.out.println(candidatesWithSeats);
        
        // Test to check that either Jones or Borg can get their party's allocated seat since they both have 1 vote
        assertTrue(candidatesWithSeats.toString().equals("[Pike (D), Foster (D), Jones (R)]") || candidatesWithSeats.toString()
            .equals("[Pike (D), Foster (D), Borg (R)]"));
        
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/testDistributeSeatsToCandidatesTieBreaksAudit1.txt".replace('/',
            FILE_SEP)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/testDistributeSeatsToCandidatesTieBreaksReport1.txt".replace('/', FILE_SEP))
            .delete();
        
        System.setOut(originalSystemOut);
    }
    
    @Test
    void testPrintSummaryTable() {
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        
        Class<?> partyInformation = null;
        try {
            partyInformation = Class.forName("org.team19.OpenPartyListSystem$PartyInformation");
            
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        assert partyInformation != null;
        
        Constructor<?> partyInformationConstructor = null;
        try {
            partyInformationConstructor = partyInformation.getDeclaredConstructor();
        }
        catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert partyInformationConstructor != null;
        partyInformationConstructor.setAccessible(true);
        
        OpenPartyListSystem.PartyInformation partyD = null;
        OpenPartyListSystem.PartyInformation partyR = null;
        OpenPartyListSystem.PartyInformation partyI = null;
        
        try {
            try {
                partyD = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            }
            catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            partyR = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            partyI = (OpenPartyListSystem.PartyInformation) partyInformationConstructor.newInstance();
            
        }
        catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        Candidate foster_d = new Candidate("Foster", "D");
        Candidate pike_d = new Candidate("Pike", "D");
        
        Candidate deutsch_r = new Candidate("Deutsch", "R");
        Candidate jones_r = new Candidate("Jones", "R");
        Candidate borg_r = new Candidate("Borg", "R");
        
        Candidate smith_i = new Candidate("Smith", "I");
        
        assert partyD != null;
        
        partyD.numCandidates = 2;
        partyD.numBallots = 5;
        partyD.numSeats = 0;
        
        partyD.orderedCandidateBallots = new ArrayList<>();
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(pike_d, 3));
        partyD.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(foster_d, 2));
        
        assert partyR != null;
        
        partyR.numCandidates = 3;
        partyR.numBallots = 3;
        partyR.numSeats = 0;
        
        partyR.orderedCandidateBallots = new ArrayList<>();
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(borg_r, 2));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(jones_r, 1));
        partyR.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(deutsch_r, 0));
        
        assert partyI != null;
        
        partyI.numCandidates = 1;
        partyI.numBallots = 1;
        partyI.numSeats = 0;
        
        partyI.orderedCandidateBallots = new ArrayList<>();
        partyI.orderedCandidateBallots.add(new AbstractMap.SimpleEntry<>(smith_i, 1));
        
        OpenPartyListSystem opl = null;
        try {
            opl = new OpenPartyListSystem(
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/testPrintSummaryTableAudit1.txt"),
                new FileOutputStream("Project1/testing/test-resources/openPartyListSystemTest/testPrintSummaryTableReport1.txt")
            );
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        assert opl != null;
        opl.numSeats = 3;
        opl.numBallots = 9;
        
        opl.partiesToPartyInformation = new LinkedHashMap<>();
        opl.partiesToPartyInformation.put("D", partyD);
        opl.partiesToPartyInformation.put("R", partyR);
        opl.partiesToPartyInformation.put("I", partyI);
        
        opl.partyToCandidateCounts = new LinkedHashMap<>();
        
        opl.partyToCandidateCounts.put("D", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("D").put(foster_d, 3);
        opl.partyToCandidateCounts.get("D").put(pike_d, 2);
        
        opl.partyToCandidateCounts.put("R", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("R").put(deutsch_r, 0);
        opl.partyToCandidateCounts.get("R").put(jones_r, 1);
        opl.partyToCandidateCounts.get("R").put(borg_r, 2);
        
        opl.partyToCandidateCounts.put("I", new LinkedHashMap<>());
        opl.partyToCandidateCounts.get("I").put(smith_i, 1);
        
        Pair<Integer, Set<String>> initialAllocationResults = opl.allocateInitialSeats(new Fraction(opl.numBallots, opl.numSeats));
        
        Integer numSeatsRemaining = initialAllocationResults.getFirst();
        Set<String> remainingParties = initialAllocationResults.getSecond();
        
        opl.allocateRemainingSeats(numSeatsRemaining, remainingParties);
        
        Map<String, Integer> partiesToInitialSeats = new HashMap<>();
        
        for(Map.Entry<String, OpenPartyListSystem.PartyInformation> party : opl.partiesToPartyInformation.entrySet()) {
            partiesToInitialSeats.put(party.getKey(), party.getValue().numSeats);
        }
        
        opl.printSummaryTable(partiesToInitialSeats);
        
        // Comparing expected output vs actual output for a typical seat distribution
        assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/testPrintSummaryTable.txt"),
            new FileInputStream("Project1/testing/test-resources/openPartyListSystemTest/testPrintSummaryTableReport1.txt"))
        );
        
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/testPrintSummaryTableAudit1.txt".replace('/', FILE_SEP)).delete();
        //noinspection ResultOfMethodCallIgnored
        new File("Project1/testing/test-resources/openPartyListSystemTest/testPrintSummaryTableReport1.txt".replace('/', FILE_SEP))
            .delete();
        
        System.setOut(originalSystemOut);
    }
    
    @Test
    void testGetNextEquivalentOrderedGroupTypical() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 3, 3, 6, 7, 8, 9));
        // Test to check the group of 3's are returned
        assertEquals("Pair{6, [3, 3, 3]}", OpenPartyListSystem.getNextEquivalentOrderedGroup(list, 3, Integer::compareTo).toString());
        // Test to check only single 2 is returned
        assertEquals("Pair{3, [2]}", OpenPartyListSystem.getNextEquivalentOrderedGroup(list, 2, Integer::compareTo).toString());
        
        
    }
    
    @Test
    void testGetNextEquivalentOrderedGroupOutOfBounds(){
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 3, 3, 6, 7, 8, 9));
    
        // Test for upper index out of bounds
        assertDoesNotThrow(() -> OpenPartyListSystem.getNextEquivalentOrderedGroup(list, 12, Integer::compareTo));
        // Test for lower index out of bounds
        assertThrows(IndexOutOfBoundsException.class, () -> OpenPartyListSystem.getNextEquivalentOrderedGroup(list, -1, Integer::compareTo));
    }
    
}
