package org.team19;

import java.io.OutputStream;
import java.text.ParseException;
import java.util.Collection;

/**
 * Represents a voting system
 */
public abstract class VotingSystem {
    
    /**
     * Initializes a {@link VotingSystem} given the number of candidates
     *
     * @param numCandidates The number of candidates in the election
     * @param auditOutput   The {@link OutputStream} to write detailed information about the running of the election
     * @param reportOutput  The {@link OutputStream} to write a summary about the running of the election
     */
    public VotingSystem(final int numCandidates, final OutputStream auditOutput, final OutputStream reportOutput) {}
    
    /**
     * Returns the name of this voting system
     *
     * @return The name of this voting system
     */
    public abstract String votingSystemName();
    
    /**
     * Returns the number of lines that makes up the header
     *
     * @return The number of lines that makes up the header
     */
    public abstract int getHeaderSize();
    
    /**
     * Returns a {@link Collection} of {@link Pair}s corresponding to the candidates, which should be available after {@link #parseCandidates} has
     * been executed successfully
     *
     * @return A {@link Collection} of {@link Pair}s with each pair's key corresponding to the candidate's name and value corresponding to the party
     */
    public abstract Collection<Pair<String, String>> getCandidates();
    
    /**
     * Returns the number of ballots that the {@link VotingSystem} contains, which should be available after {@link #parseSystemHeader} has been
     * executed successfully
     *
     * @return The number of ballots that the {@link VotingSystem} contains
     */
    public abstract int getNumBallots();
    
    /**
     * Parses a {@link String} corresponding to candidates
     * and party
     *
     * @param candidates The {@link String} representing the list of candidates and their parties
     * @param line       The line number associated with the candidates {@link String}
     * @throws ParseException Thrown if there is an issue in parsing the candidates {@link String}
     */
    public abstract void parseCandidates(final String candidates, final int line) throws ParseException;
    
    /**
     * Parses the lines corresponding to the header for the voting system
     *
     * @param header The lines corresponding to the header
     * @param line   The line number associated with the first line of the header
     * @throws ParseException Thrown if there is an issue in parsing the header
     */
    public abstract void parseSystemHeader(final String[] header, final int line) throws ParseException;
    
    /**
     * Parses a line corresponding to a ballot and returns the ballot
     *
     * @param ballot The {@link String} corresponding to a ballot
     * @param line   The line number associated with the current ballot line being read
     * @throws ParseException Thrown if there is an issue in parsing the current ballot
     */
    public abstract void parseBallot(final String ballot, final int line) throws ParseException;
    
    /**
     * Runs the election for the {@link VotingSystem} and determines the winner
     */
    public abstract void runElection();
}
