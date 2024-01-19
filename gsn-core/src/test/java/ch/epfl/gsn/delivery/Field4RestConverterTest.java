package ch.epfl.gsn.delivery;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import ch.epfl.gsn.beans.DataTypes;

public class Field4RestConverterTest {

    @Test
    public void testMarshalUnmarshal() {
        String name = "fieldName";
        Byte type = DataTypes.VARCHAR;
        Serializable value = "fieldValue";
        Field4Rest field4Rest = new Field4Rest(name, type, value);

        XStream xstream = new XStream(new DomDriver());
        xstream.registerConverter(new Field4RestConverter());
        xstream.alias("field", Field4Rest.class);

        String xml = xstream.toXML(field4Rest);
        Field4Rest unmarshalledField4Rest = (Field4Rest) xstream.fromXML(xml);

        assertEquals(name, unmarshalledField4Rest.getName());
        assertEquals(value, unmarshalledField4Rest.getValue());
    }

    @Test
    public void testMarshalUnmarshal1() {
        String name = "fieldName";
        Byte type = DataTypes.DOUBLE;
        Serializable value = 1.0;
        Field4Rest field4Rest = new Field4Rest(name, type, value);

        XStream xstream = new XStream(new DomDriver());
        xstream.registerConverter(new Field4RestConverter());
        xstream.alias("field", Field4Rest.class);

        String xml = xstream.toXML(field4Rest);
        Field4Rest unmarshalledField4Rest = (Field4Rest) xstream.fromXML(xml);

        assertEquals(name, unmarshalledField4Rest.getName());
        assertEquals(value, unmarshalledField4Rest.getValue());
    }

    @Test
    public void testMarshalUnmarshal2() {
        String name = "fieldName";
        Byte type = DataTypes.BINARY;
        Serializable value = new byte[]{0x01, 0x02, 0x03};
        Field4Rest field4Rest = new Field4Rest(name, type, value);

        XStream xstream = new XStream(new DomDriver());
        xstream.registerConverter(new Field4RestConverter());
        xstream.alias("field", Field4Rest.class);

        String xml = xstream.toXML(field4Rest);
        Field4Rest unmarshalledField4Rest = (Field4Rest) xstream.fromXML(xml);

        assertEquals(name, unmarshalledField4Rest.getName());
        assertArrayEquals((byte[]) value, (byte[]) unmarshalledField4Rest.getValue());
    }
}
