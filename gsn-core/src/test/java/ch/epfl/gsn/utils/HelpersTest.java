package ch.epfl.gsn.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class HelpersTest {
    @Test
    public void testFormatTimePeriod() {
        assertEquals("100 ms", Helpers.formatTimePeriod(100));
        assertEquals("10 sec", Helpers.formatTimePeriod(10000));
        assertEquals("1 min", Helpers.formatTimePeriod(60000));
        assertEquals("1 h", Helpers.formatTimePeriod(3600000));
        assertEquals("2 day", Helpers.formatTimePeriod(172800000));
    }

    @Test
    public void testConvertTimeFromLongToIso() {
        long timestamp = 1640995200000L;
        String expectedIsoTime = "2022-01-01T00:00:00.000Z";
        String convertedIsoTime = Helpers.convertTimeFromLongToIso(timestamp);

        assertEquals(expectedIsoTime, convertedIsoTime);
    }

    @Test
    public void testConvertTimeFromLongToIsoWithFormat() {
        long timestamp = 1672531200000L;
        String expectedIsoTime = "2023-01-01T00:00:00";
        String formatPattern = "yyyy-MM-dd'T'HH:mm:ss";

        String convertedIsoTime = Helpers.convertTimeFromLongToIso(timestamp, formatPattern);

        assertEquals(expectedIsoTime, convertedIsoTime);
    }

    @Test
    public void testConvertTimeFromIsoToLong() throws Exception {
        String isoTime = "2022-01-01T00:00:00.000Z";
        long expectedTimestamp = 1640995200000L; 
        long convertedTimestamp = Helpers.convertTimeFromIsoToLong(isoTime);

        assertEquals(expectedTimestamp, convertedTimestamp);
    }

    @Test
    public void testConvertTimeFromIsoToLongWithFormat() throws Exception {
        String isoTimeString = "2023-01-01T00:00:00";
        long expectedTimestamp = 1672531200000L; 
        String formatPattern = "yyyy-MM-dd'T'HH:mm:ss";
        long convertedTimestamp = Helpers.convertTimeFromIsoToLong(isoTimeString, formatPattern);

        assertEquals(expectedTimestamp, convertedTimestamp);
    }


    @Test
    public void testConvertGPSTimeToUnixTime() {
        double gpsTime = 518400;
        long expectedUnixTime = 1584748782; 
        long convertedUnixTime = (long) Helpers.convertGPSTimeToUnixTime(gpsTime, (short) 2097);

        assertEquals(expectedUnixTime, convertedUnixTime); 

        gpsTime = 46828800L;
        expectedUnixTime = 1560297582L; 
        convertedUnixTime = (long) Helpers.convertGPSTimeToUnixTime(gpsTime, (short) 1980);
        assertEquals(expectedUnixTime, convertedUnixTime);
    }



}
