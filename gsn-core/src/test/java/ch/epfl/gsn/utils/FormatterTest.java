package ch.epfl.gsn.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FormatterTest {
    @Test
    public void testListArrayInt() {
        int[] array = {10, 20, 30, 40, 50};

        String result = Formatter.listArray(array, 1, 3);

        assertEquals("20 30 40 ( 3)", result);
        result = Formatter.listArray(array);
        assertEquals("10 20 30 40 50 ( 5)", result);

        result = Formatter.listArray(array,4);
        assertEquals("10 20 30 40 ( 4)", result);

        result = Formatter.listArray(array,4,true);
        assertEquals("0a 14 1e 28 ( 4)", result);

    }

    @Test
    public void testListArrayByte() {
        byte[] array = {0x0A, 0x14, 0x1E, 0x28, 0x32};

        String result = Formatter.listArray(array, 2, 4);
        assertEquals("30 40 50 ( 3)", result);

        result = Formatter.listArray(array,4);
        assertEquals("10 20 30 40 ( 4)", result);

        result = Formatter.listArray(array,4,true);
        assertEquals("0a 14 1e 28 ( 4)", result);
    }

    @Test
    public void testListArrayUnsignedByte() {
        UnsignedByte[] array = {new UnsignedByte(10),new UnsignedByte(20),new UnsignedByte(30),new UnsignedByte(40),new UnsignedByte(50)};
        
        String result = Formatter.listArray(array, 1, 3, false);
        assertEquals("20 30 40 ( 3)", result);

        result = Formatter.listArray(array,4);
        assertEquals("10 20 30 40 ( 4)", result);

        result = Formatter.listArray(array,4,true);
        assertEquals("0a 14 1e 28 ( 4)", result);
    }

    @Test
    public void testListUnsignedByteList() {
        List<UnsignedByte> byteList = new ArrayList<>(Arrays.asList(new UnsignedByte(10),new UnsignedByte(20),new UnsignedByte(30),new UnsignedByte(40),new UnsignedByte(50)));
        String result = Formatter.listUnsignedByteList(byteList);
        assertEquals("10 20 30 40 50 ( 5)", result);
        
        result = Formatter.listUnsignedByteList(byteList,true);
        assertEquals("0a 14 1e 28 32 ( 5)", result);
    }

}
