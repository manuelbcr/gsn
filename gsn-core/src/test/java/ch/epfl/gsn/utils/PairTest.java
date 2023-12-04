package ch.epfl.gsn.utils;


import org.junit.Test;
import static org.junit.Assert.*;

public class PairTest {

    @Test
    public void testPairCreation() {
        String first = "Hello";
        Integer second = 2;

        Pair<String, Integer> pair = new Pair<>(first, second);

        assertEquals(first, pair.getFirst());
        assertEquals(second, pair.getSecond());
    }

    @Test
    public void testPairModification() {
        Pair<String, Integer> pair = new Pair<>("Hello", 1);

        String newFirst = "World";
        Integer newSecond = 99;

        pair.setFisrt(newFirst);
        pair.setSecond(newSecond);

        assertEquals(newFirst, pair.getFirst());
        assertEquals(newSecond, pair.getSecond());
    }
}

