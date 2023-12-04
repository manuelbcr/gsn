package ch.epfl.gsn.utils.geo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class GridToolsTest {

    @Test
    public void testDeSerializeToString() {
        Double[][] mockData = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(mockData);
            out.close();
            byte[] mockSerializedData = bos.toByteArray();

            String result = GridTools.deSerializeToString(mockSerializedData);

            String expected = "1.0 2.0 3.0 \n4.0 5.0 6.0 \n";
            assertEquals(expected, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testDeSerializeToCell() {
        Double[][] mockData = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(mockData);
            out.close();
            byte[] mockSerializedData = bos.toByteArray();
            int xcell = 1;
            int ycell = 2;
            double result = GridTools.deSerializeToCell(mockSerializedData, xcell, ycell);

            double expected = mockData[ycell][xcell];
            assertEquals(expected, result, 0.0001); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeSerializeToStringWithBoundaries() {
        Double[][] mockData = {
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0},
                {13.0, 14.0, 15.0, 16.0}
        };

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(mockData);
            out.close();
            byte[] mockSerializedData = bos.toByteArray();

            int xmin = 1;
            int xmax = 2;
            int ymin = 0;
            int ymax = 2;
            String result = GridTools.deSerializeToStringWithBoundaries(mockSerializedData, xmin, xmax, ymin, ymax);
            String expected = "2.0 3.0 \n6.0 7.0 \n10.0 11.0 \n";
            assertEquals(expected, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeSerialize() {
        Double[][] mockData = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        };

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(mockData);
            out.close();
            byte[] mockSerializedData = bos.toByteArray();

            Double[][] result = GridTools.deSerialize(mockSerializedData);

            assertEquals(mockData, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

