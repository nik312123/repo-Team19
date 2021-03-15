package org.team19;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.List;

import org.team19.InstantRunoffSystem.Ballot;

class InstantRunoffSystemTest {
    
    final static char FILE_SEP = File.separatorChar;
    
    //Creates a null device output stream to consume and ignore all output
    private static final OutputStream NULL_OUTPUT = OutputStream.nullOutputStream();
    
    //Creates an InstantRunoffSystem with null device output streams
    private static InstantRunoffSystem createIrNullStreams() {
        return new InstantRunoffSystem(NULL_OUTPUT, NULL_OUTPUT);
    }
    
    @Test
    void testGetLowestHighestCandidatesSingleHighestSingleLowest() {
        final InstantRunoffSystem ir = createIrNullStreams();
        
        ir.numCandidates = 4;
        ir.numBallots = 6;
        ir.candidates = new Candidate[4];
        
        ir.candidates[0] = new Candidate("Rosen","D");
        ir.candidates[1] = new Candidate("Kleinberg","R");
        ir.candidates[2] = new Candidate("Chou","I");
        ir.candidates[3] = new Candidate("Royce","L");
    
        final Ballot ballot1 = new Ballot(1, new Candidate[]{ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot2 = new Ballot(2, new Candidate[]{ir.candidates[0], ir.candidates[2]});
        final Ballot ballot3 = new Ballot(3, new Candidate[]{ir.candidates[0], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot4 = new Ballot(4, new Candidate[]{ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]});
        final Ballot ballot5 = new Ballot(5, new Candidate[]{ir.candidates[2], ir.candidates[3]});
        final Ballot ballot6 = new Ballot(6, new Candidate[]{ir.candidates[3]});
    
        ir.candidateBallotsMap = new LinkedHashMap<>();
        
        ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballot1, ballot2, ballot3)));
        ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());
        ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballot4, ballot5)));
        ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballot6)));
        
        assertEquals("Pair{Pair{0, [Kleinberg (R)]}, Pair{3, Rosen (D)}}", ir.getLowestHighestCandidates().toString());
    }
    
    @Test
    void testGetLowestHighestCandidatesMultipleLowest(){
        final InstantRunoffSystem ir = createIrNullStreams();
    
        ir.numCandidates = 6;
        ir.numBallots = 6;
        ir.candidates = new Candidate[6];
    
        ir.candidates[0] = new Candidate("Rosen","D");
        ir.candidates[1] = new Candidate("Kleinberg","R");
        ir.candidates[2] = new Candidate("Chou","I");
        ir.candidates[3] = new Candidate("Royce","L");
        ir.candidates[4] = new Candidate("Loser","L");
        ir.candidates[5] = new Candidate("Bobster","I");
    
        final Ballot ballot1 = new Ballot(1, new Candidate[]{ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot2 = new Ballot(2, new Candidate[]{ir.candidates[0], ir.candidates[2]});
        final Ballot ballot3 = new Ballot(3, new Candidate[]{ir.candidates[0], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot4 = new Ballot(4, new Candidate[]{ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]});
        final Ballot ballot5 = new Ballot(5, new Candidate[]{ir.candidates[2], ir.candidates[3], ir.candidates[4]});
        final Ballot ballot6 = new Ballot(6, new Candidate[]{ir.candidates[3], ir.candidates[5]});
    
        ir.candidateBallotsMap = new LinkedHashMap<>();
    
        ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballot1, ballot2, ballot3)));
        ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());
        ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballot4, ballot5)));
        ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballot6)));
        ir.candidateBallotsMap.put(ir.candidates[4], new ArrayDeque<>());
        ir.candidateBallotsMap.put(ir.candidates[5], new ArrayDeque<>());
    
        assertEquals("Pair{Pair{0, [Kleinberg (R), Loser (L), Bobster (I)]}, Pair{3, Rosen (D)}}", ir.getLowestHighestCandidates().toString());
    }
    
    @Test
    void testGetLowestHighestCandidatesMultipleHighest(){
        final InstantRunoffSystem ir = createIrNullStreams();
    
        ir.numCandidates = 5;
        ir.numBallots = 9;
        ir.candidates = new Candidate[5];
    
        ir.candidates[0] = new Candidate("Rosen","D");
        ir.candidates[1] = new Candidate("Kleinberg","R");
        ir.candidates[2] = new Candidate("Chou","I");
        ir.candidates[3] = new Candidate("Royce","L");
        ir.candidates[4] = new Candidate("Bobster", "I");
    
        final Ballot ballot1 = new Ballot(1, new Candidate[]{ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot2 = new Ballot(2, new Candidate[]{ir.candidates[0], ir.candidates[2]});
        final Ballot ballot3 = new Ballot(3, new Candidate[]{ir.candidates[0], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot4 = new Ballot(4, new Candidate[]{ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]});
        final Ballot ballot5 = new Ballot(5, new Candidate[]{ir.candidates[2], ir.candidates[3]});
        final Ballot ballot6 = new Ballot(6, new Candidate[]{ir.candidates[3]});
        final Ballot ballot7 = new Ballot(7, new Candidate[]{ir.candidates[4]});
        final Ballot ballot8 = new Ballot(8, new Candidate[]{ir.candidates[4], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot9 = new Ballot(9, new Candidate[]{ir.candidates[4], ir.candidates[2]});
    
        ir.candidateBallotsMap = new LinkedHashMap<>();
    
        ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballot1, ballot2, ballot3)));
        ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());
        ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballot4, ballot5)));
        ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballot6)));
        ir.candidateBallotsMap.put(ir.candidates[4], new ArrayDeque<>(List.of(ballot7, ballot8, ballot9)));
    
        assertEquals("Pair{Pair{0, [Kleinberg (R)]}, Pair{3, Rosen (D)}}", ir.getLowestHighestCandidates().toString());
    }
    
    @Test
    void testEliminateLowest() {
        final InstantRunoffSystem ir = createIrNullStreams();
    
        ir.numCandidates = 4;
        ir.numBallots = 6;
        ir.candidates = new Candidate[4];
    
        ir.candidates[0] = new Candidate("Rosen","D");
        ir.candidates[1] = new Candidate("Kleinberg","R");
        ir.candidates[2] = new Candidate("Chou","I");
        ir.candidates[3] = new Candidate("Royce","L");
    
        final Ballot ballot1 = new Ballot(1, new Candidate[]{ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot2 = new Ballot(2, new Candidate[]{ir.candidates[0], ir.candidates[2]});
        final Ballot ballot3 = new Ballot(3, new Candidate[]{ir.candidates[0], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot4 = new Ballot(4, new Candidate[]{ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]});
        final Ballot ballot5 = new Ballot(5, new Candidate[]{ir.candidates[2], ir.candidates[3]});
        final Ballot ballot6 = new Ballot(6, new Candidate[]{ir.candidates[3]});
    
        ir.candidateBallotsMap = new LinkedHashMap<>();
    
        ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballot1, ballot2, ballot3))); // 3
        ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());                                   // 0
        ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballot4, ballot5)));          // 2
        ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballot6)));                   // 1
    
        // Eliminates Kleinberg - 0 ballots
        ir.eliminateLowest(ir.candidates[1]);
    
        // Test to check that at eliminated candidate is removed from the map
        assertFalse(ir.candidateBallotsMap.containsKey(ir.candidates[1]));
        
        // Test to check that the other candidates' ballots are unchanged because
        // the eliminated candidate had no ballots to redistribute
        assertEquals(3, ir.candidateBallotsMap.get(ir.candidates[0]).size());
        assertEquals(2, ir.candidateBallotsMap.get(ir.candidates[2]).size());
        assertEquals(1, ir.candidateBallotsMap.get(ir.candidates[3]).size());
        
        // Eliminates Royce - 1 ballot
        ir.eliminateLowest(ir.candidates[3]);
    
        // Test to check that at eliminated candidate is removed from the map
        assertFalse(ir.candidateBallotsMap.containsKey(ir.candidates[3]));
    
        // Test to check that the other candidates' ballots are unchanged because
        // the eliminated candidate only had 1 ballot with only 1 candidate ranked
        assertEquals(3, ir.candidateBallotsMap.get(ir.candidates[0]).size());
        assertEquals(2, ir.candidateBallotsMap.get(ir.candidates[2]).size());
    
        // Eliminates Chou - 2 ballots
        ir.eliminateLowest(ir.candidates[2]);
    
        // Test to check that at eliminated candidate is removed from the map
        assertFalse(ir.candidateBallotsMap.containsKey(ir.candidates[2]));
    
        // Test to check that only Chou's ballot 4 is distributed to Rosen as indicated by the ballot
        assertTrue(ir.candidateBallotsMap.get(ir.candidates[0]).contains(ballot4));
        // Chou's ballot 5 should not be distributed to Royce they have already been eliminated
    }
    
    @Test
    void testEliminateLowestOutput(){
        
        final String auditOutput = "Project1/testing/test-resources/instantRunoffSystemTest/testEliminateLowestOutputAudit1.txt";
        
        InstantRunoffSystem ir = null;
        try {
            ir = new InstantRunoffSystem(new FileOutputStream(auditOutput), FileOutputStream.nullOutputStream());
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    
        ir.numCandidates = 4;
        ir.numBallots = 6;
        ir.candidates = new Candidate[4];
    
        ir.candidates[0] = new Candidate("Rosen","D");
        ir.candidates[1] = new Candidate("Kleinberg","R");
        ir.candidates[2] = new Candidate("Chou","I");
        ir.candidates[3] = new Candidate("Royce","L");
    
        final Ballot ballot1 = new Ballot(1, new Candidate[]{ir.candidates[0], ir.candidates[3], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot2 = new Ballot(2, new Candidate[]{ir.candidates[0], ir.candidates[2]});
        final Ballot ballot3 = new Ballot(3, new Candidate[]{ir.candidates[0], ir.candidates[1], ir.candidates[2]});
        final Ballot ballot4 = new Ballot(4, new Candidate[]{ir.candidates[2], ir.candidates[1], ir.candidates[0], ir.candidates[3]});
        final Ballot ballot5 = new Ballot(5, new Candidate[]{ir.candidates[2], ir.candidates[3]});
        final Ballot ballot6 = new Ballot(6, new Candidate[]{ir.candidates[3]});
    
        ir.candidateBallotsMap = new LinkedHashMap<>();
    
        ir.candidateBallotsMap.put(ir.candidates[0], new ArrayDeque<>(List.of(ballot1, ballot2, ballot3)));
        ir.candidateBallotsMap.put(ir.candidates[1], new ArrayDeque<>());
        ir.candidateBallotsMap.put(ir.candidates[2], new ArrayDeque<>(List.of(ballot4, ballot5)));
        ir.candidateBallotsMap.put(ir.candidates[3], new ArrayDeque<>(List.of(ballot6)));
    
        // Eliminates Kleinberg - 0 ballots
        ir.eliminateLowest(ir.candidates[1]);
    
        // Eliminates Royce - 1 ballot
        ir.eliminateLowest(ir.candidates[3]);

        // Eliminates Chou - 2 ballots
        ir.eliminateLowest(ir.candidates[2]);
    
        ir.auditWriter.close();
    
        // Comparing expected output vs actual output
        assertDoesNotThrow(() -> CompareInputStreams.compareFiles(
            new FileInputStream("Project1/testing/test-resources/instantRunoffSystemTest/testEliminateLowestOutput.txt"),
            new FileInputStream(auditOutput))
        );
        
        //noinspection ResultOfMethodCallIgnored
        new File(auditOutput.replace('/', FILE_SEP)).delete();
    }
    
    @Test
    void runElection() {
    }
}
