/*
 * File name:
 * TableFormatter.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Creates the String form of a table from a list of string column headers, a list of lists with each list representing a column, and a list of
 * Alignments corresponding to whether a column should be left-aligned or right-aligned
 */

package org.team19;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Creates the {@link String} form of a table from a list of string column headers, a list of lists with each list representing a column, and a
 * list of {@link Alignment}s corresponding to whether a column should be left-aligned or right-aligned
 */
public final class TableFormatter {
    
    /**
     * The string form of the character to use in table corners or where both rows and columns intersect
     */
    protected final String intersection;
    
    /**
     * The string form of the character to use where two rows intersect
     */
    protected final String horizontalDivider;
    
    /**
     * The string form of the character to use where two columns intersect
     */
    protected final String verticalDivider;
    
    /**
     * Represents the direction that a column in a table should be aligned
     */
    public enum Alignment {
        /**
         * Represents the left alignment for a column
         */
        LEFT,
        
        /**
         * Represents the right alignment for a column
         */
        RIGHT
    }
    
    /**
     * Initializes a {@link TableFormatter}
     *
     * @param intersection      The string form of the character to use in table corners or where both rows and columns intersect
     * @param horizontalDivider The character to use where two rows intersect
     * @param verticalDivider   The character to use where two columns intersect
     */
    public TableFormatter(final char intersection, final char horizontalDivider, final char verticalDivider) {
        this.intersection = Character.toString(intersection);
        this.horizontalDivider = Character.toString(horizontalDivider);
        this.verticalDivider = Character.toString(verticalDivider);
    }
    
    /**
     * Given a {@link List} of string column headers and a table of objects with each {@link List} in the table representing a column, returns a
     * table that includes the string column headers and the string forms of the aforementioned objects; however, each list composing the returned
     * table represents a rows instead of a column
     *
     * @param headers  The list of column headers
     * @param colTable The headerless table of objects with each list representing a column
     * @param numRows  The number of rows in the table, including the headers as a row
     * @param numCols  The number of columns in the table
     * @return The provided table but all objects are in string form, the column headers are added, and the table is returned as a list of rows
     */
    protected List<List<String>> objColTableToStrRowTable(final List<String> headers, final Collection<? extends Collection<?>> colTable,
        final int numRows, final int numCols) {
        //Creates the table of strings rows
        final List<List<String>> strRowTable = new ArrayList<>();
        for(int i = 0; i < numRows; i++) {
            strRowTable.add(new ArrayList<>(numCols));
        }
        
        //Adds the headers to the table if there are a nonzero number of columns
        if(numCols != 0) {
            strRowTable.get(0).addAll(headers);
        }
        
        for(final Collection<?> col : colTable) {
            final Iterator<?> colIter = col.iterator();
            for(int rowIndex = 1; rowIndex < numRows; rowIndex++) {
                strRowTable.get(rowIndex).add(colIter.next().toString());
            }
        }
        return strRowTable;
    }
    
    /**
     * Given the string form of the table, returns the maximum lengths of the strings in each column
     *
     * @param strRowTable The string row list representation of the object column list table
     * @param numCols     The number of columns the table contains
     * @return The maximum lengths of the strings in each column of the given table
     */
    protected int[] getColumnLengths(final List<List<String>> strRowTable, final int numCols) {
        final int[] columnLengths = new int[numCols];
        for(final List<String> row : strRowTable) {
            for(int j = 0; j < numCols; j++) {
                columnLengths[j] = Math.max(columnLengths[j], row.get(j).length());
            }
        }
        return columnLengths;
    }
    
    /**
     * Returns the formatting string for a row of data in the table
     *
     * @param alignments    The list of alignments corresponding to each column
     * @param columnLengths The array consisting of the lengths of the longest strings in each column
     * @param numCols       The number of columns in the table
     * @return The formatting string for a row of data in the table
     */
    protected String getTableFormat(final List<Alignment> alignments, final int[] columnLengths, final int numCols) {
        //The format string starts with the left boundary
        final StringBuilder tableFormatBuilder = new StringBuilder();
        tableFormatBuilder.append(verticalDivider).append(" ");
        
        /*
         * Then, for each column, the formatting string is "%-[columnLength]s" for left-aligned columns and "%[columnLength]s" for right-aligned
         * columns where [columnLength] is the length of the largest string in the column
         *
         * Then, the vertical divider is used as a divider between columns
         */
        for(int j = 0; j < numCols; j++) {
            tableFormatBuilder.append("%");
            if(alignments.get(j) == null || alignments.get(j).equals(Alignment.LEFT)) {
                tableFormatBuilder.append("-");
            }
            tableFormatBuilder.append(columnLengths[j]).append("s");
            if(j != numCols - 1) {
                tableFormatBuilder.append(" ").append(verticalDivider).append(" ");
            }
        }
        //The format string ends with the right boundary
        tableFormatBuilder.append(" ").append(verticalDivider);
        return tableFormatBuilder.toString();
    }
    
    /**
     * Returns a horizontal separator for the table
     *
     * @param columnLengths   The array consisting of the lengths of the longest strings in each column
     * @param numCols         The number of columns in the table
     * @param terminalCharStr The string form of the character to use on the beginning and end of the separator
     * @return A horizontal divider for the table
     */
    protected String getHorizontalDivider(final int[] columnLengths, final int numCols, final String terminalCharStr) {
        final StringBuilder horizontalDividerBuilder = new StringBuilder();
        //Adds the beginning of the divider
        horizontalDividerBuilder.append(terminalCharStr).append(horizontalDivider);
        
        //Adds the main divider string of the header throughout the divider, placing the intersection string between columns
        for(int j = 0; j < numCols; j++) {
            horizontalDividerBuilder.append(horizontalDivider.repeat(columnLengths[j]));
            if(j != numCols - 1) {
                horizontalDividerBuilder.append(horizontalDivider).append(intersection).append(horizontalDivider);
            }
        }
        //Adds the ending of the divider and returns it
        return horizontalDividerBuilder.append(horizontalDivider).append(terminalCharStr).toString();
    }
    
    /**
     * Returns a string representation of a table from a list of string column headers, a list of lists with each list representing a column, and a
     * list of {@link Alignment} corresponding to whether a column should be left-aligned or right-aligned
     *
     * @param headers      The list of column headers for the table
     * @param colTableData The headerless table of objects with each list representing a column
     * @param alignments   The alignments corresponding to each column
     * @return A string representation of a table from a list of string column headers, a list of lists with each list representing a column, and a
     * list of {@link Alignment} corresponding to whether a column should be left-aligned or right-aligned
     * @throws NullPointerException     Thrown if any of the given lists are null
     * @throws IllegalArgumentException Thrown if the number of columns in provided either does not match the length of the column headers or the
     *                                  length of the alignments
     */
    public String formatAsTable(final List<String> headers, final Collection<? extends Collection<?>> colTableData, final List<Alignment> alignments)
        throws NullPointerException, IllegalArgumentException {
        //Throw a NullPointerException if any of the lists provided are null
        Objects.requireNonNull(headers);
        Objects.requireNonNull(colTableData);
        Objects.requireNonNull(alignments);
        
        final int numCols = colTableData.size();
        
        //If the number of columns does not match the size of the column headers or alignment lists, then throw an IllegalArgumentException
        if(numCols != headers.size()) {
            throw new IllegalArgumentException("The given headers list must have the same size as the number of columns in the table");
        }
        else if(numCols != alignments.size()) {
            throw new IllegalArgumentException("The given alignments must equal the number of columns in the table");
        }
        
        //Get the number of rows, including the header
        final int numRows = numCols == 0 ? 0 : colTableData.iterator().next().size() + 1;
        
        /*
         * From the table of column lists composed of objects, retrieves the same table but with each object converted to its string form and the
         * table in the form of row lists
         */
        final List<List<String>> strRowTable = objColTableToStrRowTable(headers, colTableData, numRows, numCols);
        
        //Retrieves the lengths of the largest strings in each column of the string form of the table
        final int[] columnLengths = getColumnLengths(strRowTable, numCols);
        
        //Retrieves the format string that formats a row of data in the table
        final String tableFormat = getTableFormat(alignments, columnLengths, numCols);
        
        //Responsible for building the string form of the table
        final StringBuilder tableBuilder = new StringBuilder();
        
        //Retrieves the border used for the top and bottom of the table
        final String terminalSeparator = getHorizontalDivider(columnLengths, numCols, intersection);
        
        //Retrieves the horizontal separator used for separating rows
        final String horizontalSeparator = getHorizontalDivider(columnLengths, numCols, verticalDivider);
        
        //Adds the rows of data to the table in string form along with the horizontal separators
        tableBuilder.append(terminalSeparator).append("\n");
        for(int i = 0; i < numRows; i++) {
            tableBuilder.append(String.format(tableFormat, strRowTable.get(i).toArray())).append("\n");
            if(i != numRows - 1) {
                tableBuilder.append(horizontalSeparator).append("\n");
            }
        }
        tableBuilder.append(terminalSeparator);
        
        return tableBuilder.toString();
    }
    
    /**
     * Returns the string form of this {@link TableFormatter}
     *
     * @return The string form of this {@link TableFormatter} in the form "TableFormatter{intersection='[intersection]',
     * horizontalDivider='[horizontalDivider]', verticalDivider='[verticalDivider]'}" where [intersection], [horizontalDivider], and
     * [verticalDivider] correspond to the field variables' string forms
     */
    @Override
    public String toString() {
        return String.format(
            "TableFormatter{intersection='%s', horizontalDivider='%s', verticalDivider='%s'}",
            intersection,
            horizontalDivider,
            verticalDivider
        );
    }
    
    /**
     * Returns true if the other object is equivalent
     *
     * @param other The object to compare to this {@link TableFormatter}
     * @return True if the other object is a {@link TableFormatter} with the same intersection, horizontalDivider, and verticalDivider
     */
    @Override
    public boolean equals(final Object other) {
        if(this == other) {
            return true;
        }
        if(!(other instanceof TableFormatter)) {
            return false;
        }
        final TableFormatter tableFormatter = (TableFormatter) other;
        return intersection.equals(tableFormatter.intersection)
            && horizontalDivider.equals(tableFormatter.horizontalDivider)
            && verticalDivider.equals(tableFormatter.verticalDivider);
    }
    
    /**
     * Returns the hashcode for this {@link TableFormatter}
     *
     * @return The hashcode for this {@link TableFormatter}
     */
    @Override
    public int hashCode() {
        return Objects.hash(intersection, horizontalDivider, verticalDivider);
    }
    
}
