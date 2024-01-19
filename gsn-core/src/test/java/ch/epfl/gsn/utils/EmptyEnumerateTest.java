package ch.epfl.gsn.utils;



import org.junit.Test;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

public class EmptyEnumerateTest {
    
    @Test
    public void testHasMoreElements() {
        EmptyEnumerate<String> emptyEnumerate = new EmptyEnumerate<>();

        assertFalse(emptyEnumerate.hasMoreElements());
    }

    @Test
    public void testNextElement() throws NoSuchElementException{
        EmptyEnumerate<String> emptyEnumerate = new EmptyEnumerate<>();
        emptyEnumerate.nextElement();
    }
}
