package org.team19;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Map;

/**
 * Parses an {@link InputStream} and returns a {@link VotingSystem}
 */
public final class VotingStreamParser {
    
    /**
     * A private constructor for the utility class {@link VotingStreamParser} to prevent instantiation
     */
    private VotingStreamParser() {}
    
    /**
     * Parses an {@link InputStream} and returns a {@link VotingSystem} constructed from the given stream
     *
     * @param input           The {@link InputStream} to parse
     * @param auditStream     The {@link OutputStream} to write detailed information about the running of the election
     * @param reportStream    The {@link OutputStream} to write a summary about the running of the election
     * @param headerSystemMap The mapping between header strings and their corresponding {@link VotingSystem} classes
     * @return The parsed {@link VotingSystem}
     * @throws NullPointerException Thrown if any of the given streams or if the headerSystemMap is null
     * @throws ParseException       Thrown if there is an issue in parsing the provided {@link InputStream}
     */
    public static VotingSystem parse(final InputStream input, final OutputStream auditStream, final OutputStream reportStream,
        final Map<String, Class<? extends VotingSystem>> headerSystemMap) throws ParseException, NullPointerException {
        return null;
    }
    
}
