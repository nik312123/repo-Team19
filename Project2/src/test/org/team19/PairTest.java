/*
 * File name:
 * PairTest.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Tests the Pair class
 */

package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class PairTest {
    
    private PairTest() {}
    
    @Test
    void testGetKey() {
        final Object objInstance = new Object();
        final Pair<Integer, Integer> twoFour = new Pair<>(2, 4);
        final Pair<Object, Integer> objFour = new Pair<>(objInstance, 4);
        final Pair<String, String> nullDemocrat = new Pair<>(null, "Democrat");
        
        //Testing that keys are equivalent to what was put into the constructor for the key
        Assertions.assertAll(
            //Testing that a key is equal to what is put into the constructor
            () -> Assertions.assertEquals(2, twoFour.getKey()),
            //Testing essentially object equals with the key, that is ==
            () -> Assertions.assertEquals(objInstance, objFour.getKey()),
            //Special case: null key
            () -> Assertions.assertNull(nullDemocrat.getKey())
        );
    }
    
    @Test
    void testGetFirst() {
        final Object objInstance = new Object();
        final Pair<Integer, Integer> twoFour = new Pair<>(2, 4);
        final Pair<Object, Integer> objFour = new Pair<>(objInstance, 4);
        final Pair<String, String> nullDemocrat = new Pair<>(null, "Democrat");
        
        //Testing that keys are equivalent to what was put into the constructor for the key
        Assertions.assertAll(
            //Testing that a key is equal to what is put into the constructor
            () -> Assertions.assertEquals(2, twoFour.getFirst()),
            //Testing essentially object equals with the key, that is ==
            () -> Assertions.assertEquals(objInstance, objFour.getFirst()),
            //Special case: null key
            () -> Assertions.assertNull(nullDemocrat.getFirst())
        );
    }
    
    @Test
    void testGetValue() {
        final Object objInstance = new Object();
        final Pair<Integer, Integer> twoFour = new Pair<>(2, 4);
        final Pair<Object, Object> cObj = new Pair<>('c', objInstance);
        final Pair<String, Integer> obamaNull = new Pair<>("Obama", null);
        
        //Testing that values are equivalent to what was put into the constructor for the value
        Assertions.assertAll(
            //Testing that a value is equal to what is put into the constructor
            () -> Assertions.assertEquals(4, twoFour.getValue()),
            //Testing essentially object equals with the value, that is ==
            () -> Assertions.assertEquals(objInstance, cObj.getValue()),
            //Special case: null value
            () -> Assertions.assertNull(obamaNull.getValue())
        );
    }
    
    @Test
    void testGetSecond() {
        final Object objInstance = new Object();
        final Pair<Integer, Integer> twoFour = new Pair<>(2, 4);
        final Pair<Object, Object> cObj = new Pair<>('c', objInstance);
        final Pair<String, Integer> obamaNull = new Pair<>("Obama", null);
        
        //Testing that values are equivalent to what was put into the constructor for the value
        Assertions.assertAll(
            //Testing that a value is equal to what is put into the constructor
            () -> Assertions.assertEquals(4, twoFour.getSecond()),
            //Testing essentially object equals with the value, that is ==
            () -> Assertions.assertEquals(objInstance, cObj.getSecond()),
            //Special case: null value
            () -> Assertions.assertNull(obamaNull.getSecond())
        );
    }
    
    @Test
    void testSetValue() {
        final Pair<Integer, Integer> twoFour = new Pair<>(2, 4);
        final Pair<String, String> nullDemocrat = new Pair<>(null, "Democrat");
        final Pair<String, Integer> obamaNull = new Pair<>("Obama", null);
        
        //Testing that setting the values of pairs actually changes the values
        Assertions.assertEquals(4, twoFour.getValue());
        twoFour.setValue(2);
        Assertions.assertEquals(2, twoFour.getValue());
        
        //Special case: Assigning to null
        Assertions.assertEquals("Democrat", nullDemocrat.getValue());
        nullDemocrat.setValue(null);
        Assertions.assertNull(nullDemocrat.getValue());
        
        //Special case: Assigning from null
        Assertions.assertNull(obamaNull.getValue());
        obamaNull.setValue(59);
        Assertions.assertEquals(59, obamaNull.getValue());
    }
    
    @Test
    void testSetSecond() {
        final Pair<Integer, Integer> twoFour = new Pair<>(2, 4);
        final Pair<String, String> nullDemocrat = new Pair<>(null, "Democrat");
        final Pair<String, Integer> obamaNull = new Pair<>("Obama", null);
        
        //Testing that setting the values of pairs actually changes the values
        Assertions.assertEquals(4, twoFour.getSecond());
        twoFour.setSecond(2);
        Assertions.assertEquals(2, twoFour.getSecond());
        
        //Special case: Assigning to null
        Assertions.assertEquals("Democrat", nullDemocrat.getSecond());
        nullDemocrat.setSecond(null);
        Assertions.assertNull(nullDemocrat.getSecond());
        
        //Special case: Assigning from null
        Assertions.assertNull(obamaNull.getSecond());
        obamaNull.setSecond(59);
        Assertions.assertEquals(59, obamaNull.getSecond());
    }
    
    @Test
    void testToString() {
        final Pair<Integer, Integer> twoFour = new Pair<>(2, 4);
        final Pair<String, String> nullDemocrat = new Pair<>(null, "Democrat");
        final Pair<String, Integer> obamaNull = new Pair<>("Obama", null);
        
        //Testing that toString prints the pairs in the format Pair{[key], [value]} where [key] and [value] are the pair's key and value, respectively
        Assertions.assertAll(
            () -> Assertions.assertEquals("Pair{2, 4}", twoFour.toString()),
            //Special case: null key
            () -> Assertions.assertEquals("Pair{null, Democrat}", nullDemocrat.toString()),
            //Special case: null value
            () -> Assertions.assertEquals("Pair{Obama, null}", obamaNull.toString())
        );
    }
    
    @Test
    void testEquals() {
        final Pair<Integer, String> twoHi = new Pair<>(2, "Hi");
        final Pair<Character, Integer> cSix = new Pair<>('c', 6);
        final Pair<Integer, Integer> twoSix = new Pair<>(2, 6);
        final Pair<String, String> nullDemocrat = new Pair<>(null, "Democrat");
        final Pair<String, Integer> obamaNull = new Pair<>("Obama", null);
        
        Assertions.assertAll(
            //Testing pairs equivalent to the above variables (same input)
            () -> Assertions.assertEquals(new Pair<>(2, "Hi"), twoHi),
            //Special case: null key
            () -> Assertions.assertEquals(new Pair<>(null, "Democrat"), nullDemocrat),
            //Special case: null value
            () -> Assertions.assertEquals(new Pair<>("Obama", null), obamaNull),
            
            //Testing that having equivalent keys but differing values does not result in equal pairs
            () -> Assertions.assertNotEquals(twoSix, twoHi),
            
            //Testing that having equivalent values but differing keys does not result in equal pairs
            () -> Assertions.assertNotEquals(cSix, twoSix)
        );
    }
}
