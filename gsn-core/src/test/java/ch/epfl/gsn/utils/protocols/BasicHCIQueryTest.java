package ch.epfl.gsn.utils.protocols;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Vector;

public class BasicHCIQueryTest {

    @Test
    public void testBuildRawQuery() {
        BasicHCIQuery query = new BasicHCIQuery("TestQuery", "Description", new String[]{"param1"});
        Vector<Object> params = new Vector<>();
        params.add("value1");

        byte[] rawQuery = query.buildRawQuery(params);
        assertNotNull(rawQuery);
    }

}
