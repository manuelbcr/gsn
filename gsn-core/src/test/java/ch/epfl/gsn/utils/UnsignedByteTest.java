package ch.epfl.gsn.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UnsignedByteTest {
    
    @Test
    public void testConstructor() {
        UnsignedByte ub = new UnsignedByte((byte) 1);
        assertEquals(1, ub.getInt());
        assertEquals((byte) 1, ub.getByte());

        ub = new UnsignedByte(2);
        assertEquals(2, ub.getInt());
        assertEquals((byte) 2, ub.getByte());
    }

    @Test
    public void testSetValueWithByte() {
        UnsignedByte ub = new UnsignedByte();
        ub.setValue((byte) 1);
        assertEquals(1, ub.getInt());
        assertEquals((byte) 1, ub.getByte());
    }

    @Test
    public void testSetValueWithInt() {
        UnsignedByte ub = new UnsignedByte();
        ub.setValue(200);
        assertEquals(200, ub.getInt());
        assertEquals((byte) 200, ub.getByte());
    }

    @Test
    public void testToString() {
        UnsignedByte ub = new UnsignedByte(15);
        assertEquals("(byte:15, int:15)", ub.toString());
    }

    @Test
    public void testUnsignedByteArray2ByteArray() {
        UnsignedByte[] ubArray = {new UnsignedByte(1), new UnsignedByte(2), new UnsignedByte(3)};
        byte[] byteArray = UnsignedByte.UnsignedByteArray2ByteArray(ubArray);
        assertArrayEquals(new byte[]{1, 2, 3}, byteArray);
    }

    @Test
    public void testByteArray2UnsignedByteArray() {
        byte[] byteArray = {4, 5, 6};
        UnsignedByte[] ubArray = UnsignedByte.ByteArray2UnsignedByteArray(byteArray);

        assertEquals(4, ubArray[0].getInt());
        assertEquals(5, ubArray[1].getInt());
        assertEquals(6, ubArray[2].getInt());
    }
}
