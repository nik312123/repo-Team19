/*
 * File name:
 * InstantRunoffSystem.java
 *
 * Author:
 * Nikunj Chawla and Jack Fornaro
 *
 * Purpose:
 * Represents the instant runoff voting system
 */

package org.team19;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link VotingSystem} representing the instant runoff voting system
 */
public class InstantRunoffSystem extends VotingSystem {
    
    /**
     * The number of candidates in this election
     */
    protected int numCandidates;
    
    /**
     * The number of ballots provided in this election
     */
    protected int numBallots;
    
    /**
     * The array of {@link Candidate}s for this election in the order presented in the election file
     */
    protected Candidate[] candidates;
    
    /**
     * The mapping of {@link Candidate}s to their current corresponding {@link Ballot}s
     */
    protected Map<Candidate, ArrayDeque<Ballot>> candidateBallotsMap = new LinkedHashMap<>();
    
    /**
     * The writer to an output stream for the audit file to write detailed information about the running of the election
     */
    protected PrintWriter auditWriter;
    
    /**
     * The writer to an output stream for the report file to write a summary about the running of the election.
     */
    protected PrintWriter reportWriter;
    
    /**
     * The {@link TableFormatter} used to produce tables as output
     */
    protected TableFormatter tableFormatter;
    
    /**
     * The pattern associated with a valid candidates line of the form "[candidate1] ([party1]),[candidate2] ([party2]), ...", replacing the
     * corresponding bracketed items with the actual candidate's name and party
     * <p></p>
     * Regex breakdown:
     * <ol>
     *     <li>^: Match the start of string</li>
     *     <li>
     *         *: Match the following group (noncapture group as denoted by "?:") any number of times
     *         <ol>
     *             <li>[^\(\),]+: One or more nonparenthesis or comma characters associated with the candidate name</li>
     *             <li>\([^\(\),]+\): One or more nonparenthesis or comma characters associated with a party that is surrounded by parentheses</li>
     *             <li>,: Comma</li>
     *         </ol>
     *     </li>
     *     <li>[^\(\),]+: One or more nonparenthesis or comma characters associated with the candidate name</li>
     *     <li>\([^\(\),]+\): One or more nonparenthesis or comma characters associated with a party that is surrounded by parentheses</li>
     *     <li>\s*: Followed by any amount of whitespace</li>
     *     <li>$: Match the end of the string</li>
     * </ol>
     */
    @SuppressWarnings("RegExpRedundantEscape")
    protected Pattern candidatesLinePattern = Pattern.compile("^(?:[^\\(\\),]+\\([^\\(\\),]+\\)\\s*,)*[^\\(\\),]+\\([^\\(\\),]+\\)\\s*$");
    
    /**
     * The pattern associated with a valid candidate of the form "[candidate1] ([party1])", replacing the corresponding bracketed items with the
     * actual candidate's name and party
     * <p></p>
     * Regex breakdown:
     * <ol>
     *     <li>^: Match the start of the string</li>
     *     <li>([^\(\),]+): One or more nonparenthesis or comma characters associated with the candidate name in a capture group</li>
     *      <li>\(([^\(\),]+)\): One or more nonparenthesis or comma characters associated with a party in a capture group that is surrounded by
     *      parentheses</li>
     *      <li>\s*: Followed by any amount of whitespace</li>
     *      <li>$: Match the end of the string</li>
     * </ol>
     */
    @SuppressWarnings("RegExpRedundantEscape")
    protected Pattern candidatePattern = Pattern.compile("^([^\\(\\),]+)\\(([^\\(\\),]+)\\)\\s*$");
    
    /**
     * Represents a ballot in an {@link InstantRunoffSystem} election
     */
    protected static class Ballot {
        /**
         * The ballot number associated with this ballot, corresponding to its position in the provided election file, starting at 1
         */
        protected int ballotNumber;
        
        /**
         * The index corresponding to the current candidate this ballot is on at the current stage of eliminations
         */
        protected int candidateIndex = -1;
        
        /**
         * The array of {@link Candidate}s in order of rank for this {@link Ballot}
         */
        protected Candidate[] rankedCandidates;
        
        /**
         * Initializes a {@link Ballot}
         *
         * @param ballotNumber     The ballot number associated with this ballot, corresponding to its position in the provided election file,
         *                         starting at 1
         * @param rankedCandidates The array of candidates in order of rank for this {@link Ballot}
         */
        protected Ballot(final int ballotNumber, final Candidate[] rankedCandidates) {
            this.ballotNumber = ballotNumber;
            this.rankedCandidates = rankedCandidates;
        }
        
        /**
         * Returns the ballot number associated with this ballot
         *
         * @return The ballot number associated with this ballot, corresponding to its position in the provided election file, starting at 1
         */
        public int getBallotNumber() {
            return ballotNumber;
        }
        
        /**
         * Returns the array of {@link Candidate}s in order of rank for this {@link Ballot}
         *
         * @return The array of {@link Candidate}s in order of rank for this {@link Ballot}
         */
        public Candidate[] getRankedCandidates() {
            return rankedCandidates;
        }
        
        /**
         * Returns the next ranked {@link Candidate} for this ballot
         *
         * @return The next ranked {@link Candidate} for this ballot
         */
        protected Candidate getNextCandidate() {
            if(candidateIndex >= rankedCandidates.length - 1) {
                return null;
            }
            candidateIndex++;
            return rankedCandidates[candidateIndex];
        }
        
        /**
         * Returns the {@link String} form of this {@link Ballot}
         *
         * @return The {@link String} form of this {@link Ballot}
         */
        public String toString() {
            return String.format("Ballot %d, %s", ballotNumber, Arrays.toString(rankedCandidates));
        }
        
        /**
         * Returns true if the provided {@link Object} is equivalent to this {@link Ballot}
         *
         * @param obj The object to compare to this {@link Ballot}
         * @return True if the provided {@link Object} is equivalent to this {@link Ballot}
         */
        @Override
        public boolean equals(final Object obj) {
            if(this == obj) {
                return true;
            }
            if(!(obj instanceof Ballot)) {
                return false;
            }
            final Ballot ballot = (Ballot) obj;
            return ballotNumber == ballot.ballotNumber && candidateIndex == ballot.candidateIndex && Arrays
                .equals(rankedCandidates, ballot.rankedCandidates);
        }
        
        /**
         * Returns the hashcode for this {@link Ballot}
         *
         * @return The hashcode for this {@link Ballot}
         */
        @Override
        public int hashCode() {
            final int hashMultiplier = 31;
            int result = Objects.hash(ballotNumber, candidateIndex);
            result = hashMultiplier * result + Arrays.hashCode(rankedCandidates);
            return result;
        }
    }
    
    /**
     * Initializes a {@link InstantRunoffSystem}
     *
     * @param auditOutput  The {@link OutputStream} to write detailed information about the running of the election
     * @param reportOutput The {@link OutputStream} to write a summary about the running of the election
     * @throws NullPointerException Thrown if either auditOutput or reportOutput is null
     */
    public InstantRunoffSystem(final OutputStream auditOutput, final OutputStream reportOutput) throws NullPointerException {
        super(auditOutput, reportOutput);
        Objects.requireNonNull(auditOutput);
        Objects.requireNonNull(reportOutput);
        
        auditWriter = new PrintWriter(auditOutput);
        reportWriter = new PrintWriter(reportOutput);
        
        tableFormatter = new TableFormatter('+', '-', '|');
    }
    
    /**
     * Returns the number of lines that makes up the header for the candidates
     *
     * @return The number of lines that makes up the header for the candidates
     */
    @Override
    public int getCandidateHeaderSize() {
        return 1;
    }
    
    /**
     * Returns the number of lines that makes up the header for the ballots
     *
     * @return The number of lines that makes up the header for the ballots
     */
    @Override
    public int getBallotHeaderSize() {
        return 1;
    }
    
    /**
     * Parses the lines corresponding to the header for the candidates
     *
     * @param header The lines corresponding to the header
     * @param line   The line number associated with the first line of the header
     * @throws ParseException Thrown if there is an issue in parsing the header
     */
    @Override
    public void importCandidatesHeader(final String[] header, final int line) throws ParseException {
        try {
            //Parses the number of candidates as the first (and only) line
            numCandidates = Integer.parseInt(header[0].strip());
            if(numCandidates <= 0) {
                VotingStreamParser.throwParseException(String.format(
                    "The number of candidates provided in the candidates header was %d but must be at least 1", numCandidates
                ), line);
            }
            
            //Output the number of candidates to the audit, report, and summary
            final String numCandidatesOutput = String.format("Number of Candidates: %d\n", numCandidates);
            auditWriter.println(numCandidatesOutput);
            reportWriter.println(numCandidatesOutput);
            System.out.println(numCandidatesOutput);
        }
        catch(NumberFormatException e) {
            VotingStreamParser.throwParseException(String.format(
                "The number of candidates provided in the candidates header \"%s\" was not a valid integer", header[0].strip()
            ), line);
        }
    }
    
    /**
     * Parses the candidates line from the election file and returns the resultant array
     *
     * @param candidatesLine The {@link String} representing the list of candidates and their parties
     * @param line           The line number associated with the candidates {@link String}
     * @return The parsed candidates array
     * @throws ParseException Thrown if there is an issue in parsing the candidates {@link String}
     */
    private Candidate[] parseCandidates(final String candidatesLine, final int line) throws ParseException {
        //If the candidates line does not match the regular expression for a valid candidates line, then throw an exception
        if(!candidatesLinePattern.matcher(candidatesLine).matches()) {
            VotingStreamParser.throwParseException(String.format(
                "The given candidates line \"%s\" does not match the format \"[candidate1] ([party1]),[candidate2] ([party2]), ...\"",
                candidatesLine
            ), line);
        }
        
        //Split the candidates line by comma delimiter and add each candidate to an array
        final String[] candidatesStr = candidatesLine.split(",");
        final Candidate[] candidatesArr = new Candidate[candidatesStr.length];
        for(int i = 0; i < candidatesArr.length; ++i) {
            final Matcher candidateMatcher = candidatePattern.matcher(candidatesStr[i]);
            /*
             * Using the matched groups the correspond to the candidate's name and party, strip the strings of whitespace and add them to the list
             * of candidates
             */
            try {
                candidatesArr[i] = new Candidate(candidateMatcher.group(1).strip(), candidateMatcher.group(2).strip());
            }
            //If, for some inexplicable reason, there exists a candidate string that doesn't properly match the format, then throw an exception
            catch(IllegalStateException | IndexOutOfBoundsException e) {
                VotingStreamParser.throwParseException(String.format(
                    "The number of ballots provided in the ballots header \"%s\" was not a valid integer", numBallots
                ), line + 1);
                VotingStreamParser.throwParseException(String.format(
                    "The given candidates line \"%s\" does not match the format \"[candidate1] ([party1]),[candidate2] ([party2]), ...\"",
                    candidatesLine
                ), line);
            }
        }
        return candidatesArr;
    }
    
    /**
     * Parses a {@link String} corresponding to candidates and party and adds them internally
     *
     * @param candidatesLine The {@link String} representing the list of candidates and their parties
     * @param line           The line number associated with the candidates {@link String}
     * @throws ParseException Thrown if there is an issue in parsing the candidates {@link String}
     */
    @Override
    public void addCandidates(final String candidatesLine, final int line) throws ParseException {
        candidates = parseCandidates(candidatesLine, line);
        
        //Print the output corresponding to the candidates
        auditWriter.println("Candidates:");
        reportWriter.println("Candidates:");
        System.out.println("Candidates:");
        
        for(final Candidate candidate : candidates) {
            final String candidateStr = candidate.toString();
            auditWriter.println(candidateStr);
            reportWriter.println(candidateStr);
            System.out.println(candidateStr);
        }
        
        auditWriter.println();
        reportWriter.println();
        System.out.println();
    }
    
    /**
     * Parses the lines corresponding to the header for the ballots
     *
     * @param header The lines corresponding to the header
     * @param line   The line number associated with the first line of the header
     * @throws ParseException Thrown if there is an issue in parsing the header
     */
    @Override
    public void importBallotsHeader(final String[] header, final int line) throws ParseException {
        try {
            //Parses the number of ballots as the first (and only) line
            numBallots = Integer.parseInt(header[0].strip());
            if(numBallots < 0) {
                VotingStreamParser.throwParseException(String.format(
                    "The number of ballots provided in the ballots header was %d but must be nonnegative", numBallots
                ), line);
            }
            
            //Output the number of ballots to the audit, report, and summary
            final String numBallotsOutput = String.format("Number of Ballots: %d\n", numBallots);
            auditWriter.println(numBallotsOutput);
            reportWriter.println(numBallotsOutput);
            System.out.println(numBallotsOutput);
        }
        catch(NumberFormatException e) {
            VotingStreamParser.throwParseException(String.format(
                "The number of ballots provided in the ballots header \"%s\" was not a valid integer", header[0].strip()
            ), line);
        }
    }
    
    /**
     * Parses the ballot line from the election file and returns the resultant {@link Ballot}
     *
     * @param ballotNumber The number corresponding to the current ballot
     * @param ballotLine   The {@link String} corresponding to a ballot
     * @param line         The line number associated with the current ballot line being read
     * @return The {@link Ballot} from parsing the ballot line
     * @throws ParseException Thrown if the format or contents of the ballot line are invalid
     */
    private Ballot parseBallot(final int ballotNumber, final String ballotLine, final int line) throws ParseException {
        //Split the ballots line by the comma delimiter
        final String[] ballotStr = ballotLine.split(",");
        
        //If the number of values for the current ballot is not equivalent to the number of candidates, then throw an exception
        if(ballotStr.length != numCandidates) {
            VotingStreamParser.throwParseException(String.format(
                "The number of values %d for this ballot is not equivalent to the number of candidates %d", ballotStr.length, numCandidates
            ), line);
        }
        
        //Store the minimum and maximum rank found in the rankings
        int minRank = Integer.MAX_VALUE;
        int maxRank = 0;
        
        //Mapping of rankings to candidates for the ballot
        final Map<Integer, Candidate> rankedCandidateMap = new HashMap<>();
        
        for(int i = 0; i < numCandidates; ++i) {
            final String ballotRank = ballotStr[i].strip();
            
            //If the current ballot rank is empty, then continue to the next ballot rank
            if(ballotRank.isEmpty()) {
                continue;
            }
            
            try {
                //Try to parse the current rank value as an integer
                final int currentRank = Integer.parseInt(ballotRank);
                
                //If the current rank is less than 1 or greater than the number of candidates, then it is invalid, so throw an exception
                if(currentRank < 1 || currentRank > numCandidates) {
                    VotingStreamParser.throwParseException(String.format(
                        "The provided rank %d is out of the range %d to %d for %d candidates",
                        currentRank, 1, numCandidates, numCandidates
                    ), line);
                }
                
                //Update the minimum, maximum, and ranked candidates map
                minRank = Math.min(minRank, currentRank);
                maxRank = Math.max(maxRank, currentRank);
                rankedCandidateMap.put(currentRank, candidates[currentRank - 1]);
            }
            //If the current rank is not a valid integer, throw an exception
            catch(NumberFormatException e) {
                VotingStreamParser.throwParseException(String.format("The rank provided \"%s\" was not a valid integer", ballotStr[i]), line);
            }
        }
        
        //If the ballot has not ranked a single candidate, then throw an exception
        if(minRank == Integer.MAX_VALUE) {
            VotingStreamParser.throwParseException("A ballot must rank at least one candidate", line);
        }
        //If the ballot numbering does not start with 1, then throw an exception for skipping rankings
        else if(minRank != 1) {
            VotingStreamParser.throwParseException("A ballot must start ranking at 1", line);
        }
        
        //For each of the ranked candidates, add it to an array, throwing an exception if there are any skipped ranks
        final Candidate[] rankedCandidates = new Candidate[maxRank];
        for(int i = 1; i <= maxRank; ++i) {
            if(!rankedCandidateMap.containsKey(i)) {
                VotingStreamParser.throwParseException(String.format(
                    "A ballot must not skip rankings, but a ranking was not found for %d when there is a rank for %d", i, maxRank
                ), line);
            }
            rankedCandidates[i - 1] = rankedCandidateMap.get(i);
        }
        
        return new Ballot(ballotNumber, rankedCandidates);
    }
    
    /**
     * Parses a line corresponding to a ballot and adds it internally
     *
     * @param ballotNumber The number corresponding to the current ballot
     * @param ballotLine   The {@link String} corresponding to a ballot
     * @param line         The line number associated with the current ballot line being read
     * @throws ParseException Thrown if there is an issue in parsing the current ballot
     */
    @Override
    public void addBallot(final int ballotNumber, final String ballotLine, final int line) throws ParseException {
        final Ballot ballot = parseBallot(ballotNumber, ballotLine, line);
        
        //Get the candidate associated with the first ranking of the ballot
        final Candidate firstRankedCandidate = ballot.getNextCandidate();
        
        //Writes the output for this ballot to the audit output
        auditWriter.printf("Ballot %d's rankings are as follows:\n", ballotNumber);
        
        final Candidate[] rankedCandidates = ballot.getRankedCandidates();
        for(int i = 0; i < rankedCandidates.length; i++) {
            auditWriter.printf("    %d – %s\n", i + 1, rankedCandidates[i]);
        }
        auditWriter.printf("Therefore, ballot %d goes to %s\n\n", ballotNumber, firstRankedCandidate);
        
        //If the candidate is not in the candidatesBallotsMap, create an empty ArrayDeque for the candidate's ballots
        if(!candidateBallotsMap.containsKey(firstRankedCandidate)) {
            candidateBallotsMap.put(firstRankedCandidate, new ArrayDeque<>());
        }
        
        //Add the ballot to its first ranked candidate list of ballots
        candidateBallotsMap.get(firstRankedCandidate).add(ballot);
    }
    
    /**
     * Returns the name of this voting system
     *
     * @return The name of this voting system
     */
    @Override
    public String getName() {
        return "Instant Runoff Voting";
    }
    
    /**
     * Returns the short name for the voting system; that is, the name that appears at the top of an election file
     *
     * @return The short name for the voting system
     */
    @Override
    public String getShortName() {
        return "IR";
    }
    
    /**
     * Precondition: {@link #importCandidatesHeader(String[], int)} has been executed successfully
     * <p></p>
     * Returns the number of candidates that the {@link InstantRunoffSystem} contains
     *
     * @return The number of candidates that the {@link InstantRunoffSystem} contains
     */
    @Override
    public int getNumCandidates() {
        return numCandidates;
    }
    
    /**
     * Precondition: {@link #addCandidates(String, int)} has been executed successfully
     * <p></p>
     * Returns the {@link Collection} of {@link Candidate}s for this {@link InstantRunoffSystem}
     *
     * @return The {@link Collection} of {@link Candidate}s for this {@link InstantRunoffSystem}
     */
    @Override
    public Collection<Candidate> getCandidates() {
        return List.of(candidates);
    }
    
    /**
     * Precondition: {@link #importBallotsHeader(String[], int)} has been executed successfully
     * <p></p>
     * Returns the number of ballots that the {@link InstantRunoffSystem} contains, which should be available after
     *
     * @return The number of ballots that the {@link InstantRunoffSystem} contains
     */
    @Override
    public int getNumBallots() {
        return 0;
    }
    
    @Override
    public void runElection() {}
    
    /**
     * Returns the string form of this {@link InstantRunoffSystem}
     *
     * @return The string form of this {@link InstantRunoffSystem}
     */
    @Override
    public String toString() {
        return String.format("InstantRunoffSystem{candidates=%s, numBallots=%d}", Arrays.toString(candidates), numBallots);
    }
}
