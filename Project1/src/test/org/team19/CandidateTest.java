/*
 * File name:
 * CandidateTest.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Tests the Candidate class
 */

package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class CandidateTest {
    
    private CandidateTest() {}
    
    @Test
    void testConstructor() {
        Assertions.assertAll(
            //Testing typical case
            () -> Assertions.assertDoesNotThrow(() -> new Candidate("Obama", "Democrat")),
            //Testing that if null name or party is passed in, a NullPointerException is thrown
            () -> Assertions.assertThrows(NullPointerException.class, () -> new Candidate("Obama", null)),
            () -> Assertions.assertThrows(NullPointerException.class, () -> new Candidate(null, "Democrat"))
        );
    }
    
    @Test
    void testGetName() {
        final Candidate obama = new Candidate("Obama", "Democrat");
        
        //Testing that what we put as the name is what is returned by getName()
        Assertions.assertEquals("Obama", obama.getName());
    }
    
    @Test
    void testGetParty() {
        final Candidate obama = new Candidate("Obama", "Democrat");
        
        //Testing that what we put as the name is what is returned by getParty()
        Assertions.assertEquals("Democrat", obama.getParty());
    }
    
    @Test
    void testToString() {
        final Candidate obama = new Candidate("Obama", "Democrat");
        
        /*
         * Test that the string forms of the candidates are in the form "Candidate{name=[name], party=[party]}" where [name] and [party]
         * are replaced by the candidates' actual name and party, respectively
         */
        Assertions.assertEquals("Obama (Democrat)", obama.toString());
    }
    
    @Test
    void testEquals() {
        final Candidate obama = new Candidate("Obama", "Democrat");
        final Candidate obamaDoppelganger = new Candidate("Obama", "Democrat");
        final Candidate alternateUniverseObama = new Candidate("Obama", "Republican");
        final Candidate alternateUniverseRomney = new Candidate("Romney", "Democrat");
        
        Assertions.assertAll(
            //Testing that candidates with equivalent names and parties are equal
            () -> Assertions.assertEquals(obama, obamaDoppelganger),
            //Testing that candidates with the same names but differing parties are not equal
            () -> Assertions.assertNotEquals(obama, alternateUniverseObama),
            //Testing that candidates with the same parties but differing names are not equal
            () -> Assertions.assertNotEquals(obama, alternateUniverseRomney)
        );
        
    }
    
}
