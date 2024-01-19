package ch.epfl.gsn.delivery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Date;

import javax.xml.crypto.Data;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;

public class StreamElement4RestTest {
    @Test
    public void testConstructor() {
        
        Date timestamp = new Date();
        String[] fieldNames = {"fieldName1", "fieldName2"};
        Byte[] fieldTypes = new Byte[]{DataTypes.INTEGER,DataTypes.INTEGER};
        
        Serializable[] fieldValues = {1, 2};

        StreamElement streamElement = new StreamElement(fieldNames, fieldTypes, fieldValues, timestamp.getTime());
        StreamElement4Rest streamElement4Rest = new StreamElement4Rest(streamElement);
        assertEquals(timestamp, streamElement4Rest.getTimestamp());
    }

    @Test
    public void testToString() {
        Date timestamp = new Date();
        String[] fieldNames = {"fieldName1", "fieldName2"};
        Byte[] fieldTypes = {DataTypes.INTEGER, DataTypes.INTEGER};
        Serializable[] fieldValues = {1, 2};

        StreamElement streamElement = new StreamElement(fieldNames, fieldTypes, fieldValues, timestamp.getTime());
        StreamElement4Rest streamElement4Rest = new StreamElement4Rest(streamElement);

        StringBuilder expected = new StringBuilder("StreamElement4Rest: (timestamp:");
        expected.append(timestamp.toString());
        expected.append("Fields =>{ ");
        expected.append("Field(name:fieldName1,type:2,value:1), ");
        expected.append("Field(name:fieldName2,type:2,value:2), ");
        expected.append("})");

        assertEquals(expected.toString(), streamElement4Rest.toString());
    }

    @Test
    public void testToStreamElement() {
        Date timestamp = new Date();
        String[] fieldNames = {"fieldName1", "fieldName2"};
        Byte[] fieldTypes = {DataTypes.INTEGER, DataTypes.INTEGER};
        Serializable[] fieldValues = {1, 2};
    
        StreamElement streamElement = new StreamElement(fieldNames, fieldTypes, fieldValues, timestamp.getTime());
        
        StreamElement4Rest streamElement4Rest = new StreamElement4Rest(streamElement);
        assertEquals(streamElement.getFieldNames()[0], streamElement4Rest.toStreamElement().getFieldNames()[0]);
        assertEquals(streamElement.getFieldNames()[1], streamElement4Rest.toStreamElement().getFieldNames()[1]);
        assertEquals(streamElement.getData()[0], streamElement4Rest.toStreamElement().getData()[0]);
        assertEquals(streamElement.getData()[1], streamElement4Rest.toStreamElement().getData()[1]);
    }

    @Test
    public void testGetXstream() {
        XStream xstream = StreamElement4Rest.getXstream();

        assertEquals("stream-element", xstream.getMapper().serializedClass(StreamElement4Rest.class));
        assertEquals("field", xstream.getMapper().serializedClass(Field4Rest.class));
        assertEquals("strcture", xstream.getMapper().serializedClass(DataField.class));
        assertTrue(xstream.getMapper().shouldSerializeMember(StreamElement4Rest.class, "timestamp"));
        assertNotNull(xstream.getConverterLookup().lookupConverterForType(Field4Rest.class));
    }

    @Test
    public void testGetXstream4Structure() {
        XStream xstream = StreamElement4Rest.getXstream();
        assertNotNull(xstream);
    }


}
