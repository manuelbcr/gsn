package ch.epfl.gsn.utils.geo;

import static org.junit.Assert.*;
import org.junit.Test;

public class ApproxSwissProjTest{
      
 private static final double DELTA = 1;


    @Test
    public void testDecToSexAngle() {
        double result = ApproxSwissProj.DecToSexAngle(46.95240555555556);
        assertEquals(46.570866, result, DELTA);
    }

    @Test
    public void testLV03toWGS84() {
        double[] result = ApproxSwissProj.LV03toWGS84(600000.000, 200000.000, 700);
        assertArrayEquals(new double[]{46.951082918, 7.438632525, 748.961}, result, DELTA);
    }

    @Test
    public void testSexAngleToSeconds() {
        double result = ApproxSwissProj.SexAngleToSeconds(46.95240555555556);
        assertEquals(171324.0555555556, result, DELTA);
    }

    @Test
    public void testSexToDecAngle() {
        double result = ApproxSwissProj.SexToDecAngle(4693133.333333334);
        assertEquals(4693133.559259261, result, DELTA);
    }

    @Test
    public void testWGS84toLV03() {
        double[] result = ApproxSwissProj.WGS84toLV03(46.951082918, 7.438632525, 748.961);
        assertArrayEquals(new double[]{600000.000, 200000.000, 700}, result, DELTA);

    }



}