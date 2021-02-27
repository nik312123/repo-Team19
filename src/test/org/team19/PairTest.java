package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class PairTest {
    
    private PairTest() {}
    
    @Test
    void testGetKey() {
        final Object objInstance = new Object();
        final Pair<Integer, Integer> twoFour = new Pair<>(2, 4);
        final Pair<Character, Boolean> cFalse = new Pair<>('c', false);
        final Pair<Integer, Boolean> twoFalse = new Pair<>(2, false);
        final Pair<Object, Integer> objFour = new Pair<>(objInstance, 4);
        final Pair<String, String> nullDemocrat = new Pair<>(null, "Democrat");
        final Pair<String, Integer> obamaNull = new Pair<>("Obama", null);
        
        //Testing that keys are equivalennt to what was put into the constructor for the key
        Assertions.assertAll(
            () -> Assertions.assertEquals(2, twoFour.getKey()),
            () -> Assertions.assertEquals('c', cFalse.getKey()),
            () -> Assertions.assertEquals(2, twoFalse.getKey()),
            () -> Assertions.assertEquals(objInstance, objFour.getKey()),
            //Special case: null key
            () -> Assertions.assertNull(nullDemocrat.getKey()),
            () -> Assertions.assertEquals("Obama", obamaNull.getKey())
        );
    }
    
    @Test
    void testGetValue() {
        final Object objInstance = new Object();
        final Pair<Integer, Integer> twoFour = new Pair<>(2, 4);
        final Pair<Character, Boolean> cFalse = new Pair<>('c', false);
        final Pair<Integer, Boolean> twoFalse = new Pair<>(2, false);
        final Pair<Object, Integer> objFour = new Pair<>(objInstance, 4);
        final Pair<String, String> nullDemocrat = new Pair<>(null, "Democrat");
        final Pair<String, Integer> obamaNull = new Pair<>("Obama", null);
        
        //Testing that keys are equivalennt to what was put into the constructor for the value
        Assertions.assertAll(
            () -> Assertions.assertEquals(4, twoFour.getValue()),
            () -> Assertions.assertEquals(false, cFalse.getValue()),
            () -> Assertions.assertEquals(false, twoFalse.getValue()),
            () -> Assertions.assertEquals(4, objFour.getValue()),
            () -> Assertions.assertEquals("Democrat", nullDemocrat.getValue()),
            //Special case: null value
            () -> Assertions.assertNull(obamaNull.getValue())
        );
    }
    
    @Test
    void testToString() {
        final Object objInstance = new Object();
        final Pair<Integer, Integer> twoFour = new Pair<>(2, 4);
        final Pair<Character, Boolean> cFalse = new Pair<>('c', false);
        final Pair<Integer, Boolean> twoFalse = new Pair<>(2, false);
        final Pair<Object, Integer> objFour = new Pair<>(objInstance, 4);
        final Pair<String, String> nullDemocrat = new Pair<>(null, "Democrat");
        final Pair<String, Integer> obamaNull = new Pair<>("Obama", null);
        
        //Testing that toString prints the pairs in the format Pair{[key], [value]} where [key] and [value] are the pair's key and value, respectively
        Assertions.assertAll(
            () -> Assertions.assertEquals("Pair{2, 4}", twoFour.toString()),
            () -> Assertions.assertEquals("Pair{c, false}", cFalse.toString()),
            () -> Assertions.assertEquals("Pair{2, false}", twoFalse.toString()),
            () -> Assertions.assertEquals(String.format("Pair{%s, 4}", objInstance.toString()), objFour.toString()),
            () -> Assertions.assertEquals("Pair{null, Democrat}", nullDemocrat.toString()),
            () -> Assertions.assertEquals("Pair{Obama, null}", obamaNull.toString())
        );
    }
    
    @Test
    void testEquals() {
        final Object objInstance = new Object();
        final Pair<Integer, Integer> twoFour = new Pair<>(2, 4);
        final Pair<Character, Boolean> cFalse = new Pair<>('c', false);
        final Pair<Integer, Boolean> twoFalse = new Pair<>(2, false);
        final Pair<Object, Integer> objFour = new Pair<>(objInstance, 4);
        final Pair<String, String> nullDemocrat = new Pair<>(null, "Democrat");
        final Pair<String, Integer> obamaNull = new Pair<>("Obama", null);
        final Pair<Character, Boolean> cTrue = new Pair<>('c', true);
        final Pair<Integer, Integer> threeFour = new Pair<>(3, 4);
        
        Assertions.assertAll(
            //Testing pairs equivalent to the above variables (same input)
            () -> Assertions.assertEquals(new Pair<>(2, 4), twoFour),
            () -> Assertions.assertEquals(new Pair<>('c', false), cFalse),
            () -> Assertions.assertEquals(new Pair<>(2, false), twoFalse),
            () -> Assertions.assertEquals(new Pair<>(objInstance, 4), objFour),
            () -> Assertions.assertEquals(new Pair<>(null, "Democrat"), nullDemocrat),
            () -> Assertions.assertEquals(new Pair<>("Obama", null), obamaNull),
            
            //Testing that having equivalent keys but differing values does not result in equal pairs
            () -> Assertions.assertNotEquals(twoFalse, twoFour),
            () -> Assertions.assertNotEquals(cTrue, cFalse),
            
            //Testing that having equivalent values but differing keys does not result in equal pairs
            () -> Assertions.assertNotEquals(cFalse, twoFalse),
            () -> Assertions.assertNotEquals(twoFour, threeFour)
        );
    }
}
