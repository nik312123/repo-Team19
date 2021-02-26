package org.team19;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Map;

/**
 * Interface for parsing an {@link InputStream} and returns a {@link VotingSystem}
 */
public interface VotingStreamParserInterface {
    
    /**
     * Parses an {@link InputStream} and returns a {@link VotingSystem} constructed from the given stream
     *
     * @param input           The {@link InputStream} to parse
     * @param headerSystemMap The mapping between header strings and their corresponding {@link VotingSystem} classes
     * @return The parsed {@link VotingSystem}
     * @throws ParseException Thrown if there is an issue in parsing the provided {@link InputStream}
     */
    VotingSystem parse(final InputStream input, final Map<String, Class<? extends VotingSystem>> headerSystemMap)
        throws ParseException;
    
    /**
     * Returns the {@link String} form of this {@link VotingStreamParserInterface}
     *
     * @return The {@link String} form of this {@link VotingStreamParserInterface}
     */
    @Override
    String toString();
    
    /**
     * Returns true if the given object is equivalent to this {@link VotingStreamParserInterface}
     *
     * @param other The object to compare to this {@link VotingStreamParserInterface}
     * @return True if the given object is equivalent to this {@link VotingStreamParserInterface}
     */
    @Override
    boolean equals(final Object other);
    
    /**
     * Returns the hashcode for this {@link VotingStreamParserInterface}
     *
     * @return The hashcode for this {@link VotingStreamParserInterface}
     */
    @Override
    int hashCode();
}
