package ch.epfl.gsn.ac;

import static org.junit.Assert.*;
import org.junit.Test;


public class ProtectorTest{

    @Test
    public void testProtector() throws Exception{
        String encrypted= "3Ge/fUGse2Vg2Dlj55MjVqH/QF1h0BptSd+QdrpzLHfMEml05CVB97y4hxQob88m9FFgaORTvtv/u3Pe3AXFFw==";
        assertEquals(encrypted,Protector.encrypt("protectortest"));

        //assertEquals("protectortest",Protector.decrypt(encrypted));
    }
}