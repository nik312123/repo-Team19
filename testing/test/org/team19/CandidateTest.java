package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class CandidateTest {
    
    private CandidateTest() {}
    
    @Test
    public void testConstructor() {
        Assertions.assertAll(
            //Testing typical case
            () -> Assertions.assertDoesNotThrow(() -> new Candidate("Obama", "Democrat")),
            //Testing that if null name or party is passed in, a NullPointerException is thrown
            () -> Assertions.assertThrows(NullPointerException.class, () -> new Candidate("Obama", null)),
            () -> Assertions.assertThrows(NullPointerException.class, () -> new Candidate(null, "Democrat")),
            () -> Assertions.assertThrows(NullPointerException.class, () -> new Candidate(null, null))
        );
    }
    
    @Test
    public void testGetName() {
        final Candidate obama = new Candidate("Obama", "Democrat");
        final Candidate romney = new Candidate("Romney", "Republican");
        final Candidate alternateUniverseObama = new Candidate("Obama", "Republican");
        final Candidate alternateUniverseRomney = new Candidate("Romney", "Democrat");
        
        Assertions.assertAll(
            //Testing that what we put as the name is what is returned by getName()
            () -> Assertions.assertEquals("Obama", obama.getName()),
            () -> Assertions.assertEquals("Romney", romney.getName()),
            //Testing the equality of two candidates' names if their names were inputted as equivalent names
            () -> Assertions.assertEquals(obama.getName(), alternateUniverseObama.getName()),
            () -> Assertions.assertEquals(romney.getName(), alternateUniverseRomney.getName())
        );
    }
    
    @Test
    public void testGetParty() {
        final Candidate obama = new Candidate("Obama", "Democrat");
        final Candidate romney = new Candidate("Romney", "Republican");
        final Candidate alternateUniverseObama = new Candidate("Obama", "Republican");
        final Candidate alternateUniverseRomney = new Candidate("Romney", "Democrat");
        
        Assertions.assertAll(
            //Testing that what we put as the name is what is returned by getParty()
            () -> Assertions.assertEquals("Democrat", obama.getParty()),
            () -> Assertions.assertEquals("Republican", romney.getParty()),
            //Testing the equality of two candidates' parties if their parties were inputted as equivalent parties
            () -> Assertions.assertEquals(obama.getParty(), alternateUniverseRomney.getParty()),
            () -> Assertions.assertEquals(romney.getParty(), alternateUniverseObama.getParty())
        );
    }
    
    @Test
    public void testToString() {
        final Candidate obama = new Candidate("Obama", "Democrat");
        final Candidate romney = new Candidate("Romney", "Republican");
        final Candidate alternateUniverseObama = new Candidate("Obama", "Republican");
        final Candidate alternateUniverseRomney = new Candidate("Romney", "Democrat");
        
        /*
         * Test that the string forms of the candidates are in the form "Candidate{name=[name], party=[party]}" where [name] and [party]
         * are replaced by the candidates' actual name and party, respectively
         */
        Assertions.assertAll(
            () -> Assertions.assertEquals("Obama (Democrat)", obama.toString()),
            () -> Assertions.assertEquals("Romney (Republican)", romney.toString()),
            () -> Assertions.assertEquals("Obama (Republican)", alternateUniverseObama.toString()),
            () -> Assertions.assertEquals("Romney (Democrat)", alternateUniverseRomney.toString())
        );
    }
    
    @Test
    public void testEquals() {
        final Candidate obama = new Candidate("Obama", "Democrat");
        final Candidate romney = new Candidate("Romney", "Republican");
        final Candidate alternateUniverseObama = new Candidate("Obama", "Republican");
        final Candidate alternateUniverseRomney = new Candidate("Romney", "Democrat");
        final Candidate obamaDoppelganger = new Candidate("Obama", "Democrat");
        final Candidate romneyDoppelganger = new Candidate("Romney", "Republican");
        
        Assertions.assertAll(
            //Testing that candidates with equivalent names and parties are equal
            () -> Assertions.assertEquals(obama, obamaDoppelganger),
            () -> Assertions.assertEquals(romney, romneyDoppelganger),
            //Testing that candidates with the same names but differing parties are not equal
            () -> Assertions.assertNotEquals(obama, alternateUniverseObama),
            () -> Assertions.assertNotEquals(romney, alternateUniverseRomney),
            //Testing that candidates with the same parties but differing names are not equal
            () -> Assertions.assertNotEquals(obama, alternateUniverseRomney),
            () -> Assertions.assertNotEquals(romney, alternateUniverseObama)
        );
        
    }
    
}
