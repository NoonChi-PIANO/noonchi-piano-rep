package org.gliderwiki.Sound_Vanila;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    
    public void sumTest() {
    	int a= 1;
    	int b= 2;
    	int expected = 3;
    	int result = App.sum(a, b);
    	
    	assertEquals(expected, result);
    }
    
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
}
