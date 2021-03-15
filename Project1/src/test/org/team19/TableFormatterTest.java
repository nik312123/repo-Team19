/*
 * File name:
 * TableFormatterTest.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Tests the TableFormatter class
 */

package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.team19.TableFormatter.Alignment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

final class TableFormatterTest {
    
    private TableFormatterTest() {}
    
    //Date class made for testing
    static class Date {
        private final int year, month, day;
        
        private Date(final int year, final int month, final int day) {
            this.day = day;
            this.month = month;
            this.year = year;
        }
        
        @Override
        public String toString() {
            return String.format("%d/%02d/%02d", year, month, day);
        }
    }
    
    @Test
    void testObjColTableToStrRowTable() {
        final TableFormatter tableFormatter = new TableFormatter('+', '-', '|');
        
        try {
            final Method objColTableToStrRowTable = TableFormatter.class.getDeclaredMethod("objColTableToStrRowTable", List.class, List.class,
                int.class, int.class);
            objColTableToStrRowTable.setAccessible(true);
            
            //Creating arbitrary data table for testing
            final double avogadrosConstant = 6.02214 * Math.pow(10, 23);
            final double speedOfLight = 299792458;
            final double plancksConstant = 6.62607 * Math.pow(10, -34);
            final double earthGravity = 9.81;
            final double molarGasConstant = 0.0821;
            final List<String> randomTopics = List.of("Numbers", "Fruit", "Names", "Physics Constants");
            final List<List<Object>> randomTopicData = List.of(
                List.of(-3, -2, -1, 0, 1, 2, 3),
                List.of("Apple", "Banana", "Cantaloupe", "Date", "Eggplant", "Fig", "Guava"),
                List.of("Adam", "Ben", "Craig", "Dan", "Edward", "Fred", "Gary"),
                List.of(Math.PI, Math.E, avogadrosConstant, speedOfLight, plancksConstant, earthGravity, molarGasConstant)
            );
            
            Assertions.assertAll(
                //Testing for empty table
                () -> Assertions.assertEquals(
                    Collections.emptyList(),
                    objColTableToStrRowTable.invoke(tableFormatter, Collections.emptyList(), Collections.emptyList(), 0, 0)
                ),
                //Testing for single item table
                () -> Assertions.assertEquals(
                    List.of(Collections.singletonList("Name"), Collections.singletonList("Nikunj Chawla")),
                    objColTableToStrRowTable.invoke(
                        tableFormatter,
                        Collections.singletonList("Name"),
                        Collections.singletonList(Collections.singletonList("Nikunj Chawla")),
                        2,
                        1
                    )
                ),
                //Testing arbitrary tables
                () -> Assertions.assertEquals(
                    List.of(
                        List.of("Numbers", "Fruit", "Names", "Physics Constants"),
                        List.of("-3", "Apple", "Adam", Double.toString(Math.PI)),
                        List.of("-2", "Banana", "Ben", Double.toString(Math.E)),
                        List.of("-1", "Cantaloupe", "Craig", Double.toString(avogadrosConstant)),
                        List.of("0", "Date", "Dan", Double.toString(speedOfLight)),
                        List.of("1", "Eggplant", "Edward", Double.toString(plancksConstant)),
                        List.of("2", "Fig", "Fred", Double.toString(earthGravity)),
                        List.of("3", "Guava", "Gary", Double.toString(molarGasConstant))
                    ),
                    objColTableToStrRowTable.invoke(
                        tableFormatter,
                        randomTopics,
                        randomTopicData,
                        1 + randomTopicData.get(0).size(),
                        randomTopics.size()
                    )
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the objColTableToStrRowTable method from TableFormatter");
        }
    }
    
    @Test
    void testGetColumnLengths() {
        final TableFormatter tableFormatter = new TableFormatter('+', '-', '|');
        
        try {
            final Method getColumnLengths = TableFormatter.class.getDeclaredMethod("getColumnLengths", List.class, int.class);
            getColumnLengths.setAccessible(true);
            
            //Creating arbitrary data table for testing
            final double avogadrosConstant = 6.02214 * Math.pow(10, 23);
            final double speedOfLight = 299792458;
            final double plancksConstant = 6.62607 * Math.pow(10, -34);
            final double earthGravity = 9.81;
            final double molarGasConstant = 0.0821;
            final List<String> randomTopics = List.of("Numbers", "Fruit", "Names", "Physics Constants");
            
            Assertions.assertAll(
                //Testing for empty table
                () -> Assertions.assertArrayEquals(
                    new int[] {},
                    (int[]) getColumnLengths.invoke(tableFormatter, Collections.emptyList(), 0)
                ),
                //Testing for single item table
                () -> Assertions.assertArrayEquals(
                    new int[] {"Nikunj Chawla".length()},
                    (int[]) getColumnLengths.invoke(
                        tableFormatter,
                        List.of(Collections.singletonList("Name"), Collections.singletonList("Nikunj Chawla")),
                        1
                    )
                ),
                //Testing arbitrary tables
                () -> Assertions.assertArrayEquals(
                    new int[] {"Numbers".length(), "Cantaloupe".length(), "Edward".length(), Double.toString(Math.PI).length()},
                    (int[]) getColumnLengths.invoke(
                        tableFormatter,
                        List.of(
                            List.of("Numbers", "Fruit", "Names", "Physics Constants"),
                            List.of("-3", "Apple", "Adam", Double.toString(Math.PI)),
                            List.of("-2", "Banana", "Ben", Double.toString(Math.E)),
                            List.of("-1", "Cantaloupe", "Craig", Double.toString(avogadrosConstant)),
                            List.of("0", "Date", "Dan", Double.toString(speedOfLight)),
                            List.of("1", "Eggplant", "Edward", Double.toString(plancksConstant)),
                            List.of("2", "Fig", "Fred", Double.toString(earthGravity)),
                            List.of("3", "Guava", "Gary", Double.toString(molarGasConstant))
                        ),
                        randomTopics.size()
                    )
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getColumnLengths method from TableFormatter");
        }
    }
    
    @Test
    void testGetTableFormat() {
        final TableFormatter tableFormatter = new TableFormatter('+', '-', '|');
        
        try {
            final Method getTableFormat = TableFormatter.class.getDeclaredMethod("getTableFormat", List.class, int[].class, int.class);
            getTableFormat.setAccessible(true);
            
            Assertions.assertAll(
                //Testing for empty table
                () -> Assertions.assertEquals(
                    "|  |",
                    getTableFormat.invoke(tableFormatter, Collections.emptyList(), new int[] {}, 0)
                ),
                //Testing for a left-aligned single column
                () -> Assertions.assertEquals(
                    "| %-42s |",
                    getTableFormat.invoke(tableFormatter, Collections.singletonList(Alignment.LEFT), new int[] {42}, 1)
                ),
                //Testing for a right-aligned single column
                () -> Assertions.assertEquals(
                    "| %42s |",
                    getTableFormat.invoke(tableFormatter, Collections.singletonList(Alignment.RIGHT), new int[] {42}, 1)
                ),
                //Testing for five arbitrary columns with left and right aligning
                () -> Assertions.assertEquals(
                    "| %-42s | %24s | %-5s | %-2s | %3s |",
                    getTableFormat.invoke(
                        tableFormatter,
                        List.of(Alignment.LEFT, Alignment.RIGHT, Alignment.LEFT, Alignment.LEFT, Alignment.RIGHT),
                        new int[] {42, 24, 5, 2, 3},
                        5
                    )
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getTableFormat method from TableFormatter");
        }
    }
    
    @Test
    void testGetHorizontalDivider() {
        final TableFormatter tableFormatter = new TableFormatter('+', '-', '|');
        
        try {
            final Method getHorizontalDivider = TableFormatter.class.getDeclaredMethod("getHorizontalDivider", int[].class, int.class, String.class);
            getHorizontalDivider.setAccessible(true);
            
            Assertions.assertAll(
                //Testing for empty table
                () -> Assertions.assertEquals(
                    "|--|",
                    getHorizontalDivider.invoke(tableFormatter, new int[] {}, 0, "|")
                ),
                //Testing one zero-width column
                () -> Assertions.assertEquals(
                    "|--|",
                    getHorizontalDivider.invoke(tableFormatter, new int[] {0}, 1, "|")
                ),
                //Testing multiple zero-width columns
                () -> Assertions.assertEquals(
                    "|--+--+--|",
                    getHorizontalDivider.invoke(tableFormatter, new int[] {0, 0, 0}, 3, "|")
                ),
                //Testing for the failure of negative column widths
                () -> Assertions.assertEquals(
                    IllegalArgumentException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getHorizontalDivider.invoke(tableFormatter, new int[] {-1}, 1, "|")
                    ).getCause().getClass()
                ),
                //Arbitrary test
                () -> Assertions.assertEquals(
                    "|--+---+----+-----+------|",
                    getHorizontalDivider.invoke(tableFormatter, new int[] {0, 1, 2, 3, 4}, 5, "|")
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getHorizontalDivider method from TableFormatter");
        }
    }
    
    @Test
    void testFormatAsTable() {
        final TableFormatter tableFormatter = new TableFormatter('+', '-', '|');
        
        //Creating each of the test table suppliers
        
        //Testing NullPointerException thrown for null parameters
        final Supplier<String> nullHeader = () -> tableFormatter.formatAsTable(null, new ArrayList<>(), new ArrayList<>());
        final Supplier<String> nullTable = () -> tableFormatter.formatAsTable(new ArrayList<>(), null, new ArrayList<>());
        final Supplier<String> nullAlignments = () -> tableFormatter.formatAsTable(new ArrayList<>(), new ArrayList<>(), null);
        
        //Testing for empty table
        final Supplier<String> emptyTable = () ->
            tableFormatter.formatAsTable(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        
        //Testing for single-item table
        final Supplier<String> oneItem = () -> tableFormatter.formatAsTable(
            Collections.singletonList("Name"),
            Collections.singletonList(Collections.singletonList("Nikunj Chawla")),
            Collections.singletonList(Alignment.LEFT)
        );
        
        //Testing for incorrect differences in sizes between headers, columns, and alignments
        final Supplier<String> undersizedHeader = () -> tableFormatter.formatAsTable(
            Collections.singletonList("Hello"),
            List.of(
                List.of("a", "b"),
                List.of(1, 2)
            ),
            List.of(Alignment.RIGHT, Alignment.LEFT)
        );
        final Supplier<String> oversizedHeader = () -> tableFormatter.formatAsTable(
            List.of("Hello", "Goodbye", "1337"),
            List.of(
                List.of("a", "b"),
                List.of(1, 2)
            ),
            List.of(Alignment.RIGHT, Alignment.LEFT)
        );
        final Supplier<String> undersizedNumColumns = () -> tableFormatter.formatAsTable(
            List.of("Hello", "Goodbye"),
            Collections.singletonList(List.of("a", "b")),
            List.of(Alignment.RIGHT, Alignment.LEFT)
        );
        final Supplier<String> oversizedNumColumns = () -> tableFormatter.formatAsTable(
            List.of("Hello", "Goodbye"),
            List.of(
                List.of("a", "b"),
                List.of(1, 2),
                List.of(3.14159, 2.71828)
            ),
            List.of(Alignment.RIGHT, Alignment.LEFT)
        );
        final Supplier<String> undersizedAlignments = () -> tableFormatter.formatAsTable(
            List.of("Hello", "Goodbye"),
            List.of(
                List.of("a", "b"),
                List.of(1, 2)
            ),
            Collections.singletonList(Alignment.RIGHT)
        );
        final Supplier<String> oversizedAlignments = () -> tableFormatter.formatAsTable(
            List.of("Hello", "Goodbye"),
            List.of(
                List.of("a", "b"),
                List.of(1, 2)
            ),
            List.of(Alignment.RIGHT, Alignment.LEFT, Alignment.LEFT)
        );
        
        //Creating arbitrary data table for testing
        final double avogadrosConstant = 6.02214 * Math.pow(10, 23);
        final double speedOfLight = 299792458;
        final double plancksConstant = 6.62607 * Math.pow(10, -34);
        final double earthGravity = 9.81;
        final double molarGasConstant = 0.0821;
        final List<String> randomTopics = List.of("Numbers", "Fruit", "Names", "Physics Constants");
        final List<List<?>> randomTopicData = List.of(
            List.of(-3, -2, -1, 0, 1, 2, 3),
            List.of("Apple", "Banana", "Cantaloupe", "Date", "Eggplant", "Fig", "Guava"),
            List.of("Adam", "Ben", "Craig", "Dan", "Edward", "Fred", "Gary"),
            List.of(Math.PI, Math.E, avogadrosConstant, speedOfLight, plancksConstant, earthGravity, molarGasConstant)
        );
        
        //Testing left alignment with arbitrary data
        final Supplier<String> allLeft = () -> tableFormatter.formatAsTable(
            randomTopics,
            randomTopicData,
            List.of(Alignment.LEFT, Alignment.LEFT, Alignment.LEFT, Alignment.LEFT)
        );
        
        //Testing right alignment with arbitrary data
        final Supplier<String> allRight = () -> tableFormatter.formatAsTable(
            randomTopics,
            randomTopicData,
            List.of(Alignment.RIGHT, Alignment.RIGHT, Alignment.RIGHT, Alignment.RIGHT)
        );
        
        //Testing left and right alignments with arbitrary data
        final Supplier<String> alternatingLeftRight = () -> tableFormatter.formatAsTable(
            randomTopics,
            randomTopicData,
            List.of(Alignment.LEFT, Alignment.RIGHT, Alignment.LEFT, Alignment.RIGHT)
        );
        
        //Testing table with alternate symbols
        final TableFormatter tableFormatter2 = new TableFormatter('┼', '─', '│');
        
        final Supplier<String> randomCandidateData = () -> tableFormatter2.formatAsTable(
            List.of("Name", "Party", "Age", "Sex", "DOB"),
            List.of(
                List.of("Joseph Biden", "Kamala Harris", "Donald Trump", "Michael Pence"),
                List.of("Democrat", "Democrat", "Republican", "Republican"),
                List.of(78, 56, 74, 61),
                List.of('M', 'F', 'M', 'M'),
                List.of(
                    new Date(1942, 11, 20),
                    new Date(1964, 10, 20),
                    new Date(1946, 6, 14),
                    new Date(1959, 6, 7)
                )
            ),
            List.of(Alignment.LEFT, Alignment.LEFT, Alignment.RIGHT, Alignment.LEFT, Alignment.RIGHT)
        );
        
        Assertions.assertAll(
            //Testing NullPointerException thrown for null parameters
            () -> Assertions.assertThrows(NullPointerException.class, nullHeader::get),
            () -> Assertions.assertThrows(NullPointerException.class, nullTable::get),
            () -> Assertions.assertThrows(NullPointerException.class, nullAlignments::get),
            
            //Testing for empty table
            () -> Assertions.assertEquals(
                "+--+\n"
                    + "+--+",
                emptyTable.get()
            ),
            
            //Testing for single-item table
            () -> Assertions.assertEquals(
                "+---------------+\n"
                    + "| Name          |\n"
                    + "|---------------|\n"
                    + "| Nikunj Chawla |\n"
                    + "+---------------+",
                oneItem.get()
            ),
            
            //Testing for incorrect differences in sizes between headers, columns, and alignments
            () -> Assertions.assertThrows(IllegalArgumentException.class, undersizedHeader::get),
            () -> Assertions.assertThrows(IllegalArgumentException.class, oversizedHeader::get),
            () -> Assertions.assertThrows(IllegalArgumentException.class, undersizedNumColumns::get),
            () -> Assertions.assertThrows(IllegalArgumentException.class, oversizedNumColumns::get),
            () -> Assertions.assertThrows(IllegalArgumentException.class, undersizedAlignments::get),
            () -> Assertions.assertThrows(IllegalArgumentException.class, oversizedAlignments::get),
            
            //Testing left alignment with arbitrary data
            () -> Assertions.assertEquals(
                "+---------+------------+--------+-------------------+\n"
                    + "| Numbers | Fruit      | Names  | Physics Constants |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| -3      | Apple      | Adam   | 3.141592653589793 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| -2      | Banana     | Ben    | 2.718281828459045 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| -1      | Cantaloupe | Craig  | 6.02214E23        |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| 0       | Date       | Dan    | 2.99792458E8      |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| 1       | Eggplant   | Edward | 6.62607E-34       |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| 2       | Fig        | Fred   | 9.81              |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| 3       | Guava      | Gary   | 0.0821            |\n"
                    + "+---------+------------+--------+-------------------+",
                allLeft.get()
            ),
            
            //Testing right alignment with arbitrary data
            () -> Assertions.assertEquals(
                "+---------+------------+--------+-------------------+\n"
                    + "| Numbers |      Fruit |  Names | Physics Constants |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|      -3 |      Apple |   Adam | 3.141592653589793 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|      -2 |     Banana |    Ben | 2.718281828459045 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|      -1 | Cantaloupe |  Craig |        6.02214E23 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|       0 |       Date |    Dan |      2.99792458E8 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|       1 |   Eggplant | Edward |       6.62607E-34 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|       2 |        Fig |   Fred |              9.81 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|       3 |      Guava |   Gary |            0.0821 |\n"
                    + "+---------+------------+--------+-------------------+",
                allRight.get()
            ),
            
            //Testing left and right alignment with arbitrary data
            () -> Assertions.assertEquals(
                "+---------+------------+--------+-------------------+\n"
                    + "| Numbers |      Fruit | Names  | Physics Constants |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| -3      |      Apple | Adam   | 3.141592653589793 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| -2      |     Banana | Ben    | 2.718281828459045 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| -1      | Cantaloupe | Craig  |        6.02214E23 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| 0       |       Date | Dan    |      2.99792458E8 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| 1       |   Eggplant | Edward |       6.62607E-34 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| 2       |        Fig | Fred   |              9.81 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "| 3       |      Guava | Gary   |            0.0821 |\n"
                    + "+---------+------------+--------+-------------------+",
                alternatingLeftRight.get()
            ),
            
            //Testing table with alternate symbols
            () -> Assertions.assertEquals(
                "┼───────────────┼────────────┼─────┼─────┼────────────┼\n"
                    + "│ Name          │ Party      │ Age │ Sex │        DOB │\n"
                    + "│───────────────┼────────────┼─────┼─────┼────────────│\n"
                    + "│ Joseph Biden  │ Democrat   │  78 │ M   │ 1942/11/20 │\n"
                    + "│───────────────┼────────────┼─────┼─────┼────────────│\n"
                    + "│ Kamala Harris │ Democrat   │  56 │ F   │ 1964/10/20 │\n"
                    + "│───────────────┼────────────┼─────┼─────┼────────────│\n"
                    + "│ Donald Trump  │ Republican │  74 │ M   │ 1946/06/14 │\n"
                    + "│───────────────┼────────────┼─────┼─────┼────────────│\n"
                    + "│ Michael Pence │ Republican │  61 │ M   │ 1959/06/07 │\n"
                    + "┼───────────────┼────────────┼─────┼─────┼────────────┼",
                randomCandidateData.get()
            )
        );
    }
    
}
