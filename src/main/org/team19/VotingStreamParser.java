package org.team19;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Map;

/**
 * Parses an {@link InputStream} and returns a {@link VotingSystem}
 */
public class VotingStreamParser {
    
    /**
     * Parses an {@link InputStream} and returns a {@link VotingSystem} constructed from the given stream
     *
     * @param input           The {@link InputStream} to parse
     * @param headerSystemMap The mapping between header strings and their corresponding {@link VotingSystem} classes
     * @return The parsed {@link VotingSystem}
     * @throws ParseException Thrown if there is an issue in parsing the provided {@link InputStream}
     */
    public VotingSystem parse(final InputStream input, final Map<String, Class<? extends VotingSystem>> headerSystemMap) throws ParseException {
        return null;
    }
    
    /**
     * Returns the {@link String} form of this {@link VotingStreamParser}
     *
     * @return The {@link String} form of this {@link VotingStreamParser}
     */
    @Override
    public String toString() {
        return null;
    }
    
    /**
     * Returns true if the given object is equivalent to this {@link VotingStreamParser}
     *
     * @param other The object to compare to this {@link VotingStreamParser}
     * @return True if the given object is equivalent to this {@link VotingStreamParser}
     */
    @Override
    public boolean equals(final Object other) {
        return false;
    }
    
    /**
     * Returns the hashcode for this {@link VotingStreamParser}
     *
     * @return The hashcode for this {@link VotingStreamParser}
     */
    @Override
    public int hashCode() {
        return 0;
    }
    
}
