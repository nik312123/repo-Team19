/*
 * File name:
 * VotingSystemParserTest.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Tests the VotingSystemParserTest class
 */

package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

final class VotingStreamParserTest {
    
    private VotingStreamParserTest() {}
    
    @Test
    void testThrowParseException() {
        //Tests that throwParseException throws a ParseException
        final ParseException parseException = Assertions.assertThrows(
            ParseException.class,
            () -> VotingStreamParser.throwParseException("Sample message", 92)
        );
        
        Assertions.assertAll(
            /*
             * Check that the ParseException's error message matches the format "Error on line [lineNumber]: [message]", replacing [lineNumber] and
             * [message] with the corresponding parameters to throwParseException
             */
            () -> Assertions.assertEquals("Error on line 92: Sample message", parseException.getMessage()),
            
            //Check that the error offset is equivalent to the provided line number
            () -> Assertions.assertEquals(92, parseException.getErrorOffset())
        );
    }
    
    // TODO: Finish along with system tests
    @Test
    void testParse() {
        Assertions.fail();
    }
}
