/*
 * File name:
 * VotingSystemParserTest.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Parses an InputStream and returns a VotingSystem
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
    
    //Generates an example input stream for the purpose of many of the tests
    private static InputStream generateExampleInputStream() {
        
        final String exampleInput = "This is the first line\nThis is the second line\nThis is the third line";
        
        //Crates an InputStream that returns the next character from exampleInput as an integer
        return new InputStream() {
            private int i = 0;
            
            @Override
            public int read() {
                if(i >= exampleInput.length()) {
                    return -1;
                }
                final int byteResult = exampleInput.charAt(i);
                i++;
                return byteResult;
            }
        };
    }
    
    @Test
    void testReadLine() {
        //Retrieve the example input stream and use a BufferedReader to read in lines
        final InputStream testInput = generateExampleInputStream();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(testInput));
        
        try {
            //Retrieve the readLine function for testing and make it accessible
            final Method readLine = VotingStreamParser.class.getDeclaredMethod("readLine", BufferedReader.class, int.class);
            readLine.setAccessible(true);
            
            Assertions.assertAll(
                //Test each of the lines of the above input stream using readLine
                () -> Assertions.assertEquals("This is the first line", readLine.invoke(VotingStreamParser.class, bufferedReader, 1)),
                () -> Assertions.assertEquals("This is the second line", readLine.invoke(VotingStreamParser.class, bufferedReader, 2)),
                () -> Assertions.assertEquals("This is the third line", readLine.invoke(VotingStreamParser.class, bufferedReader, 3)),
                () -> Assertions.assertNull(readLine.invoke(VotingStreamParser.class, bufferedReader, 4))
            );
        }
        //If the method could not be retrieved for testing, then fail
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the readLine method from VotingStreamParser");
        }
    }
    
    @Test
    void testReadLines() {
        //Retrieve the example input stream and use a BufferedReader to read in lines
        final InputStream testInput = generateExampleInputStream();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(testInput));
        
        try {
            //Retrieve the readLines function for testing and make it accessible
            final Method readLines = VotingStreamParser.class.getDeclaredMethod("readLines", BufferedReader.class, int.class, int.class);
            readLines.setAccessible(true);
            
            Assertions.assertAll(
                //Test that reading in 0 lines results in an empty array
                () -> Assertions.assertArrayEquals(new String[0], (String[]) readLines.invoke(VotingStreamParser.class, bufferedReader, 1, 0)),
                //Test that reading in the first two lines is successful
                () -> Assertions.assertArrayEquals(
                    new String[] {"This is the first line", "This is the second line"},
                    (String[]) readLines.invoke(VotingStreamParser.class, bufferedReader, 1, 2)
                ),
                //Test that reading the last line is successful and that null is returned after the last line
                () -> Assertions.assertArrayEquals(
                    new String[] {"This is the third line", null},
                    (String[]) readLines.invoke(VotingStreamParser.class, bufferedReader, 3, 2)
                )
            );
        }
        //If the method could not be retrieved for testing, then fail
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the readLine method from VotingStreamParser");
        }
    }
    
    @Test
    void testThrowParseExceptionIfEofLine() {
        //Retrieve the example input stream and use a BufferedReader to read in lines
        final InputStream testInput = generateExampleInputStream();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(testInput));
        
        try {
            //Retrieve the throwParseExceptionIfEofLine function for testing and make it accessible
            final Method throwParseExceptionIfEofLine = VotingStreamParser.class.getDeclaredMethod("throwParseExceptionIfEofLine", String.class,
                int.class);
            throwParseExceptionIfEofLine.setAccessible(true);
            
            //Retrieve the readLine function for testing and make it accessible
            final Method readLine = VotingStreamParser.class.getDeclaredMethod("readLine", BufferedReader.class, int.class);
            readLine.setAccessible(true);
            
            //noinspection JavaReflectionInvocation
            Assertions.assertAll(
                //Test that the first line of the InputStream does not cause an exception to be thrown
                () -> Assertions.assertDoesNotThrow(() ->
                    throwParseExceptionIfEofLine.invoke(VotingStreamParser.class, readLine.invoke(VotingStreamParser.class, bufferedReader, 1), 1)
                ),
                //Test that the second line of the InputStream does not cause an exception to be thrown
                () -> Assertions.assertDoesNotThrow(() ->
                    throwParseExceptionIfEofLine.invoke(VotingStreamParser.class, readLine.invoke(VotingStreamParser.class, bufferedReader, 1), 2)
                ),
                //Test that the third line of the InputStream does not cause an exception to be thrown
                () -> Assertions.assertDoesNotThrow(() ->
                    throwParseExceptionIfEofLine.invoke(VotingStreamParser.class, readLine.invoke(VotingStreamParser.class, bufferedReader, 1), 3)
                ),
                //Test that, because there is no more lines remaining, an exception is returned because readLine returns null
                () -> Assertions.assertEquals(
                    ParseException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        throwParseExceptionIfEofLine.invoke(VotingStreamParser.class, readLine.invoke(VotingStreamParser.class, bufferedReader, 1), 1)
                    ).getCause().getClass()
                )
            );
        }
        //If the method could not be retrieved for testing, then fail
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the readLine method from VotingStreamParser");
        }
    }
    
    @Test
    void testThrowParseExceptionIfEofLines() {
        //Retrieve the example input stream and use a BufferedReader to read in lines
        final InputStream testInput = generateExampleInputStream();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(testInput));
        
        try {
            //Retrieve the throwParseExceptionIfEofLines function for testing and make it accessible
            final Method throwParseExceptionIfEofLines = VotingStreamParser.class.getDeclaredMethod("throwParseExceptionIfEofLines", String[].class,
                int.class);
            throwParseExceptionIfEofLines.setAccessible(true);
            
            //Retrieve the readLines function for testing and make it accessible
            final Method readLines = VotingStreamParser.class.getDeclaredMethod("readLines", BufferedReader.class, int.class, int.class);
            readLines.setAccessible(true);
            
            //noinspection JavaReflectionInvocation
            Assertions.assertAll(
                //Test that none of the first two lines throw an exception because no line indicates reading beyond EOF (null)
                () -> Assertions.assertDoesNotThrow(() ->
                    throwParseExceptionIfEofLines.invoke(
                        VotingStreamParser.class,
                        readLines.invoke(VotingStreamParser.class, bufferedReader, 1, 2),
                        1
                    )
                ),
                //Test that the third and fourth lines together throw an exception because the fourth line indicates reading beyond EOF (null)
                () -> Assertions.assertEquals(
                    ParseException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        throwParseExceptionIfEofLines.invoke(
                            VotingStreamParser.class,
                            readLines.invoke(VotingStreamParser.class, bufferedReader, 3, 2),
                            1
                        )
                    ).getCause().getClass()
                )
            );
        }
        //If the method could not be retrieved for testing, then fail
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the readLine method from VotingStreamParser");
        }
    }
    
    // TODO: Finish along with system tests
    @Test
    void testParse() {
        Assertions.fail();
    }
}
