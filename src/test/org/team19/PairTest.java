package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    }
    
    @Test
    void testGetValue() {
        Assertions.assertAll(
            () -> Assertions.assertEquals(5, new Pair<>(3, 5).getValue()),
            () -> Assertions.assertNull(new Pair<>(3, null).getValue()),
            () -> Assertions.assertEquals("Hello", new Pair<>(3, "Hello").getValue()),
            () -> Assertions.assertEquals('c', new Pair<>("Hello", 'c').getValue())
        );
    }
    
    @Test
    void testToString() {
        Assertions.assertAll(
            () -> Assertions.assertEquals("Pair{3, 5}", new Pair<>(3, 5).toString()),
            () -> Assertions.assertEquals("Pair{3, null}", new Pair<>(3, null).toString()),
            () -> Assertions.assertEquals("Pair{3, Hello}", new Pair<>(3, "Hello").toString()),
            () -> Assertions.assertEquals("Pair{Hello, c}", new Pair<>("Hello", 'c').toString())
        );
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
