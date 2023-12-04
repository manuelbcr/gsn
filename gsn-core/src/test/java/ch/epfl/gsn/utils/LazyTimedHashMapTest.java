package ch.epfl.gsn.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.util.List;

public class LazyTimedHashMapTest {
    private LazyTimedHashMap lazyTimedHashMap;

    @Before
    public void setUp() {
        lazyTimedHashMap = new LazyTimedHashMap(100);
    }

    @Test
    public void testPutAndGet() {
        Object key = "testKey";
        Object value = "testValue";
        lazyTimedHashMap.put(key, value);

        Object retrievedValue = lazyTimedHashMap.get(key);
        assertEquals(value, retrievedValue);
    }

    @Test
    public void testRemove() {
        Object key = "testKey";
        Object value = "testValue";

        
        lazyTimedHashMap.put(key, value);


        Object removedValue = lazyTimedHashMap.remove(key);
        assertEquals(value, removedValue);
    }

    @Test
    public void testExpiredElement() throws InterruptedException {
        Object key = "testKey";
        Object value = "testValue";


        lazyTimedHashMap.put(key, value);

  
        Thread.sleep(200);

 
        Object retrievedValue = lazyTimedHashMap.get(key);
        assertNull(retrievedValue);
    }

    @Test
    public void testGetKeys() {
        Object key1 = "testKey1";
        Object value1 = "testValue1";
        Object key2 = "testKey2";
        Object value2 = "testValue2";

      
        lazyTimedHashMap.put(key1, value1);
        lazyTimedHashMap.put(key2, value2);

        
        List<Object> keys = lazyTimedHashMap.getKeys();
        assertTrue(keys.contains(key1));
        assertTrue(keys.contains(key2));
        assertEquals(2, keys.size());
    }

    @Test
    public void testGetValues() {
        Object key1 = "testKey1";
        Object value1 = "testValue1";
        Object key2 = "testKey2";
        Object value2 = "testValue2";

   
        lazyTimedHashMap.put(key1, value1);
        lazyTimedHashMap.put(key2, value2);

 
        List<Object> values = lazyTimedHashMap.getValues();
        assertTrue(values.contains(value1));
        assertTrue(values.contains(value2));
        assertEquals(2, values.size());
    }
}
