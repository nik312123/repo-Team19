/*
 * File name:
 * CompareInputStream.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Compares two input streams
 */

package org.team19;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Objects;

/**
 * A utility class that contains method to compare the contents of {@link InputStream}s
 */
public class CompareInputStreams {
    
    /**
     * A private constructor for the utility class {@link CompareInputStreams} to prevent instantiation
     */
    private CompareInputStreams() {}
    
    /**
     * Throws a {@link ParseException} with the message in the form "Error on line [lineNumber]: [message]", replacing [lineNumber] and [message]
     * with the corresponding parameters
     *
     * @param message    The message explaining why this exception was thrown
     * @param lineNumber The line number in the file at which the parsing error occurred
     * @throws ParseException Thrown always
     */
    private static void throwParseException(final String message, final int lineNumber) throws ParseException {
        throw new ParseException(String.format("Error on line %d: %s", lineNumber, message), lineNumber);
    }
    
    /**
     * Compares two {@link InputStream}s
     *
     * @param inputStream1 an {@link InputStream} to compare
     * @param inputStream2 another {@link InputStream} to compare
     * @throws ParseException thrown when there is an error on a line
     */
    public static void compareFiles(final InputStream inputStream1, final InputStream inputStream2) throws ParseException {
        final BufferedReader reader1 = new BufferedReader(new InputStreamReader(inputStream1));
        final BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2));
        
        int lineNumber = 1;
        
        try {
            String curLine1 = reader1.readLine();
            String curLine2 = reader2.readLine();
            
            while(curLine1 != null && curLine2 != null) {
                if(!Objects.equals(curLine1, curLine2)) {
                    throwParseException(String.format(
                        "Mismatch between lines:\n"
                            + "    Input Stream 1: %s\n"
                            + "    Input Stream 2: %s",
                        curLine1,
                        curLine2
                    ), lineNumber);
                }
                curLine1 = reader1.readLine();
                curLine2 = reader2.readLine();
                lineNumber++;
            }
        }
        catch(IOException e) {
            throwParseException("There was an error reading from one of the files on this line", lineNumber);
        }
    }
}
