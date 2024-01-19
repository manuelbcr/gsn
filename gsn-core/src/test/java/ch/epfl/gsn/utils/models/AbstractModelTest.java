package ch.epfl.gsn.utils.models;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.vsensor.ModellingVirtualSensor;

public class AbstractModelTest {

    @Test
    public void testAbstractModel() {
        AbstractModel concreteModel = new AbstractModel() {
            @Override
            public StreamElement[] pushData(StreamElement streamElement, String origin) {
                return null;
            }

            @Override
            public StreamElement[] query(StreamElement params) {   
                return null;
            }

            @Override
            public void setParam(String k, String string) {    
            }
        };

        DataField[] mockOutputFields = {};
        ModellingVirtualSensor mockVirtualSensor = mock(ModellingVirtualSensor.class);

        concreteModel.setOutputFields(mockOutputFields);
        concreteModel.setVirtualSensor(mockVirtualSensor);

        assertArrayEquals(mockOutputFields, concreteModel.getOutputFields());

        DataField[] newOutputFields = {};
        concreteModel.setOutputFields(newOutputFields);
        assertArrayEquals(newOutputFields, concreteModel.getOutputFields());

        assertEquals(mockVirtualSensor, concreteModel.getVirtualSensor());

        assertTrue(concreteModel.initialize());
    }
}