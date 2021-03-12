package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

final class InstantRunoffTest {
    
    private InstantRunoffTest() {}
    
    @Test
    void testConstructor() {
    
    }
    
    @Test
    void testGetCandidateHeaderSize() {
    
    }
    
    @Test
    void testGetBallotHeaderSize() {
    
    }
    
    @Test
    void testImportCandidatesHeader() {
    
    }
    
    @Test
    void testAddCandidates() {
    
    }
    
    @Test
    void testImportBallotsHeader() {
    
    }
    
    @Test
    void testAddBallot() {
    
    }
    
    @Test
    void testGetName() {
    
    }
    
    @Test
    void testGetShortName() {
    
    }
    
    @Test
    void testGetNumCandidates() {
    
    }
    
    @Test
    void testGetCandidates() {
    
    }
    
    @Test
    void testGetNumBallots() {
    
    }
    
    @Test
    void testToString() {
    
    }
    
    @Test
    void testGetLowestHighestCandidates() {
        final Method getLowestHighestCandidates = InstantRunoffSystem.class.getDeclaredMethod("getLowestHighestCandidates");
        getLowestHighestCandidates.setAccessible(true);
    
        final Field numCandidates = InstantRunoffSystem.class.getDeclaredField("numCandidates");
        numCandidates.setAccessible(true);
    
        final Field numBallots = InstantRunoffSystem.class.getDeclaredField("numBallots");
        numBallots.setAccessible(true);
    
        final Field candidates = InstantRunoffSystem.class.getDeclaredField("candidates");
        candidates.setAccessible(true);
    
        final Field candidateBallotsMap = InstantRunoffSystem.class.getDeclaredField("candidateBallotsMap");
        candidateBallotsMap.setAccessible(true);
    
        final Class<?> ballotClass = Class.forName("org.team19.InstantRunoffSystem$Ballot");
        final Constructor<?> ballotConstructor = ballotClass.getDeclaredConstructor(Candidate[].class, int.class);
        ballotConstructor.setAccessible(true);
    
        final OutputStream nullOutput = OutputStream.nullOutputStream();
        final InstantRunoffSystem irs = new InstantRunoffSystem(nullOutput, nullOutput);
        
        /* Test 1: Acts as normal, where there is a single highest and single lowest candidate found.
         * candidates = [Candidate 1, Candidate 2, Candidate 3]
         * Candidate 1:
         *  Ballot 1:
         *   [1, ,]
         *  Ballot 2:
         *   [1, 2, 3]
         *  Ballot 3:
         *   [1, 3, 2]
         *
         * Candidate 2:
         *  Ballot 1:
         *   [2, 1, 3]
         *
         * Candidate 3:
         *  Ballot 1:
         *   [3, 2, 1]
         *  Ballot 2:
         *   [2, 3, 1]
         *
         * While the program iterates through each candidate, it will find that Candidate 1 will have more ballots than the current
         * highest ballots value.  Additionally, it will find that Candidate 2 has the lowest ballot count.  The pair that will be returned will be:
         * Pair(Pair(1, lowestCandidates), Pair(3, Candidate 1))
         * Where lowestCandidates includes: Candidate 2
         */
        
        /* Test 2: A single highest candidate is found, but multiple lowest candidates are found
         * Candidate 1:
         *  Ballot 1:
         *   [1, ,]
         *  Ballot 2:
         *   [1, 2, 3]
         *  Ballot 3:
         *   [1, 3, 2]
         *
         * Candidate 2:
         *  Ballot 1:
         *   [2, 1, 3]
         *
         * Candidate 3:
         *  Ballot 1:
         *   [3, 2, 1]
         *
         * While the program iterates through each candidate, it will find that Candidate 1 will have more ballots than the current
         * highest ballots value.  Additionally, it will find that Candidate 2 and Candidate 3 have the same ballot count, and the lowest ballot
         * count.  The pair that will be returned will be:
         * Pair(Pair(1, lowestCandidates), Pair(3, Candidate 1))
         * Where lowestCandidates includes: Candidate 2, Candidate 3
         */
        
        /* Test 3: Multiple Candidates share the highest ballot count
         * Candidate 1:
         *  Ballot 1:
         *   [1, ,]
         *  Ballot 2:
         *   [1, 2, 3]
         *  Ballot 3:
         *   [1, 3, 2]
         *
         * Candidate 2:
         *  Ballot 1:
         *   [2, 1, 3]
         *  Ballot 2:
         *   [, 1, 2]
         *  Ballot 3:
         *   [3, 1, 2]
         *
         * Candidate 3:
         *  Ballot 1:
         *   [3, 2, 1]
         *
         * The program will initially find that Candidate 1 has the highest ballot count.  During the next iteration,
         * Candidate 2 will have the same ballot count as highest ballot count, but Candidate 2's ballot count
         * will not be saved.  Candidate 1 is still considered the highest candidate, and the algorithm won't change
         * based on this decision.  The lowest candidate is Candidate 3.  The pair that will be returned will be:
         * Pair(Pair(1, lowestCandidates), Pair(3, Candidate 1))
         */
        
        /* Test 4: All Candidates have the same number of ballots.
         *
         * Candidate 1:
         *  Ballot 1:
         *   [1, ,]
         *  Ballot 2:
         *   [1, 2, 3]
         *  Ballot 3:
         *   [1, 3, 2]
         *
         * Candidate 2:
         *  Ballot 1:
         *   [2, 1, 3]
         *  Ballot 2:
         *   [, 1, 2]
         *  Ballot 3:
         *   [3, 1, 2]
         *
         * Candidate 3:
         *  Ballot 1:
         *   [3, 2, 1]
         *  Ballot 2:
         *   [, , 1]
         *  Ballot 3:
         *   [2, , 1]
         *
         * The only conditional the program will reach is the elif statement where numBallots = lowestBallots, and this case, all
         * Candidates will be added to the lowestCandidates ArrayList.  The highest candidate was initialized to be UNDEFINED, and its respective
         * ballot count was initialized to -1.  These are the values that will be returned in the pair.  The pair that will be returned will be:
         * Pair(Pair(1, lowestCandidates), Pair(-1, highestCandidate))
         * Where lowestCandidtes includes: Candidate 1, Candidate 2, Candidate 3
         *
         */
    }
    
    @Test
    void testEliminateLowest() {
        final Method eliminateLowest = InstantRunoffSystem.class.getDeclaredMethod("eliminateLowest", Candidate.class);
        eliminateLowest.setAccessible(true);
        
        final Field numCandidates = InstantRunoffSystem.class.getDeclaredField("numCandidates");
        numCandidates.setAccessible(true);
    
        final Field numBallots = InstantRunoffSystem.class.getDeclaredField("numBallots");
        numBallots.setAccessible(true);
    
        final Field candidates = InstantRunoffSystem.class.getDeclaredField("candidates");
        candidates.setAccessible(true);
    
        final Field candidateBallotsMap = InstantRunoffSystem.class.getDeclaredField("candidateBallotsMap");
        candidateBallotsMap.setAccessible(true);
        
        final Class<?> ballotClass = Class.forName("org.team19.InstantRunoffSystem$Ballot");
        final Constructor<?> ballotConstructor = ballotClass.getDeclaredConstructor(Candidate[].class, int.class);
        ballotConstructor.setAccessible(true);
        
        final OutputStream nullOutput = OutputStream.nullOutputStream();
        final InstantRunoffSystem irs = new InstantRunoffSystem(nullOutput, nullOutput);
    
        //ballotConstructor.newInstance(test, 1)
        
        numCandidates.set(irs, 5);
        
        eliminateLowest.invoke(irs, new Candidate("Obama", "Democrat"));
        Assertions.assertEquals(candidateBallotsMap.get(irs), Map.of(
            new Candidate("Romney", "Republican"),
            new ArrayList
        ));
        
        /* Test 1: The lowest candidate is eliminated, and ballots are redistributed
         * candidates = [Candidate 1, Candidate 2, Candidate 3]
         * Candidate 1:
         *  Ballot 1:
         *   [1, 2, 3]
         *  Ballot 2:
         *   [1, 3, 2]
         *  Ballot 3:
         *   [1, 2, 3]
         *
         * Candidate 2:
         *  Ballot 1:
         *   [2, 1, 3]
         *
         * Candidate 3:
         *  Ballot 1:
         *   [3, 2, 1]
         *  Ballot 2:
         *   [2, 3, 1]
         *  Ballot 3:
         *   [3, 2, 1]
         *
         * Candidate 2 is eliminated.  candidates = [Candidate 1, Candidate 3].
         * Ballots are redistributed, and shown is the state of the candidates and their ballots
         * after the run.
         * Candidate 1:
         *  Ballot 1:
         *   [1, , 2]
         *  Ballot 2:
         *   [1, , 2]
         *  Ballot 3:
         *   [1, , 2]
         *  Ballot 4:
         *   [1, , 2]
         *
         * Candidate 3:
         *  Ballot 1:
         *   [2, , 1]
         *  Ballot 2:
         *   [2, , 1]
         *  Ballot 3:
         *   [2, , 1]
         *
         */
        
        /* Test 2: Lowest candidate is eliminated, but the nextCandidate for some ballots is nonexistant.
         * candidates = [Candidate 1, Candidate 2, Candidate 3]
         * Candidate 1:
         *  Ballot 1:
         *   [1, 2, 3]
         *  Ballot 2:
         *   [1, 3, 2]
         *  Ballot 3:
         *   [1, , ,]
         *
         * Candidate 2:
         *  Ballot 1:
         *   [2, 1, 3]
         *  Ballot 2:
         *   [, 1 ,]
         *
         * Candidate 3:
         *  Ballot 1:
         *   [3, 2, 1]
         *  Ballot 2:
         *   [2, 3, 1]
         *  Ballot 3:
         *   [3, 2, 1]
         *
         * The test will continue as expected, and Candidate 2 is determined the lowest.  Ballot 2 in Candidate 2 has no
         * subsequent nextCandidate, and so the ballot is made null.
         *
         * candidates = [Candidate 1, Candidate 3]
         * Candidate 1:
         *  Ballot 1:
         *   [1, , 2]
         *  Ballot 2:
         *   [1, , 2]
         *  Ballot 3:
         *   [1, , ,]
         *  Ballot 4: <- reallocated Candidate2: Ballot 1
         *   [1, , 2]
         *
         * Candidate 2: <- eliminated at this point
         *  Ballot 2:
         *   [, , ,] <- reallocated nowhere
         *
         * Candidate 3:
         *  Ballot 1:
         *   [2, , 1]
         *  Ballot 2:
         *   [2, , 1]
         *  Ballot 3:
         *   [2, , 1]
         */
    }
    
    @Test
    void testRunElection() {
        final Method runElection = InstantRunoffSystem.class.getDeclaredMethod("runElection", Candidate.class);
        runElection.setAccessible(true);
    
        final Field numCandidates = InstantRunoffSystem.class.getDeclaredField("numCandidates");
        numCandidates.setAccessible(true);
    
        final Field numBallots = InstantRunoffSystem.class.getDeclaredField("numBallots");
        numBallots.setAccessible(true);
    
        final Field candidates = InstantRunoffSystem.class.getDeclaredField("candidates");
        candidates.setAccessible(true);
    
        final Field candidateBallotsMap = InstantRunoffSystem.class.getDeclaredField("candidateBallotsMap");
        candidateBallotsMap.setAccessible(true);
    
        final Class<?> ballotClass = Class.forName("org.team19.InstantRunoffSystem$Ballot");
        final Constructor<?> ballotConstructor = ballotClass.getDeclaredConstructor(Candidate[].class, int.class);
        ballotConstructor.setAccessible(true);
    
        final OutputStream nullOutput = OutputStream.nullOutputStream();
        final InstantRunoffSystem irs = new InstantRunoffSystem(nullOutput, nullOutput);
        
        /* Test 1: only 1 candidate exists
         * candidates = [Candidate 1]
         * Candidate 1:
         *  Ballot 1:
         *    [Candidate 1]
         *  Ballot 2:
         *    [Candidate 1]
         *  Ballot 3:
         *    [Candidate 1]
         *
         * Only one candidate exists.  Therefore, run election will be ran and
         * candidateBallotsMapLen will be set to 1.  The program will
         * displayWinnerInfo(), and the election will end.
         *
         */
        
        /* Test 2: 2 candidates exist, and there is no majority
         * candidates = [Candidate 1, Candidate 2]
         * Candidate 1:
         *  Ballot 1:
         *   [1, 2]
         *  Ballot 2:
         *   [1, 2]
         *
         * Candidate 2:
         *  Ballot 1:
         *    [2, 1]
         *  Ballot 2:
         *    [2, 1]
         *
         * At this point, candidateBallotsMapLen is = 2.
         * Both candidates have 2 first choice votes, so neither holds a majority.
         * The program will create a random int, either 0 or 1, and the winner
         * will be declared upon this choosing.  Either Candidate 1 will win,
         * or Candidate 2 will win.
         */
        
        /* Test 3: 3 or more candidates exist and a majority is found
         * candidates = [Candidate 1, Candidate 2, Candidate 3]
         * Candidate 1:
         *  Ballot 1:
         *   [1, 3, 2]
         *  Ballot 2:
         *   [1, 2, 3]
         *  Ballot 3:
         *   [1, , 2]
         *  Ballot 4:
         *   [1 , ,]
         * Candidate 2:
         *  Ballot 1:
         *   [2, 1, 3]
         * Candidate 3:
         *  Ballot 1:
         *   [3, 2, 1]
         *  Ballot 2:
         *   [3, , 1]
         *
         * The program will enter the conditional where candidateBallotsMapLen > 2
         * Here, since Candidate 1 has 4/7 first choice votes, Candidate 1 will
         * be declared the winner.  Output will display the winner information
         * to the audit and report files, as well as the summary.
         */
        
        /* Test 4: 2 candidates exist, and a majority is found
         * candidates = [Candidate 1, Candidate 2]
         * Candidate 1:
         *  Ballot 1:
         *   [1, 2]
         *  Ballot 2:
         *   [1, ,]
         *  Ballot 3:
         *   [1, 2]
         *
         * Candidate 2:
         *  Ballot 1:
         *   [2, 1]
         *
         * At this stage, candidateBallotsMapLen == 2, and so the conditional will be
         * entered.  The program will compare the first and second Candidate, and
         * determine which has a majority.  Candidate 1 has 3/4 first choice votes,
         * and so they are declared the winner.  Output will display the winner information
         * to the audit and report files, as well as the summary.
         */
    }
    
}
