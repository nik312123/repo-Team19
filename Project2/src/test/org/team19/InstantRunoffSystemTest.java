/*
 * File name:
 * InstantRunoffSystemTest.java
 *
 * Author:
 * Nikunj Chawla, Jack Fornaro, and Aaron Kandikatla
 *
 * Purpose:
 * Tests the InstantRunoffSystem class
 */

package org.team19;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.team19.InstantRunoffSystem.Ballot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

final class InstantRunoffSystemTest {
    
    final static char FILE_SEP = File.separatorChar;
    
    //Creates a null device output stream to consume and ignore all output
    private static final OutputStream NULL_OUTPUT = OutputStream.nullOutputStream();
    
    //Creates an InstantRunoffSystem with null device output streams
    private static InstantRunoffSystem createIrNullStreams() {
        return new InstantRunoffSystem(NULL_OUTPUT, NULL_OUTPUT);
    }
    
    private InstantRunoffSystemTest() {}
    
    @Test
    void testConstructor() {
        Assertions.assertAll(
            //Testing that a typical creation of InstantRunoffSystem does not throw an exception
            () -> Assertions.assertDoesNotThrow(InstantRunoffSystemTest::createIrNullStreams),
            //Testing that a the creation of InstantRunoffSystem with a null report output stream throws NullPointerException
            () -> Assertions.assertThrows(NullPointerException.class, () -> new InstantRunoffSystem(NULL_OUTPUT, null)),
            //Testing that a the creation of InstantRunoffSystem with a null audit output stream throws NullPointerException
            () -> Assertions.assertThrows(NullPointerException.class, () -> new InstantRunoffSystem(null, NULL_OUTPUT))
        );
    }
    
    @Test
    void testGetCandidateHeaderSize() {
        //Test that an instant runoff system has 1 line as its candidate header size
        Assertions.assertEquals(1, createIrNullStreams().getCandidateHeaderSize());
    }
    
    @Test
    void testGetBallotHeaderSize() {
        //Test that an instant runoff system has 1 line as its ballot header size
        Assertions.assertEquals(1, createIrNullStreams().getBallotHeaderSize());
    }
    
    //getNumCandidates is tested here indirectly as well
    @Test
    void testImportCandidatesHeader() {
        final InstantRunoffSystem instantRunoffSystem = createIrNullStreams();
        
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        try {
            Assertions.assertAll(
                //Test that a non-positive candidate header results in an exception being thrown
                () -> Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.importCandidatesHeader(new String[] {"0"}, "1", 2)),
                () -> Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.importCandidatesHeader(new String[] {"-2"}, "1", 2)),
                //Test that a nonnumerical candidate header results in an exception being thrown
                () -> Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.importCandidatesHeader(new String[] {"a"}, "1", 2)),
                /*
                 * Try executing importCandidatesHeader with a positive integer, failing if it is unable to run without exception and ensure that
                 * the number of candidates was properly imported from the candidates header
                 */
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.importCandidatesHeader(new String[] {"2"}, "1", 2)),
                () -> Assertions.assertEquals(2, instantRunoffSystem.getNumCandidates())
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
        final InstantRunoffSystem instantRunoffSystem = createIrNullStreams();
        
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        try {
            //Test example candidates array
            final Candidate[] c0c1 = new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")};
            
            Assertions.assertAll(
                //Tests issue in candidates format from lack of parentheses
                () -> Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.addCandidates("C0 (P0), C1 P1", "1", 3)),
                //Tests issue in candidates format due to extra text
                () -> Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.addCandidates("C0 (P0)a, C1 (P1)", "1", 3)),
                //Tests valid typical candidates string is valid and properly parsed
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.addCandidates("C0 (P0), C1 (P1)", "1", 3)),
                () -> Assertions.assertEquals(List.of(c0c1), instantRunoffSystem.getCandidates()),
                //Tests valid candidates string with excess whitespace is valid and properly parsed
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.addCandidates("   C0 (   P0   )   ,  C1    (   P1   )   ", "1", 3)),
                () -> Assertions.assertEquals(List.of(c0c1), instantRunoffSystem.getCandidates())
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
        final InstantRunoffSystem instantRunoffSystem = createIrNullStreams();
        
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        try {
            Assertions.assertAll(
                //Test that a negative ballots header results in an exception being thrown
                () -> {
                    Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.importBallotsHeader(new String[] {"-2"}, "1", 4));
                    instantRunoffSystem.numBallots = 0;
                },
                //Test that a nonnumerical ballots header results in an exception being thrown
                () -> {
                    Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.importBallotsHeader(new String[] {"a"}, "1", 4));
                    instantRunoffSystem.numBallots = 0;
                },
                /*
                 * Try executing importBallotsHeader with an input of 0, failing if it is unable to run without exception; then, ensure that the
                 * number was properly imported
                 */
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.importBallotsHeader(new String[] {"0"}, "1", 4)),
                () -> {
                    Assertions.assertEquals(0, instantRunoffSystem.getNumBallots());
                    instantRunoffSystem.numBallots = 0;
                },
                /*
                 * Try executing importBallotsHeader with a positive integer, failing if it is unable to run without exception; then, ensure that
                 * the number was properly imported
                 */
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.importBallotsHeader(new String[] {"2"}, "1", 4)),
                () -> {
                    Assertions.assertEquals(2, instantRunoffSystem.getNumBallots());
                    instantRunoffSystem.numBallots = 0;
                },
                //Test that executing importBallotsHeader multiple times results in all of the ballot counts from the ballot header being added up
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.importBallotsHeader(new String[] {"2"}, "1", 4)),
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.importBallotsHeader(new String[] {"0"}, "2", 4)),
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.importBallotsHeader(new String[] {"6"}, "3", 4)),
                () -> {
                    Assertions.assertEquals(8, instantRunoffSystem.getNumBallots());
                    instantRunoffSystem.numBallots = 0;
                }
            );
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    @Test
    void testAddBallot() {
        final InstantRunoffSystem instantRunoffSystem = createIrNullStreams();
        
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        try {
            //Retrieve the ballot parsing method for the voting system
            Method parseBallotTmp = null;
            try {
                parseBallotTmp = InstantRunoffSystem.class.getDeclaredMethod("parseBallot", int.class, String.class, String.class, int.class);
                parseBallotTmp.setAccessible(true);
            }
            catch(NoSuchMethodException e) {
                Assertions.fail("Unable to retrieve parseBallot from InstantRunoffSystem");
            }
            final Method parseBallot = parseBallotTmp;
            
            //Set up the voting system with the following candidate header information and candidates
            try {
                instantRunoffSystem.importCandidatesHeader(new String[] {"5"}, "1", 2);
                instantRunoffSystem.addCandidates("C0 (P0), C1 (P1), C2 (P2), C3 (P3), C4 (P4)", "1", 3);
            }
            catch(ParseException e) {
                Assertions.fail("Unable to properly set up the candidates for the test");
            }
            
            Assertions.assertAll(
                //Test the case where there are not enough values provided
                () -> Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.addBallot(1, "1,2,3,4", "1", 5)),
                //Test the case where no ballot is ranked
                () -> Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.addBallot(1, ",,,,", "1", 5)),
                //Test the case where there is a non-integer rank
                () -> Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.addBallot(1, "1,2,a,,", "1", 5)),
                //Test the case where there is a rank below the possible range
                () -> Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.addBallot(1, "1,2,0,4,3", "1", 5)),
                //Test the case where there is a rank above the possible range
                () -> Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.addBallot(1, "1,2,6,4,5", "1", 5)),
                //Test the case where a number is skipped in ranking
                () -> Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.addBallot(1, "1,2,5,4,", "1", 5)),
                //Test the case where the start is specifically skipped
                () -> Assertions.assertThrows(ParseException.class, () -> instantRunoffSystem.addBallot(1, "2,4,3,5,", "1", 5)),
                //Test the case where all candidates are ranked
                () -> Assertions.assertEquals(
                    new Ballot(1, new Candidate[] {
                        new Candidate("C3", "P3"),
                        new Candidate("C1", "P1"),
                        new Candidate("C4", "P4"),
                        new Candidate("C2", "P2"),
                        new Candidate("C0", "P0")
                    }),
                    parseBallot.invoke(instantRunoffSystem, 1, "5,2,4,1,3", "1", 5)
                ),
                //Test the case where not all candidates are ranked
                () -> Assertions.assertEquals(
                    new Ballot(1, new Candidate[] {
                        new Candidate("C4", "P4"),
                        new Candidate("C3", "P3"),
                        new Candidate("C1", "P1")
                    }),
                    parseBallot.invoke(instantRunoffSystem, 1, ",3,,2,1", "1", 5)
                )
            );
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    @Test
    void testAddBallotInvalidationOdd() {
        final InstantRunoffSystem instantRunoffSystem = createIrNullStreams();
        
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        try {
            //Set up the voting system with the following candidate header information and candidates
            try {
                instantRunoffSystem.importCandidatesHeader(new String[] {"5"}, "1", 2);
                instantRunoffSystem.addCandidates("C0 (P0), C1 (P1), C2 (P2), C3 (P3), C4 (P4)", "1", 3);
            }
            catch(ParseException e) {
                Assertions.fail("Unable to properly set up the candidates for the test");
            }
            
            //Retrieve the list of candidates
            List<Candidate> candidateListTmp = null;
            try {
                candidateListTmp = (List<Candidate>) instantRunoffSystem.getCandidates();
            }
            catch(ClassCastException e) {
                Assertions.fail("The provided collection of candidates could not be casted to a list");
            }
            final List<Candidate> candidateList = candidateListTmp;
            
            //Retrieve candidates 0 and 2
            final Candidate c0 = candidateList.get(0);
            final Candidate c2 = candidateList.get(2);
            
            /*
             * Set the expected ballots for candidate 0, calling getNextCandidate on each ballot to increment the candidate index as is the case for
             * the actual ballots
             */
            final Ballot[] c0ExpectedBallots = new Ballot[] {
                new Ballot(3, new Candidate[] {candidateList.get(0), candidateList.get(3), candidateList.get(2)})
            };
            for(final Ballot ballot : c0ExpectedBallots) {
                ballot.getNextCandidate();
            }
            
            /*
             * Set the expected ballots for candidate 2, calling getNextCandidate on each ballot to increment the candidate index as is the case for
             * the actual ballots
             */
            final Ballot[] c2ExpectedBallots = new Ballot[] {
                new Ballot(1, new Candidate[] {
                    candidateList.get(2), candidateList.get(3), candidateList.get(0), candidateList.get(1), candidateList.get(4)
                })
            };
            for(final Ballot ballot : c2ExpectedBallots) {
                ballot.getNextCandidate();
            }
            
            Assertions.assertAll(
                //Adding a ballot that ranks all of the candidates and testing that it does not throw an exception
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.addBallot(1, "3,4,1,2,5", "testAddBallotInvalidation", 5)),
                //Adding a ballot that ranks less than half of the candidates and testing that it does not throw an exception
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.addBallot(2, ",,,1,", "testAddBallotInvalidation", 6)),
                //Adding a ballot that ranks just over half of the candidates and testing that it does not throw an exception
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.addBallot(3, "1,,3,2,", "testAddBallotInvalidation", 7)),
                //Adding a ballot that ranks just under half of the candidates and testing that it does not throw an exception
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.addBallot(4, ",2,1,,", "testAddBallotInvalidation", 8)),
                
                /*
                 * Note: The following tests are done in place of the use of Map.equals because Deque-inheriting classes apparently do not
                 * overwrite equals
                 */
                
                //Test that only the candidates that have ballots were added to the map
                () -> Assertions.assertEquals(
                    Set.of(c0, c2),
                    instantRunoffSystem.candidateBallotsMap.keySet()
                ),
                //Test that candidate 0 only has ballot 3
                () -> Assertions.assertArrayEquals(
                    instantRunoffSystem.candidateBallotsMap.get(c0).toArray(),
                    c0ExpectedBallots
                ),
                //Test that candidate 2 only has ballot 1
                () -> Assertions.assertArrayEquals(
                    instantRunoffSystem.candidateBallotsMap.get(c2).toArray(),
                    c2ExpectedBallots
                )
            );
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    @Test
    void testAddBallotInvalidationEven() {
        final InstantRunoffSystem instantRunoffSystem = createIrNullStreams();
        
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        try {
            //Set up the voting system with the following candidate header information and candidates
            try {
                instantRunoffSystem.importCandidatesHeader(new String[] {"4"}, "1", 2);
                instantRunoffSystem.addCandidates("C0 (P0), C1 (P1), C2 (P2), C3 (P3)", "1", 3);
            }
            catch(ParseException e) {
                Assertions.fail("Unable to properly set up the candidates for the test");
            }
            
            //Retrieve the list of candidates
            List<Candidate> candidateListTmp = null;
            try {
                candidateListTmp = (List<Candidate>) instantRunoffSystem.getCandidates();
            }
            catch(ClassCastException e) {
                Assertions.fail("The provided collection of candidates could not be casted to a list");
            }
            final List<Candidate> candidateList = candidateListTmp;
            
            //Retrieve candidates 0 and 2
            final Candidate c0 = candidateList.get(0);
            final Candidate c2 = candidateList.get(2);
            
            /*
             * Set the expected ballots for candidate 0, calling getNextCandidate on each ballot to increment the candidate index as is the case for
             * the actual ballots
             */
            final Ballot[] c0ExpectedBallots = new Ballot[] {
                new Ballot(3, new Candidate[] {candidateList.get(0), candidateList.get(3), candidateList.get(2)})
            };
            for(final Ballot ballot : c0ExpectedBallots) {
                ballot.getNextCandidate();
            }
            
            /*
             * Set the expected ballots for candidate 2, calling getNextCandidate on each ballot to increment the candidate index as is the case for
             * the actual ballots
             */
            final Ballot[] c2ExpectedBallots = new Ballot[] {
                new Ballot(1, new Candidate[] {
                    candidateList.get(2), candidateList.get(3), candidateList.get(0), candidateList.get(1)
                }),
                new Ballot(4, new Candidate[] {candidateList.get(2), candidateList.get(1)})
            };
            for(final Ballot ballot : c2ExpectedBallots) {
                ballot.getNextCandidate();
            }
            
            Assertions.assertAll(
                //Adding a ballot that ranks all of the candidates and testing that it does not throw an exception
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.addBallot(1, "3,4,1,2", "testAddBallotInvalidation", 5)),
                //Adding a ballot that ranks less than half of the candidates and testing that it does not throw an exception
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.addBallot(2, ",,,1", "testAddBallotInvalidation", 6)),
                //Adding a ballot that ranks more than half of the candidates and testing that it does not throw an exception
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.addBallot(3, "1,,3,2", "testAddBallotInvalidation", 7)),
                //Adding a ballot that ranks half of the candidates and testing that it does not throw an exception
                () -> Assertions.assertDoesNotThrow(() -> instantRunoffSystem.addBallot(4, ",2,1,", "testAddBallotInvalidation", 8)),
                
                /*
                 * Note: The following tests are done in place of the use of Map.equals because Deque-inheriting classes apparently do not
                 * overwrite equals
                 */
                
                //Test that only the candidates that have ballots were added to the map
                () -> Assertions.assertEquals(
                    Set.of(c0, c2),
                    instantRunoffSystem.candidateBallotsMap.keySet()
                ),
                //Test that candidate 0 only has ballot 3
                () -> Assertions.assertArrayEquals(
                    instantRunoffSystem.candidateBallotsMap.get(c0).toArray(),
                    c0ExpectedBallots
                ),
                //Test that candidate 2 only has ballot 1
                () -> Assertions.assertArrayEquals(
                    instantRunoffSystem.candidateBallotsMap.get(c2).toArray(),
                    c2ExpectedBallots
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
        //Test that the name returned is "Instant Runoff Voting"
        Assertions.assertEquals("Instant Runoff Voting", createIrNullStreams().getName());
    }
    
    @Test
    void testGetShortName() {
        //Test that the short name returned is "IR"
        Assertions.assertEquals("IR", createIrNullStreams().getShortName());
    }
    
    @Test
    void testToString() {
        final InstantRunoffSystem instantRunoffSystem = createIrNullStreams();
        
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        try {
            //Put required sample data
            try {
                instantRunoffSystem.addCandidates("C0 (P0), C1 (P1), C2 (P2), C3 (P3), C4 (P4)", "1", 3);
                instantRunoffSystem.importBallotsHeader(new String[] {"143"}, "1", 4);
            }
            catch(ParseException e) {
                Assertions.fail("Unable to properly set up the candidates for the test");
            }
            
            /*
             * Test that InstantRunoffSystem's toString produces output like "InstantRunoffSystem{candidates=[candidates], numBallots=<numBallots>}"
             * where [candidates] is replaced by the string form of the candidates array and [numBallots] is replaced by the number of ballots
             */
            Assertions.assertEquals(
                "InstantRunoffSystem{candidates=[C0 (P0), C1 (P1), C2 (P2), C3 (P3), C4 (P4)], numBallots=143}",
                instantRunoffSystem.toString()
            );
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    @Test
    void testGetLowestHighestCandidatesSingleHighestSingleLowest() {
        //Initializes InstantRunoffSystem with null OutputStreams
        final InstantRunoffSystem ir = createIrNullStreams();
        
        ir.numCandidates = 4;
        ir.numBallots = 6;
        ir.candidates = new Candidate[4];
        
        //Creates Candidates
        ir.candidates[0] = new Candidate("Rosen", "D");
        ir.candidates[1] = new Candidate("Kleinberg", "R");
        ir.candidates[2] = new Candidate("Chou", "I");
        ir.candidates[3] = new Candidate("Royce", "L");
        
        //Creates ballots
        final Ballot ballot1 = new Ballot(1, new Candidate[] {ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot2 = new Ballot(2, new Candidate[] {ir.candidates[0], ir.candidates[2]});
        final Ballot ballot3 = new Ballot(3, new Candidate[] {ir.candidates[0], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot4 = new Ballot(4, new Candidate[] {ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]});
        final Ballot ballot5 = new Ballot(5, new Candidate[] {ir.candidates[2], ir.candidates[3]});
        final Ballot ballot6 = new Ballot(6, new Candidate[] {ir.candidates[3]});
        
        ir.candidateBallotsMap = new LinkedHashMap<>();
        //Maps candidates to their ballots
        ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballot1, ballot2, ballot3)));
        ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());
        ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballot4, ballot5)));
        ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballot6)));
        
        //Tests that only 1 lowest and 1 highest are returned
        assertEquals("Pair{Pair{0, [Kleinberg (R)]}, Pair{3, Rosen (D)}}", ir.getLowestHighestCandidates().toString());
    }
    
    @Test
    void testGetLowestHighestCandidatesMultipleLowest() {
        //Initializes InstantRunoffSystem with null OutputStreams
        final InstantRunoffSystem ir = createIrNullStreams();
        
        ir.numCandidates = 6;
        ir.numBallots = 6;
        ir.candidates = new Candidate[6];
        
        //Creates Candidates
        ir.candidates[0] = new Candidate("Rosen", "D");
        ir.candidates[1] = new Candidate("Kleinberg", "R");
        ir.candidates[2] = new Candidate("Chou", "I");
        ir.candidates[3] = new Candidate("Royce", "L");
        ir.candidates[4] = new Candidate("Loser", "L");
        ir.candidates[5] = new Candidate("Bobster", "I");
        
        //Creates ballots
        final Ballot ballot1 = new Ballot(1, new Candidate[] {ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot2 = new Ballot(2, new Candidate[] {ir.candidates[0], ir.candidates[2]});
        final Ballot ballot3 = new Ballot(3, new Candidate[] {ir.candidates[0], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot4 = new Ballot(4, new Candidate[] {ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]});
        final Ballot ballot5 = new Ballot(5, new Candidate[] {ir.candidates[2], ir.candidates[3], ir.candidates[4]});
        final Ballot ballot6 = new Ballot(6, new Candidate[] {ir.candidates[3], ir.candidates[5]});
        
        ir.candidateBallotsMap = new LinkedHashMap<>();
        //Maps candidates to their ballots
        ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballot1, ballot2, ballot3)));
        ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());
        ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballot4, ballot5)));
        ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballot6)));
        ir.candidateBallotsMap.put(ir.candidates[4], new ArrayDeque<>());
        ir.candidateBallotsMap.put(ir.candidates[5], new ArrayDeque<>());
        
        //Tests the multiple lowest candidates are returned
        assertEquals("Pair{Pair{0, [Kleinberg (R), Loser (L), Bobster (I)]}, Pair{3, Rosen (D)}}", ir.getLowestHighestCandidates().toString());
    }
    
    @Test
    void testGetLowestHighestCandidatesMultipleHighest() {
        //Initializes InstantRunoffSystem with null OutputStreams
        final InstantRunoffSystem ir = createIrNullStreams();
        
        ir.numCandidates = 5;
        ir.numBallots = 9;
        ir.candidates = new Candidate[5];
        
        //Creates candidates
        ir.candidates[0] = new Candidate("Rosen", "D");
        ir.candidates[1] = new Candidate("Kleinberg", "R");
        ir.candidates[2] = new Candidate("Chou", "I");
        ir.candidates[3] = new Candidate("Royce", "L");
        ir.candidates[4] = new Candidate("Bobster", "I");
        
        //Creates ballots
        final Ballot ballot1 = new Ballot(1, new Candidate[] {ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot2 = new Ballot(2, new Candidate[] {ir.candidates[0], ir.candidates[2]});
        final Ballot ballot3 = new Ballot(3, new Candidate[] {ir.candidates[0], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot4 = new Ballot(4, new Candidate[] {ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]});
        final Ballot ballot5 = new Ballot(5, new Candidate[] {ir.candidates[2], ir.candidates[3]});
        final Ballot ballot6 = new Ballot(6, new Candidate[] {ir.candidates[3]});
        final Ballot ballot7 = new Ballot(7, new Candidate[] {ir.candidates[4]});
        final Ballot ballot8 = new Ballot(8, new Candidate[] {ir.candidates[4], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot9 = new Ballot(9, new Candidate[] {ir.candidates[4], ir.candidates[2]});
        
        ir.candidateBallotsMap = new LinkedHashMap<>();
        //Maps candidates to their ballots
        ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballot1, ballot2, ballot3)));
        ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());
        ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballot4, ballot5)));
        ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballot6)));
        ir.candidateBallotsMap.put(ir.candidates[4], new ArrayDeque<>(List.of(ballot7, ballot8, ballot9)));
        
        //Tests that only 1 highest candidate is returned
        assertEquals("Pair{Pair{0, [Kleinberg (R)]}, Pair{3, Rosen (D)}}", ir.getLowestHighestCandidates().toString());
    }
    
    @Test
    void testEliminateLowest() {
        //Initializes InstantRunoffSystem with null OutputStreams
        final InstantRunoffSystem ir = createIrNullStreams();
        
        ir.numCandidates = 4;
        ir.numBallots = 6;
        ir.candidates = new Candidate[4];
        
        //Creates candidates
        ir.candidates[0] = new Candidate("Rosen", "D");
        ir.candidates[1] = new Candidate("Kleinberg", "R");
        ir.candidates[2] = new Candidate("Chou", "I");
        ir.candidates[3] = new Candidate("Royce", "L");
        
        //Creates ballots
        final Ballot[] ballots = new Ballot[] {
            new Ballot(1, new Candidate[] {ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]}),
            new Ballot(2, new Candidate[] {ir.candidates[0], ir.candidates[2]}),
            new Ballot(3, new Candidate[] {ir.candidates[0], ir.candidates[1], ir.candidates[2]}),
            new Ballot(4, new Candidate[] {ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]}),
            new Ballot(5, new Candidate[] {ir.candidates[2], ir.candidates[3]}),
            new Ballot(6, new Candidate[] {ir.candidates[3]}),
        };
        
        for(final Ballot ballot : ballots) {
            ballot.getNextCandidate();
        }
        
        ir.candidateBallotsMap = new LinkedHashMap<>();
        
        //Maps candidates to their ballots
        ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballots[0], ballots[1], ballots[2])));  //3
        ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());                                             //0
        ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballots[3], ballots[4])));              //2
        ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballots[5])));                          //1
        
        //Eliminates Kleinberg - 0 ballots
        ir.eliminateLowest(ir.candidates[1]);
        
        //Test to check that at eliminated candidate is removed from the map
        assertFalse(ir.candidateBallotsMap.containsKey(ir.candidates[1]));
        
        //Test to check that the other candidates' ballots are unchanged because
        //the eliminated candidate had no ballots to redistribute
        assertEquals(3, ir.candidateBallotsMap.get(ir.candidates[0]).size());
        assertEquals(2, ir.candidateBallotsMap.get(ir.candidates[2]).size());
        assertEquals(1, ir.candidateBallotsMap.get(ir.candidates[3]).size());
        
        //Eliminates Royce - 1 ballot
        ir.eliminateLowest(ir.candidates[3]);
        
        //Test to check that at eliminated candidate is removed from the map
        assertFalse(ir.candidateBallotsMap.containsKey(ir.candidates[3]));
        
        //Test to check that the other candidates' ballots are unchanged because
        //the eliminated candidate only had 1 ballot had not next candidate indicated
        assertEquals(3, ir.candidateBallotsMap.get(ir.candidates[0]).size());
        assertEquals(2, ir.candidateBallotsMap.get(ir.candidates[2]).size());
        
        //Eliminates Chou - 2 ballots
        ir.eliminateLowest(ir.candidates[2]);
        
        //Test to check that at eliminated candidate is removed from the map
        assertFalse(ir.candidateBallotsMap.containsKey(ir.candidates[2]));
        
        //Test to check that only Chou's ballot 4 is distributed to Rosen as indicated by the ballot
        assertTrue(ir.candidateBallotsMap.get(ir.candidates[0]).contains(ballots[3]));
    }
    
    @Test
    void testEliminateLowestOutput() {
        final String auditOutput = "Project2/testing/test-resources/instantRunoffSystemTest/test_eliminate_lowest_output_audit_actual.txt"
            .replace('/', FILE_SEP);
        
        //Initializes InstantRunoffSystem with audit OutputStream
        InstantRunoffSystem ir = null;
        try {
            ir = new InstantRunoffSystem(new FileOutputStream(auditOutput), NULL_OUTPUT);
        }
        catch(FileNotFoundException e) {
            Assertions.fail("Unable to create test_eliminate_lowest_output_audit_actual.txt");
        }
        
        ir.numCandidates = 4;
        ir.numBallots = 6;
        ir.candidates = new Candidate[4];
        
        //Creates candidates
        ir.candidates[0] = new Candidate("Rosen", "D");
        ir.candidates[1] = new Candidate("Kleinberg", "R");
        ir.candidates[2] = new Candidate("Chou", "I");
        ir.candidates[3] = new Candidate("Royce", "L");
        
        //Creates candidates
        final Ballot[] ballots = new Ballot[] {
            new Ballot(1, new Candidate[] {ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]}),
            new Ballot(2, new Candidate[] {ir.candidates[0], ir.candidates[2]}),
            new Ballot(3, new Candidate[] {ir.candidates[0], ir.candidates[1], ir.candidates[2]}),
            new Ballot(4, new Candidate[] {ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]}),
            new Ballot(5, new Candidate[] {ir.candidates[2], ir.candidates[3]}),
            new Ballot(6, new Candidate[] {ir.candidates[3]}),
        };
        
        for(final Ballot ballot : ballots) {
            ballot.getNextCandidate();
        }
        
        ir.candidateBallotsMap = new LinkedHashMap<>();
        //Maps candidates to their ballots
        ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballots[0], ballots[1], ballots[2])));  //3
        ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());                                             //0
        ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballots[3], ballots[4])));              //2
        ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballots[5])));                          //1
        
        //Eliminates Kleinberg - 0 ballots
        ir.eliminateLowest(ir.candidates[1]);
        
        //Eliminates Royce - 1 ballot
        ir.eliminateLowest(ir.candidates[3]);
        
        //Eliminates Chou - 2 ballots
        ir.eliminateLowest(ir.candidates[2]);
        
        ir.auditWriter.close();
        
        //Comparing expected output vs actual output
        assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream(
                "Project2/testing/test-resources/instantRunoffSystemTest/test_eliminate_lowest_output_expected.txt".replace('/', FILE_SEP)
            ),
            new FileInputStream(auditOutput))
        );
        
        //Run garbage collector manually to properly allow deletion of the file on Windows due to Java bug
        System.gc();
        
        //noinspection ResultOfMethodCallIgnored
        new File(auditOutput).delete();
    }
    
    @Test
    void runElectionMajority() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        try {
            final String auditOutput = "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_majority_audit_actual.txt"
                .replace('/', FILE_SEP);
            final String reportOutput = "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_majority_report_actual.txt"
                .replace('/', FILE_SEP);
            
            InstantRunoffSystem ir = null;
            try {
                ir = new InstantRunoffSystem(new FileOutputStream(auditOutput), new FileOutputStream(reportOutput));
            }
            catch(FileNotFoundException e) {
                Assertions.fail("Unable to create test_run_election_majority_audit_actual.txt or test_run_election_majority_report_actual.txt");
                
            }
            
            ir.numCandidates = 4;
            ir.numBallots = 9;
            ir.halfNumBallots = ir.numBallots / 2;
            ir.candidates = new Candidate[4];
            
            //Creates candidates
            ir.candidates[0] = new Candidate("Rosen", "D");
            ir.candidates[1] = new Candidate("Kleinberg", "R");
            ir.candidates[2] = new Candidate("Chou", "I");
            ir.candidates[3] = new Candidate("Royce", "L");
            
            //Creates ballots
            final Ballot[] ballots = new Ballot[] {
                new Ballot(1, new Candidate[] {ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]}),
                new Ballot(2, new Candidate[] {ir.candidates[0], ir.candidates[2]}),
                new Ballot(3, new Candidate[] {ir.candidates[0], ir.candidates[1], ir.candidates[2]}),
                new Ballot(4, new Candidate[] {ir.candidates[2], ir.candidates[0], ir.candidates[1], ir.candidates[3]}),
                new Ballot(5, new Candidate[] {ir.candidates[2], ir.candidates[3]}),
                new Ballot(6, new Candidate[] {ir.candidates[3]}),
                new Ballot(7, new Candidate[] {ir.candidates[3]}),
                new Ballot(8, new Candidate[] {ir.candidates[1], ir.candidates[0]}),
                new Ballot(9, new Candidate[] {ir.candidates[1]})
            };
            
            for(final Ballot ballot : ballots) {
                ballot.getNextCandidate();
            }
            
            ir.candidateBallotsMap = new LinkedHashMap<>();
            
            //Maps candidates to their ballots
            ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballots[0], ballots[1], ballots[2], ballots[8])));  //4
            ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>(List.of(ballots[7])));                                      //1
            ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballots[3], ballots[4])));                          //2
            ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballots[5], ballots[6])));                          //2
            
            ir.runElection();
            
            //Comparing expected output vs actual output of audit
            assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
                new FileInputStream(
                    "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_majority_audit_expected.txt".replace('/', FILE_SEP)
                ),
                new FileInputStream(auditOutput))
            );
            
            //Comparing expected output vs actual output of report
            assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
                new FileInputStream(
                    "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_majority_report_expected.txt".replace('/', FILE_SEP)
                ),
                new FileInputStream(reportOutput))
            );
            
            //Run garbage collector manually to properly allow deletion of the file on Windows due to Java bug
            System.gc();
            
            //noinspection ResultOfMethodCallIgnored
            new File(auditOutput).delete();
            //noinspection ResultOfMethodCallIgnored
            new File(reportOutput).delete();
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    @Test
    void runElectionPopularity() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        try {
            final String auditOutput = "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_popularity_audit_actual.txt"
                .replace('/', FILE_SEP);
            final String reportOutput = "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_popularity_report_actual.txt"
                .replace('/', FILE_SEP);
            
            InstantRunoffSystem ir = null;
            try {
                ir = new InstantRunoffSystem(new FileOutputStream(auditOutput), new FileOutputStream(reportOutput));
            }
            catch(FileNotFoundException e) {
                Assertions.fail(
                    "Unable to create test_run_election_popularity_audit_actual.txt or test_run_election_popularity_report_actual.txt"
                );
            }
            
            ir.numCandidates = 4;
            ir.numBallots = 6;
            ir.halfNumBallots = ir.numBallots / 2;
            ir.candidates = new Candidate[4];
            
            //Creates candidates
            ir.candidates[0] = new Candidate("Rosen", "D");
            ir.candidates[1] = new Candidate("Kleinberg", "R");
            ir.candidates[2] = new Candidate("Chou", "I");
            ir.candidates[3] = new Candidate("Royce", "L");
            
            //Creates ballots
            final Ballot[] ballots = new Ballot[] {
                new Ballot(1, new Candidate[] {ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]}),
                new Ballot(2, new Candidate[] {ir.candidates[0], ir.candidates[2]}),
                new Ballot(3, new Candidate[] {ir.candidates[0], ir.candidates[1], ir.candidates[2]}),
                new Ballot(4, new Candidate[] {ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]}),
                new Ballot(5, new Candidate[] {ir.candidates[2], ir.candidates[3]}),
                new Ballot(6, new Candidate[] {ir.candidates[3]}),
            };
            
            for(final Ballot ballot : ballots) {
                ballot.getNextCandidate();
            }
            
            ir.candidateBallotsMap = new LinkedHashMap<>();
            
            //Maps candidates to their ballots
            ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballots[0], ballots[1], ballots[2])));  //3
            ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());                                             //0
            ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballots[3], ballots[4])));              //2
            ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballots[5])));                          //1
            
            ir.runElection();
            
            //Comparing expected output vs actual output of audit
            assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
                new FileInputStream(
                    "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_popularity_audit_expected.txt".replace('/', FILE_SEP)
                ),
                new FileInputStream(auditOutput))
            );
            
            //Comparing expected output vs actual output of report
            assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
                new FileInputStream(
                    "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_popularity_report_expected.txt".replace('/', FILE_SEP)
                ),
                new FileInputStream(reportOutput))
            );
            
            //Run garbage collector manually to properly allow deletion of the file on Windows due to Java bug
            System.gc();
            
            //noinspection ResultOfMethodCallIgnored
            new File(auditOutput).delete();
            //noinspection ResultOfMethodCallIgnored
            new File(reportOutput).delete();
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    @Test
    void testRunElectionTieBreaksOutput() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        final String auditOutput =
            "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_tie_breaks_output_audit_actual.txt"
                .replace('/', FILE_SEP);
        
        try {
            InstantRunoffSystem ir = null;
            try {
                ir = new InstantRunoffSystem(new FileOutputStream(auditOutput), NULL_OUTPUT);
            }
            catch(FileNotFoundException e) {
                Assertions.fail("Unable to create test_run_election_tie_breaks_output_audit_actual.txt");
            }
            
            ir.numCandidates = 6;
            ir.numBallots = 7;
            ir.halfNumBallots = ir.numBallots / 2;
            ir.candidates = new Candidate[6];
            
            //Creates candidates
            ir.candidates[0] = new Candidate("Rosen", "D");
            ir.candidates[1] = new Candidate("Kleinberg", "R");
            ir.candidates[2] = new Candidate("Chou", "I");
            ir.candidates[3] = new Candidate("Royce", "L");
            ir.candidates[4] = new Candidate("Biden", "D");
            ir.candidates[5] = new Candidate("Trump", "R");
            
            //Creates ballots
            final Ballot[] ballots = new Ballot[] {
                new Ballot(1, new Candidate[] {ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]}),
                new Ballot(2, new Candidate[] {ir.candidates[0], ir.candidates[2]}),
                new Ballot(3, new Candidate[] {ir.candidates[0], ir.candidates[1], ir.candidates[2]}),
                new Ballot(4, new Candidate[] {ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]}),
                new Ballot(5, new Candidate[] {ir.candidates[2], ir.candidates[3]}),
                new Ballot(6, new Candidate[] {ir.candidates[3]}),
                new Ballot(7, new Candidate[] {ir.candidates[2]}),
            };
            
            for(final Ballot ballot : ballots) {
                ballot.getNextCandidate();
            }
            
            ir.candidateBallotsMap = new LinkedHashMap<>();
            
            //Maps candidates to their ballots
            ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballots[0], ballots[1], ballots[2])));  //3
            ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());                                             //0
            ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballots[3], ballots[4], ballots[6])));  //3
            ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballots[5])));                          //1
            ir.candidateBallotsMap.put(ir.candidates[4], new ArrayDeque<>());                                             //0
            ir.candidateBallotsMap.put(ir.candidates[5], new ArrayDeque<>());                                             //0
            
            //Sets a seed so that the output is always the same
            ir.rand = new Random(10L);
            
            ir.runElection();
            
            //Comparing expected output vs actual output of audit
            assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
                new FileInputStream(
                    "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_tie_breaks_output_audit_expected.txt"
                        .replace('/', FILE_SEP)
                ),
                new FileInputStream(auditOutput))
            );
            
            //Run garbage collector manually to properly allow deletion of the file on Windows due to Java bug
            System.gc();
            
            //noinspection ResultOfMethodCallIgnored
            new File(auditOutput).delete();
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    @Test
    void testRunElectionTwoCandidateMajority() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        try {
            final String auditOutput =
                "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_two_candidate_majority_audit_actual.txt"
                    .replace('/', FILE_SEP);
            final String reportOutput =
                "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_two_candidate_majority_report_actual.txt"
                    .replace('/', FILE_SEP);
            
            InstantRunoffSystem ir = null;
            try {
                ir = new InstantRunoffSystem(new FileOutputStream(auditOutput), new FileOutputStream(reportOutput));
            }
            catch(FileNotFoundException e) {
                Assertions.fail(
                    "Unable to create test_run_election_two_candidate_majority_audit_actual.txt or "
                        + "test_run_election_two_candidate_majority_report_actual.txt"
                );
            }
            
            ir.numCandidates = 4;
            ir.numBallots = 6;
            ir.halfNumBallots = ir.numBallots / 2;
            ir.candidates = new Candidate[4];
            
            //Creates candidates
            ir.candidates[0] = new Candidate("Rosen", "D");
            ir.candidates[1] = new Candidate("Kleinberg", "R");
            ir.candidates[2] = new Candidate("Chou", "I");
            ir.candidates[3] = new Candidate("Royce", "L");
            
            //Creates ballots
            final Ballot[] ballots = new Ballot[] {
                new Ballot(1, new Candidate[] {ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]}),
                new Ballot(2, new Candidate[] {ir.candidates[0], ir.candidates[2]}),
                new Ballot(3, new Candidate[] {ir.candidates[0], ir.candidates[1], ir.candidates[2]}),
                new Ballot(4, new Candidate[] {ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]}),
                new Ballot(5, new Candidate[] {ir.candidates[2], ir.candidates[3]}),
                new Ballot(6, new Candidate[] {ir.candidates[3], ir.candidates[0]}),
            };
            
            for(final Ballot ballot : ballots) {
                ballot.getNextCandidate();
            }
            
            ir.candidateBallotsMap = new LinkedHashMap<>();
            
            //Maps candidates to their ballots
            ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballots[0], ballots[1], ballots[2])));  //3
            ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());                                             //0
            ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballots[3], ballots[4])));              //2
            ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballots[5])));                          //1
            
            ir.runElection();
            
            //Comparing expected output vs actual output of audit
            assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
                new FileInputStream(
                    "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_two_candidate_majority_audit_expected.txt"
                        .replace('/', FILE_SEP)
                ),
                new FileInputStream(auditOutput))
            );
            
            //Comparing expected output vs actual output of report
            assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
                new FileInputStream(
                    "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_two_candidate_majority_report_expected.txt"
                        .replace('/', FILE_SEP)
                ),
                new FileInputStream(reportOutput))
            );
            
            //Run garbage collector manually to properly allow deletion of the file on Windows due to Java bug
            System.gc();
            
            //noinspection ResultOfMethodCallIgnored
            new File(auditOutput).delete();
            //noinspection ResultOfMethodCallIgnored
            new File(reportOutput).delete();
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
    
    @Test
    void testRunElectionZeroBallots() {
        //Store the original STDOUT and redirect it to go to a null device print stream
        final PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(NULL_OUTPUT));
        
        try {
            final String auditOutput = "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_zero_ballots_audit_actual.txt"
                .replace('/', FILE_SEP);
            final String reportOutput =
                "Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_zero_ballots_report_actual.txt"
                    .replace('/', FILE_SEP);
            
            InstantRunoffSystem ir = null;
            try {
                ir = new InstantRunoffSystem(new FileOutputStream(auditOutput), new FileOutputStream(reportOutput));
            }
            catch(FileNotFoundException e) {
                Assertions.fail(
                    "Unable to create test_run_election_two_candidate_majority_audit_actual.txt or "
                        + "test_run_election_two_candidate_majority_report_actual.txt"
                );
            }
            
            ir.numCandidates = 4;
            ir.numBallots = 0;
            ir.halfNumBallots = ir.numBallots / 2;
            ir.candidates = new Candidate[4];
            
            //Creates candidates
            ir.candidates[0] = new Candidate("Rosen", "D");
            ir.candidates[1] = new Candidate("Kleinberg", "R");
            ir.candidates[2] = new Candidate("Chou", "I");
            ir.candidates[3] = new Candidate("Royce", "L");
            
            //Creates ballots
            final Ballot[] ballots = new Ballot[0];
            
            ir.candidateBallotsMap = new LinkedHashMap<>();
            
            //Sets a seed so that the output is always the same
            ir.rand = new Random(10L);
            
            ir.runElection();
            
            //Comparing expected output vs actual output of audit
            assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
                new FileInputStream("Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_zero_ballots_audit_expected.txt"
                    .replace('/', FILE_SEP)
                ),
                new FileInputStream(auditOutput))
            );
            
            //Comparing expected output vs actual output of report
            assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
                new FileInputStream("Project2/testing/test-resources/instantRunoffSystemTest/test_run_election_zero_ballots_report_expected.txt"
                    .replace('/', FILE_SEP)
                ),
                new FileInputStream(reportOutput))
            );
            
            //Run garbage collector manually to properly allow deletion of the file on Windows due to Java bug
            System.gc();
            
            //noinspection ResultOfMethodCallIgnored
            new File(auditOutput).delete();
            //noinspection ResultOfMethodCallIgnored
            new File(reportOutput).delete();
        }
        finally {
            //Redirect STDOUT back to STDOUT
            System.setOut(originalSystemOut);
        }
    }
}
