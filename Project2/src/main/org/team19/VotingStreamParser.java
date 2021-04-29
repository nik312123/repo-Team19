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
     * Returns the next line from the {@link BufferedReader}
     *
     * @param bufferedReader  The {@link BufferedReader} from which to retrieve the next line
     * @param inputIdentifier The identifier for the current input source
     * @param lineNumber      The line number for the line currently being read
     * @return The next line from the {@link BufferedReader}
     * @throws ParseException Thrown if an {@link IOException} occurs when trying to read in the next line
     */
    private static String readLine(final BufferedReader bufferedReader, final String inputIdentifier, final int lineNumber) throws ParseException {
        try {
            return bufferedReader.readLine();
        }
        catch(IOException e) {
            throwParseException(String.format("Line %d could not be read", lineNumber), inputIdentifier, lineNumber);
        }
        return null;
    }
    
    /**
     * Returns an array of the number of lines specified from the provided {@link BufferedReader}
     *
     * @param bufferedReader  The {@link BufferedReader} from which to read the lines
     * @param numLines        The number of lines to read
     * @param inputIdentifier The identifier for the current input source
     * @param lineNumber      The line number of the first line in the group of lines to read
     * @return An array of the number of lines specified from the provided {@link BufferedReader}
     * @throws ParseException Thrown if an {@link IOException} occurs when trying to read in the next line
     */
    private static String[] readLines(final BufferedReader bufferedReader, final int numLines, final String inputIdentifier, final int lineNumber)
        throws ParseException {
        final String[] lines = new String[numLines];
        for(int i = 0; i < numLines; i++) {
            final String nextLine = readLine(bufferedReader, inputIdentifier, lineNumber + i);
            lines[i] = nextLine;
        }
        return lines;
    }
    
    /**
     * Throws a {@link ParseException} if the given line is null, meaning the input ends earlier than anticipated
     *
     * @param line            The line to test
     * @param inputIdentifier The identifier for the current input source
     * @param lineNumber      The line number associated with the given line
     * @throws ParseException Thrown if the given line is null, meaning that the input ended earlier than anticipated
     */
    private static void throwParseExceptionIfEofLine(final String line, final String inputIdentifier, final int lineNumber) throws ParseException {
        if(line == null) {
            throwParseException("Expected line to exist but the input ended early", inputIdentifier, lineNumber);
        }
    }
    
    /**
     * Throws a {@link ParseException} if any of the given lines is null, meaning the input ends earlier than anticipated
     *
     * @param lines           The lines to test
     * @param inputIdentifier The identifier for the current input source
     * @param lineNumber      The line number associated with the first of the lines
     * @throws ParseException Thrown if any of the given lines is null, meaning that the input ended earlier than anticipated
     */
    private static void throwParseExceptionIfEofLines(final String[] lines, final String inputIdentifier, final int lineNumber)
        throws ParseException {
        for(int i = 0; i < lines.length; i++) {
            throwParseExceptionIfEofLine(lines[i], inputIdentifier, lineNumber + i);
        }
    }
    
    /**
     * Parses the election type from the first line of the input and initiates and returns the corresponding {@link VotingSystem}
     *
     * @param inReader        The {@link BufferedReader} for the first provided {@link InputStream}
     * @param inputSourceOne  The name for the first input source
     * @param auditStream     The {@link OutputStream} to write detailed information about the running of the election
     * @param reportStream    The {@link OutputStream} to write a summary about the running of the election
     * @param headerSystemMap The mapping between header strings and their corresponding {@link VotingSystem} classes
     * @param lineNumber      The current line number of the input being parsed
     * @return The {@link VotingSystem} created based on the first line of the input
     * @throws ParseException The {@link ParseException} thrown if the first line of the input is not one of the supported headers or if there are
     *                        0 lines in the input (if such an input exists)
     */
    private static VotingSystem parseElectionType(final BufferedReader inReader, final String inputSourceOne, final OutputStream auditStream,
        final OutputStream reportStream, final Map<String, Class<? extends VotingSystem>> headerSystemMap, final int lineNumber)
        throws ParseException {
        VotingSystem votingSystem = null;
        
        //Attempt to read the first line of the input
        String firstLine = readLine(inReader, inputSourceOne, lineNumber);
        
        //Throw an exception if the input has 0 lines (if such an input exists)
        throwParseExceptionIfEofLine(firstLine, inputSourceOne, lineNumber);
        
        //Strip any whitespace from the beginning or end of the line
        //noinspection ConstantConditions
        firstLine = firstLine.strip();
        
        //If the election input's header does not match one of the headers from the headerSystemMap, then throw an exception
        if(!headerSystemMap.containsKey(firstLine)) {
            String headers = headerSystemMap.keySet().toString();
            headers = headers.substring(1, headers.length() - 1);
            throwParseException(String.format(
                "The given input header %s does not match any of the supported headers that were provided: %s",
                firstLine,
                headers
            ), inputSourceOne, lineNumber);
        }
        //If the election input header is valid
        else {
            //Try to create the VotingSystem instance using the corresponding VotingSystem instance in headerSystemMap
            try {
                votingSystem = headerSystemMap.get(firstLine)
                    .getConstructor(OutputStream.class, OutputStream.class)
                    .newInstance(auditStream, reportStream);
                
                //Output the election type to the audit, report, and summary
                final String electionTypeOutput = String.format("Election type: %s\n", firstLine);
                new PrintWriter(auditStream, true).println(electionTypeOutput);
                new PrintWriter(reportStream, true).println(electionTypeOutput);
                System.out.println(electionTypeOutput);
            }
            //If there is an issue in creating the VotingSystem instance, throw an error
            catch(InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throwParseException(String.format(
                    "There was an issue in trying to construct the VotingSystem %s with the audit and report streams passed as arguments",
                    headerSystemMap.get(firstLine).getSimpleName()
                ), inputSourceOne, lineNumber);
            }
        }
        return votingSystem;
    }
    
    /**
     * Parses the candidate header, stores the header information in the {@link VotingSystem}, and returns the size of the candidate header
     *
     * @param inReader       The {@link BufferedReader} for the first provided {@link InputStream}
     * @param inputSourceOne The name for the first input source
     * @param votingSystem   The {@link VotingSystem} that will parse the candidate header
     * @param lineNumber     The current line number of the input being parsed
     * @return The size of the candidate header
     * @throws ParseException Thrown if the candidate header cannot be parsed
     */
    private static int parseCandidateHeader(final VotingSystem votingSystem, final BufferedReader inReader, final String inputSourceOne,
        final int lineNumber) throws ParseException {
        final int candidateHeaderSize = votingSystem.getCandidateHeaderSize();
        final String[] candidatesHeader = readLines(inReader, candidateHeaderSize, inputSourceOne, lineNumber);
        throwParseExceptionIfEofLines(candidatesHeader, inputSourceOne, lineNumber);
        votingSystem.importCandidatesHeader(candidatesHeader, inputSourceOne, lineNumber);
        return candidateHeaderSize;
    }
    
    /**
     * Parses the candidates and stores the candidates in the {@link VotingSystem}
     *
     * @param votingSystem   The {@link VotingSystem} that will parse the candidates
     * @param inReader       The {@link BufferedReader} for the first provided {@link InputStream}
     * @param inputSourceOne The name for the first input source
     * @param lineNumber     The current line number of the input being parsed
     * @throws ParseException Thrown if the candidates cannot be parsed
     */
    private static void parseCandidates(final VotingSystem votingSystem, final BufferedReader inReader, final String inputSourceOne,
        final int lineNumber) throws ParseException {
        final int numCandidates = votingSystem.getNumCandidates();
        final String candidatesLine = readLine(inReader, inputSourceOne, lineNumber);
        throwParseExceptionIfEofLine(candidatesLine, inputSourceOne, lineNumber);
        votingSystem.addCandidates(candidatesLine, inputSourceOne, lineNumber);
        
        //Throw an exception if the number of candidates parsed does not match the number of candidates provided in the candidates header
        if(votingSystem.getCandidates().size() != numCandidates) {
            throwParseException(String.format(
                "The number of parsed candidates %d is not equivalent to the number provided in the candidates header %d",
                votingSystem.getCandidates().size(),
                numCandidates
            ), inputSourceOne, lineNumber);
        }
    }
    
    /**
     * Parses the ballots header, stores the header information in the {@link VotingSystem}, and returns the size of the ballots header
     *
     * @param votingSystem    The {@link VotingSystem} that will parse the candidates
     * @param inReader        The {@link BufferedReader} for the current {@link InputStream}
     * @param inputIdentifier The identifier associated with the current input source
     * @param lineNumber      The current line number of the input being parsed
     * @return The size of the ballots header
     * @throws ParseException Thrown if the ballots header cannot be parsed
     */
    private static int parseBallotsHeader(final VotingSystem votingSystem, final BufferedReader inReader, final String inputIdentifier,
        final int lineNumber) throws ParseException {
        final int ballotsHeaderSize = votingSystem.getBallotHeaderSize();
        final String[] ballotsHeader = readLines(inReader, ballotsHeaderSize, inputIdentifier, lineNumber);
        throwParseExceptionIfEofLines(ballotsHeader, inputIdentifier, lineNumber);
        votingSystem.importBallotsHeader(ballotsHeader, inputIdentifier, lineNumber);
        return ballotsHeaderSize;
    }
    
    /**
     * Parses the ballots for an input source
     *
     * @param votingSystem    The {@link VotingSystem} that will parse the candidates
     * @param numBallots      The number of ballots in the current input source
     * @param ballotNumber    The current ballot number we are on for the election
     * @param inReader        The {@link BufferedReader} for the current {@link InputStream}
     * @param inputIdentifier The identifier associated with the current input source
     * @param lineNumber      The current line number of the input being parsed
     * @return The ballot number after parsing all of the ballots in the current input source
     * @throws ParseException Thrown if any ballots could not be parsed or if there is a mismatch in the number of expected and provided ballots
     */
    private static int parseBallots(final VotingSystem votingSystem, final int numBallots, int ballotNumber, final BufferedReader inReader,
        final String inputIdentifier, int lineNumber) throws ParseException {
        String nextBallot;
        
        //Read in ballots until the end of the input is reached
        while((nextBallot = readLine(inReader, inputIdentifier, lineNumber)) != null) {
            votingSystem.addBallot(ballotNumber, nextBallot, inputIdentifier, lineNumber);
            lineNumber++;
            ballotNumber++;
        }
        
        //Throw an exception if the number of ballots parsed does not match the number of ballots provided in the ballots header
        if(ballotNumber - 1 != numBallots) {
            throwParseException(String.format(
                "The number of parsed ballots %d is not equivalent to the sum of ballot counts provided in the ballots headers %d",
                ballotNumber - 1,
                numBallots
            ), inputIdentifier, lineNumber);
        }
        return ballotNumber;
    }
    
    /**
     * Throws a {@link ParseException} with the message in the form "Error on line [lineNumber]: [message]", replacing [lineNumber] and [message]
     * with the corresponding parameters
     *
     * @param message         The message explaining why this exception was thrown
     * @param inputIdentifier The identifier for the current input source
     * @param lineNumber      The line number in the input at which the parsing error occurred
     * @throws ParseException Thrown always
     */
    static void throwParseException(final String message, final String inputIdentifier, final int lineNumber) throws ParseException {
        throw new ParseException(String.format("Error for input source %s on line %d: %s", inputIdentifier, lineNumber, message), lineNumber);
    }
    
    /**
     * Parses {@link InputStream}s corresponding to one election and returns a {@link VotingSystem} constructed from the given stream
     *
     * @param inputs          The {@link InputStream}s to parse as a single election
     * @param inputNames      The names corresponding to each of the {@link InputStream}s
     * @param auditStream     The {@link OutputStream} to write detailed information about the running of the election
     * @param reportStream    The {@link OutputStream} to write a summary about the running of the election
     * @param headerSystemMap The mapping between header strings and their corresponding {@link VotingSystem} classes
     * @return The parsed {@link VotingSystem}
     * @throws NullPointerException     Thrown if any of the given streams or if the headerSystemMap is null
     * @throws IllegalArgumentException Thrown if the number of {@link InputStream}s is not at least 1
     * @throws ParseException           Thrown if there is an issue in parsing the provided {@link InputStream}
     */
    public static VotingSystem parse(final InputStream[] inputs, final String[] inputNames, final OutputStream auditStream,
        final OutputStream reportStream, final Map<String, Class<? extends VotingSystem>> headerSystemMap) throws ParseException,
        NullPointerException, IllegalArgumentException {
        //Require that the input stream and output streams are nonnull
        Objects.requireNonNull(inputs);
        for(final InputStream input : inputs) {
            Objects.requireNonNull(input);
        }
        Objects.requireNonNull(auditStream);
        Objects.requireNonNull(reportStream);
        
        //Throw an exception if not at least 1 InputStream instance is provided
        if(inputs.length < 1) {
            throw new IllegalArgumentException("The number of InputStream instances provided must be at least 1");
        }
        
        final String inputSourceOne = inputNames[0];
        
        //Use a BufferedReader to read from the input stream and PrintWriters to write to the output streams
        BufferedReader inReader = new BufferedReader(new InputStreamReader(inputs[0]));
        
        int lineNumber = 1;
        
        final VotingSystem votingSystem =
            parseElectionType(inReader, inputSourceOne, auditStream, reportStream, headerSystemMap, lineNumber);
        
        lineNumber++;
        
        //For testing purposes, modify the voting system before parsing
        if(VotingSystemRunner.votingSystemModifierBeforeParsing != null) {
            VotingSystemRunner.votingSystemModifierBeforeParsing.accept(votingSystem);
        }
        
        //Import the candidates header
        final int candidateHeaderSize = parseCandidateHeader(votingSystem, inReader, inputSourceOne, lineNumber);
        
        lineNumber += candidateHeaderSize;
        
        //Parse and add the candidates
        parseCandidates(votingSystem, inReader, inputSourceOne, lineNumber);
        
        lineNumber++;
        
        //The line number at which the ballot header line starts
        final int ballotHeaderLineNumber = lineNumber;
        
        //The current ballot number
        int ballotNumber = 1;
        
        //For each of the input sources
        for(int i = 0; i < inputs.length; i++) {
            //Get the identifier for the input source
            final String inputIdentifier = inputNames[i];
            
            /*
             * If not the first input source, then advance to the line at which the ballots header is located, throwing an exception if there is
             * not enough lines
             */
            if(i != 0) {
                inReader = new BufferedReader(new InputStreamReader(inputs[i]));
                for(int j = 1; j < ballotHeaderLineNumber; j++) {
                    final String nextLine = readLine(inReader, inputIdentifier, j);
                    throwParseExceptionIfEofLine(nextLine, inputIdentifier, j);
                }
            }
            
            //Start the line number at the ballot header line number
            lineNumber = ballotHeaderLineNumber;
            
            //Import the ballots header
            final int ballotsHeaderSize = parseBallotsHeader(votingSystem, inReader, inputIdentifier, lineNumber);
            
            lineNumber += ballotsHeaderSize;
            
            //Parse the ballots for the current input source
            final int numBallots = votingSystem.getNumBallots();
            ballotNumber = parseBallots(votingSystem, numBallots, ballotNumber, inReader, inputIdentifier, lineNumber);
        }
        
        return votingSystem;
    }
    
}
