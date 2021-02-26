package org.team19;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Map;

/**
 * Parses an {@link InputStream} and returns a {@link VotingSystem}
 */
public interface VotingStreamParser {
    
    /**
     * Parses an {@link InputStream} and returns a {@link VotingSystem} constructed from the given stream
     *
     * @param input           The {@link InputStream} to parse
     * @param auditOutput     The {@link OutputStream} to write detailed information about the running of the election
     * @param reportOutput    The {@link OutputStream} to write a summary about the running of the election
     * @param headerSystemMap The mapping between header strings and their corresponding {@link VotingSystem} classes
     * @return The parsed {@link VotingSystem}
     * @throws ParseException Thrown if there is an issue in parsing the provided {@link InputStream}
     */
    VotingSystem parse(final InputStream input, final OutputStream auditOutput, final OutputStream reportOutput,
        final Map<String, Class<? extends VotingSystem>> headerSystemMap) throws ParseException;
}
