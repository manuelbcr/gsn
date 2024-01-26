package ch.epfl.gsn.ac;

import static org.junit.Assert.*;
import org.junit.Test;

public class ColumnTest{
    @Test
    public void testColumnCreation(){
        Column c= new Column("xy");
        assertEquals("xy",c.columnLabel);
        Column c1= new Column("xy","value");
        assertEquals("xy",c1.columnLabel);
        assertEquals("value",c1.columnValue);
    }
}

