/*
 * File name:
 * OpenPartyListSystem.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Represents the open party list voting system
 */

package org.team19;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The {@link VotingSystem} representing the open party list voting system
 */
public class OpenPartyListSystem extends VotingSystem {
    
    /**
     * The number of candidates in this election
     */
    protected int numCandidates;
    
    /**
     * The number of ballots provided in this election
     */
    protected int numBallots;
    
    /**
     * The number of seats in this election
     */
    protected int numSeats;
    
    /**
     * The array of {@link Candidate}s for this election in the order presented in the election file
     */
    protected Candidate[] candidates;
    
    /**
     * A mapping of parties to a mapping of their candidates to their ballot counts
     */
    protected Map<String, Map<Candidate, Integer>> partyToCandidateCounts = new LinkedHashMap<>();
    
    /**
     * A mapping of parties to their respective {@link PartyInformation} instance
     */
    protected Map<String, PartyInformation> partyToPartyInformation = new HashMap<>();
    
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
     * The pattern associated with a valid candidate of the form "[[canididate1], [party1]], [[canididate2], [party2]], ...", replacing the
     * corresponding bracketed arguments (not including the outer brackets for candidate-party pairs) with the actual candidate's name and party
     * <p></p>
     * Regex breakdown:
     * <ol>
     *     <li>^: Match the start of the string</li>
     *     <li>\s*: Beginning with any amount of whitespace</li>
     *     <li>
     *         *: Any number of the noncapture group
     *         <li>[^\(\),]+: One or more nonparenthesis or comma characters associated with the candidate name</li>
     *         <li>,: Comma</li>
     *         <li>[^\(\),]+: One or more nonparenthesis or comma characters associated with the candidate party</li>
     *         <li>\s*,\s*: Comma surrounded by any amount of whitespace</li>
     *     </li>
     *     <li>[^\(\),]+: One or more nonparenthesis or comma characters associated with the candidate name</li>
     *     <li>,: Comma</li>
     *     <li>[^\(\),]+: One or more nonparenthesis or comma characters associated with the candidate party</li>
     *     <li>\s*: Ending with any amount of whitespace</li>
     *     <li>$: Match the end of the string</li>
     * </ol>
     */
    @SuppressWarnings("RegExpRedundantEscape")
    protected Pattern candidatesLinePattern = Pattern.compile("^\\s*(?:\\[[^\\[\\],]+,[^\\[\\]+,]+\\]\\s*,\\s*)*\\[[^\\[\\],]+,[^\\[\\]+,]+\\]\\s*$");
    
    /**
     * Represents party information for a party in an {@link OpenPartyListSystem} election
     */
    protected static class PartyInformation {
        
        /**
         * The number of candidates that this party has
         */
        protected int numCandidates = 0;
        
        /**
         * The number of seats that this party currently has obtained
         */
        protected int numSeats;
        
        /**
         * The number of ballots that this party received
         */
        protected int numBallots = 0;
        
        /**
         * The remaining votes that this party has after the initial allocation of seats
         */
        protected Fraction remainder;
        
        /**
         * The pairs of candidates for this party and their corresponding number of ballots they were given, sorted by number of ballots given
         */
        protected List<Map.Entry<Candidate, Integer>> orderedCandidateBallots = new ArrayList<>();
        
        /**
         * Initializes a {@link PartyInformation}
         */
        protected PartyInformation() {}
        
        /**
         * Returns the {@link String} representation of this {@link PartyInformation}
         *
         * @return The {@link String} representation of this {@link PartyInformation}
         */
        @Override
        public String toString() {
            return String.format(
                "PartyInformation{numCandidates=%d, numSeats=%d, numBallots=%d, remainder=%s, orderedCandidateBallots=%s}",
                numCandidates, numSeats, numBallots, remainder, orderedCandidateBallots
            );
        }
        
    }
    
    /**
     * Initializes a {@link OpenPartyListSystem}
     *
     * @param auditOutput  The {@link OutputStream} to write detailed information about the running of the election
     * @param reportOutput The {@link OutputStream} to write a summary about the running of the election
     * @throws NullPointerException Thrown if either auditOutput or reportOutput is null
     */
    public OpenPartyListSystem(final OutputStream auditOutput, final OutputStream reportOutput) throws NullPointerException {
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
        return 2;
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
                "The given candidates line \"%s\" does not match the format \"[[candidate1], [party1]],[[candidate2], [party2]], ...\"",
                candidatesLine
            ), line);
        }
        
        //Split the candidates line by comma delimiter and add each candidate to an array
        final String[] candidatesStr = candidatesLine.split("],");
        final Candidate[] candidatesArr = new Candidate[candidatesStr.length];
        for(int i = 0; i < candidatesArr.length; ++i) {
            //Substring starting on 1 before splitting to get rid of the left bracket
            final String[] candidate = candidatesStr[i].substring(1).split(",");
            candidatesArr[i] = new Candidate(candidate[0].strip(), candidate[1].strip());
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
        
        //Add PartyInformation instances for each unique party
        for(final Candidate candidate : candidates) {
            final String party = candidate.getParty();
            if(!partyToPartyInformation.containsKey(party)) {
                partyToPartyInformation.put(party, new PartyInformation());
                partyToCandidateCounts.put(party, new LinkedHashMap<>());
            }
            partyToCandidateCounts.get(party).put(candidate, 0);
        }
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
            //Parses the number of seats as the first line
            numSeats = Integer.parseInt(header[0].strip());
            if(numSeats < 0) {
                VotingStreamParser.throwParseException(String.format(
                    "The number of seats provided in the ballots header was %d but must be nonnegative", numBallots
                ), line);
            }
            
            //Output the number of seats to the audit, report, and summary
            final String numBallotsOutput = String.format("Number of Seats: %d\n", numSeats);
            auditWriter.println(numBallotsOutput);
            reportWriter.println(numBallotsOutput);
            System.out.println(numBallotsOutput);
        }
        catch(NumberFormatException e) {
            VotingStreamParser.throwParseException(String.format(
                "The number of seats provided in the ballots header \"%s\" was not a valid integer", header[0].strip()
            ), line);
        }
        try {
            //Parses the number of ballots as the second line
            numBallots = Integer.parseInt(header[1].strip());
            if(numBallots < 0) {
                VotingStreamParser.throwParseException(String.format(
                    "The number of ballots provided in the ballots header was %d but must be nonnegative", numBallots
                ), line + 1);
            }
            
            //Output the number of ballots to the audit, report, and summary
            final String numBallotsOutput = String.format("Number of Ballots: %d\n", numBallots);
            auditWriter.println(numBallotsOutput);
            reportWriter.println(numBallotsOutput);
            System.out.println(numBallotsOutput);
        }
        catch(NumberFormatException e) {
            VotingStreamParser.throwParseException(String.format(
                "The number of ballots provided in the ballots header \"%s\" was not a valid integer", header[1].strip()
            ), line + 1);
        }
    }
    
    /**
     * Parses the ballot line from the election file and returns the resultant {@link Candidate}
     *
     * @param ballotLine The {@link String} corresponding to a ballot
     * @param line       The line number associated with the current ballot line being read
     * @return The {@link Candidate} from parsing the ballot line
     * @throws ParseException Thrown if the format or contents of the ballot line are invalid
     */
    private Candidate parseBallot(final String ballotLine, final int line) throws ParseException {
        final String[] ballotStr = ballotLine.split(",");
        if(ballotStr.length != numCandidates) {
            VotingStreamParser.throwParseException(String.format(
                "The number of values %d for this ballot is not equivalent to the number of candidates %d", ballotStr.length, numCandidates
            ), line);
        }
        
        Integer oneLocation = null;
        for(int i = 1; i <= numCandidates; ++i) {
            final String value = ballotStr[i].strip();
            if(value.equals("1") && oneLocation == null) {
                oneLocation = i;
            }
            else if(value.equals("1")) {
                VotingStreamParser.throwParseException("There can only be one choice for the OPL ballots", line);
            }
            else if(!value.isEmpty()) {
                VotingStreamParser.throwParseException(String.format(
                    "Ballot values for OPL ballots can either be empty or 1, but \"%s\" was found", value
                ), line);
            }
        }
        
        if(oneLocation == null) {
            VotingStreamParser.throwParseException("There must be a choice selected for the OPL ballots", line);
        }
        
        return candidates[oneLocation - 1];
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
        //Get the candidate and party associated with the ballot
        final Candidate candidate = parseBallot(ballotLine, line);
        final String party = candidate.getParty();
        
        //Increment the ballot count for the party-candidate pair
        partyToCandidateCounts.get(party).merge(candidate, 1, Integer::sum);
        
        //Increment the number of votes for the party in party information
        partyToPartyInformation.get(party).numBallots++;
        
        //Writes the output for this ballot to the audit output
        auditWriter.printf("Ballot %d chose %s\n", ballotNumber, candidate);
    }
    
    /**
     * Returns the name of this voting system
     *
     * @return The name of this voting system
     */
    @Override
    public String getName() {
        return "Open Party List Voting";
    }
    
    /**
     * Returns the short name for the voting system; that is, the name that appears at the top of an election file
     *
     * @return The short name for the voting system
     */
    @Override
    public String getShortName() {
        return "OPL";
    }
    
    /**
     * Precondition: {@link #importCandidatesHeader(String[], int)} has been executed successfully
     * <p></p>
     * Returns the number of candidates that the {@link OpenPartyListSystem} contains
     *
     * @return The number of candidates that the {@link OpenPartyListSystem} contains
     */
    @Override
    public int getNumCandidates() {
        return numCandidates;
    }
    
    /**
     * Precondition: {@link #addCandidates(String, int)} has been executed successfully
     * <p></p>
     * Returns the {@link Collection} of {@link Candidate}s for this {@link OpenPartyListSystem}
     *
     * @return The {@link Collection} of {@link Candidate}s for this {@link OpenPartyListSystem}
     */
    @Override
    public Collection<Candidate> getCandidates() {
        return List.of(candidates);
    }
    
    /**
     * Precondition: {@link #importBallotsHeader(String[], int)} has been executed successfully
     * <p></p>
     * Returns the number of ballots that the {@link OpenPartyListSystem} contains, which should be available after
     *
     * @return The number of ballots that the {@link OpenPartyListSystem} contains
     */
    @Override
    public int getNumBallots() {
        return numBallots;
    }
    
    @Override
    public void runElection() {}
    
    /**
     * Returns the string form of this {@link OpenPartyListSystem}
     *
     * @return The string form of this {@link OpenPartyListSystem}
     */
    @Override
    public String toString() {
        return null;
    }
}
