/*
 * File name:
 * Candidate.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Represents an election candidate
 */

package org.team19;

import java.util.Objects;

/**
 * Represents an election candidate
 */
public class Candidate {
    
    /**
     * The name of the election candidate
     */
    protected final String name;
    
    /**
     * The name of thee candidate's party
     */
    protected final String party;
    
    /**
     * The result of calling {@link #toString()} on this {@link Candidate}
     */
    protected final String candidateStr;
    
    /**
     * The result of calling {@link #hashCode()} on this {@link Candidate}
     */
    protected final int hashCode;
    
    /**
     * Initializes a {@link Candidate}
     *
     * @param name  The name of the election candidate
     * @param party The name of the candidate's party
     * @throws NullPointerException Thrown if the provided name or party is null
     */
    public Candidate(final String name, final String party) {
        this.name = Objects.requireNonNull(name);
        this.party = Objects.requireNonNull(party);
        candidateStr = String.format("%s (%s)", name, party);
        hashCode = Objects.hash(name, party);
    }
    
    /**
     * Returns the name of the election candidate
     *
     * @return The name of the election candidate
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the name of thee candidate's party
     *
     * @return The name of thee candidate's party
     */
    public String getParty() {
        return party;
    }
    
    /**
     * Returns the string form of the {@link Candidate} in the form "Candidate{name=[name], party=[party]}" where [name] and [party] are the
     * candidate's name and party, respectively
     *
     * @return The string form of the {@link Candidate}
     */
    @Override
    public String toString() {
        return candidateStr;
    }
    
    /**
     * Returns true if the other object is a {@link Candidate} and has the same name and party
     *
     * @param other The object to compare to this {@link Candidate}
     * @return True if the other object is a {@link Candidate} and has the same name and party
     */
    @Override
    public boolean equals(final Object other) {
        if(this == other) {
            return true;
        }
        if(!(other instanceof Candidate)) {
            return false;
        }
        final Candidate candidate = (Candidate) other;
        return name.equals(candidate.name) && party.equals(candidate.party);
    }
    
    /**
     * Returns the hashcode for this {@link Candidate}
     *
     * @return The hashcode for this {@link Candidate}
     */
    @Override
    public int hashCode() {
        return hashCode;
    }
}
