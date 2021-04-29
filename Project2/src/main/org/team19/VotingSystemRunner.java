/*
 * File name:
 * VotingSystemRunner.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Runs the election for a VotingSystem given a path to a file, which can be absolute or relative to the current working directory
 *
 * If no file is given, then standard input will be used as the source
 *
 * Creates an audit files that shows the steps, process, computations, etc., along with various statistics pertaining to the election
 *
 * Creates a report file that shows a summary of various statistics pertaining to the election
 */

package org.team19;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Runs the election for a {@link VotingSystem} given paths to election files that can be absolute or relative to the current working directory and
 * compose a single election
 * <p></p>
 * If no command-line arguments are given, then standard input will be used as the source
 * <p></p>
 * Creates an audit files that shows the steps, process, computations, etc., along with various statistics pertaining to the election
 * <p></p>
 * Creates a report file that shows a summary of various statistics pertaining to the election
 */
public final class VotingSystemRunner {
    
    /**
     * The potential source for the audit output set by test classes in this package to specify an alternative output location for the audit contents
     */
    static OutputStream auditOutputPotentialSource = null;
    
    /**
     * The potential source for the audit output set by test classes in this package to specify an alternative output location for the report contents
     */
    static OutputStream reportOutputPotentialSource = null;
    
    /**
     * The potential consumer set by test classes in this package used to modify the {@link VotingSystem} before parsing
     */
    static Consumer<VotingSystem> votingSystemModifierBeforeParsing = null;
    
    /**
     * The potential consumer set by test classes in this package used to modify the {@link VotingSystem} before the election
     */
    static Consumer<VotingSystem> votingSystemModifierBeforeElection = null;
    
    /**
     * A private constructor for the utility class {@link VotingSystemRunner} to prevent instantiation
     */
    private VotingSystemRunner() {}
    
    /**
     * Returns the full, unique canonical form of the provided file path
     *
     * @param filePath The provided file path command-line argument
     * @return The full, unique canonical form of the provided file path
     * @throws IOException Thrown if the provided file path cannot be resolved
     */
    private static String getFullFilePath(String filePath) throws IOException {
        //If it starts with ~ plus the file separator, replace it with the home directory
        if(filePath.startsWith("~" + File.separator)) {
            filePath = System.getProperty("user.home") + filePath.substring(1);
        }
        
        //Attempt to resolve the path name to a valid file path (system dependent)
        return new File(filePath).getCanonicalPath();
    }
    
    /**
     * Retrieves the {@link FileInputStream} for the file at the provided canonical file path
     *
     * @param canonicalPath The canonical path from which to retrieve an input stream
     * @return The {@link FileInputStream} for the file at the provided canonical file path
     * @throws FileNotFoundException Thrown if a file cannot be found or opened from the provided file path
     */
    private static FileInputStream getFileInputStream(final String canonicalPath) throws FileNotFoundException {
        return new FileInputStream(canonicalPath);
    }
    
    /**
     * Returns a timestamped file name using the given prefix and timestamp
     * variable and the other items are replaced with the temporal information from currentTimeStamp
     *
     * @param prefix           The prefix for the timestamped file name
     * @param currentTimestamp The timestamp to use for the file name
     * @return A file name in the form "[prefix]_[year]-[month]-[day]_[hours]-[minutes]-[seconds].txt" where [prefix] is replaced with the provided
     * variable and the other items are replaced with the temporal information from currentTimeStamp
     */
    private static String generateTimestampedFileName(final String prefix, final LocalDateTime currentTimestamp) {
        return String.format(
            "%s_%d-%02d-%02d_%02d-%02d-%02d.txt",
            prefix,
            currentTimestamp.getYear(),
            currentTimestamp.getMonth().getValue(),
            currentTimestamp.getDayOfMonth(),
            currentTimestamp.getHour(),
            currentTimestamp.getMinute(),
            currentTimestamp.getSecond()
        );
    }
    
    /**
     * Retrieves the {@link FileOutputStream} for the file at the provided file path, creating parent directories as needed
     *
     * @param path The path at which the file should be written
     * @return The {@link FileOutputStream} for the file at the provided file path
     * @throws FileNotFoundException Thrown if the file cannot be created or written to at the provided file path
     */
    private static FileOutputStream getFileOutputStream(final String path) throws FileNotFoundException {
        final File outputFile = new File(path);
        
        //noinspection ResultOfMethodCallIgnored
        outputFile.getParentFile().mkdirs();
        return new FileOutputStream(outputFile);
    }
    
    /**
     * Given the command-line arguments, which are presumed to be file paths, create {@link InputStream}s from them, and return the array of
     * {@link InputStream}s
     *
     * @param args The command-line arguments for the program
     * @return An array of {@link InputStream}s converted from the command-line arguments
     */
    private static InputStream[] getInputStreams(final String[] args) {
        final InputStream[] inputs;
        inputs = new InputStream[args.length];
        for(int i = 0; i < inputs.length; i++) {
            try {
                final String fullFilePath = getFullFilePath(args[i]);
                System.out.println("Reading from " + fullFilePath);
                inputs[i] = getFileInputStream(fullFilePath);
            }
            catch(FileNotFoundException e) {
                System.err.printf("The provided file path %s could not be found or opened: %s\n", args[i], e.getMessage());
                System.exit(2);
            }
            catch(IOException e) {
                System.err.printf("The provided file path %s could not be resolved: %s\n", args[i], e.getMessage());
                System.exit(2);
            }
        }
        return inputs;
    }
    
    /**
     * Returns the audit {@link OutputStream}
     *
     * @param currentTimestamp The current timestamp upon running the program
     * @return The audit {@link OutputStream}
     */
    private static OutputStream getAuditOutput(final LocalDateTime currentTimestamp) {
        OutputStream auditOutput = null;
        //If the audit location is not set by tests
        if(auditOutputPotentialSource == null) {
            try {
                auditOutput = getFileOutputStream(
                    "Project2/audits/".replace('/', File.separatorChar)
                        + generateTimestampedFileName("audit", currentTimestamp)
                );
            }
            catch(FileNotFoundException e) {
                System.err.println("The audit file could not be created");
                System.exit(2);
            }
        }
        //If the audit location is set by tests
        else {
            auditOutput = auditOutputPotentialSource;
        }
        return auditOutput;
    }
    
    /**
     * Returns the report {@link OutputStream}
     *
     * @param currentTimestamp The current timestamp upon running the program
     * @return The report {@link OutputStream}
     */
    private static OutputStream getReportOutput(final LocalDateTime currentTimestamp) {
        OutputStream reportOutput = null;
        //If the report location is not set by tests
        if(reportOutputPotentialSource == null) {
            try {
                reportOutput = getFileOutputStream(
                    "Project2/reports/".replace('/', File.separatorChar)
                        + generateTimestampedFileName("report", currentTimestamp)
                );
            }
            catch(FileNotFoundException e) {
                System.err.println("The report file could not be created");
                System.exit(2);
            }
        }
        //If the report location is set by tests
        else {
            reportOutput = reportOutputPotentialSource;
        }
        return reportOutput;
    }
    
    /**
     * Close an {@link OutputStream} corresponding to a type of output, printing an error message specific to the output type if the
     * {@link OutputStream} could not be closed
     *
     * @param output     The {@link OutputStream} to close
     * @param outputType The name associated with the output
     */
    private static void closeOutput(final OutputStream output, final String outputType) {
        try {
            output.close();
        }
        catch(IOException e) {
            System.err.printf("Error: Was unable to close %s file successfully\n", outputType);
            System.exit(2);
        }
    }
    
    /**
     * Runs the election for a {@link VotingSystem} given paths to election files that can be absolute or relative to the current working directory
     * and compose a single election, using standard input if none are given
     *
     * @param args The command-line arguments to the program, which should only consist of paths to election files that can be absolute or relative
     *             to the current working directory and compose a single election
     */
    public static void main(final String... args) {
        //Get the current date/time
        final LocalDateTime currentTimestamp = LocalDateTime.now();
        
        //The input stream from which to read input
        final InputStream[] inputs;
        
        //The names corresponding to the input streams
        final String[] inputNames;
        
        //If there are no arguments provided, then assume standard input is being used
        if(args.length == 0) {
            System.out.println("Reading from standard input");
            inputs = new InputStream[] {System.in};
            inputNames = new String[] {"Standard Input"};
        }
        //If there is one argument provided, then assume it is a file, and try to retrieve its input stream
        else {
            inputs = getInputStreams(args);
            inputNames = args;
        }
        
        //Retrieves the output streams for the audit and report files, using the potential source variables if set
        final OutputStream auditOutput = getAuditOutput(currentTimestamp);
        final OutputStream reportOutput = getReportOutput(currentTimestamp);
        
        //Mapping of nonnull header strings to corresponding nonnull VotingSystem classes
        final Map<String, Class<? extends VotingSystem>> headerSystemMap = Map.of(
            "IR", InstantRunoffSystem.class,
            "OPL", OpenPartyListSystem.class
        );
        
        //Attempt to retrieve a voting system from parsing and run its election
        try {
            final VotingSystem votingSystem = VotingStreamParser.parse(inputs, inputNames, auditOutput, reportOutput, headerSystemMap);
            
            //For testing purposes, modify the voting system before running the election
            if(votingSystemModifierBeforeElection != null) {
                votingSystemModifierBeforeElection.accept(votingSystem);
            }
            
            votingSystem.runElection();
        }
        //If there is an issue in parsing the election file
        catch(ParseException e) {
            System.err.println(e.getMessage());
            final int dataFormattingExitCode = 65;
            System.exit(dataFormattingExitCode);
        }
        
        closeOutput(auditOutput, "audit");
        closeOutput(reportOutput, "report");
    }
    
}
