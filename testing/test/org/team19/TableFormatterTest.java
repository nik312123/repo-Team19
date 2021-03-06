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
            final List<String> randomTopics = Arrays.asList("Numbers", "Fruit", "Names", "Physics Constants");
            final List<List<Object>> randomTopicData = Arrays.asList(
                Arrays.asList(-3, -2, -1, 0, 1, 2, 3),
                Arrays.asList("Apple", "Banana", "Cantaloupe", "Date", "Eggplant", "Fig", "Guava"),
                Arrays.asList("Adam", "Ben", "Craig", "Dan", "Edward", "Fred", "Gary"),
                Arrays.asList(Math.PI, Math.E, avogadrosConstant, speedOfLight, plancksConstant, earthGravity, molarGasConstant)
            );
            
            //Creating random candidate table for testing
            final List<String> randomCandidateHeader = Arrays.asList("Name", "Party", "Age", "Sex", "DOB");
            final List<List<Object>> randomCandidateData = Arrays.asList(
                Arrays.asList("Joseph Biden", "Kamala Harris", "Donald Trump", "Michael Pence"),
                Arrays.asList("Democrat", "Democrat", "Republican", "Republican"),
                Arrays.asList(78, 56, 74, 61),
                Arrays.asList('M', 'F', 'M', 'M'),
                Arrays.asList(
                    new Date(1942, 11, 20),
                    new Date(1964, 10, 20),
                    new Date(1946, 6, 14),
                    new Date(1959, 6, 7)
                )
            );
            
            Assertions.assertAll(
                //Testing for empty table
                () -> Assertions.assertEquals(
                    Collections.emptyList(),
                    objColTableToStrRowTable.invoke(tableFormatter, Collections.emptyList(), Collections.emptyList(), 0, 0)
                ),
                //Testing for single item table
                () -> Assertions.assertEquals(
                    Arrays.asList(Collections.singletonList("Name"), Collections.singletonList("Nikunj Chawla")),
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
                    Arrays.asList(
                        Arrays.asList("Numbers", "Fruit", "Names", "Physics Constants"),
                        Arrays.asList("-3", "Apple", "Adam", Double.toString(Math.PI)),
                        Arrays.asList("-2", "Banana", "Ben", Double.toString(Math.E)),
                        Arrays.asList("-1", "Cantaloupe", "Craig", Double.toString(avogadrosConstant)),
                        Arrays.asList("0", "Date", "Dan", Double.toString(speedOfLight)),
                        Arrays.asList("1", "Eggplant", "Edward", Double.toString(plancksConstant)),
                        Arrays.asList("2", "Fig", "Fred", Double.toString(earthGravity)),
                        Arrays.asList("3", "Guava", "Gary", Double.toString(molarGasConstant))
                    ),
                    objColTableToStrRowTable.invoke(
                        tableFormatter,
                        randomTopics,
                        randomTopicData,
                        1 + randomTopicData.get(0).size(),
                        randomTopics.size()
                    )
                ),
                () -> Assertions.assertEquals(
                    Arrays.asList(
                        Arrays.asList("Name", "Party", "Age", "Sex", "DOB"),
                        Arrays.asList("Joseph Biden", "Democrat", "78", "M", "1942/11/20"),
                        Arrays.asList("Kamala Harris", "Democrat", "56", "F", "1964/10/20"),
                        Arrays.asList("Donald Trump", "Republican", "74", "M", "1946/06/14"),
                        Arrays.asList("Michael Pence", "Republican", "61", "M", "1959/06/07")
                    ),
                    objColTableToStrRowTable.invoke(
                        tableFormatter,
                        randomCandidateHeader,
                        randomCandidateData,
                        1 + randomCandidateData.get(0).size(),
                        randomCandidateData.size()
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
            final List<String> randomTopics = Arrays.asList("Numbers", "Fruit", "Names", "Physics Constants");
            
            //Creating random candidate table for testing
            final List<String> randomCandidateHeader = Arrays.asList("Name", "Party", "Age", "Sex", "DOB");
            
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
                        Arrays.asList(Collections.singletonList("Name"), Collections.singletonList("Nikunj Chawla")),
                        1
                    )
                ),
                //Testing arbitrary tables
                () -> Assertions.assertArrayEquals(
                    new int[] {"Numbers".length(), "Cantaloupe".length(), "Edward".length(), Double.toString(Math.PI).length()},
                    (int[]) getColumnLengths.invoke(
                        tableFormatter,
                        Arrays.asList(
                            Arrays.asList("Numbers", "Fruit", "Names", "Physics Constants"),
                            Arrays.asList("-3", "Apple", "Adam", Double.toString(Math.PI)),
                            Arrays.asList("-2", "Banana", "Ben", Double.toString(Math.E)),
                            Arrays.asList("-1", "Cantaloupe", "Craig", Double.toString(avogadrosConstant)),
                            Arrays.asList("0", "Date", "Dan", Double.toString(speedOfLight)),
                            Arrays.asList("1", "Eggplant", "Edward", Double.toString(plancksConstant)),
                            Arrays.asList("2", "Fig", "Fred", Double.toString(earthGravity)),
                            Arrays.asList("3", "Guava", "Gary", Double.toString(molarGasConstant))
                        ),
                        randomTopics.size()
                    )
                ),
                () -> Assertions.assertArrayEquals(
                    new int[] {"Kamala Harris".length(), "Republican".length(), "Age".length(), "Sex".length(), "1942/11/20".length()},
                    (int[]) getColumnLengths.invoke(
                        tableFormatter,
                        Arrays.asList(
                            Arrays.asList("Name", "Party", "Age", "Sex", "DOB"),
                            Arrays.asList("Joseph Biden", "Democrat", "78", "M", "1942/11/20"),
                            Arrays.asList("Kamala Harris", "Democrat", "56", "F", "1964/10/20"),
                            Arrays.asList("Donald Trump", "Republican", "74", "M", "1946/06/14"),
                            Arrays.asList("Michael Pence", "Republican", "61", "M", "1959/06/07")
                        ),
                        randomCandidateHeader.size()
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
                //Testing for two columns with left and right aligning
                () -> Assertions.assertEquals(
                    "| %-42s | %24s |",
                    getTableFormat.invoke(tableFormatter, Arrays.asList(Alignment.LEFT, Alignment.RIGHT), new int[] {42, 24}, 2)
                ),
                //Testing for five columns with left and right aligning
                () -> Assertions.assertEquals(
                    "| %-42s | %24s | %-5s | %-2s | %3s |",
                    getTableFormat.invoke(
                        tableFormatter,
                        Arrays.asList(Alignment.LEFT, Alignment.RIGHT, Alignment.LEFT, Alignment.LEFT, Alignment.RIGHT),
                        new int[] {42, 24, 5, 2, 3},
                        5
                    )
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getColumnLengths method from TableFormatter");
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
                () -> Assertions.assertEquals(
                    IllegalArgumentException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getHorizontalDivider.invoke(tableFormatter, new int[] {5, 4, 3, -1}, 4, "|")
                    ).getCause().getClass()
                ),
                () -> Assertions.assertEquals(
                    IllegalArgumentException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getHorizontalDivider.invoke(tableFormatter, new int[] {5, 4, -1, 3}, 4, "|")
                    ).getCause().getClass()
                ),
                () -> Assertions.assertEquals(
                    IllegalArgumentException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getHorizontalDivider.invoke(tableFormatter, new int[] {-1, 5, 4, 3}, 4, "|")
                    ).getCause().getClass()
                ),
                //Arbitrary tests
                () -> Assertions.assertEquals(
                    "|---|",
                    getHorizontalDivider.invoke(tableFormatter, new int[] {1}, 1, "|")
                ),
                () -> Assertions.assertEquals(
                    "|--+---+----+-----+------|",
                    getHorizontalDivider.invoke(tableFormatter, new int[] {0, 1, 2, 3, 4}, 5, "|")
                ),
                () -> Assertions.assertEquals(
                    "|------+-----+----+---+--|",
                    getHorizontalDivider.invoke(tableFormatter, new int[] {4, 3, 2, 1, 0}, 5, "|")
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getColumnLengths method from TableFormatter");
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
            Arrays.asList(
                Arrays.asList("a", "b"),
                Arrays.asList(1, 2)
            ),
            Arrays.asList(Alignment.RIGHT, Alignment.LEFT)
        );
        final Supplier<String> oversizedHeader = () -> tableFormatter.formatAsTable(
            Arrays.asList("Hello", "Goodbye", "1337"),
            Arrays.asList(
                Arrays.asList("a", "b"),
                Arrays.asList(1, 2)
            ),
            Arrays.asList(Alignment.RIGHT, Alignment.LEFT)
        );
        final Supplier<String> undersizedNumColumns = () -> tableFormatter.formatAsTable(
            Arrays.asList("Hello", "Goodbye"),
            Collections.singletonList(Arrays.asList("a", "b")),
            Arrays.asList(Alignment.RIGHT, Alignment.LEFT)
        );
        final Supplier<String> oversizedNumColumns = () -> tableFormatter.formatAsTable(
            Arrays.asList("Hello", "Goodbye"),
            Arrays.asList(
                Arrays.asList("a", "b"),
                Arrays.asList(1, 2),
                Arrays.asList(3.14159, 2.71828)
            ),
            Arrays.asList(Alignment.RIGHT, Alignment.LEFT)
        );
        final Supplier<String> undersizedAlignments = () -> tableFormatter.formatAsTable(
            Arrays.asList("Hello", "Goodbye"),
            Arrays.asList(
                Arrays.asList("a", "b"),
                Arrays.asList(1, 2)
            ),
            Collections.singletonList(Alignment.RIGHT)
        );
        final Supplier<String> oversizedAlignments = () -> tableFormatter.formatAsTable(
            Arrays.asList("Hello", "Goodbye"),
            Arrays.asList(
                Arrays.asList("a", "b"),
                Arrays.asList(1, 2)
            ),
            Arrays.asList(Alignment.RIGHT, Alignment.LEFT, Alignment.LEFT)
        );
        
        //Creating arbitrary data table for testing
        final double avogadrosConstant = 6.02214 * Math.pow(10, 23);
        final double speedOfLight = 299792458;
        final double plancksConstant = 6.62607 * Math.pow(10, -34);
        final double earthGravity = 9.81;
        final double molarGasConstant = 0.0821;
        final List<String> randomTopics = Arrays.asList("Numbers", "Fruit", "Names", "Physics Constants");
        final List<List<Object>> randomTopicData = Arrays.asList(
            Arrays.asList(-3, -2, -1, 0, 1, 2, 3),
            Arrays.asList("Apple", "Banana", "Cantaloupe", "Date", "Eggplant", "Fig", "Guava"),
            Arrays.asList("Adam", "Ben", "Craig", "Dan", "Edward", "Fred", "Gary"),
            Arrays.asList(Math.PI, Math.E, avogadrosConstant, speedOfLight, plancksConstant, earthGravity, molarGasConstant)
        );
        
        //Testing left alignment with arbitrary data
        final Supplier<String> allLeft = () -> tableFormatter.formatAsTable(
            randomTopics,
            randomTopicData,
            Arrays.asList(Alignment.LEFT, Alignment.LEFT, Alignment.LEFT, Alignment.LEFT)
        );
        
        //Testing right alignment with arbitrary data
        final Supplier<String> allRight = () -> tableFormatter.formatAsTable(
            randomTopics,
            randomTopicData,
            Arrays.asList(Alignment.RIGHT, Alignment.RIGHT, Alignment.RIGHT, Alignment.RIGHT)
        );
        
        //Testing left/right alignment with arbitrary data
        final Supplier<String> alternatingLeftRight = () -> tableFormatter.formatAsTable(
            randomTopics,
            randomTopicData,
            Arrays.asList(Alignment.LEFT, Alignment.RIGHT, Alignment.LEFT, Alignment.RIGHT)
        );
        
        //Testing right/left alignment with arbitrary data
        final Supplier<String> alternatingRightLeft = () -> tableFormatter.formatAsTable(
            randomTopics,
            randomTopicData,
            Arrays.asList(Alignment.RIGHT, Alignment.LEFT, Alignment.RIGHT, Alignment.LEFT)
        );
        
        //Testing table with alternate symbols
        final TableFormatter tableFormatter2 = new TableFormatter('┼', '─', '│');
        
        final Supplier<String> randomCandidateData = () -> tableFormatter2.formatAsTable(
            Arrays.asList("Name", "Party", "Age", "Sex", "DOB"),
            Arrays.asList(
                Arrays.asList("Joseph Biden", "Kamala Harris", "Donald Trump", "Michael Pence"),
                Arrays.asList("Democrat", "Democrat", "Republican", "Republican"),
                Arrays.asList(78, 56, 74, 61),
                Arrays.asList('M', 'F', 'M', 'M'),
                Arrays.asList(
                    new Date(1942, 11, 20),
                    new Date(1964, 10, 20),
                    new Date(1946, 6, 14),
                    new Date(1959, 6, 7)
                )
            ),
            Arrays.asList(Alignment.LEFT, Alignment.LEFT, Alignment.RIGHT, Alignment.LEFT, Alignment.RIGHT)
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
            
            //Testing left/right alignment with arbitrary data
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
            
            //Testing right/left alignment with arbitrary data
            () -> Assertions.assertEquals(
                "+---------+------------+--------+-------------------+\n"
                    + "| Numbers | Fruit      |  Names | Physics Constants |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|      -3 | Apple      |   Adam | 3.141592653589793 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|      -2 | Banana     |    Ben | 2.718281828459045 |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|      -1 | Cantaloupe |  Craig | 6.02214E23        |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|       0 | Date       |    Dan | 2.99792458E8      |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|       1 | Eggplant   | Edward | 6.62607E-34       |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|       2 | Fig        |   Fred | 9.81              |\n"
                    + "|---------+------------+--------+-------------------|\n"
                    + "|       3 | Guava      |   Gary | 0.0821            |\n"
                    + "+---------+------------+--------+-------------------+",
                alternatingRightLeft.get()
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
