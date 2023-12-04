package ch.epfl.gsn.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CaseInsensitiveComparatorTest {

    @Test
    public void testCompareEqualStrings() {
        CaseInsensitiveComparator comparator = new CaseInsensitiveComparator();
        int result = comparator.compare("GlobalSensorNetworks", "globalsensornetworks");
        assertEquals(0, result);
    }

    @Test
    public void testCompareDifferentStrings() {
        CaseInsensitiveComparator comparator = new CaseInsensitiveComparator();
        int result = comparator.compare("teststring", "Tststring");
        assertEquals(true, result < 0);
    }
    @Test
    public void testCompareNullInput() {
        CaseInsensitiveComparator comparator = new CaseInsensitiveComparator();
        int result = comparator.compare(null, "test");

        assertEquals(true, result < 0);
        result = comparator.compare("test", null);
        assertEquals(true, result > 0);
        result = comparator.compare(null, null);
        assertEquals(0, result);
    }
}
