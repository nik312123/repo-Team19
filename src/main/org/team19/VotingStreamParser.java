package org.team19;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Map;

/**
 * Parses an {@link InputStream} and returns a {@link VotingSystem}
 */
public abstract class VotingStreamParser {
    
    /**
     * Initializes a {@link VotingStreamParser} given the audit and report streams
     *
     * @param auditOutput  The {@link OutputStream} to write detailed information about the running of the election
     * @param reportOutput The {@link OutputStream} to write a summary about the running of the election
     */
    public VotingStreamParser(final OutputStream auditOutput, final OutputStream reportOutput) {}
    
    /**
     * Parses an {@link InputStream} and returns a {@link VotingSystem} constructed from the given stream
     *
     * @param input           The {@link InputStream} to parse
     * @param headerSystemMap The mapping between header strings and their corresponding {@link VotingSystem} classes
     * @return The parsed {@link VotingSystem}
     * @throws ParseException Thrown if there is an issue in parsing the provided {@link InputStream}
     */
    public abstract VotingSystem parse(final InputStream input, final Map<String, Class<? extends VotingSystem>> headerSystemMap)
        throws ParseException;
    
    /**
     * Returns the {@link String} form of this {@link VotingStreamParser}
     *
     * @return The {@link String} form of this {@link VotingStreamParser}
     */
    @Override
    public abstract String toString();
    
    /**
     * Returns true if the given object is equivalent to this {@link VotingStreamParser}
     *
     * @param other The object to compare to this {@link VotingStreamParser}
     * @return True if the given object is equivalent to this {@link VotingStreamParser}
     */
    @Override
    public abstract boolean equals(Object other);
    
    /**
     * Returns the hashcode for this {@link VotingStreamParser}
     *
     * @return The hashcode for this {@link VotingStreamParser}
     */
    @Override
    public abstract int hashCode();
}
