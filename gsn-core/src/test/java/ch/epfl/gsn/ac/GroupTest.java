package ch.epfl.gsn.ac;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

public class GroupTest {

    @Test
    public void testGroup(){
        Group g= new Group("test");
        assertEquals("test",g.getGroupName());
        g.setGroupName("group");
        g.setGroupType("add");
        assertEquals("group",g.getGroupName());
        assertEquals("add",g.getGroupType());

        Group g2= new Group("testgroup","n");
        assertEquals("testgroup",g2.getGroupName());
        assertEquals("n",g2.getGroupType());
        DataSource ds= new DataSource("test","3","test.xml","xml","test");
        Vector<DataSource> dataSourceVector = new Vector<>();
        dataSourceVector.add(ds);
        g2.setDataSourceList(dataSourceVector);
        assertEquals(dataSourceVector,g2.getDataSourceList());
        assertTrue(g2.hasReadAccessRight("test"));
        assertTrue(g2.hasWriteAccessRight("test"));
        assertTrue(g2.hasReadWriteAccessRight("test"));
        
        Group g3= new Group("testgroup",dataSourceVector);
        assertEquals(dataSourceVector,g3.getDataSourceList());
        assertEquals("testgroup",g3.getGroupName());

        Group g4= new Group("testgroup","add",dataSourceVector);
        assertEquals(dataSourceVector,g4.getDataSourceList());
        assertEquals("testgroup",g4.getGroupName());
    }
}
