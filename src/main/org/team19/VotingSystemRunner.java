package org.team19;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Runs the election for a {@link VotingSystem} given a path to a file, which can be absolute or relative to the current working directory
 * <p></p>
 * Creates an audit files that shows the steps, process, computations, etc., along with various statistics pertaining to the election
 * <p></p>
 * Creates a report file that shows a summary of various statistics pertaining to the election
 */
public final class VotingSystemRunner {
    
    /**
     * A private constructor for the utility class {@link VotingSystemRunner} to prevent instantiation
     */
    private VotingSystemRunner() {}
    
    /**
     * Runs the election for a {@link VotingSystem} given a filepath relative to the workspace
     *
     * @param args The command-line arguments to the program, which should only consist of one command-line argument: a path to a file, which can
     *             be absolute or relative to the current working directory
     */
    public static void main(final String[] args) {
        //The input stream from which to read input
        final InputStream input;
    
        //If there are no arguments provided, then assume standard input is being used
        if(args.length == 0) {
            System.out.println("Reading from standard input");
            input = System.in;
        }
        //If there is one argument provided, then assume it is a file, and try to retrieve its input stream
        else if(args.length == 1) {
            String filePath = args[0];
    
            //If it starts with ~ plus the file separator, replace it with the home directory
            if(filePath.startsWith("~" + File.separator)) {
                filePath = System.getProperty("user.home") + filePath.substring(1);
            }
    
            //Attempt to resolve the path name to a valid file path (system dependent)
            try {
                filePath = new File(filePath).getCanonicalPath();
            }
            catch(IOException e) {
                System.err.println("The provided file path could not be resolved: " + e.getMessage());
            }
    
            //Attempt to retrieve the file at the given file path
            try {
                System.out.println("Reading from " + filePath);
                input = new FileInputStream(filePath);
            }
            catch(FileNotFoundException e) {
                System.err.println("The provided file could not be opened: " + e.getMessage());
                System.exit(2);
            }
        }
        //If there are more than one command-line arguments given, then print an error and exit with a nonzero status
        else {
            System.err.println("CompuVote can have 0 command-line arguments for standard input or 1 for a path to an election CSV file");
            System.exit(2);
        }
    }
    
}
