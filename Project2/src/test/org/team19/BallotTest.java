/*
 * File name:
 * BallotTest.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Tests the internal Ballot class within InstantRunoffSystem
 */

package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.team19.InstantRunoffSystem.Ballot;

final class BallotTest {
    
    private BallotTest() {}
    
    @Test
    void testGetBallotNumber() {
        //Test that getBallotNumber returns the ballot number passed into the constructor
        final Ballot ballot = new Ballot(23, new Candidate[0]);
        Assertions.assertEquals(23, ballot.getBallotNumber());
    }
    
    @Test
    void testGetRankedCandidates() {
        //Test that getRankedCandidates returns the candidates array passed into the constructor
        final Ballot ballot = new Ballot(
            23,
            new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}
        );
        Assertions.assertArrayEquals(new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}, ballot.getRankedCandidates());
    }
    
    @Test
    void testGetNextCandidate() {
        final Ballot ballot = new Ballot(
            23,
            new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}
        );
        
        Assertions.assertAll(
            //Check that the first call to getNextCandidate returns the first element of the candidates passed in
            () -> Assertions.assertEquals(new Candidate("C0", "P0"), ballot.getNextCandidate()),
            //Check that the second call to getNextCandidate returns the second element of the candidates passed in
            () -> Assertions.assertEquals(new Candidate("C1", "P1"), ballot.getNextCandidate()),
            //Check that the third call to getNextCandidate returns null as the array of candidates has been iterated through
            () -> Assertions.assertNull(ballot.getNextCandidate())
        );
    }
    
    @Test
    void testToString() {
        //Test toString for Ballot with a sample ballot
        final Candidate c0 = new Candidate("C0", "P0");
        final Candidate c1 = new Candidate("C1", "P1");
        
        final Ballot ballot = new Ballot(
            23,
            new Candidate[] {c0, c1}
        );
        Assertions.assertEquals(
            String.format("Ballot{ballotNumber=23, candidateIndex=-1, rankedCandidates=[%s, %s]}", c0, c1),
            ballot.toString()
        );
    }
    
    @Test
    void testEquals() {
        //Creates typical ballot
        final Ballot ballot = new Ballot(
            23,
            new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}
        );
        
        //Creates copy of ballot
        final Ballot ballotDoppelganger = new Ballot(
            23,
            new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}
        );
        
        //Creates copy of ballot but advances the position to the next candidate
        final Ballot ballotDifferentPosition = new Ballot(
            23,
            new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}
        );
        ballotDifferentPosition.getNextCandidate();
        
        //Creates copy of ballot but with a modified candidates array
        final Ballot sameNumber = new Ballot(
            23,
            new Candidate[] {new Candidate("C0", "P0"), new Candidate("C2", "P2")}
        );
        
        //Creates copy of ballot but with a modified ballot number
        final Ballot sameRankings = new Ballot(
            24,
            new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}
        );
        
        Assertions.assertAll(
            //Test that ballots with equal ballot numbers, equal ranked candidates array, and equal positions in array are equal
            () -> Assertions.assertEquals(ballotDoppelganger, ballot),
            //Test that ballots with equal ballot numbers, equal ranked candidates array, and different positions in array are not equal
            () -> Assertions.assertNotEquals(ballotDifferentPosition, ballot),
            //Test that ballots with equal ballot numbers, different ranked candidates array, and equal positions in array are not equal
            () -> Assertions.assertNotEquals(sameNumber, ballot),
            //Test that ballots with different ballot numbers, equal ranked candidates array, and equal positions in array are not equal
            () -> Assertions.assertNotEquals(sameRankings, ballot)
        );
    }
    
}
