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

/**
 * Runs the election for a {@link VotingSystem} given a path to a file, which can be absolute or relative to the current working directory
 * <p></p>
 * If no file is given, then standard input will be used as the source
 * <p></p>
 * Creates an audit files that shows the steps, process, computations, etc., along with various statistics pertaining to the election
 * <p></p>
 * Creates a report file that shows a summary of various statistics pertaining to the election
 */
public final class VotingSystemRunner {
    
    /**
     * The potential source for the audit output set by classes in this package to specify an alternative output location for the audit contents
     */
    static OutputStream auditOutputPotentialSource = null;
    
    /**
     * The potential source for the audit output set by classes in this package to specify an alternative output location for the report contents
     */
    static OutputStream reportOutputPotentialSource = null;
    
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
     * Runs the election for a {@link VotingSystem} given a filepath relative to the workspace, using standard input if no file is provided
     *
     * @param args The command-line arguments to the program, which should only consist of at most one command-line argument: a path to a file, which
     *             can be absolute or relative to the current working directory
     */
    public static void main(final String... args) {
        //The input stream from which to read input
        InputStream input = null;
        
        //If there are no arguments provided, then assume standard input is being used
        if(args.length == 0) {
            System.out.println("Reading from standard input");
            input = System.in;
        }
        //If there is one argument provided, then assume it is a file, and try to retrieve its input stream
        else if(args.length == 1) {
            try {
                final String fullFilePath = getFullFilePath(args[0]);
                System.out.println("Reading from " + fullFilePath);
                input = getFileInputStream(fullFilePath);
            }
            catch(FileNotFoundException e) {
                System.err.println("The provided file could not be found or opened: " + e.getMessage());
                System.exit(2);
            }
            catch(IOException e) {
                System.err.println("The provided file's path could not be resolved: " + e.getMessage());
                System.exit(2);
            }
        }
        //If there are more than one command-line arguments given, then print an error and exit with a nonzero status
        else {
            System.err.println("CompuVote can have 0 command-line arguments for standard input or 1 for a path to an election CSV file");
            System.exit(2);
        }
        
        //Get the current date/time
        final LocalDateTime currentTimestamp = LocalDateTime.now();
        
        //Streams for the audit and report files
        OutputStream auditOutput = null;
        OutputStream reportOutput = null;
        
        //Retrieves the output streams for the audit and report files, using the potential source variables if set
        if(auditOutputPotentialSource == null) {
            try {
                auditOutput = getFileOutputStream(
                    "Project1/audits/".replace('/', File.separatorChar)
                        + generateTimestampedFileName("audit", currentTimestamp)
                );
            }
            catch(FileNotFoundException e) {
                System.err.println("The audit file could not be created");
                System.exit(2);
            }
        }
        else {
            auditOutput = auditOutputPotentialSource;
        }
        
        if(reportOutputPotentialSource == null) {
            try {
                reportOutput = getFileOutputStream(
                    "Project1/reports/".replace('/', File.separatorChar)
                        + generateTimestampedFileName("report", currentTimestamp)
                );
            }
            catch(FileNotFoundException e) {
                System.err.println("The report file could not be created");
                System.exit(2);
            }
        }
        else {
            reportOutput = reportOutputPotentialSource;
        }
        
        //Mapping of nonnull header strings to corresponding nonnull VotingSystem classes
        final Map<String, Class<? extends VotingSystem>> headerSystemMap = Map.of(
            "IR", InstantRunoffSystem.class,
            "OPL", OpenPartyListSystem.class
        );
        
        //Attempt to retrieve a voting system from parsing and run its election
        try {
            final VotingSystem votingSystem = VotingStreamParser.parse(input, auditOutput, reportOutput, headerSystemMap);
            votingSystem.runElection();
        }
        //If there is an issue in parsing the election file
        catch(ParseException e) {
            System.err.println(e.getMessage());
            final int dataFormattingExitCode = 65;
            System.exit(dataFormattingExitCode);
        }
        
        try {
            auditOutput.close();
        }
        catch(IOException e) {
            System.err.println("Error: Was unable to close audit file successfully");
            System.exit(2);
        }
        try {
            reportOutput.close();
        }
        catch(IOException e) {
            System.err.println("Error: Was unable to close report file successfully");
            System.exit(2);
        }
    }
    
}
