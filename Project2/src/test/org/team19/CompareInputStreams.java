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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class that contains method to compare the contents of {@link InputStream}s, replacing Project2 path file separators accordingly
 */
public final class CompareInputStreams {
    
    /**
     * The compiled regex for a path in the Project2 directory, excluding the file
     */
    @SuppressWarnings("RegExpRedundantEscape")
    private static final Pattern PROJECT_2_PATH = Pattern.compile("(Project2\\/([\\w\\-]+\\/)+)");
    
    /**
     * The file separator but with double the backslashes (if any) for use with {@link Matcher#replaceAll(Function)} since backslashes used with
     * {@link Matcher#replaceAll(Function)} are considered escape characters, making two backslashes a normal backslash
     */
    private static final String FILE_SEP_DOUBLE_BACKSLASH = File.separator.replace("\\", "\\\\");
    
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
     * Returns the result of replacing any forward slashes in Project2 paths in the provided string with the file separator character
     *
     * @param str The string to replace any forward slashes in Project2 paths with the file separator character
     * @return The result of replacing any forward slashes in Project2 paths in the provided string with the file separator character
     */
    private static String replaceStringFileSeparator(final String str) {
        if(str != null) {
            final Matcher matcher = PROJECT_2_PATH.matcher(str);
            
            //If any Project2 paths have been found
            if(matcher.find()) {
                /*
                 * Replace all forward slashes with the file separator, doubling any backslashes in the file separator that exists since
                 * backslashes are taken as escape characters
                 */
                return matcher.replaceAll(
                    matchResult -> matchResult.group().replace(
                        "/",
                        FILE_SEP_DOUBLE_BACKSLASH
                    )
                );
            }
        }
        return str;
    }
    
    /**
     * Compares two {@link InputStream}s
     *
     * @param expected The expected {@link InputStream} to compare
     * @param actual   another {@link InputStream} to compare
     * @throws ParseException thrown when there is an error on a line
     */
    public static void compareFiles(final InputStream expected, final InputStream actual) throws ParseException {
        final BufferedReader expectedReader = new BufferedReader(new InputStreamReader(expected));
        final BufferedReader actualReader = new BufferedReader(new InputStreamReader(actual));
        
        int lineNumber = 1;
        
        try {
            //Get the first line of both inputs, replacing any Project2 path file separators in the expected input with the correct separators
            String curExpectedLine = replaceStringFileSeparator(expectedReader.readLine());
            String curActualLine = actualReader.readLine();
            
            //While neither file is finished, go through each line, and throw an exception if a line does not match
            while(curExpectedLine != null && curActualLine != null) {
                if(!Objects.equals(curExpectedLine, curActualLine)) {
                    throwParseException(String.format(
                        "Mismatch between lines:\n"
                            + "    Expected: %s\n"
                            + "      Actual: %s",
                        curExpectedLine,
                        curActualLine
                    ), lineNumber);
                }
                curExpectedLine = replaceStringFileSeparator(expectedReader.readLine());
                curActualLine = actualReader.readLine();
                lineNumber++;
            }
            
            //Test the cases where one of the inputs ends early and throw an exception if that was the case
            if(curActualLine == null && curExpectedLine != null) {
                throwParseException("Error: Actual input ended before expected input", lineNumber);
            }
            else if(curActualLine != null) {
                throwParseException("Error: Actual input continued beyond expected input", lineNumber);
            }
        }
        catch(IOException e) {
            throwParseException("There was an error reading from one of the files on this line", lineNumber);
        }
    }
}
