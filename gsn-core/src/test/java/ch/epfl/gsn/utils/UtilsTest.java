package ch.epfl.gsn.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.epfl.gsn.utils.Pair;
import ch.epfl.gsn.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class UtilsTest {
    @Test
    public void testLoadProperties() throws IOException {
        File tempFile = File.createTempFile("test", ".properties");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("key1=value1\nkey2=value2");
        }

        Properties properties = Utils.loadProperties(tempFile.getAbsolutePath());
        assertNotNull(properties);
        assertEquals("value1", properties.getProperty("key1"));
        assertEquals("value2", properties.getProperty("key2"));

        Properties nonExistingProperties = Utils.loadProperties("wrong.properties");
        assertNull(nonExistingProperties);

        assertTrue(tempFile.delete());
    }

    @Test
    public void testParseWindowSize() {
        String validWindowSize = "5m";
        Pair<Boolean, Long> result = Utils.parseWindowSize(validWindowSize);
        assertTrue(result.getFirst());
        assertEquals(Long.valueOf(300000), result.getSecond());

        validWindowSize = "2h";
        result = Utils.parseWindowSize(validWindowSize);
        assertTrue(result.getFirst());
        assertEquals(Long.valueOf(2 * 3600000), result.getSecond());

        validWindowSize = "30s";
        result = Utils.parseWindowSize(validWindowSize);
        assertTrue(result.getFirst());
        assertEquals(Long.valueOf(30 * 1000), result.getSecond());

        String invalidWindowSize = "invalid";
        try {
            Utils.parseWindowSize(invalidWindowSize);
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            System.out.println((e));
        }
    }


}
