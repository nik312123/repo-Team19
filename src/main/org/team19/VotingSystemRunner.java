package org.team19;

import java.io.File;

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
        if(args.length != 1) {
            System.err.println(
                "CompuVote requires exactly one command-line argument, which is an absolute file path or file path relative to repo-Team19 to the "
                    + "election file to parse"
            );
            System.exit(2);
        }
        String filePath = args[0];
        if(filePath.startsWith("~" + File.separator)) {
            filePath = System.getProperty("user.home") + filePath.substring(1);
        }
    }
    
}
