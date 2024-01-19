package ch.epfl.gsn.utils.models;



import static org.junit.Assert.*;
import org.junit.Test;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import java.io.Serializable;

public class DummyModelTest {

    @Test
    public void testPushData() {
        DummyModel dummyModel = new DummyModel();
        StreamElement inputElement = new StreamElement(new String[] { "x", "y", "z" }, new Byte[] { DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE },new Serializable[] { 1.0, 2.0, 3.0 });

        StreamElement[] result = dummyModel.pushData(inputElement, "origin");

        assertEquals(1, result.length);
        assertEquals(inputElement, result[0]);
    }


    @Test
    public void testQueryWithLastOneSet() {
        DummyModel dummyModel = new DummyModel();
        StreamElement inputElement = new StreamElement(new String[] { "x", "y", "z" }, new Byte[] { DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE }, new Serializable[] { 1.0, 2.0, 3.0 });
        dummyModel.pushData(inputElement, "origin");

        StreamElement[] result = dummyModel.query(new StreamElement(new String[] { "x", "y", "z" }, new Byte[] { DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE }, new Serializable[] { 1.0, 2.0, 3.0 }));

        assertEquals(1, result.length);
        assertEquals(inputElement, result[0]);
    }

    @Test
    public void testQueryWithNoLastOneSet() {
        DummyModel dummyModel = new DummyModel();

        StreamElement[] result = dummyModel.query(new StreamElement(new String[] { "x", "y", "z" }, new Byte[] { DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE }, new Serializable[] { 1.0, 2.0, 3.0 }));

        assertEquals(1, result.length);
        String [] fieldnames= result[0].getFieldNames();
        assertEquals("value",fieldnames[0]);
    }

    @Test
    public void testSetParam() {
        DummyModel dummyModel = new DummyModel();

        dummyModel.setParam("default", "42");
    }

    @Test
    public void testInitialize() {
        DummyModel dummyModel = new DummyModel();

        assertTrue(dummyModel.initialize());
    }


}
