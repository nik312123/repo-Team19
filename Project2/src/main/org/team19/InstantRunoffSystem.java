/*
 * File name:
 * InstantRunoffSystem.java
 *
 * Author:
 * Nikunj Chawla, Jack Fornaro, and Aaron Kandikatla
 *
 * Purpose:
 * Represents the instant runoff voting system
 */

package org.team19;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link VotingSystem} representing the instant runoff voting system
 */
public class InstantRunoffSystem extends VotingSystem {
    
    /**
     * Used for randomization for breaking ties
     */
    protected static Random rand = new SecureRandom();
    
    /**
     * The number of candidates in this election
     */
    protected int numCandidates;
    
    /**
     * The number of ballots provided in this election
     */
    protected int numBallots;
    
    /**
     * Half of the number of ballots provided in this election
     */
    protected int halfNumBallots;
    
    /**
     * The array of {@link Candidate}s for this election in the order presented in the election file
     */
    protected Candidate[] candidates;
    
    /**
     * The mapping of {@link Candidate}s to their current corresponding {@link Ballot}s
     */
    protected Map<Candidate, Deque<Ballot>> candidateBallotsMap = new LinkedHashMap<>();
    
    /**
     * The writer to an output stream for the audit file to write detailed information about the running of the election
     */
    protected PrintWriter auditWriter;
    
    /**
     * The writer to an output stream for the report file to write a summary about the running of the election.
     */
    protected PrintWriter reportWriter;
    
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
            return String.format("Ballot %d: %s", ballotNumber, Arrays.toString(rankedCandidates));
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
            return ballotNumber == ballot.ballotNumber && candidateIndex == ballot.candidateIndex &&
                Arrays.equals(rankedCandidates, ballot.rankedCandidates);
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
     * Initializes a {@link VotingSystem}
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
        //If the candidates line does not consist of any candidates
        if(candidatesLine.isBlank()) {
            VotingStreamParser.throwParseException(String.format(
                "The given candidates line \"%s\" must consist of at least one candidate",
                candidatesLine
            ), line);
        }
        
        //Split the candidates line by comma delimiter and add each candidate to an array
        final String[] candidatesStr = candidatesLine.split(",", -1);
        final Candidate[] candidatesArr = new Candidate[candidatesStr.length];
        for(int i = 0; i < candidatesArr.length; i++) {
            final Matcher candidateMatcher = candidatePattern.matcher(candidatesStr[i]);
            /*
             * Using the matched groups the correspond to the candidate's name and party, strip the strings of whitespace and add them to the list
             * of candidates
             */
            try {
                //noinspection ResultOfMethodCallIgnored
                candidateMatcher.find();
                candidatesArr[i] = new Candidate(candidateMatcher.group(1).strip(), candidateMatcher.group(2).strip());
                
                final String candidateStr = candidatesArr[i].toString();
                auditWriter.println(candidateStr);
                reportWriter.println(candidateStr);
                System.out.println(candidateStr);
            }
            //If the candidates line does not match the regular expression for a valid candidates line, then throw an exception
            catch(IllegalStateException | IndexOutOfBoundsException e) {
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
        //Print the output corresponding to the candidates
        auditWriter.println("Candidates:");
        reportWriter.println("Candidates:");
        System.out.println("Candidates:");
        
        candidates = parseCandidates(candidatesLine, line);
        
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
            halfNumBallots = numBallots / 2;
            
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
     * Retrieves the index of the string after the positive integer
     *
     * @param str The string from which to find the index after the positive integer
     * @param pos The position at which to find the index after the positive integer
     * @return The index of the string after the positive integer
     */
    private int getIndexAfterPositiveInteger(final String str, int pos) {
        //We can already include the current character in the integer or this method would not be called
        pos++;
        
        //Continues iteration through the indices of the string only if the current character is a digit
        for(; pos < str.length(); pos++) {
            if(!Character.isDigit(str.charAt(pos))) {
                break;
            }
        }
        
        return pos;
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
        int numCommas = 0;
        
        //Store the minimum and maximum rank found in the rankings
        int minRank = Integer.MAX_VALUE;
        int maxRank = 0;
        
        //Mapping of rankings to candidates for the ballot
        final Map<Integer, Candidate> rankedCandidateMap = new HashMap<>();
        
        //Iterate through the characters of the ballot line
        for(int i = 0; i < ballotLine.length(); i++) {
            final char curChar = ballotLine.charAt(i);
            
            if(curChar == ',') {
                numCommas++;
            }
            else if(Character.isDigit(curChar)) {
                //Retrieve the index after the full integer rank
                final int posAfterRank = getIndexAfterPositiveInteger(ballotLine, i);
                
                //Retrieve the rank by parsing the integer rank string
                final int rank = Integer.parseUnsignedInt(ballotLine.substring(i, posAfterRank));
                
                //If the current rank is less than 1 or greater than the number of candidates, then it is invalid, so throw an exception
                if(rank < 1 || rank > numCandidates) {
                    VotingStreamParser.throwParseException(String.format(
                        "The provided rank %d is out of the range %d to %d for %d candidates",
                        rank, 1, numCandidates, numCandidates
                    ), line);
                }
                
                //Update the minimum, maximum, and ranked candidates map
                minRank = Math.min(minRank, rank);
                maxRank = Math.max(maxRank, rank);
                rankedCandidateMap.put(rank, candidates[numCommas]);
                
                //Change the current index i to the position of the last character of the rank number
                i = posAfterRank - 1;
            }
            else if(!Character.isWhitespace(curChar)) {
                VotingStreamParser.throwParseException(String.format(
                    "Ballot lines can only consist of commas, digits, and whitespace for IR, but character %c was found",
                    curChar
                ), line);
            }
        }
        
        //If the number of values for the current ballot is not equivalent to the number of candidates, then throw an exception
        if(numCommas + 1 != numCandidates) {
            VotingStreamParser.throwParseException(String.format(
                "The number of values %d for this ballot is not equivalent to the number of candidates %d", numCommas + 1, numCandidates
            ), line);
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
        for(int i = 1; i <= maxRank; i++) {
            if(!rankedCandidateMap.containsKey(i)) {
                VotingStreamParser.throwParseException(String.format(
                    "A ballot must not skip rankings, but a ranking was not found for %d when there is a rank for %d", i, maxRank
                ), line);
            }
            rankedCandidates[i - 1] = rankedCandidateMap.get(i);
            auditWriter.printf("    %d – %s\n", i, rankedCandidates[i - 1]);
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
        //Writes the output for this ballot to the audit output
        auditWriter.printf("Ballot %d's rankings are as follows:\n", ballotNumber);
        
        final Ballot ballot = parseBallot(ballotNumber, ballotLine, line);
        
        //Get the candidate associated with the first ranking of the ballot
        final Candidate firstRankedCandidate = ballot.getNextCandidate();
        
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
     * Returns the number of ballots that the {@link InstantRunoffSystem} contains
     *
     * @return The number of ballots that the {@link InstantRunoffSystem} contains
     */
    @Override
    public int getNumBallots() {
        return numBallots;
    }
    
    /**
     * Returns the string form of this {@link InstantRunoffSystem}
     *
     * @return The string form of this {@link InstantRunoffSystem}
     */
    @Override
    public String toString() {
        return String.format("InstantRunoffSystem{candidates=%s, numBallots=%d}", Arrays.toString(candidates), numBallots);
    }
    
    /**
     * Returns one of the candidate with the highest ballot counts and the candidate(s) with the lowest ballot counts
     *
     * @return one of the candidate with the highest ballot counts and the candidate(s) with the lowest ballot counts
     */
    protected Pair<Pair<Integer, List<Candidate>>, Pair<Integer, Candidate>> getLowestHighestCandidates() {
        /*
         * Note: The reason we only return one of the candidates with the highest ballot counts is that we only care about the case in which a
         * candidate has the majority of votes, and there can only be one candidate with the majority of votes at any time
         */
        
        //Initializes the the highest ballot count and the candidate associated with it
        int highestBallots = -1;
        Candidate highestCandidate = null;
        
        //Initializes the lowest ballot count and the candidate(s) associated with it
        int lowestBallots = Integer.MAX_VALUE;
        final List<Candidate> lowestCandidates = new ArrayList<>();
        
        for(final Candidate candidate : candidateBallotsMap.keySet()) {
            //Gets the number of ballots for each candidate
            final int candidateNumBallots = candidateBallotsMap.get(candidate).size();
            
            //Identifies new highest count and replaces highestCandidate
            if(candidateNumBallots > highestBallots) {
                highestBallots = candidateNumBallots;
                highestCandidate = candidate;
            }
            //Identifies new lowest count and replaces all lowest with new lowest candidate
            if(candidateNumBallots < lowestBallots) {
                lowestBallots = candidateNumBallots;
                lowestCandidates.clear();
                lowestCandidates.add(candidate);
            }
            //Multiple lowest candidates are collected as a group
            else if(candidateNumBallots == lowestBallots) {
                lowestCandidates.add(candidate);
            }
        }
        return new Pair<>(new Pair<>(lowestBallots, lowestCandidates), new Pair<>(highestBallots, highestCandidate));
    }
    
    /**
     * Eliminates a candidate and redistributes their ballots
     *
     * @param lowest The candidate who is eliminated and needs their ballots redistributed
     */
    protected void eliminateLowest(final Candidate lowest) {
        //Eliminates candidate from map
        final Deque<Ballot> ballotsToRedistribute = candidateBallotsMap.remove(lowest);
        
        //Candidate has 0 ballots to distribute
        if(ballotsToRedistribute.isEmpty()) {
            auditWriter.printf("%s has no ballots to have distributed.\n\n", lowest);
            return;
        }
        
        for(final Ballot ballot : ballotsToRedistribute) {
            //Gets next ranked candidate on the ballot
            Candidate nextCandidate = ballot.getNextCandidate();
            
            //While the current candidate for the ballot has been eliminated, get the next candidate
            while(nextCandidate != null && !candidateBallotsMap.containsKey(nextCandidate)) {
                auditWriter.printf(
                    "Ballot %d associated with %s has their next choice as candidate %s. but %s was already eliminated. Trying the next choice.\n\n",
                    ballot.getBallotNumber(), lowest, nextCandidate, nextCandidate
                );
                nextCandidate = ballot.getNextCandidate();
            }
            
            //If there are no more candidates ranked for the ballot
            if(nextCandidate == null) {
                auditWriter.printf(
                    "Ballot %d associated with %s did not have any other candidates ranked. As such, their ballot will not be distributed.\n\n",
                    ballot.ballotNumber, lowest
                );
            }
            //If there is a next ranked candidate that is not eliminated, transfer the ballot
            else {
                candidateBallotsMap.get(nextCandidate).add(ballot);
                auditWriter.printf("Ballot %d has their next choice as candidate %s. The ballot will be distributed to %s.\n\n", ballot.ballotNumber,
                    nextCandidate, nextCandidate
                );
            }
        }
    }
    
    /**
     * Returns a {@link String} of all the non-eliminated candidates and their number of ballots
     *
     * @return A {@link String} of all the non-eliminated candidates and their number of ballots
     */
    private String getCurrentChoiceBallots() {
        final StringBuilder candidateBallotsBuilder = new StringBuilder();
        for(final Candidate candidate : candidateBallotsMap.keySet()) {
            candidateBallotsBuilder.append(String.format("%s: %d ballots\n", candidate, candidateBallotsMap.get(candidate).size()));
        }
        return candidateBallotsBuilder.toString();
    }
    
    /**
     * Runs the IR election algorithm
     */
    @Override
    public void runElection() {
        //Write the first choice ballot counts for each candidate
        String strToWriteToAll = "First-choice ballots (excluding candidates with 0 ballots):";
        auditWriter.println(strToWriteToAll);
        reportWriter.println(strToWriteToAll);
        System.out.println(strToWriteToAll);
        
        strToWriteToAll = getCurrentChoiceBallots();
        auditWriter.println(strToWriteToAll);
        reportWriter.println(strToWriteToAll);
        System.out.println(strToWriteToAll);
        
        //If there is only 1 candidate, they are automatically declared the winner
        if(candidateBallotsMap.size() == 1) {
            final Candidate winner = candidateBallotsMap.keySet().iterator().next();
            final Deque<Ballot> winnerBallots = candidateBallotsMap.get(winner);
            strToWriteToAll = String.format(
                "%s has received %d/%d votes giving them a majority of %s%% of the ballots. They have therefore won.",
                winner,
                winnerBallots.size(),
                numBallots,
                String.format("%.2f", 100.0 * winnerBallots.size() / numBallots)
            );
            auditWriter.println(strToWriteToAll);
            reportWriter.println(strToWriteToAll);
            System.out.println(strToWriteToAll);
            auditWriter.close();
            reportWriter.close();
            return;
        }
        
        while(true) {
            final int candidateBallotsMapLen = candidateBallotsMap.size();
            
            //If there are 2 candidates remaining, the winner is decided by whose votes are greater
            if(candidateBallotsMapLen == 2) {
                //Stores the winner of the election
                final Candidate winner;
                
                //True only if both candidates have an equal number of ballots, making randomization required
                boolean randomSelectionRequired = false;
                
                //Store the last remaining candidates in an array
                final Candidate[] topTwo = candidateBallotsMap.keySet().toArray(new Candidate[0]);
                
                //Compare the candidates' ballot counts
                final int firstSecondCandidateComparison = Integer.compare(
                    candidateBallotsMap.get(topTwo[0]).size(),
                    candidateBallotsMap.get(topTwo[1]).size()
                );
                
                if(firstSecondCandidateComparison > 0) {
                    winner = topTwo[0];
                }
                else if(firstSecondCandidateComparison < 0) {
                    winner = topTwo[1];
                }
                
                //If the difference is 0, they share the same number of ballots, so randomization is needed to choose the candidate
                else {
                    randomSelectionRequired = true;
                    winner = topTwo[rand.nextInt(2)];
                }
                
                //If there is a tie
                if(randomSelectionRequired) {
                    auditWriter.printf("There exists a tie between %s and %s.\n", topTwo[0], topTwo[1]);
                    auditWriter.println(winner + " won the random tie break. They have therefore won the election.");
                }
                else {
                    final int winnerBallotCount = candidateBallotsMap.get(winner).size();
                    
                    if(winnerBallotCount > halfNumBallots) {
                        strToWriteToAll = String.format(
                            "%s has received %d/%d ballots, giving them the majority with %s%% of the ballots",
                            winner,
                            winnerBallotCount,
                            numBallots,
                            String.format("%.2f", 100.0 * winnerBallotCount / numBallots)
                        );
                    }
                    else {
                        strToWriteToAll = String.format(
                            "%s has received %d/%d ballots, giving them the greater popularity with %s%% of the ballots",
                            winner,
                            winnerBallotCount,
                            numBallots,
                            String.format("%.2f", 100.0 * winnerBallotCount / numBallots)
                        );
                    }
                    auditWriter.println(strToWriteToAll);
                    reportWriter.println(strToWriteToAll);
                    System.out.println(strToWriteToAll);
                }
                break;
            }
            //More than 2 candidates
            else {
                //Gets highest candidate and lowest candidate(s) by ballots
                final Pair<Pair<Integer, List<Candidate>>, Pair<Integer, Candidate>> lowestHighestCandidates = getLowestHighestCandidates();
                final Pair<Integer, Candidate> highestCandidateBallots = lowestHighestCandidates.getSecond();
                
                //If the highest candidate has the majority, the winner is declared
                if(highestCandidateBallots.getFirst() > halfNumBallots) {
                    strToWriteToAll = String.format(
                        "%s has received %d/%d ballots giving them a majority of %s%% of the ballots. They have therefore won.",
                        highestCandidateBallots.getSecond(),
                        highestCandidateBallots.getFirst(),
                        numBallots,
                        String.format("%.2f", 100.0 * highestCandidateBallots.getKey() / numBallots)
                    );
                    auditWriter.println(strToWriteToAll);
                    reportWriter.println(strToWriteToAll);
                    System.out.println(strToWriteToAll);
                    break;
                }
                //If no candidate has the majority, then eliminate a candidate
                else {
                    final Pair<Integer, List<Candidate>> lowestCandidateBallots = lowestHighestCandidates.getFirst();
                    final List<Candidate> lowestCandidates = lowestCandidateBallots.getSecond();
                    
                    //Randomly picks a candidate from the lowest candidates to eliminate
                    final Candidate lowest = lowestCandidates.get(rand.nextInt(lowestCandidates.size()));
                    
                    //If no tie breaking was required
                    if(lowestCandidates.size() == 1) {
                        auditWriter.println("No candidate has a majority. Eliminating the candidate with the lowest ballots: " + lowest + "\n");
                    }
                    //If there were multiple lowest candidates
                    else {
                        String lowestCandidatesStr = lowestCandidates.toString();
                        lowestCandidatesStr = lowestCandidatesStr.substring(1, lowestCandidatesStr.length() - 1);
                        
                        auditWriter.printf(
                            "No candidate has a majority. There is a tie for lowest ballots between the following: %s\n",
                            lowestCandidatesStr
                        );
                        
                        auditWriter.println("Random choice from the above for elimination: " + lowest);
                        auditWriter.println();
                    }
                    //Eliminated the lowest candidate chosen
                    eliminateLowest(lowest);
                    
                    //Prints table of ballot counts after elimination
                    strToWriteToAll = "Ballots after " + lowest + " was eliminated:";
                    auditWriter.println(strToWriteToAll);
                    reportWriter.println(strToWriteToAll);
                    System.out.println(strToWriteToAll);
                    
                    strToWriteToAll = getCurrentChoiceBallots();
                    auditWriter.println(strToWriteToAll);
                    reportWriter.println(strToWriteToAll);
                    System.out.println(strToWriteToAll);
                }
            }
        }
        
        auditWriter.close();
        reportWriter.close();
    }
}
