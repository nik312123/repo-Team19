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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * The {@link VotingSystem} representing the instant runoff voting system
 */
public class InstantRunoffSystem extends VotingSystem {
    
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
    
    protected int numCandidates;
    protected int numBallots;
    protected Candidate[] candidates;
    protected ArrayList<Candidate> nonEliminatedCandidates = new ArrayList<>();
    protected Map<Candidate, ArrayDeque<Ballot>> candidateBallotsMap = new LinkedHashMap<>();
    protected PrintWriter auditWriter;
    protected PrintWriter reportWriter;
    protected TableFormatter tableFormatter;
    protected static final Random random = new SecureRandom();
    
    /**
     * Initializes a {@link VotingSystem}
     *
     * @param auditOutput  The {@link OutputStream} to write detailed information about the running of the election
     * @param reportOutput The {@link OutputStream} to write a summary about the running of the election
     * @throws NullPointerException Thrown if either auditOutput or reportOutput is null
     */
    
    public InstantRunoffSystem(final OutputStream auditOutput, final OutputStream reportOutput) throws NullPointerException {
        super(auditOutput, reportOutput);
        auditWriter = new PrintWriter(auditOutput);
        reportWriter = new PrintWriter(reportOutput);
        
        tableFormatter = new TableFormatter('+', '-', '|');
    }
    
    @Override
    public int getCandidateHeaderSize() {
        return 0;
    }
    
    @Override
    public int getBallotHeaderSize() {
        return 0;
    }
    
    @Override
    public void importCandidatesHeader(String[] header, int line) throws ParseException {
    
    }
    
    @Override
    public void addCandidates(String candidates, int line) throws ParseException {
    
    }
    
    @Override
    public void importBallotsHeader(String[] header, int line) throws ParseException {
    
    }
    
    @Override
    public void addBallot(int ballotNumber, String ballot, int line) throws ParseException {
    
    }
    
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public String getShortName() {
        return null;
    }
    
    @Override
    public int getNumCandidates() {
        return 0;
    }
    
    @Override
    public Collection<Candidate> getCandidates() {
        return null;
    }
    
    @Override
    public int getNumBallots() {
        return 0;
    }
    
    /**
     * Returns the candidate with the highest votes and the candidate with the lowest votes
     *
     * @return the candidate with the highest votes and the candidate with the lowest votes
     */
    protected Pair<Pair<Integer, List<Candidate>>, Pair<Integer, Candidate>> getLowestHighestCandidates() {
        int highestBallots = -1;
        Candidate highestCandidate;
        highestCandidate = null;
        int lowestBallots = Integer.MAX_VALUE;
        final ArrayList<Candidate> lowestCandidates = new ArrayList<>();
        
        for(final Candidate candidate : nonEliminatedCandidates) {
            // Gets the number of ballots for each candidate
            final int numBallots = candidateBallotsMap.get(candidate).size();
            
            // Identifies new highest count and replaces highestCandidate
            if(numBallots > highestBallots) {
                highestBallots = numBallots;
                highestCandidate = candidate;
            }
            // Identifies new lowest count and replaces all lowest with new lowest candidate
            if(numBallots < lowestBallots) {
                lowestBallots = numBallots;
                lowestCandidates.clear();
                lowestCandidates.add(candidate);
            }
            // Multiple lowest candidates are collected as a group
            else if(numBallots == lowestBallots) {
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
        // Eliminates candidate from map
        final ArrayDeque<Ballot> ballotsToRedistribute = candidateBallotsMap.remove(lowest);
        nonEliminatedCandidates.remove(lowest);
        
        // Candidate has 0 ballots to distribute
        if(ballotsToRedistribute.isEmpty()) {
            auditWriter.println(lowest + " has no ballots to distribute.\n");
            return;
        }
        for(final Ballot ballot : ballotsToRedistribute) {
            // Gets next ranked candidate on the ballot
            Candidate nextCandidate = ballot.getNextCandidate();
            // If there exists a next candidate
            while(nextCandidate != null) {
                if(candidateBallotsMap.containsKey(nextCandidate)) { // Checks if next candidate is not already eliminated
                    candidateBallotsMap.get(nextCandidate).add(ballot); // Transfers ballot
                    auditWriter.println("Ballot " + ballot.ballotNumber + " has their next choice as candidate " + nextCandidate + ". The ballot "
                        + "will be distributed to " + nextCandidate + ".\n");
                    break;
                }
                else {
                    if(!nextCandidate.equals(lowest)) { // The next candidate is already eliminated
                        auditWriter.println(
                            "Ballot " + ballot.ballotNumber + " associated with " + lowest + " has their next choice as candidate " + nextCandidate +
                                ". but " +
                                nextCandidate + " was already eliminated. Trying the next choice.\n");
                    }
                    nextCandidate = ballot.getNextCandidate(); // Moves on to next ranked candidate on ballot
                }
            }
            if(nextCandidate == null) { // No more candidates ranked on ballot
                auditWriter.println(
                    "Ballot " + ballot.ballotNumber + " associated with " + lowest + " did not have any other candidates ranked. As such, their "
                        + "ballot will not be "
                        + "distributed.\n");
            }
        }
    }
    
    /**
     * Returns a table formatted as a {@link String} with all non-eliminated candidates and their number of ballots
     *
     * @return a table formatted as a {@link String} with all non-eliminated candidates and their number of ballots
     */
    protected String getCurrentChoiceBallots() {
        final ArrayList<Integer> ballotCounts = new ArrayList<>();
        for(final Candidate candidate : nonEliminatedCandidates) { // Goes through all non-eliminated candidates
            final ArrayDeque<Ballot> ballots = candidateBallotsMap.get(candidate);
            ballotCounts.add(ballots.size()); // creates list of ballot counts for all non-eliminated candidates
        }
        // Returns String as table
        return tableFormatter.formatAsTable(
            Arrays.asList("Candidate", "Ballots"),
            Arrays.asList(nonEliminatedCandidates, ballotCounts),
            Arrays.asList(TableFormatter.Alignment.LEFT, TableFormatter.Alignment.RIGHT)) + "\n";
    }
    
    /**
     * Runs the IR election algorithm
     */
    @Override
    public void runElection() {
        String writeToOutputs = "First-choice ballots:";
        auditWriter.println(writeToOutputs);
        reportWriter.println(writeToOutputs);
        System.out.println(writeToOutputs);
        // Initializes nonEliminatedCandidates to all existing candidate.
        nonEliminatedCandidates = new ArrayList<>(Arrays.asList(candidates));
        
        writeToOutputs = getCurrentChoiceBallots();
        auditWriter.println(writeToOutputs);
        reportWriter.println(writeToOutputs);
        System.out.println(writeToOutputs);
        
        while(true) {
            final int candidateBallotsMapLen = candidateBallotsMap.size();
            // If there is only 1 candidate, they are automatically declared the winner
            if(candidateBallotsMapLen == 1) {
                final Candidate winner = nonEliminatedCandidates.get(0);
                final ArrayDeque<Ballot> winnerBallots = candidateBallotsMap.get(winner);
                writeToOutputs = winner + "has received " + winnerBallots.size() + "/" + numBallots + " votes giving them a majority "
                    + "of " + String.format("%2f", winnerBallots.size() / (double) numBallots) + ". They have therefore won.";
                auditWriter.println(writeToOutputs);
                reportWriter.println(writeToOutputs);
                System.out.println(writeToOutputs);
                return;
            }
            // If there are 2 candidates remaining, the winner is decided by popularity
            if(candidateBallotsMapLen == 2) {
                final Candidate winner;
                boolean randomSelectionRequired = false;
                // Calculating the difference in ballot counts
                final int firstSecondCandidateComparison =
                    candidateBallotsMap.get(nonEliminatedCandidates.get(0)).size() - candidateBallotsMap.get(nonEliminatedCandidates.get(1)).size();
                
                if(firstSecondCandidateComparison > 0) {
                    winner = nonEliminatedCandidates.get(0);
                }
                else if(firstSecondCandidateComparison < 0) {
                    winner = nonEliminatedCandidates.get(1);
                }
                
                else { // If the difference is 0, they share the same number of ballots
                    randomSelectionRequired = true;
                    winner = nonEliminatedCandidates.get(random.nextInt(2)); // Picks winner using a random index
                }
                if(randomSelectionRequired) { // Printed when a tie is broken
                    auditWriter
                        .println("There exists a tie between " + nonEliminatedCandidates.get(0) + " and " + nonEliminatedCandidates.get(1) + ".");
                    auditWriter.println(winner + " wins tie break. They have therefore won the election.");
                }
                else { // Printed after popularity winner is determined (with no tie)
                    writeToOutputs = winner + " had received " + candidateBallotsMap.get(winner).size() + "/" + numBallots + ", votes giving them a "
                        + "greatest "
                        + "popularity with " + String.format("%.2f", candidateBallotsMap.get(winner).size() / (double) numBallots * 100) + "%";
                    auditWriter.println(writeToOutputs);
                    reportWriter.println(writeToOutputs);
                    System.out.println(writeToOutputs);
                }
                return;
            }
            else { // More than 2 candidates
                // Gets highest candidate and lowest candidates by ballots
                final Pair<Pair<Integer, List<Candidate>>, Pair<Integer, Candidate>> lowestHighestCandidates = getLowestHighestCandidates();
                final Pair<Integer, Candidate> highestCandidateBallots = lowestHighestCandidates.getSecond();
                
                if(highestCandidateBallots.getKey() > numBallots / 2) { // If highest candidate has majority, winner is declared
                    final Candidate winner = highestCandidateBallots.getSecond();
                    writeToOutputs = winner + "has received " + highestCandidateBallots.getKey() + "/" + numBallots + " votes giving them a majority "
                        + "of " + String.format("%.2f", highestCandidateBallots.getKey() / (double) numBallots * 100) + "%. They have therefore won"
                        + ".";
                    auditWriter.println(writeToOutputs);
                    reportWriter.println(writeToOutputs);
                    System.out.println(writeToOutputs);
                    break;
                }
                else { // If no majority
                    final Pair<Integer, List<Candidate>> lowestCandidateBallots = getLowestHighestCandidates().getFirst();
                    final List<Candidate> lowestCandidates = lowestCandidateBallots.getSecond();
                    // Randomly picks a candidate from lowestCandidates (could have only 1 candidate);
                    final Candidate lowest = lowestCandidates.get(random.nextInt(lowestCandidates.size()));
                    
                    if(lowestCandidates.size() == 1) { // If tie breaking was not required
                        auditWriter.println("No candidate has a majority. Eliminating the candidate with the lowest ballots: " + lowest + "\n");
                    }
                    else { // if there were multiple lowest candidates
                        auditWriter.print("No candidate has a majority. There is a tie for lowest ballots between ");
                        for(final Candidate candidate : lowestCandidates) {
                            auditWriter.print(" " + candidate + ",");
                        }
                        auditWriter.println();
                        auditWriter.println("Random choice from the above for elimination: " + lowest);
                        auditWriter.println();
                    }
                    // eliminated lowest candidate chosen
                    eliminateLowest(lowest);
                    
                    //  Prints table should ballot counts after elimination
                    writeToOutputs = "Ballots after " + lowest + " was eliminated:";
                    auditWriter.println(writeToOutputs);
                    reportWriter.println(writeToOutputs);
                    System.out.println(writeToOutputs);
                    
                    writeToOutputs = getCurrentChoiceBallots();
                    auditWriter.println(writeToOutputs);
                    reportWriter.println(writeToOutputs);
                    System.out.println(writeToOutputs);
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return null;
    }
}
