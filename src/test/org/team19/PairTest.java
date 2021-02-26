package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Random;

class PairTest {
    
    @Test
    void testGetKey() {
        Assertions.assertAll(
            () -> Assertions.assertEquals(new Pair<>(5, 3).getKey(), 5),
            () -> Assertions.assertNull(new Pair<>(null, 3).getKey()),
            () -> Assertions.assertEquals(new Pair<>("Hello", 3).getKey(), "Hello"),
            () -> Assertions.assertEquals(new Pair<>('c', "Hello").getKey(), 'c')
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
                Assertions.assertEquals(new Pair<>(randomObjs[finalI], randomObjs[rand.nextInt(randomObjsLen)]).getKey(), randomObjs[finalI]);
        }
        
        //Runs the random tests
        Assertions.assertAll(randTests);
    }
    
    @Test
    void testGetValue() {
        Assertions.assertAll(
            () -> Assertions.assertEquals(new Pair<>(3, 5).getValue(), 5),
            () -> Assertions.assertNull(new Pair<>(3, null).getValue()),
            () -> Assertions.assertEquals(new Pair<>(3, "Hello").getValue(), "Hello"),
            () -> Assertions.assertEquals(new Pair<>("Hello", 'c').getValue(), 'c')
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
                Assertions.assertEquals(new Pair<>(randomObjs[rand.nextInt(randomObjsLen)], randomObjs[finalI]).getValue(), randomObjs[finalI]);
        }
    
        //Runs the random tests
        Assertions.assertAll(randTests);
    }
    
    @Test
    void testToString() {
        Assertions.assertAll(
            () -> Assertions.assertEquals(new Pair<>(3, 5).toString(), "Pair{3, 5}"),
            () -> Assertions.assertEquals(new Pair<>(3, null).toString(), "Pair{3, null}"),
            () -> Assertions.assertEquals(new Pair<>(3, "Hello").toString(), "Pair{3, Hello}"),
            () -> Assertions.assertEquals(new Pair<>("Hello", 'c').toString(), "Pair{Hello, c}")
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
                    Assertions.assertEquals(new Pair<>(randomObjs[finalI], randomObjs[finalJ]).toString(), String.format("Pair{%s, %s}",
                        randomObjs[finalI], randomObjs[finalJ]));
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
            () -> Assertions.assertEquals(new Pair<>(3, (Number) 10), new Pair<>(3, 10)),
            () -> Assertions.assertEquals(new Pair<>((Object) 5, 3), new Pair<>(5, 3)),
            () -> Assertions.assertEquals(new Pair<>(null, 4), new Pair<>(null, 4)),
            () -> Assertions.assertNotEquals(new Pair<>(new Pair<>(3, 5), null), new Pair<>(new Pair<>(3, 4), null))
        );
    }
}
