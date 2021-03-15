/*
 * File name:
 * OpenPartyListSystem.java
 *
 * Author:
 * Nikunj Chawla and Aaron Kandikatla
 *
 * Purpose:
 * Represents the open party list voting system
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Objects;

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
     * The pattern associated with a valid candidate of the form "[[candidate1], [party1]], [[candidate2], [party2]], ...", replacing the
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
    
        //Split the candidates line by bracket and comma delimiter (with potential whitespace in between) and add each candidate to an array
        final String[] candidatesStr = candidatesLine.split("]\\s*,", -1);
        final Candidate[] candidatesArr = new Candidate[candidatesStr.length];
        for(int i = 0; i < candidatesArr.length; ++i) {
            //Substring starting on 1 before splitting to get rid of the left bracket
            final String[] candidate = candidatesStr[i].strip().substring(1).split(",");
        
            //Removes the right bracket for the last candidate's party
            if(i == candidatesArr.length - 1) {
                final String party = candidate[1];
                candidate[1] = party.substring(0, party.length() - 1);
            }
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
        //Split using a comma as a delimiter
        final String[] ballotStr = ballotLine.split(",", -1);
    
        //If the number of values for the current ballot is not equivalent to the number of candidates, then throw an exception
        if(ballotStr.length != numCandidates) {
            VotingStreamParser.throwParseException(String.format(
                "The number of values %d for this ballot is not equivalent to the number of candidates %d", ballotStr.length, numCandidates
            ), line);
        }
    
        //The location of the last one found in the ballot
        Integer oneLocation = null;
    
        for(int i = 1; i <= numCandidates; ++i) {
            final String value = ballotStr[i - 1].strip();
        
            //If a value is equal to 1 and oneLocation is not already set, then set it
            if(value.equals("1") && oneLocation == null) {
                oneLocation = i;
            }
            //Otherwise, if a value is equal to 1 and oneLocation is set, then throw an exception
            else if(value.equals("1")) {
                VotingStreamParser.throwParseException("There can only be one choice for the OPL ballots", line);
            }
            //Otherwise, if a value is equal to something other than 1 or an empty string, throw an exception
            else if(!value.isEmpty()) {
                VotingStreamParser.throwParseException(String.format(
                    "Ballot values for OPL ballots can either be empty or 1, but \"%s\" was found", value
                ), line);
            }
        }
    
        //If there are no 1s for the ballot, then throw an exception
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
     * Returns the number of ballots that the {@link OpenPartyListSystem} contains
     *
     * @return The number of ballots that the {@link OpenPartyListSystem} contains
     */
    @Override
    public int getNumBallots() {
        return numBallots;
    }
    
    /**
     * Precondition: {@link #importBallotsHeader(String[], int)} has been executed successfully
     * <p></p>
     * Returns the number of seats that the {@link OpenPartyListSystem} contains
     *
     * @return The number of seats that the {@link OpenPartyListSystem} contains
     */
    public int getNumSeats() {
        return numSeats;
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
        return String.format("OpenPartyListSystem{candidates=%s, numBallots=%d}", Arrays.toString(candidates), numBallots);
    }
    
    /**
     * Prints the calculation of the initial allocation of seats using the quota
     *
     * @param quota             The total number of votes/candidates to calculate the initial seat allocations per party
     * @param numSeatsRemaining The number of seats remaining after the initial allocation
     */
    protected void printAllocation(Fraction quota, int numSeatsRemaining) {
        for(Map.Entry<String, PartyInformation> party : partiesToPartyInformation.entrySet()) {
            
            String stringQuota = quota.denominator == 1 ? Long.toString(quota.numerator) : String.format("(%s)", quota.toString());
            
            auditWriter.println(
                party.getKey() + " initial allocation of seats: floor(" + party.getValue().numBallots + " / " + stringQuota + ") = " + party
                    .getValue().numSeats);
            if(party.getValue().numSeats > 0) {
                if(party.getValue().remainder.denominator == 1) {
                    auditWriter.println("Remaining ballots: " + party.getValue().remainder.numerator);
                }
                else {
                    auditWriter.println("Remaining ballots: " + String.format("%.4f", party.getValue().remainder.getDoubleValue()));
                }
            }
            else {
                auditWriter.println("Remaining ballots: " + party.getValue().numBallots);
            }
            
            auditWriter.println();
        }
        int numberOfSeatsAllocated = numSeats - numSeatsRemaining;
        auditWriter.println(numberOfSeatsAllocated + " / " + numSeats + " have been allocates. " + numSeatsRemaining + " seats remaining");
        auditWriter.println();
    }
    
    /**
     * Prints a table with each party's initial allocation of seats and remaining ballots to auditOutput
     */
    protected void printInitialAllocations() {
        auditWriter.println("Initial Seat Allocation Results:");
        ArrayList<String> parties = new ArrayList<>();
        ArrayList<Integer> seats = new ArrayList<>();
        ArrayList<String> remainingBallots = new ArrayList<>();
        
        for(Map.Entry<String, PartyInformation> party : partiesToPartyInformation.entrySet()) {
            parties.add(party.getKey());
            seats.add(party.getValue().numSeats);
            if(party.getValue().numSeats > 0) {
                if(party.getValue().remainder.denominator == 1) {
                    remainingBallots.add(party.getValue().remainder.numerator + "");
                }
                else {
                    remainingBallots.add(String.format("%.4f", party.getValue().remainder.getDoubleValue()));
                }
            }
            else {
                remainingBallots.add(party.getValue().numBallots + "");
            }
        }
        String table = tableFormatter.formatAsTable(
            Arrays.asList("Party", "Initial Seats", "Remaining Ballots"),
            Arrays.asList(parties, seats, remainingBallots),
            Arrays.asList(TableFormatter.Alignment.LEFT, TableFormatter.Alignment.RIGHT, TableFormatter.Alignment.RIGHT));
        auditWriter.println(table + "\n");
    }
    
    /**
     * Allocates initial seats based off the quota
     *
     * @param quota The total number of votes/candidates to calculate the initial seat allocations per party
     * @return A {@link Pair} of number of seats remaining after initial allocation and {@link Set} of {@link String} representing parties
     * able to receive additional seats
     */
    protected Pair<Integer, Set<String>> allocateInitialSeats(Fraction quota) {
        int numSeatsRemaining = numSeats;
        Set<String> remainingParties = new HashSet<>();
        for(Map.Entry<String, PartyInformation> party : partiesToPartyInformation.entrySet()) {
            PartyInformation partyInformation = partiesToPartyInformation.get(party.getKey());
            Fraction ballotQuotaMultiples = new Fraction(partyInformation.numBallots, 1).divide(quota);
            
            partyInformation.numSeats = Math.min((int) ballotQuotaMultiples.getWholePart(), partyInformation.numCandidates);
            if(partyInformation.numSeats != partyInformation.numCandidates) { remainingParties.add(party.getKey()); }
            partyInformation.remainder =
                new Fraction(partyInformation.numBallots, 1).subtract(new Fraction(partyInformation.numSeats, 1).multiply(quota));
            numSeatsRemaining -= partyInformation.numSeats;
            
        }
        printAllocation(quota, numSeatsRemaining);
        printInitialAllocations();
        
        return new Pair<>(numSeatsRemaining, remainingParties);
    }
    
    /**
     * Writes message to audit and report {@link OutputStream}s when there
     *
     * @param numSeatsRemaining number of seats remaining after no more candidates to which seats can be distributed
     */
    protected void printNotEnoughCandidates(int numSeatsRemaining) {
        String message = "There are " + numSeatsRemaining + " seats remaining, but there are no more candidates to which seats can be distributed\n";
        auditWriter.println(message);
        reportWriter.println(message);
        System.out.println(message);
    }
    
    /**
     * Prints information regrading the seat allocated to the party with the next largest remaining ballots
     *
     * @param numSeatsRemaining number of seats remaining after remaining allocation
     * @param chosenParty       The party chosen to be allocated a remaining seat
     * @param partyInformation  The {@link PartyInformation} associated the chosen party
     */
    protected void printNextChosen(int numSeatsRemaining, String chosenParty, PartyInformation partyInformation) {
        int seatNumber = (numSeats - numSeatsRemaining) + 1;
        
        String remainingBallots = "";
        if(partiesToPartyInformation.get(chosenParty).numSeats > 0) {
            if(partiesToPartyInformation.get(chosenParty).remainder.denominator == 1) {
                remainingBallots = partiesToPartyInformation.get(chosenParty).remainder.numerator + "";
            }
            else {
                remainingBallots = String.format("%.4f", partiesToPartyInformation.get(chosenParty).remainder.getDoubleValue());
            }
        }
        else {
            remainingBallots = partiesToPartyInformation.get(chosenParty).numBallots + "";
        }
        
        auditWriter.println("Allocating seat " + seatNumber + ":");
        auditWriter
            .println("The party with the next largest remaining ballots is " + chosenParty + " with " + remainingBallots +
                " remaining ballots");
        auditWriter.println();
    }
    
    /**
     * Allocates remaining seats to parties with the highest remaining votes
     *
     * @param numSeatsRemaining Number of seats remaining after initial allocation
     * @param remainingParties  Parties that still have enough candidates for additional seats
     */
    protected void allocateRemainingSeats(int numSeatsRemaining, Set<String> remainingParties) {
        List<Pair<String, Fraction>> partyRemainingSeats = new ArrayList<>();
        for(String party : remainingParties) { partyRemainingSeats.add(new Pair<>(party, partiesToPartyInformation.get(party).remainder)); }
        partyRemainingSeats.sort((el1, el2) -> el2.getSecond().compareTo(el1.getSecond()));
        List<Pair<String, Fraction>> highestRemainingParties = new ArrayList<>();
        int curIdx = 0;
        while(numSeatsRemaining > 0) {
            if(curIdx >= partyRemainingSeats.size() && highestRemainingParties.isEmpty()) {
                List<Pair<String, Fraction>> partyRemainingSeatsTmp = new ArrayList<>();
                for(Pair<String, Fraction> partyRemainderPair : partyRemainingSeats) {
                    if(remainingParties.contains(partyRemainderPair.getFirst())) { partyRemainingSeatsTmp.add(partyRemainderPair); }
                }
                partyRemainingSeats = partyRemainingSeatsTmp;
                curIdx = 0;
                if(partyRemainingSeatsTmp.size() == 0) {
                    printNotEnoughCandidates(numSeatsRemaining);
                    break;
                }
            }
            if(highestRemainingParties.isEmpty()) {
                Pair<Integer, List<Pair<String, Fraction>>> idxNextGroupPair =
                    getNextEquivalentOrderedGroup(partyRemainingSeats, curIdx, Comparator.comparing(Pair::getSecond));
                curIdx = idxNextGroupPair.getFirst();
                highestRemainingParties = idxNextGroupPair.getSecond();
                Collections.shuffle(highestRemainingParties);
            }
            String chosenParty = highestRemainingParties.get(highestRemainingParties.size() - 1).getFirst();
            highestRemainingParties.remove(highestRemainingParties.size() - 1);
            PartyInformation partyInformation = partiesToPartyInformation.get(chosenParty);
            
            printNextChosen(numSeatsRemaining, chosenParty, partyInformation);
            
            partyInformation.numSeats++;
            if(partyInformation.numSeats == partyInformation.numCandidates) { remainingParties.remove(chosenParty); }
            numSeatsRemaining--;
            
            int numberOfSeatsAllocated = numSeats - numSeatsRemaining;
            auditWriter.println(numberOfSeatsAllocated + " / " + numSeats + " have been allocated. " + numSeatsRemaining + " seats remaining");
            auditWriter.println();
            
        }
    }
    
    /**
     * Distributes each party’s seats to their candidates by popularity
     *
     * @return an {@link ArrayList} containing the candidates who received seats
     */
    protected ArrayList<Candidate> distributeSeatsToCandidates() {
        ArrayList<Candidate> finalSeats = new ArrayList<>();
        for(Map.Entry<String, PartyInformation> party : partiesToPartyInformation.entrySet()) {
            PartyInformation partyInformation = partiesToPartyInformation.get(party.getKey());
            int numSeatsRemaining = partyInformation.numSeats;
    
            if(numSeatsRemaining > 0) { auditWriter.println("Distributing " + numSeatsRemaining + " seats for the party " + party.getKey() + ":\n"); }
            
            List<Map.Entry<Candidate, Integer>> orderedCandidateBallots = partyInformation.orderedCandidateBallots;
            int curIdx = 0;
            List<Map.Entry<Candidate, Integer>> highestRemainingCandidates = new ArrayList<>();
            while(numSeatsRemaining > 0) {
                if(highestRemainingCandidates.isEmpty()) {
                    Pair<Integer, List<Map.Entry<Candidate, Integer>>> idxNextGroupPair =
                        getNextEquivalentOrderedGroup(orderedCandidateBallots, curIdx, Map.Entry.comparingByValue());
                    curIdx = idxNextGroupPair.getFirst();
                    highestRemainingCandidates = idxNextGroupPair.getSecond();
                    Collections.shuffle(highestRemainingCandidates);
                }
                Map.Entry<Candidate, Integer> next = highestRemainingCandidates.get(highestRemainingCandidates.size() - 1);
                highestRemainingCandidates.remove(highestRemainingCandidates.size() - 1);
                Candidate selected = next.getKey();
                finalSeats.add(selected);
                numSeatsRemaining--;
                
                auditWriter
                    .println("From party " + party.getKey() + "'s " + "remaining candidates, " + selected.getName() + " had the greatest number of "
                        + "votes: " + next.getValue() + ". As such, " + selected.getName() + " will be allocated a seat.");
                auditWriter.println(numSeatsRemaining + " seats remaining");
                auditWriter.println();
            }
        }
        auditWriter.println("Final Seats:");
        reportWriter.println("Final Seats:");
        System.out.println("Final Seats");
        
        for(Candidate candidate : finalSeats) {
            auditWriter.println(candidate.getName() + " (" + candidate.getParty() + ")");
            reportWriter.println(candidate.getName() + " (" + candidate.getParty() + ")");
            System.out.println(candidate.getName() + " (" + candidate.getParty() + ")");
            
        }
        return finalSeats;
    }
    
    /**
     * Given a sorted {@link List}, an index from which to begin, and a comparator for comparing elements in the {@link List},
     * returns the next group of equivalent elements as determined by the comparator in addition to the index after the last added element
     *
     * @param list       The sorted {@link List} from which to retrieve the next equivalent ordered group
     * @param idx        The index from which to begin retrieving the group
     * @param comparator The comparator used to compare elements when determining if elements are equivalent and should be added to the group
     * @return An {@link List} of equivalent elements in the provided {@link List} as determined by the provided comparator, starting at
     * the provided index
     */
    protected static <T> Pair<Integer, List<T>> getNextEquivalentOrderedGroup(final List<T> list, int idx,
        final Comparator<T> comparator) {
        final ArrayList<T> orderedGroup = new ArrayList<>();
        final int len = list.size();
        if(len - idx <= 0) {
            return new Pair<>(idx, orderedGroup);
        }
        
        final T firstValue = list.get(idx);
        orderedGroup.add(firstValue);
        ++idx;
        
        for(; idx < len; idx++) {
            final T curValue = list.get(idx);
            if(comparator.compare(firstValue, curValue) != 0) {
                break;
            }
            orderedGroup.add(curValue);
        }
        
        return new Pair<>(idx, orderedGroup);
    }
    
    /**
     * Prints the calculation of the quota to audit and report {@link OutputStream}s
     *
     * @param quota The total number of votes/candidates to calculate the initial seat allocations per party
     */
    protected void printQuotaInformation(Fraction quota) {
        String ballotsPerSeat = quota.getWholePart() + " ballots per seat";
        auditWriter.println("Quota: floor(" + quota.toString() + ") = " + ballotsPerSeat + "\n");
        reportWriter.println("Quota: " + ballotsPerSeat + "\n");
        System.out.println("Quota: " + ballotsPerSeat + "\n");
    }
    
    /**
     * Prints the grouping of candidates to each party to audit file
     */
    protected void printPartyGrouping() {
        auditWriter.println("Grouping by Party:");
        for(Map.Entry<String, Map<Candidate, Integer>> party : partyToCandidateCounts.entrySet()) {
            auditWriter.println("Party: " + party.getKey());
            for(Map.Entry<Candidate, Integer> candidate : party.getValue().entrySet()) { auditWriter.println("\t" + candidate.getKey().name); }
        }
        auditWriter.println();
    }
    
    /**
     * Print a table containing all parties and the total number of ballots each received to audit file
     */
    protected void printPartyBallots() {
        auditWriter.println("The ballot counts for each party are as follows:");
        ArrayList<String> parties = new ArrayList<>();
        ArrayList<Integer> ballots = new ArrayList<>();
        for(Map.Entry<String, PartyInformation> party : partiesToPartyInformation.entrySet()) {
            parties.add(party.getKey());
            ballots.add(party.getValue().numBallots);
        }
        String table = tableFormatter.formatAsTable(
            Arrays.asList("Party", "Ballots"),
            Arrays.asList(parties, ballots),
            Arrays.asList(TableFormatter.Alignment.LEFT, TableFormatter.Alignment.RIGHT));
        auditWriter.println(table + "\n");
    }
    
    /**
     * Prints a table contains all parties and the number of final seats allocated to each to audit file
     */
    protected void printFinalSeatAllocations() {
        auditWriter.println("Final Seat Allocations:");
        
        ArrayList<String> parties = new ArrayList<>();
        ArrayList<Integer> seats = new ArrayList<>();
        for(Map.Entry<String, PartyInformation> party : partiesToPartyInformation.entrySet()) {
            parties.add(party.getKey());
            seats.add(party.getValue().numSeats);
        }
        String table = tableFormatter.formatAsTable(
            Arrays.asList("Party", "Final Seats"),
            Arrays.asList(parties, seats),
            Arrays.asList(TableFormatter.Alignment.LEFT, TableFormatter.Alignment.RIGHT));
        auditWriter.println(table + "\n");
    }
    
    /**
     * Prints the a summary table with each party's name, total ballots, seats from first allocation, remaining ballots after initial allocation,
     * seats received from second allocation, total seats allocation, and percent of ballots to percent of seats to audit file, report file, and to
     * screen.
     *
     * @param initialSeats a Map of parties to the number of seats each received during the initial allocation
     */
    protected void printSummaryTable(Map<String, Integer> initialSeats) {
        ArrayList<String> parties = new ArrayList<>();
        ArrayList<Integer> ballots = new ArrayList<>();
        ArrayList<Integer> initialAllocation = new ArrayList<>();
        ArrayList<String> remainingBallots = new ArrayList<>();
        ArrayList<Integer> secondAllocation = new ArrayList<>();
        ArrayList<Integer> finalSeats = new ArrayList<>();
        ArrayList<String> percentOfBallotsToPercentOfSeats = new ArrayList<>();
        
        for(Map.Entry<String, PartyInformation> party : partiesToPartyInformation.entrySet()) {
            parties.add(party.getKey());
            ballots.add(partiesToPartyInformation.get(party.getKey()).numBallots);
            initialAllocation.add(initialSeats.get(party.getKey()));
            if(party.getValue().numSeats > 0) {
                if(party.getValue().remainder.denominator == 1) {
                    remainingBallots.add(party.getValue().remainder.numerator + "");
                }
                else {
                    remainingBallots.add(String.format("%.4f", party.getValue().remainder.getDoubleValue()));
                }
            }
            else {
                remainingBallots.add(party.getValue().numBallots + "");
            }
            secondAllocation.add(initialSeats.get(party.getKey()) - partiesToPartyInformation.get(party.getKey()).numSeats);
            finalSeats.add(partiesToPartyInformation.get(party.getKey()).numSeats);
            
            int percentOfBallots = Math.round(partiesToPartyInformation.get(party.getKey()).numBallots / (float) numBallots * 100);
            int percentOfSeats = Math.round(partiesToPartyInformation.get(party.getKey()).numSeats / (float) numSeats * 100);
            
            percentOfBallotsToPercentOfSeats.add(percentOfBallots + "%/" + percentOfSeats + "%");
        }
        
        String table = tableFormatter.formatAsTable(
            Arrays.asList("Parties", "Ballots", "First Allocation", "Remaining Ballots", "Second Allocation", "Final Seats", "% of Ballots to % of "
                + "Seats"),
            Arrays.asList(parties, ballots, initialAllocation, remainingBallots, secondAllocation, finalSeats, percentOfBallotsToPercentOfSeats),
            Arrays.asList(TableFormatter.Alignment.LEFT, TableFormatter.Alignment.RIGHT, TableFormatter.Alignment.RIGHT,
                TableFormatter.Alignment.RIGHT, TableFormatter.Alignment.RIGHT, TableFormatter.Alignment.RIGHT, TableFormatter.Alignment.RIGHT));
        
        auditWriter.println(table + "\n");
        reportWriter.println(table + "\n");
        System.out.println(table + "\n");
        
    }
    
    /**
     * Runs the election for the {@link VotingSystem} and determines the winner
     */
    @Override
    public void runElection() {
        for(Map.Entry<String, Map<Candidate, Integer>> party : partyToCandidateCounts.entrySet()) {
            PartyInformation partyInformation = partiesToPartyInformation.get(party.getKey());
            ArrayList<Map.Entry<Candidate, Integer>> orderedCandidateBallots = new ArrayList<>(partyToCandidateCounts.get(party.getKey()).entrySet());
            orderedCandidateBallots.sort(Map.Entry.comparingByValue());
            partyInformation.numCandidates = orderedCandidateBallots.size();
            partyInformation.orderedCandidateBallots = orderedCandidateBallots;
        }
        Fraction quota = new Fraction(numBallots, numSeats);
        
        printQuotaInformation(quota);
        printPartyGrouping();
        printPartyBallots();
        
        Pair<Integer, Set<String>> initialAllocationResults = allocateInitialSeats(quota);
        
        Map<String, Integer> partiesToInitialSeats = new HashMap<>();
        
        for(Map.Entry<String, PartyInformation> party : partiesToPartyInformation.entrySet()) {
            partiesToInitialSeats.put(party.getKey(), party.getValue().numSeats);
        }
        
        ArrayDeque<Pair<String, Integer>> initialAllocations = new ArrayDeque<>();
        for(Map.Entry<String, Map<Candidate, Integer>> party : partyToCandidateCounts.entrySet()) {
            initialAllocations.add(new Pair(party, partiesToPartyInformation.get(party.getKey()).numSeats));
        }
        
        Integer numSeatsRemaining = initialAllocationResults.getFirst();
        Set<String> remainingParties = initialAllocationResults.getSecond();
        
        if(numSeatsRemaining != 0) { allocateRemainingSeats(numSeatsRemaining, remainingParties); }
        
        printFinalSeatAllocations();
        printSummaryTable(partiesToInitialSeats);
        
        distributeSeatsToCandidates();
    }
}
