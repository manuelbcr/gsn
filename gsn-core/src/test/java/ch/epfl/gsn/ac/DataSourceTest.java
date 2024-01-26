package ch.epfl.gsn.ac;

import static org.junit.Assert.*;



import org.junit.Test;

public class DataSourceTest {
     @Test
    public void testDataSource(){
        DataSource ds1= new DataSource("test1");
        DataSource ds2= new DataSource("test2","2","test.xml","xml","test","access");
        DataSource ds3= new DataSource("test3","3");
        DataSource ds4= new DataSource("test4","4","access");
        DataSource ds5= new DataSource("test5");

        assertEquals("test1",ds1.getDataSourceName());
        assertEquals("test2",ds2.getDataSourceName());
        assertEquals("test3",ds3.getDataSourceName());
        assertEquals("test4",ds4.getDataSourceName());
        ds1.setDataSourceType("1");
        assertEquals("1",ds1.getDataSourceType());
        assertEquals("2",ds2.getDataSourceType());
        assertEquals("3",ds3.getDataSourceType());
        assertEquals("4",ds4.getDataSourceType());
        ds1.setFileName("test1.xml");
        assertEquals("test1.xml",ds1.getFileName());
        ds1.setFileType("xml");
        assertEquals("xml", ds1.getFileType());
        ds1.setPath("test");
        assertEquals("test",ds1.getPath());
        ds1.setIsCandidate("yes");
        assertEquals("yes",ds1.getIsCandidate());
        ds1.setOwnerDecision("access");
        assertEquals("access",ds1.getOwnerDecision());
        ds1.setDataSourceName("datasourcetest");
        assertEquals("datasourcetest",ds1.getDataSourceName());
        ds1.setDataSourceName("test1");
        assertEquals("test1",ds1.getDataSourceName());

        assertFalse(ds1.hasWriteAccessRight("test1"));
        assertFalse(ds1.hasReadWriteAccessRight("test1"));
        assertFalse(ds1.hasOwnAccessRight("test1"));
        assertTrue(ds1.hasReadAccessRight("test1"));

        assertTrue(ds2.hasWriteAccessRight("test2"));
        assertFalse(ds2.hasReadWriteAccessRight("test2"));
        assertFalse(ds2.hasOwnAccessRight("test2"));
        assertFalse(ds2.hasReadAccessRight("test2"));

        assertTrue(ds3.hasWriteAccessRight("test3"));
        assertTrue(ds3.hasReadWriteAccessRight("test3"));
        assertFalse(ds3.hasOwnAccessRight("test3"));
        assertTrue(ds3.hasReadAccessRight("test3"));

        assertTrue(ds4.hasWriteAccessRight("test4"));
        assertTrue(ds4.hasReadWriteAccessRight("test4"));
        assertTrue(ds4.hasOwnAccessRight("test4"));
        assertTrue(ds4.hasReadAccessRight("test4"));

        assertEquals("read", ds1.getAccessRightsString());
        assertEquals("write",ds2.getAccessRightsString());
        assertEquals("read/write",ds3.getAccessRightsString());
        assertEquals("own",ds4.getAccessRightsString());
        
        ds5.setDataSourceType("nothing");
        assertEquals("unknown",ds5.getAccessRightsString());
        User u= new User("test");
        ds5.setOwner(u);
        assertEquals(u,ds5.getOwner());
        
        
    }
}
