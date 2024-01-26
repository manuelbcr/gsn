package ch.epfl.gsn.ac;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

public class UserTest {
    @Test
    public void testUser() {
        User u= new User("test");
        assertEquals("test",u.getUserName());
        u.setUserName("user");
        assertEquals("user",u.getUserName());
        u.setPassword("<PASSWORD>");
        assertEquals("<PASSWORD>",u.getPassword());
        u.setFirstName("firstName");
        assertEquals("firstName",u.getFirstName());
        u.setLastName("lastname");
        assertEquals("lastname", u.getLastName());
        u.setEmail("<EMAIL>");
        assertEquals("<EMAIL>",u.getEmail());

        DataSource ds= new DataSource("test","3","test.xml","xml","test");
        Vector<DataSource> dataSourceVector = new Vector<>();
        dataSourceVector.add(ds);
        Group g= new Group("testgroup","n");
        g.setDataSourceList(dataSourceVector);
        Vector<Group> groupVector = new Vector<>();
        groupVector.add(g);

        u.setGroupList(groupVector);
        assertEquals(groupVector,u.getGroupList());
        u.setDataSourceList(dataSourceVector);
        assertEquals(dataSourceVector, u.getDataSourceList());
        assertEquals("no", u.getIsWaiting());
        assertEquals("no", u.getIsCandidate());
        u.setIsCandidate("yes");
        u.setIsWaiting("yes");
        assertEquals("yes", u.getIsWaiting());
        assertEquals("yes", u.getIsCandidate());
        u.setDataSource(ds);
        assertEquals(ds,u.getDataSource());
        assertTrue(u.hasReadAccessRight("test"));
        assertTrue(u.hasWriteAccessRight("test"));
        assertTrue(u.hasReadWriteAccessRight("test"));

        assertTrue(u.DataSourceListHasReadAccessRight("test"));
        assertFalse(u.DataSourceListHasOwnAccessRight("test"));
        assertTrue(u.DataSourceListHasWriteAccessRight("test"));
        assertTrue(u.DataSourceListHasReadWriteAccessRight("test"));
    }

    @Test
    public void testUser2(){
        DataSource ds= new DataSource("test","1","test.xml","xml","test");
        Vector<DataSource> dataSourceVector = new Vector<>();
        dataSourceVector.add(ds);
        Group g= new Group("testgroup","n");
        Vector<Group> groupVector = new Vector<>();
        g.setDataSourceList(dataSourceVector);
        groupVector.add(g);
        User u = new User("user","passwd",dataSourceVector,groupVector);
        assertFalse(u.hasOwnAccessRight("test"));
        assertTrue(u.hasReadAccessRight("test"));
        assertFalse(u.hasWriteAccessRight("test"));
        assertFalse(u.hasReadWriteAccessRight("test"));
    }

    @Test
    public void testUser3(){
        DataSource ds= new DataSource("test","2","test.xml","xml","test");
        Vector<DataSource> dataSourceVector = new Vector<>();
        dataSourceVector.add(ds);
        Group g= new Group("testgroup","n");
        Vector<Group> groupVector = new Vector<>();
        g.setDataSourceList(dataSourceVector);
        groupVector.add(g);
        User u = new User("user","passwd","firstname","lastname","email",groupVector,"no");
        assertTrue(u.hasWriteAccessRight("test"));
        assertFalse(u.isAdmin());

        User u2= new User(u);
        assertEquals(u.getUserName(),u2.getUserName());
        assertEquals(u.getPassword(),u2.getPassword());
        assertEquals(u.getFirstName(),u2.getFirstName());
        assertEquals(u.getLastName(),u2.getLastName());
        assertEquals(u.getEmail(),u2.getEmail());
    }

}
