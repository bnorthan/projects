package com.truenorth;

import junit.framework.Test;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;

import com.truenorth.gpu.Multiply;
import com.truenorth.gpu.wrappers.YacuDecuWrapper;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws IOException
    {
    	System.out.println("testing!");
    	
    	new Multiply().MultiplyTest();
    	
    	YacuDecuWrapper wrapper=new YacuDecuWrapper();
    	
    	wrapper.LoadLib();
    	wrapper.runYacuDecu(10, 20, 20, 20, null, null, null);
    }
}
