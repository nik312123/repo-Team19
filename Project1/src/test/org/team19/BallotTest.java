package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.team19.InstantRunoffSystem.Ballot;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

final class BallotTest {
    
    private BallotTest() {}
    
    @Test
    void testGetBallotNumber() {
        try {
            //Retrieve the Ballot class using reflection, retrieve its constructor, and make it accessible for testing
            final Class<?> ballotClass = Class.forName("org.team19.InstantRunoffSystem$Ballot");
            final Constructor<?> ballotConstructor = ballotClass.getDeclaredConstructor(int.class, Candidate[].class);
            ballotConstructor.setAccessible(true);
    
            //Test that getBallotNumber returns the ballot number passed into the constructor
            final Ballot ballot = (Ballot) ballotConstructor.newInstance(23, new Candidate[0]);
            Assertions.assertEquals(23, ballot.getBallotNumber());
        }
        //If unable to retrieve the constructor, fail
        catch(NoSuchMethodException e) {
            Assertions.fail("Could not retrieve the Ballot constructor");
        }
        //If unable to retrieve the class, fail
        catch(ClassNotFoundException e) {
            Assertions.fail("Could not retrieve the Ballot class");
        }
        //If unable to access or execute the constructor, fail
        catch(IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Assertions.fail("Unable to properly access or execute Ballot constructor");
        }
    }
    
    @Test
    void testGetRankedCandidates() {
        try {
            //Retrieve the Ballot class using reflection, retrieve its constructor, and make it accessible for testing
            final Class<?> ballotClass = Class.forName("org.team19.InstantRunoffSystem$Ballot");
            final Constructor<?> ballotConstructor = ballotClass.getDeclaredConstructor(int.class, Candidate[].class);
            ballotConstructor.setAccessible(true);
    
            //Test that getRankedCandidates returns the candidates array passed into the constructor
            final Ballot ballot = (Ballot) ballotConstructor.newInstance(
                23,
                new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}
            );
            Assertions.assertArrayEquals(new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}, ballot.getRankedCandidates());
        }
        //If unable to retrieve the constructor, fail
        catch(NoSuchMethodException e) {
            Assertions.fail("Could not retrieve the Ballot constructor");
        }
        //If unable to retrieve the class, fail
        catch(ClassNotFoundException e) {
            Assertions.fail("Could not retrieve the Ballot class");
        }
        //If unable to access or execute the constructor, fail
        catch(IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Assertions.fail("Unable to properly access or execute Ballot constructor");
        }
    }
    
    @Test
    void testGetNextCandidate() {
        try {
            //Retrieve the Ballot class using reflection, retrieve its constructor, and make it accessible for testing
            final Class<?> ballotClass = Class.forName("org.team19.InstantRunoffSystem$Ballot");
            final Constructor<?> ballotConstructor = ballotClass.getDeclaredConstructor(int.class, Candidate[].class);
            ballotConstructor.setAccessible(true);
    
            final Ballot ballot = (Ballot) ballotConstructor.newInstance(
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
        //If unable to retrieve the constructor, fail
        catch(NoSuchMethodException e) {
            Assertions.fail("Could not retrieve the Ballot constructor");
        }
        //If unable to retrieve the class, fail
        catch(ClassNotFoundException e) {
            Assertions.fail("Could not retrieve the Ballot class");
        }
        //If unable to access or execute the constructor, fail
        catch(IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Assertions.fail("Unable to properly access or execute Ballot constructor");
        }
    }
    
    @Test
    void testToString() {
        try {
            //Retrieve the Ballot class using reflection, retrieve its constructor, and make it accessible for testing
            final Class<?> ballotClass = Class.forName("org.team19.InstantRunoffSystem$Ballot");
            final Constructor<?> ballotConstructor = ballotClass.getDeclaredConstructor(int.class, Candidate[].class);
            ballotConstructor.setAccessible(true);
    
            //Test toString for Ballot with a sample ballot
            final Candidate c0 = new Candidate("C0", "P0");
            final Candidate c1 = new Candidate("C1", "P1");
    
            final Ballot ballot = (Ballot) ballotConstructor.newInstance(
                23,
                new Candidate[] {c0, c1}
            );
            Assertions.assertEquals(String.format("Ballot 23, [%s, %s]", c0, c1), ballot.toString());
        }
        //If unable to retrieve the constructor, fail
        catch(NoSuchMethodException e) {
            Assertions.fail("Could not retrieve the Ballot constructor");
        }
        //If unable to retrieve the class, fail
        catch(ClassNotFoundException e) {
            Assertions.fail("Could not retrieve the Ballot class");
        }
        //If unable to access or execute the constructor, fail
        catch(IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Assertions.fail("Unable to properly access or execute Ballot constructor");
        }
    }
    
    @Test
    void testEquals() {
        try {
            //Retrieve the Ballot class using reflection, retrieve its constructor, and make it accessible for testing
            final Class<?> ballotClass = Class.forName("org.team19.InstantRunoffSystem$Ballot");
            final Constructor<?> ballotConstructor = ballotClass.getDeclaredConstructor(int.class, Candidate[].class);
            ballotConstructor.setAccessible(true);
    
            //Creates typical ballot
            final Ballot ballot = (Ballot) ballotConstructor.newInstance(
                23,
                new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}
            );
    
            //Creates copy of ballot
            final Ballot ballotDoppleganger = (Ballot) ballotConstructor.newInstance(
                23,
                new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}
            );
    
            //Creates copy of ballot but advances the position to the next candidate
            final Ballot ballotDifferentPosition = (Ballot) ballotConstructor.newInstance(
                23,
                new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}
            );
            ballotDifferentPosition.getNextCandidate();
    
            //Creates copy of ballot but with a modified candidates array
            final Ballot sameNumber = (Ballot) ballotConstructor.newInstance(
                23,
                new Candidate[] {new Candidate("C0", "P0"), new Candidate("C2", "P2")}
            );
    
            //Creates copy of ballot but with a modified ballot number
            final Ballot sameRankings = (Ballot) ballotConstructor.newInstance(
                24,
                new Candidate[] {new Candidate("C0", "P0"), new Candidate("C1", "P1")}
            );
    
            Assertions.assertAll(
                //Test that ballots with equal ballot numbers, equal ranked candidates array, and equal positions in array are equal
                () -> Assertions.assertEquals(ballotDoppleganger, ballot),
                //Test that ballots with equal ballot numbers, equal ranked candidates array, and different positions in array are not equal
                () -> Assertions.assertNotEquals(ballotDifferentPosition, ballot),
                //Test that ballots with equal ballot numbers, different ranked candidates array, and equal positions in array are not equal
                () -> Assertions.assertNotEquals(sameNumber, ballot),
                //Test that ballots with different ballot numbers, equal ranked candidates array, and equal positions in array are not equal
                () -> Assertions.assertNotEquals(sameRankings, ballot)
            );
        }
        //If unable to retrieve the constructor, fail
        catch(NoSuchMethodException e) {
            Assertions.fail("Could not retrieve the Ballot constructor");
        }
        //If unable to retrieve the class, fail
        catch(ClassNotFoundException e) {
            Assertions.fail("Could not retrieve the Ballot class");
        }
        //If unable to access or execute the constructor, fail
        catch(IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Assertions.fail("Unable to properly access or execute Ballot constructor");
        }
    }
    
}
