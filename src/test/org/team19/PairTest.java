package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Random;

class PairTest {
    
    private PairTest() {}
    
    @Test
    void testGetKey() {
        Assertions.assertAll(
            () -> Assertions.assertEquals(5, new Pair<>(5, 3).getKey()),
            () -> Assertions.assertEquals("Hello", new Pair<>("Hello", 3).getKey()),
            () -> Assertions.assertEquals('c', new Pair<>('c', "Hello").getKey()),
            () -> Assertions.assertNull(new Pair<>(null, 3).getKey())
        );
        
        /*
         * Creates 33 random objects consisting of null, the two booleans (true and false), 10 random integers, 10 random doubles, and 10 random
         * characters
         */
        final int randomObjsLen = 33;
        final Object[] randomObjs = new Object[randomObjsLen];
        final Random rand = new Random();
        randomObjs[0] = null;
        randomObjs[1] = true;
        randomObjs[2] = false;
        for(int i = 3; i < 13; ++i) {
            randomObjs[i] = rand.nextInt();
            randomObjs[i + 10] = rand.nextDouble();
            randomObjs[i + 20] = (char) rand.nextInt(Character.MAX_VALUE + 1);
        }
        
        //Compiles each random object and a randomly chosen value from the random objects into a list of tests
        final Executable[] randTests = new Executable[randomObjsLen];
        for(int i = 0; i < randomObjsLen; ++i) {
            final int finalI = i;
            randTests[i] = () ->
                Assertions.assertEquals(randomObjs[finalI], new Pair<>(randomObjs[finalI], randomObjs[rand.nextInt(randomObjsLen)]).getKey());
        }
        
        //Runs the random tests
        Assertions.assertAll(randTests);
    }
    
    @Test
    void testGetValue() {
        Assertions.assertAll(
            () -> Assertions.assertEquals(5, new Pair<>(3, 5).getValue()),
            () -> Assertions.assertNull(new Pair<>(3, null).getValue()),
            () -> Assertions.assertEquals("Hello", new Pair<>(3, "Hello").getValue()),
            () -> Assertions.assertEquals('c', new Pair<>("Hello", 'c').getValue())
        );
        
        /*
         * Creates 33 random objects consisting of null, the two booleans (true and false), 10 random integers, 10 random doubles, and 10 random
         * characters
         */
        final int randomObjsLen = 33;
        final Object[] randomObjs = new Object[randomObjsLen];
        final Random rand = new Random();
        randomObjs[0] = null;
        randomObjs[1] = true;
        randomObjs[2] = false;
        for(int i = 3; i < 13; ++i) {
            randomObjs[i] = rand.nextInt();
            randomObjs[i + 10] = rand.nextDouble();
            randomObjs[i + 20] = (char) rand.nextInt(Character.MAX_VALUE + 1);
        }
        
        //Compiles each random object and a randomly chosen value from the random objects into a list of tests
        final Executable[] randTests = new Executable[randomObjsLen];
        for(int i = 0; i < randomObjsLen; ++i) {
            final int finalI = i;
            randTests[i] = () ->
                Assertions.assertEquals(randomObjs[finalI], new Pair<>(randomObjs[rand.nextInt(randomObjsLen)], randomObjs[finalI]).getValue());
        }
        
        //Runs the random tests
        Assertions.assertAll(randTests);
    }
    
    @Test
    void testToString() {
        Assertions.assertAll(
            () -> Assertions.assertEquals("Pair{3, 5}", new Pair<>(3, 5).toString()),
            () -> Assertions.assertEquals("Pair{3, null}", new Pair<>(3, null).toString()),
            () -> Assertions.assertEquals("Pair{3, Hello}", new Pair<>(3, "Hello").toString()),
            () -> Assertions.assertEquals("Pair{Hello, c}", new Pair<>("Hello", 'c').toString())
        );
        
        /*
         * Creates 33 random objects consisting of null, the two booleans (true and false), 10 random integers, 10 random doubles, and 10 random
         * characters
         */
        final int randomObjsLen = 33;
        final Object[] randomObjs = new Object[randomObjsLen];
        final Random rand = new Random();
        randomObjs[0] = null;
        randomObjs[1] = true;
        randomObjs[2] = false;
        for(int i = 3; i < 13; ++i) {
            randomObjs[i] = rand.nextInt();
            randomObjs[i + 10] = rand.nextDouble();
            randomObjs[i + 20] = (char) rand.nextInt(Character.MAX_VALUE + 1);
        }
        
        //Compiles each random object and a randomly chosen value from the random objects into a list of tests
        final Executable[] randTests = new Executable[randomObjsLen * randomObjsLen];
        for(int i = 0; i < randomObjsLen; ++i) {
            for(int j = 0; j < randomObjsLen; ++j) {
                final int finalI = i;
                final int finalJ = j;
                randTests[i * randomObjsLen + j] = () ->
                    Assertions.assertEquals(String.format(
                        "Pair{%s, %s}",
                        randomObjs[finalI], randomObjs[finalJ]), new Pair<>(randomObjs[finalI], randomObjs[finalJ]).toString()
                    );
            }
        }
        
        //Runs the random tests
        Assertions.assertAll(randTests);
    }
    
    @Test
    void testEquals() {
        Assertions.assertAll(
            () -> Assertions.assertEquals(new Pair<>(3, 5), new Pair<>(3, 5)),
            () -> Assertions.assertNotEquals(new Pair<>(3, 5), new Pair<>(3, 4)),
            () -> Assertions.assertEquals(new Pair<>(3, 10), new Pair<>(3, (Number) 10)),
            () -> Assertions.assertEquals(new Pair<>(5, 3), new Pair<>((Object) 5, 3)),
            () -> Assertions.assertEquals(new Pair<>(null, 4), new Pair<>(null, 4)),
            () -> Assertions.assertNotEquals(new Pair<>(new Pair<>(3, 5), null), new Pair<>(new Pair<>(3, 4), null))
        );
    }
}
