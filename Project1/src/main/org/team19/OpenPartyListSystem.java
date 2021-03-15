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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private void printInitialAllocation(final Fraction quota, final int numSeatsRemaining) {
        //Prints the initial allocation of seats for each party
        for(final Map.Entry<String, PartyInformation> party : partyToPartyInformation.entrySet()) {
            //Gets the string format of the quota (parenthesized if it is a fraction)
            final String stringQuota = quota.denominator == 1 ? Long.toString(quota.numerator) : String.format("(%s)", quota.toString());

            //Prints initial allocation of seats for a party
            auditWriter.printf(
                "%s initial allocation of seats: floor(%d / %s) = %d\n",
                party.getKey(),
                party.getValue().numBallots,
                stringQuota,
                party.getValue().numSeats
            );

            auditWriter.println("Remaining ballots: " + getRemainingBallots(party.getValue()));
            auditWriter.println();
        }
        final int numSeatsAllocated = numSeats - numSeatsRemaining;
        auditWriter.printf("%d / %d have been allocates. %d seats remaining\n\n", numSeatsAllocated, numSeats, numSeatsRemaining);
    }

    /**
     * Returns a string representing the number of ballots a party has
     *
     * @param partyInformation a {@link PartyInformation} object containing data related to a party
     * @return a string representing the number of ballots a party has
     */
    private String getRemainingBallots(final PartyInformation partyInformation) {
        final String remainingBallots;

        //If a candidate has been allocated seats
        if(partyInformation.numSeats > 0) {
            //If the remaining value is a whole number
            if(partyInformation.remainder.denominator == 1) {
                remainingBallots = String.valueOf(partyInformation.remainder.numerator);
            }
            else {
                remainingBallots = String.format("%.4f", partyInformation.remainder.getDoubleValue());
            }
        }

        //If a candidate has not been allocated any seats, then remaining ballots is the same as the total number of ballots for the party
        else {
            remainingBallots = String.valueOf(partyInformation.numBallots);
        }
        return remainingBallots;
    }

    /**
     * Prints a table with each party's initial allocation of seats and remaining ballots to the audit output
     */
    private void printInitialAllocations() {
        auditWriter.println("Initial Seat Allocation Results:");

        //The list of the number of seats received by each party
        final List<Integer> seats = new ArrayList<>();

        //The list of remaining ballots for each party after initial allocation
        final List<String> remainingBallots = new ArrayList<>();

        for(final PartyInformation partyInformation : partyToPartyInformation.values()) {
            seats.add(partyInformation.numSeats);
            remainingBallots.add(getRemainingBallots(partyInformation));
        }
        //Prints as table with Parties, Seats, and Remaining Ballots
        final String table = tableFormatter.formatAsTable(
            Arrays.asList("Party", "Initial Seats", "Remaining Ballots"),
            Arrays.asList(
                partyToPartyInformation.keySet(),
                seats,
                remainingBallots
            ),
            Arrays.asList(TableFormatter.Alignment.LEFT, TableFormatter.Alignment.RIGHT, TableFormatter.Alignment.RIGHT));
        auditWriter.println(table + "\n");
    }

    /**
     * Allocates the initial seats using the quota
     *
     * @param quota The proportion of number of ballots to number of seats
     * @return A {@link Pair} that contains the number of seats remaining after initial allocation and a {@link Set} of {@link String} representing
     * the parties that still have candidates without seats
     */
    protected Pair<Integer, Set<String>> allocateInitialSeats(final Fraction quota) {
        int numSeatsRemaining = numSeats;
        final Set<String> remainingParties = new HashSet<>();

        //Allocate initial votes for each party
        for(final String party : partyToPartyInformation.keySet()) {
            final PartyInformation partyInformation = partyToPartyInformation.get(party);

            //Divides the party's ballot count by the quota
            final Fraction ballotQuotaMultiples = new Fraction(partyInformation.numBallots, 1).divide(quota);

            //Sets a party's numSeats to the ballot quota multiple with a maximum possible seats of the number of candidates for the party
            partyInformation.numSeats = Math.min((int) ballotQuotaMultiples.getWholePart(), partyInformation.numCandidates);

            //If the party has seats left, then it can be added to the remaining parties who can get more seats
            if(partyInformation.numSeats != partyInformation.numCandidates) {
                remainingParties.add(party);
            }

            //Calculates remaining ballots after initial allocation
            partyInformation.remainder = new Fraction(partyInformation.numBallots, 1)
                .subtract(new Fraction(partyInformation.numSeats, 1).multiply(quota));

            //Decrements number of seats remaining by number of seats obtained by each party
            numSeatsRemaining -= partyInformation.numSeats;
        }

        printInitialAllocation(quota, numSeatsRemaining);
        printInitialAllocations();

        //Returns number of seats remaining and remaining parties with candidate with no seats
        return new Pair<>(numSeatsRemaining, remainingParties);
    }

    /**
     * Writes message to the audit and report outputs when there are not enough candidates to distribute all the seats to
     *
     * @param numSeatsRemaining number of seats remaining after no more candidates to which seats can be distributed
     */
    private void printNotEnoughCandidates(final int numSeatsRemaining) {
        final String message = String.format(
            "There are %d seats remaining, but there are no more candidates to which seats can be distributed\n",
            numSeatsRemaining
        );
        auditWriter.println(message);
        reportWriter.println(message);
        System.out.println(message);
    }

    /**
     * Prints information regarding the seat allocated to the party with the next largest remaining ballots
     *
     * @param numSeatsRemaining number of seats remaining after remaining allocation
     * @param chosenParty       The party chosen to be allocated a remaining seat
     */
    private void printNextChosen(final int numSeatsRemaining, final String chosenParty) {
        //The seat number that is currently being allocated
        final int seatNumber = numSeats - numSeatsRemaining + 1;

        final String remainingBallots;
        // TODO: Replace this redundant code with the correspondding method call
        if(partyToPartyInformation.get(chosenParty).numSeats > 0) {
            if(partyToPartyInformation.get(chosenParty).remainder.denominator == 1) {
                remainingBallots = partyToPartyInformation.get(chosenParty).remainder.numerator + "";
            }
            else {
                remainingBallots = String.format("%.4f", partiesToPartyInformation.get(chosenParty).remainder.getDoubleValue());
            }
        }
        else {
            remainingBallots = partiesToPartyInformation.get(chosenParty).numBallots + "";
        }

        auditWriter.println("Allocating seat " + seatNumber + ":");
        auditWriter.printf("The party with the next largest remaining ballots is %s with %s remaining ballots\n", chosenParty, remainingBallots);
        auditWriter.println();
    }

    /**
     * Given a sorted {@link List}, an index from which to begin, and a comparator for comparing elements in the {@link List},
     * returns the next group of equivalent elements as determined by the comparator in addition to the index after the last added element
     *
     * @param <T>        Type of elements that the {@link List} contains
     * @param list       The sorted {@link List} from which to retrieve the next equivalent ordered group
     * @param idx        The index from which to begin retrieving the group
     * @param comparator The comparator used to compare elements when determining if elements are equivalent and should be added to the group
     * @return An {@link List} of equivalent elements in the provided {@link List} as determined by the provided comparator, starting at
     * the provided index
     */
    protected static <T> Pair<Integer, List<T>> getNextEquivalentOrderedGroup(final List<T> list, int idx,
        final Comparator<T> comparator) {
        final List<T> orderedGroup = new ArrayList<>();
        final int len = list.size();

        //If the index is beyond the last index of the list, then return an empty group
        if(len - idx <= 0) {
            return new Pair<>(idx, orderedGroup);
        }

        //Add at least the first value at the current index to the ordered group, and store it for comparison
        final T firstValue = list.get(idx);
        orderedGroup.add(firstValue);
        ++idx;

        //For each remaining item in the list
        for(; idx < len; idx++) {
            final T curValue = list.get(idx);

            //If the current value is not equivalent to firstValue, then we are done
            if(comparator.compare(firstValue, curValue) != 0) {
                break;
            }

            orderedGroup.add(curValue);
        }
        return new Pair<>(idx, orderedGroup);
    }

    /**
     * Allocates remaining seats to parties with the highest remaining votes
     *
     * @param numSeatsRemaining Number of seats remaining after initial allocation
     * @param remainingParties  Parties that still have enough candidates for additional seats
     */
    protected void allocateRemainingSeats(int numSeatsRemaining, final Set<String> remainingParties) {
        //Get a list of the remaining ballots for each party, sort it, and collect it
        List<Pair<String, Fraction>> partyRemainingBallots = remainingParties.stream()
            .map(party -> new Pair<>(party, partyToPartyInformation.get(party).remainder))
            .sorted((el1, el2) -> el2.getSecond().compareTo(el1.getSecond()))
            .collect(Collectors.toList());

        //The list containing the current group of parties with the highest remaining ballots
        List<Pair<String, Fraction>> highestRemainingParties = new ArrayList<>();

        //The current index we are at in partyRemainingBallots
        int curIdx = 0;

        //While there are seats remaining to distribute
        while(numSeatsRemaining > 0) {
            //If we have gone through all of the parties and still have seats remaining to distribute
            if(curIdx >= partyRemainingBallots.size() && highestRemainingParties.isEmpty()) {

                /*
                 * Create a list,
                 * copy all parties that still have candidates without seats and their remainders to it,
                 * and replace partyRemainingBallots with it
                 */
                final List<Pair<String, Fraction>> partyRemainingSeatsTmp = new ArrayList<>();
                for(final Pair<String, Fraction> partyRemainderPair : partyRemainingBallots) {
                    if(remainingParties.contains(partyRemainderPair.getFirst())) {
                        partyRemainingSeatsTmp.add(partyRemainderPair);
                    }
                }
                partyRemainingBallots = partyRemainingSeatsTmp;
                curIdx = 0;

                //If there are no parties that have candidates without seats, then print such and break out of the loop
                if(partyRemainingSeatsTmp.size() == 0) {
                    printNotEnoughCandidates(numSeatsRemaining);
                    break;
                }
            }

            //If the list of parties with the highest remaining ballots is empty, get the next set group of parties with the highest remaining ballots
            if(highestRemainingParties.isEmpty()) {
                final Pair<Integer, List<Pair<String, Fraction>>> idxNextGroupPair = getNextEquivalentOrderedGroup(
                    partyRemainingBallots,
                    curIdx,
                    Comparator.comparing(Pair::getSecond)
                );

                //Update the index from where it ended up and the list of highest remaining parties in getNextEquivalentOrderedGroup
                curIdx = idxNextGroupPair.getFirst();
                highestRemainingParties = idxNextGroupPair.getSecond();

                //Shuffle the group of next highest remaining parties for tie breaking
                Collections.shuffle(highestRemainingParties, RAND);
            }

            //Get the next highest party from the end so its removal will be constant time
            final String chosenParty = highestRemainingParties.get(highestRemainingParties.size() - 1).getFirst();
            highestRemainingParties.remove(highestRemainingParties.size() - 1);

            //Get the party information for the currently-chosen party
            final PartyInformation partyInformation = partyToPartyInformation.get(chosenParty);

            printNextChosen(numSeatsRemaining, chosenParty);

            //Increment the party's number of seats, and if they have no more candidates to assign seats to, then remove it from remainingParties
            partyInformation.numSeats++;
            if(partyInformation.numSeats == partyInformation.numCandidates) {
                remainingParties.remove(chosenParty);
            }

            //Decrement the total number of seats remaining to allocate
            numSeatsRemaining--;

            final int numberOfSeatsAllocated = numSeats - numSeatsRemaining;
            auditWriter.printf("%d / %d have been allocated. %d seats remaining\n", numberOfSeatsAllocated, numSeats, numSeatsRemaining);
            auditWriter.println();
        }
    }

    /**
     * Distributes each party’s seats to their candidates by popularity
     *
     * @return a {@link List} containing the candidates who received seats
     */
    protected List<Candidate> distributeSeatsToCandidates() {
        //The final list of candidates who received seats
        final List<Candidate> finalSeats = new ArrayList<>();

        //For each party, distribute the seats for the party
        for(final String party : partyToPartyInformation.keySet()) {
            //Get the party information associated with the current party
            final PartyInformation partyInformation = partyToPartyInformation.get(party);

            int numSeatsRemaining = partyInformation.numSeats;

            if(numSeatsRemaining > 0) {
                auditWriter.println("Distributing " + numSeatsRemaining + " seats for the party " + party + ":\n");
            }

            //The list of candidates ordered by ballot count
            final List<Map.Entry<Candidate, Integer>> orderedCandidateBallots = partyInformation.orderedCandidateBallots;

            //The list containing the current group of candidates with the highest ballot counts
            List<Map.Entry<Candidate, Integer>> highestRemainingCandidates = new ArrayList<>();

            //The current index we are at in orderedCandidateBallots
            int curIdx = 0;

            //While there are seats remaining to distribute for the party
            while(numSeatsRemaining > 0) {
                /*
                 * If the list of candidates with the highest remaining ballots is empty, get the next set group of candidates with the highest
                 * remaining ballots
                 */
                if(highestRemainingCandidates.isEmpty()) {
                    final Pair<Integer, List<Map.Entry<Candidate, Integer>>> idxNextGroupPair = getNextEquivalentOrderedGroup(
                        orderedCandidateBallots,
                        curIdx,
                        Map.Entry.comparingByValue()
                    );

                    //Update the index from where it ended up and the list of highest remaining candidates in getNextEquivalentOrderedGroup
                    curIdx = idxNextGroupPair.getFirst();
                    highestRemainingCandidates = idxNextGroupPair.getSecond();

                    //Shuffle the group of next highest remaining candidates for tie breaking
                    Collections.shuffle(highestRemainingCandidates);

                    if(highestRemainingCandidates.size() > 1) {
                        auditWriter.println("A tie between candidates has to be broken");
                    }
                }

                //Get the next highest candidate from the end so its removal will be constant time
                final Map.Entry<Candidate, Integer> nextHighestCandidateBallots =
                    highestRemainingCandidates.get(highestRemainingCandidates.size() - 1);
                highestRemainingCandidates.remove(highestRemainingCandidates.size() - 1);

                //Get the next highest candidate from the nextHighestCandidateBallots pair and add it to the final seats
                final Candidate selected = nextHighestCandidateBallots.getKey();
                finalSeats.add(selected);

                //Decrement the total number of seats remaining to allocate
                numSeatsRemaining--;

                auditWriter.printf(
                    "From party %s's remaining candidates, %s had the greatest number of votes: %d. As such, %s will be allocated a seat.\n",
                    party,
                    selected.getName(),
                    nextHighestCandidateBallots.getValue(),
                    selected.getName()
                );
                auditWriter.println(numSeatsRemaining + " seats remaining");
                auditWriter.println();
            }
        }

        //Print the final seat distribution
        auditWriter.println("Final Seats:");
        reportWriter.println("Final Seats:");
        System.out.println("Final Seats");
        for(final Candidate candidate : finalSeats) {
            auditWriter.printf("%s (%s)\n", candidate.getName(), candidate.getParty());
            reportWriter.printf("%s (%s)\n", candidate.getName(), candidate.getParty());
            System.out.printf("%s (%s)\n", candidate.getName(), candidate.getParty());
        }
        return finalSeats;
    }

    /**
     * Prints the calculation of the quota to audit and report {@link OutputStream}s
     *
     * @param quota The total number of votes/candidates to calculate the initial seat allocations per party
     */
    private void printQuotaInformation(final Fraction quota) {
        final String ballotsPerSeat = quota.getWholePart() + " ballots per seat";
        auditWriter.printf("Quota: floor(%s) = %s\n\n", quota.toString(), ballotsPerSeat);
        reportWriter.printf("Quota: %s\n\n", ballotsPerSeat);
        System.out.printf("Quota: %s\n\n", ballotsPerSeat);
    }

    /**
     * Prints the grouping of candidates to each party to audit file
     */
    private void printPartyGrouping() {
        auditWriter.println("Grouping by Party:");
        for(final String party : partyToCandidateCounts.keySet()) {
            auditWriter.println("Party: " + party);
            for(final Candidate candidate : partyToCandidateCounts.get(party).keySet()) {
                auditWriter.println("    " + candidate.getName());
            }
        }
        auditWriter.println();
    }

    /**
     * Prints a table containing all parties and the respective {@link PartyInformation} field
     *
     * @param message        The message to print before printing the table
     * @param fieldExactName The exact field name to retrieve from the {@link PartyInformation}
     * @param tableFieldName The name that will be used when displaying the field in table form
     */
    private void printPartyInformationField(final String message, final String fieldExactName, final String tableFieldName) {
        auditWriter.println(message);

        //Gets the specific party information field for the parties
        final List<?> partyInformationFieldValues = partyToPartyInformation.values().stream()
            .map(partyInformation -> {
                try {
                    return PartyInformation.class.getField(fieldExactName).get(partyInformation);
                }
                catch(IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
                return null;
            })
            .collect(Collectors.toList());

        //Creates the table of parties and their corresponding party information field
        final String table = tableFormatter.formatAsTable(
            Arrays.asList("Party", tableFieldName),
            Arrays.asList(partyToPartyInformation.keySet(), partyInformationFieldValues),
            Arrays.asList(TableFormatter.Alignment.LEFT, TableFormatter.Alignment.RIGHT)
        );

        auditWriter.println(table + "\n");
    }

    /**
     * Print a table containing all parties and the total number of ballots each received to audit file
     */
    private void printPartyBallots() {
        printPartyInformationField("The ballot counts for each party are as follows:", "numBallots", "Ballots");
    }

    /**
     * Prints a table contains all parties and the number of final seats allocated to each to audit file
     */
    protected void printFinalSeatAllocations() {
        auditWriter.println("Final Seat Allocations:");

        final ArrayList<String> parties = new ArrayList<>();
        final ArrayList<Integer> seats = new ArrayList<>();
        for(final Map.Entry<String, PartyInformation> party : partiesToPartyInformation.entrySet()) {
            parties.add(party.getKey());
            seats.add(party.getValue().numSeats);
        }
        final String table = tableFormatter.formatAsTable(
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
    private void printSummaryTable(final Map<String, Integer> initialSeats) {
        final List<Integer> ballots = new ArrayList<>();
        final List<Integer> initialAllocation = new ArrayList<>();
        final List<String> remainingBallots = new ArrayList<>();
        final List<Integer> secondAllocation = new ArrayList<>();
        final List<Integer> finalSeats = new ArrayList<>();
        final List<String> percentOfBallotsToPercentOfSeats = new ArrayList<>();

        //Retrieves all parties and the corresponding party information for the party
        for(final String party : partyToPartyInformation.keySet()) {
            final PartyInformation partyInformation = partyToPartyInformation.get(party);
            ballots.add(partyInformation.numBallots);
            initialAllocation.add(initialSeats.get(party));
            remainingBallots.add(getRemainingBallots(partyInformation));
            secondAllocation.add(partyInformation.numSeats - initialSeats.get(party));
            finalSeats.add(partyInformation.numSeats);

            final int percentOfBallots = (int) Math.round(100.0 * partyInformation.numBallots / numBallots);
            final int percentOfSeats = (int) Math.round(100.0 * partyInformation.numSeats / numSeats);

            percentOfBallotsToPercentOfSeats.add(String.format("%d%%/%d%%", percentOfBallots, percentOfSeats));
        }

        final String table = tableFormatter.formatAsTable(
            Arrays.asList(
                "Parties", "Ballots", "First Allocation", "Remaining Ballots", "Second Allocation", "Final Seats", "% of Ballots to % of Seats"
            ),
            Arrays.asList(
                partyToPartyInformation.keySet(), ballots, initialAllocation, remainingBallots, secondAllocation, finalSeats,
                percentOfBallotsToPercentOfSeats
            ),
            Arrays.asList(
                TableFormatter.Alignment.LEFT, TableFormatter.Alignment.RIGHT, TableFormatter.Alignment.RIGHT, TableFormatter.Alignment.RIGHT,
                TableFormatter.Alignment.RIGHT, TableFormatter.Alignment.RIGHT, TableFormatter.Alignment.RIGHT
            )
        );

        auditWriter.println(table + "\n");
        reportWriter.println(table + "\n");
        System.out.println(table + "\n");

    }

    /**
     * Runs the election for the {@link VotingSystem} and determines the winner
     */
    @Override
    public void runElection() {
        //For each party, add to its respective party information the candidates and their respective ballot counts sorted by ballot count
        for(final String party : partyToCandidateCounts.keySet()) {
            final PartyInformation partyInformation = partyToPartyInformation.get(party);

            final List<Map.Entry<Candidate, Integer>> orderedCandidateBallots = new ArrayList<>(
                partyToCandidateCounts.get(party).entrySet()
            );
            orderedCandidateBallots.sort(Map.Entry.comparingByValue());
            
            partyInformation.numCandidates = orderedCandidateBallots.size();
            partyInformation.orderedCandidateBallots = orderedCandidateBallots;
        }

        //Create the quota from the total number of ballots and seats
        final Fraction quota = new Fraction(numBallots, numSeats);

        printQuotaInformation(quota);
        printPartyGrouping();
        printPartyBallots();

        //Initial allocation
        final Pair<Integer, Set<String>> initialAllocationResults = allocateInitialSeats(quota);

        //Get the number of seats remaining and set of parties with candidates that don't have seats from initialAllocationResults
        final int numSeatsRemaining = initialAllocationResults.getFirst();
        final Set<String> remainingParties = initialAllocationResults.getSecond();

        //Creates a map of parties to the number of seats each received during initial allocation
        final Map<String, Integer> partiesToInitialSeats = new HashMap<>();
        for(final Map.Entry<String, PartyInformation> party : partiesToPartyInformation.entrySet()) {
            partiesToInitialSeats.put(party.getKey(), party.getValue().numSeats);
        }

        //If there are still more seats available after initial allocation
        if(numSeatsRemaining != 0) {
            allocateRemainingSeats(numSeatsRemaining, remainingParties);
        }

        printFinalSeatAllocations();
        printSummaryTable(partiesToInitialSeats);

        //Distributes each party's seats to their candidates by popularity
        distributeSeatsToCandidates();

        auditWriter.close();
        reportWriter.close();
    }
}
