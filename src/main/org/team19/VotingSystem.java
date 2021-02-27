package org.team19;

import java.io.OutputStream;
import java.text.ParseException;
import java.util.Collection;

/**
 * Represents a voting system
 */
public abstract class VotingSystem {
    
    /**
     * Initializes a {@link VotingSystem}
     *
     * @param numCandidates The number of candidates in the election
     * @param auditOutput   The {@link OutputStream} to write detailed information about the running of the election
     * @param reportOutput  The {@link OutputStream} to write a summary about the running of the election
     */
    public VotingSystem(final int numCandidates, final OutputStream auditOutput, final OutputStream reportOutput) {}
    
    /**
     * Returns the number of lines that makes up the header for the candidates
     *
     * @return The number of lines that makes up the header for the candidates
     */
    public abstract int getCandidateHeaderSize();
    
    /**
     * Returns the number of lines that makes up the header for the ballots
     *
     * @return The number of lines that makes up the header for the ballots
     */
    public abstract int getBallotHeaderSize();
    
    /**
     * Parses the lines corresponding to the header for the candidates
     *
     * @param header The lines corresponding to the header
     * @param line   The line number associated with the first line of the header
     * @throws ParseException Thrown if there is an issue in parsing the header
     */
    public abstract void parseCandidatesHeader(final String[] header, final int line) throws ParseException;
    
    /**
     * Parses a {@link String} corresponding to candidates and party and adds them internally
     *
     * @param candidates The {@link String} representing the list of candidates and their parties
     * @param line       The line number associated with the candidates {@link String}
     * @throws ParseException Thrown if there is an issue in parsing the candidates {@link String}
     */
    public abstract void parseCandidates(final String candidates, final int line) throws ParseException;
    
    /**
     * Parses the lines corresponding to the header for the ballots
     *
     * @param header The lines corresponding to the header
     * @param line   The line number associated with the first line of the header
     * @throws ParseException Thrown if there is an issue in parsing the header
     */
    public abstract void parseBallotHeader(final String[] header, final int line) throws ParseException;
    
    /**
     * Parses a line corresponding to a ballot and adds it internally
     *
     * @param ballot The {@link String} corresponding to a ballot
     * @param line   The line number associated with the current ballot line being read
     * @throws ParseException Thrown if there is an issue in parsing the current ballot
     */
    public abstract void parseBallot(final String ballot, final int line) throws ParseException;
    
    /**
     * Returns the name of this voting system
     *
     * @return The name of this voting system
     */
    public abstract String getName();
    
    /**
     * Returns the number of candidates that the {@link VotingSystem} contains, which should be available after
     * {@link #parseCandidatesHeader(String[], int)} has been executed successfully
     *
     * @return The number of candidates that the {@link VotingSystem} contains
     */
    public abstract int getNumCandidates();
    
    /**
     * Returns the {@link Collection} of {@link Candidate}s for this {@link VotingSystem}, which should be available after
     * {@link #parseCandidates(String, int)} has been executed successfully
     *
     * @return The {@link Collection} of {@link Candidate}s for this {@link VotingSystem}
     */
    public abstract Collection<Candidate> getCandidates();
    
    /**
     * Returns the number of ballots that the {@link VotingSystem} contains, which should be available after
     * {@link #parseBallotHeader(String[], int)} has been executed successfully
     *
     * @return The number of ballots that the {@link VotingSystem} contains
     */
    public abstract int getNumBallots();
    
    /**
     * Runs the election for the {@link VotingSystem} and determines the winner
     *
     * @param useRandom True if randomization should be used in breaking ties and false if the first item should be chosen in the event of ties
     */
    public abstract void runElection(final boolean useRandom);
    
    /**
     * Returns the string form of this {@link VotingSystem}
     *
     * @return The string form of this {@link VotingSystem}
     */
    @Override
    public abstract String toString();
    
    /**
     * Returns true if the given object is equivalent to this {@link VotingSystem}
     *
     * @param other The object to compare to this {@link VotingSystem}
     * @return True if the given object is equivalent to this {@link VotingSystem}
     */
    @Override
    public abstract boolean equals(Object other);
    
    /**
     * Returns the hashcode for this {@link VotingSystem}
     *
     * @return The hashcode for this {@link VotingSystem}
     */
    @Override
    public abstract int hashCode();
}
