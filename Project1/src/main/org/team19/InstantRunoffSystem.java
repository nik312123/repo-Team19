package org.team19;

import com.sun.jdi.Value;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The {@link VotingSystem} representing the instant runoff voting system
 */
public class InstantRunoffSystem extends VotingSystem {
    
    protected static class Ballot {
        protected int ballotNumber;
        protected int candidateIndex = -1;
        protected Candidate[] rankedCandidates;
        protected Ballot(Candidate[] rankedCandidates, int ballotNumber){}
        protected Candidate getNextCandidate(){
            return null;
        }
        public String toString(){
            return null;
        }
        public boolean equals(Object other){
            return true;
        }
        public int hashCode(){
            return 1;
        }
        
    }
    
    protected int numCandidates;
    protected int numBallots;
    protected Candidate[] candidates;
    protected Map<Candidate, ArrayDeque<Ballot>> candidateBallotsMap = new LinkedHashMap<>();
    protected PrintWriter auditWriter;
    protected PrintWriter reportWriter;
    protected TableFormatter tableFormatter;
    protected static final Random rand = new SecureRandom();
    
    /**
     * Initializes a {@link VotingSystem}
     *
     * @param auditOutput  The {@link OutputStream} to write detailed information about the running of the election
     * @param reportOutput The {@link OutputStream} to write a summary about the running of the election
     * @throws NullPointerException Thrown if either auditOutput or reportOutput is null
     */
    
    public InstantRunoffSystem(OutputStream auditOutput, OutputStream reportOutput) throws NullPointerException {
        super(auditOutput, reportOutput);
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
    
    protected Pair<Pair<Integer, List<Candidate>>, Pair<Integer, Candidate>> getLowestHighestCandidates() {
        int highestBallots = -1;
        Candidate highestCandidate;
        highestCandidate = null;
        int lowestBallots = Integer.MAX_VALUE;
        final ArrayList<Candidate> lowestCandidates = new ArrayList<>();
        for(int i = 0; i < candidates.length; i++){
            final int numCurrBallots = candidateBallotsMap.get(i).size();
            if(numCurrBallots > highestBallots){
                highestBallots = numCurrBallots;
                highestCandidate = candidates[i];
            }
            if(numCurrBallots < lowestBallots){
                lowestBallots = numCurrBallots;
                lowestCandidates.clear();
                lowestCandidates.add(candidates[i]);
            }
            else if(numCurrBallots == lowestBallots){
                lowestCandidates.add(candidates[i]);
            }
        }
        final Pair<Integer, List<Candidate>> lowestPair = new Pair<>(lowestBallots, lowestCandidates);
        final Pair <Integer, Candidate> highestPair = new Pair<>(highestBallots, highestCandidate);
        return new Pair<>(lowestPair, highestPair);
    }
    
    protected void eliminateLowest(final Candidate lowest){
        final ArrayDeque<Ballot> ballotsToRedistribute = candidateBallotsMap.remove(lowest);
        for(final Ballot ballot : ballotsToRedistribute){
            Candidate nextCandidate = ballot.getNextCandidate();
            while(nextCandidate != null){
                if()
                candidateBallotsMap.get(nextCandidate).add(ballot);
                nextCandidate = ballot.getNextCandidate();
                auditWriter.println(ballot);
                auditWriter.println(" has their next choice as candidate ");
                auditWriter.println(nextCandidate);
                auditWriter.println(". The ballot will be redistributed to ");
                auditWriter.println(nextCandidate);
                auditWriter.println(".");
            }
            auditWriter.println(ballot);
            auditWriter.println(" associated with candidate ");
            auditWriter.println(lowest);
            auditWriter.println(" has their next choice as candidate ");
            auditWriter.println(nextCandidate);
            auditWriter.println(". But ");
            auditWriter.println(nextCandidate);
            auditWriter.println(" was already eliminated.  Trying the next choice.");
    
            auditWriter.println(ballot);
            auditWriter.println(" did not have any other candidates ranked.  As such, their ballot will not be distributed.");
        }
    }
    
    private String getFirstChoiceBallots(){
        final List<Candidate> list = new ArrayList<>(candidateBallotsMap.keySet());
        final ArrayList<Integer> intList = new ArrayList<>();
        for(final Candidate candidate : list){
            final ArrayDeque<Ballot> ballots = candidateBallotsMap.get(candidate);
            intList.add(ballots.size());
        }
        return tableFormatter.formatAsTable(
            Arrays.asList("Candidate", "Ballots"),
            Arrays.asList(list, intList),
            Arrays.asList(TableFormatter.Alignment.LEFT, TableFormatter.Alignment.RIGHT)
        );
    }
    
    @Override
    public void runElection() {
        final String firstChoiceBallots;
        firstChoiceBallots = getFirstChoiceBallots();
        auditWriter.println(firstChoiceBallots);
        reportWriter.println(firstChoiceBallots);
        System.out.println(firstChoiceBallots);
        while(true) {
            final int candidateBallotsMapLen = candidateBallotsMap.size();
            if(candidateBallotsMapLen == 1){
                final Map.Entry<Candidate, ArrayDeque<Ballot>> candidateBallotsWinner = candidateBallotsMap.entrySet().iterator().next();
                auditWriter.print(candidateBallotsWinner.getKey());
                auditWriter.print(" has received ");
                auditWriter.print(candidateBallotsWinner.getValue());
                auditWriter.print("/");
                auditWriter.print(numBallots);
                auditWriter.println("votes giving them a majority.  They have therefore won");
            }
            if(candidateBallotsMapLen == 2) {
                final Candidate winner;
                final int randomCandidate = rand.nextInt(2);
                final Map.Entry<Candidate, ArrayDeque<Ballot>>[] candidateBallotsArr = candidateBallotsMap.entrySet().toArray(new Map.Entry[0]);
                final int firstSecondCandidateComparison =
                    Integer.compare(candidateBallotsArr[0].getValue().size(), candidateBallotsArr[1].getValue().size());
                if(firstSecondCandidateComparison > 0) {
                    winner = candidateBallotsArr[0].getKey();
                }
                else if(firstSecondCandidateComparison < 0) {
                    winner = candidateBallotsArr[1].getKey();
                }
                else {
                    winner = candidateBallotsArr[randomCandidate].getKey();
                }
                auditWriter.print(winner);
                auditWriter.print(" has received ");
                auditWriter.print(candidateBallotsArr[randomCandidate].getValue());
                auditWriter.print("/");
                auditWriter.print(numBallots);
                auditWriter.println("votes giving them a majority.  They have therefore won");
            }
            else {
                final Pair<Pair<Integer, List<Candidate>>, Pair<Integer, Candidate>> lowestHighestCandidates = getLowestHighestCandidates();
                final Pair<Integer, Candidate> highestCandidateBallots = lowestHighestCandidates.getSecond();
                if(highestCandidateBallots.getKey() > numBallots / 2) {
                    final Candidate winner = highestCandidateBallots.getValue();
                    auditWriter.print(winner);
                    auditWriter.print(" has received ");
                    auditWriter.print(highestCandidateBallots.getValue());
                    auditWriter.print("/");
                    auditWriter.print(numBallots);
                    auditWriter.println("votes giving them a majority.  They have therefore won");
                }
                final Pair<Integer, List<Candidate>> lowestCandidateBallots = lowestHighestCandidates.getFirst();
                final int rando = rand.nextInt(lowestCandidateBallots.getValue().size());
                final Candidate lowest = lowestCandidateBallots.getValue().get(rando);
                auditWriter.print("No candidate has a majority.  Eliminating the candidate with the lowest ballots: ");
                auditWriter.print(lowest);
                auditWriter.print(".");
                eliminateLowest(lowest);
                final String ballotsAfterElimination;
                ballotsAfterElimination = getFirstChoiceBallots();
                auditWriter.println(ballotsAfterElimination);
                reportWriter.println(ballotsAfterElimination);
                System.out.println(ballotsAfterElimination);
            }
        }
    }
    
    @Override
    public String toString() {
        return null;
    }
}
