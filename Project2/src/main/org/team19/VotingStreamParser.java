/*
 * File name:
 * VotingSystemParser.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Parses an InputStream and returns a VotingSystem
 */

package org.team19;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

/**
 * Parses an {@link InputStream} and returns a {@link VotingSystem}
 */
public final class VotingStreamParser {
    
    /**
     * A private constructor for the utility class {@link VotingStreamParser} to prevent instantiation
     */
    private VotingStreamParser() {}
    
    /**
     * Throws a {@link ParseException} with the message in the form "Error on line [lineNumber]: [message]", replacing [lineNumber] and [message]
     * with the corresponding parameters
     *
     * @param message    The message explaining why this exception was thrown
     * @param lineNumber The line number in the file at which the parsing error occurred
     * @throws ParseException Thrown always
     */
    static void throwParseException(final String message, final int lineNumber) throws ParseException {
        throw new ParseException(String.format("Error on line %d: %s", lineNumber, message), lineNumber);
    }
    
    /**
     * Returns the next line from the {@link BufferedReader}
     *
     * @param bufferedReader The {@link BufferedReader} from which to retrieve the next line
     * @param lineNumber     The line number for the line currently being read
     * @return The next line from the {@link BufferedReader}
     * @throws ParseException Thrown if an {@link IOException} occurs when trying to read in the next line
     */
    private static String readLine(final BufferedReader bufferedReader, final int lineNumber) throws ParseException {
        try {
            return bufferedReader.readLine();
        }
        catch(IOException e) {
            throwParseException(String.format("Line %d could not be read", lineNumber), lineNumber);
        }
        return null;
    }
    
    /**
     * Returns an array of the number of lines specified from the provided {@link BufferedReader}
     *
     * @param bufferedReader The {@link BufferedReader} from which to read the lines
     * @param lineNumber     The line number of the first line in the group of lines to read
     * @param numLines       The number of lines to read
     * @return An array of the number of lines specified from the provided {@link BufferedReader}
     * @throws ParseException Thrown if an {@link IOException} occurs when trying to read in the next line
     */
    private static String[] readLines(final BufferedReader bufferedReader, final int lineNumber, final int numLines) throws ParseException {
        final String[] lines = new String[numLines];
        for(int i = 0; i < numLines; i++) {
            final String nextLine = readLine(bufferedReader, lineNumber + i);
            lines[i] = nextLine;
        }
        return lines;
    }
    
    /**
     * Throws a {@link ParseException} if the given line is null, meaning the file ends earlier than anticipated
     *
     * @param line       The line to test
     * @param lineNumber The line number associated with the given line
     * @throws ParseException Thrown if the given line is null, meaning that the file ended earlier than anticipated
     */
    private static void throwParseExceptionIfEofLine(final String line, final int lineNumber) throws ParseException {
        if(line == null) {
            throwParseException("Expected line to exist but the file ended early", lineNumber);
        }
    }
    
    /**
     * Throws a {@link ParseException} if any of the given lines is null, meaning the file ends earlier than anticipated
     *
     * @param lines      The lines to test
     * @param lineNumber The line number associated with the first of the lines
     * @throws ParseException Thrown if any of the given lines is null, meaning that the file ended earlier than anticipated
     */
    private static void throwParseExceptionIfEofLines(final String[] lines, final int lineNumber) throws ParseException {
        for(int i = 0; i < lines.length; i++) {
            throwParseExceptionIfEofLine(lines[i], lineNumber + i);
        }
    }
    
    /**
     * Parses an {@link InputStream} and returns a {@link VotingSystem} constructed from the given stream
     *
     * @param input           The {@link InputStream} to parse
     * @param auditStream     The {@link OutputStream} to write detailed information about the running of the election
     * @param reportStream    The {@link OutputStream} to write a summary about the running of the election
     * @param headerSystemMap The mapping between header strings and their corresponding {@link VotingSystem} classes
     * @return The parsed {@link VotingSystem}
     * @throws NullPointerException Thrown if any of the given streams or if the headerSystemMap is null
     * @throws ParseException       Thrown if there is an issue in parsing the provided {@link InputStream}
     */
    public static VotingSystem parse(final InputStream input, final OutputStream auditStream, final OutputStream reportStream,
        final Map<String, Class<? extends VotingSystem>> headerSystemMap) throws ParseException, NullPointerException {
        //Require that the input stream and output streams are nonnull
        Objects.requireNonNull(input);
        Objects.requireNonNull(auditStream);
        Objects.requireNonNull(reportStream);
        
        //Use a BufferedReader to read from the input stream and PrintWriters to write to the output streams
        final BufferedReader inReader = new BufferedReader(new InputStreamReader(input));
        final PrintWriter auditWriter = new PrintWriter(auditStream, true);
        final PrintWriter reportWriter = new PrintWriter(reportStream, true);
        
        int lineNumber = 1;
        
        VotingSystem votingSystem = null;
        
        //Attempt to read the first line of the file
        String firstLine = readLine(inReader, lineNumber);
        
        //Throw an exception if the file has 0 lines (if such a file exists)
        throwParseExceptionIfEofLine(firstLine, lineNumber);
        
        //Strip any whitespace from the beginning or end of the line
        //noinspection ConstantConditions
        firstLine = firstLine.strip();
        
        //If the election file's header does not match one of the headers from the headerSystemMap, then throw an exception
        if(!headerSystemMap.containsKey(firstLine)) {
            String headers = headerSystemMap.keySet().toString();
            headers = headers.substring(1, headers.length() - 1);
            throwParseException(String.format(
                "The given file header %s does not match any of the supported headers that were provided: %s",
                firstLine,
                headers
            ), lineNumber);
        }
        //If the election file header is valid
        else {
            //Try to create the VotingSystem instance using the corresponding VotingSystem instance in headerSystemMap
            try {
                votingSystem = headerSystemMap.get(firstLine)
                    .getConstructor(OutputStream.class, OutputStream.class)
                    .newInstance(auditStream, reportStream);
                
                //Output the election type to the audit, report, and summary
                final String electionTypeOutput = String.format("Election type: %s\n", firstLine);
                auditWriter.println(electionTypeOutput);
                reportWriter.println(electionTypeOutput);
                System.out.println(electionTypeOutput);
            }
            //If there is an issue in creating the VotingSystem instance, throw an error
            catch(InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throwParseException(String.format(
                    "There was an issue in trying to construct the VotingSystem %s with the audit and report streams passed as arguments",
                    headerSystemMap.get(firstLine).getSimpleName()
                ), lineNumber);
            }
        }
        
        lineNumber++;
        
        //Import the candidates header, throwing an exception if there was an issue in reading or parsing the candidates header
        final int candidateHeaderSize = votingSystem.getCandidateHeaderSize();
        final String[] candidatesHeader = readLines(inReader, lineNumber, candidateHeaderSize);
        throwParseExceptionIfEofLines(candidatesHeader, lineNumber);
        votingSystem.importCandidatesHeader(candidatesHeader, lineNumber);
        
        lineNumber += candidateHeaderSize;
        
        /*
         * Parse and add the candidates, throwing an exception if there was an issue in reading or parsing the candidates or if there is a
         * difference in the number of candidates parsed and the number of candidates mentioned in the candidates header
         */
        final int numCandidates = votingSystem.getNumCandidates();
        final String candidatesLine = readLine(inReader, lineNumber);
        throwParseExceptionIfEofLine(candidatesLine, lineNumber);
        votingSystem.addCandidates(candidatesLine, lineNumber);
        
        //Throw an exception if the number of candidates parsed does not match the number of candidates provided in the candidates header
        if(votingSystem.getCandidates().size() != numCandidates) {
            throwParseException(String.format(
                "The number of parsed candidates %d is not equivalent to the number provided in the candidates header %d",
                votingSystem.getCandidates().size(),
                numCandidates
            ), lineNumber);
        }
        
        lineNumber++;
        
        //Import the ballots header, throwing an exception if there was an issue in reading or parsing the candidates header
        final int ballotsHeaderSize = votingSystem.getBallotHeaderSize();
        final String[] ballotsHeader = readLines(inReader, lineNumber, ballotsHeaderSize);
        throwParseExceptionIfEofLines(ballotsHeader, lineNumber);
        votingSystem.importBallotsHeader(ballotsHeader, lineNumber);
        
        lineNumber += ballotsHeaderSize;
        
        /*
         * Parse and add the ballots, throwing an exception if there was an issue in reading or parsing the ballots or if there is a
         * difference in the number of ballots parsed and the number of ballots mentioned in the ballots header
         */
        final int numBallots = votingSystem.getNumBallots();
        int ballotNumber = 1;
        String nextBallot;
        
        //Read in ballots until the end of the file is reached
        while((nextBallot = readLine(inReader, lineNumber)) != null) {
            votingSystem.addBallot(ballotNumber, nextBallot, lineNumber);
            lineNumber++;
            ballotNumber++;
        }
        
        //Throw an exception if the number of ballots parsed does not match the number of ballots provided in the ballots header
        if(ballotNumber - 1 != numBallots) {
            throwParseException(String.format(
                "The number of parsed ballots %d is not equivalent to the number provided in the ballots header %d",
                ballotNumber - 1,
                numBallots
            ), lineNumber);
        }
        
        return votingSystem;
    }
    
}
