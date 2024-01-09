package ch.epfl.gsn.delivery.datarequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OutputInputStreamTest {
    
    @Test
    public void testOutputStreamWrite() throws IOException {
        OutputInputStream ois = new OutputInputStream(4);
        OutputStream os = ois.getOutputStream();

        os.write("ABCD".getBytes());
        os.close();

        InputStream is = ois.getInputStream();
        byte[] buffer = new byte[10];
        int bytesRead = is.read(buffer);

        assertEquals("ABCD", new String(buffer, 0, bytesRead));
    }

    @Test(expected = IOException.class)
    public void testOutputStreamWriteAfterClose() throws IOException {
        OutputInputStream ois = new OutputInputStream(4);
        OutputStream os = ois.getOutputStream();

        os.write("ABCD".getBytes());
        os.close();

        os.write("EFGH".getBytes());
    }

    @Test
    public void testInputStreamAvailable() throws IOException {
        OutputInputStream ois = new OutputInputStream(4);
        OutputStream os = ois.getOutputStream();

        os.write("ABCD".getBytes());
        os.close();

        InputStream is = ois.getInputStream();
        int available = is.available();

        assertEquals(4, available);
    }
    
    @Test
    public void testReadAndWrite() throws IOException {
        OutputInputStream ois = new OutputInputStream(4);
        OutputStream os = ois.getOutputStream();

        os.write("ABCD".getBytes());
        os.close();

        InputStream is = ois.getInputStream();
        assertEquals('A', is.read());
        assertEquals('B', is.read());
        assertEquals('C', is.read());
        assertEquals('D', is.read());

        assertEquals(-1, is.read());
        is.close();
        ois.close();
    }


}
