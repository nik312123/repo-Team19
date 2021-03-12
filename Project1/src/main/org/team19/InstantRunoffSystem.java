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
    protected OutputStream auditOutput;
    protected OutputStream reportOutput;
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
    
    protected Pair<Pair<Integer, List<Candidate>>, Pair<Integer, Candidate>> getLowestHighestCandidates(){
        int highestBallots = -1;
        Candidate highestCandidate = new Candidate("null", "null");
        int lowestBallots = Integer.MAX_VALUE;
        ArrayList<Candidate> lowestCandidates = new ArrayList<>();
        for(int i = 0; i < candidates.length; i++){
            int numBallots = candidateBallotsMap.get(i).size();
            if(numBallots > highestBallots){
                highestBallots = numBallots;
                highestCandidate = candidates[i];
            }
            if(numBallots < lowestBallots){
                lowestBallots = numBallots;
                lowestCandidates.clear();
                lowestCandidates.add(candidates[i]);
            }
            else if(numBallots == lowestBallots){
                lowestCandidates.add(candidates[i]);
            }
        }
        Pair lowestPair = new Pair(lowestBallots, lowestCandidates);
        Pair highestPair = new Pair(highestBallots, highestCandidate);
        return new Pair<Pair<Integer, List<Candidate>>, Pair<Integer, Candidate>>(lowestPair, highestPair);
    }
    
    protected void eliminateLowest(Candidate lowest){
        ArrayDeque<Ballot> ballotsToRedistribute = candidateBallotsMap.remove(lowest);
        for(int i = 0; i < ballotsToRedistribute.size(); i++){
            Candidate nextCandidate = ballotsToRedistribute.getNextCandidate();
            //if(nextCandidate != UNDEFINED){
                //candidateBallotsMap.get(nextCandidate).addBallot();
            //}
            //auditOutput.printBallotRedistribution(ballot, nextCandidate)
        }
    }
    
    protected String printFirstChoiceBallotsAudit(TableFormatter t, Map m){
        //objColTableToStrRowTable(final List<String> headers, final List<List<Object>> colTable, final int numRows,
        //final int numCols)
        List<Candidate> list = new ArrayList<Candidate>(m.values());
        ArrayList<Integer> intList = new ArrayList<Integer>();
        for(Candidate c : list){
            ArrayDeque<Ballot> ballots = (ArrayDeque<Ballot>)(m.get(c));
            intList.add(ballots.size());
        }
        String toStream = tableFormatter.formatAsTable(
            Arrays.asList("Candidate", "Ballots"),
            Arrays.asList(list, intList),
            Arrays.asList(TableFormatter.Alignment.LEFT, TableFormatter.Alignment.RIGHT)
        );
        return toStream;
    }
    
    @Override
    public void runElection() {
        try (PrintWriter p = new PrintWriter(auditOutput)) {
            p.println(printFirstChoiceBallotsAudit(tableFormatter, candidateBallotsMap));
        }
        //auditOutput.printFirstChoiceBallots(tableFormatter, candidateBallotsMap);
        //reportOutput.printFirstChoiceBallots(tableFormatter, candidateBallotsMap)
        //summaryOutput.printFirstChoiceBallots(tableFormatter, candidateBallotsMap)
        while(true) {
            int candidateBallotsMapLen = candidateBallotsMap.size();
            //if candidateBallotsMapLen == 1:
            //Map.Entry<Candidate, ArrayDeque<Ballot>>  = candidateBallotsMap.entrySet().iterator().next();
            //auditOutput.displayWinnerInfo()
            if(candidateBallotsMapLen == 2) {
                Candidate winner;
                boolean randomSelectionRequired = false;
                Map.Entry<Candidate, ArrayDeque<Ballot>>[] candidateBallotsArr = candidateBallotsMap.entrySet().toArray(new Map.Entry[0]);
                int firstSecondCandidateComparison =
                    Integer.compare(candidateBallotsArr[0].getValue().size(), candidateBallotsArr[1].getValue().size());
                if(firstSecondCandidateComparison > 0) {
                    winner = candidateBallotsArr[0].getKey();
                }
                else if(firstSecondCandidateComparison < 0) {
                    winner = candidateBallotsArr[1].getKey();
                }
                else {
                    randomSelectionRequired = true;
                    int randomCandidate = rand.nextInt(2);
                    winner = candidateBallotsArr[randomCandidate].getKey();
                    //auditOutput.displayWinnerInfo(winner, randomSelectionRequired, numBallots, tableFormatter, candidateBallotsMap
                }
            }
            else {
                Pair<Pair<Integer, List<Candidate>>, Pair<Integer, Candidate>> lowestHighestCandidates = getLowestHighestCandidates();
                Pair highestCandidateBallots = lowestHighestCandidates.getSecond();
                if(highestCandidateBallots.getKey() > numBallots / 2) {
                    Candidate winner = highestCandidateBallots.getValue();
                    //auditOutput.displayWinnerInfo(winner, false, numBallots, tableFormatter, candidateBallotsMap);
                    //}
                    Pair lowestCandidateBallots = lowestHighestCandidates.getFirst();
                    int rando = rand.nextInt(lowestCandidateBallots.getValue().size());
                    Candidate lowest = lowestCandidateBallots.getValue().get(rando);
                    //auditOutput.printCandidateToEliminate(lowest)
                    eliminateLowest(lowest);
                    //auditOutput.printCandidatesAfterElimination(lowest, tableFormatter, candidateBallotsMap)
                    //reportOutput.printCandidatesAfterElimination(lowest, tableFormatter, candidateBallotsMap)
                    //summaryOutput..printCandidatesAfterElimination(lowest, tableFormatter, candidateBallotsMap)
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return null;
    }
}
