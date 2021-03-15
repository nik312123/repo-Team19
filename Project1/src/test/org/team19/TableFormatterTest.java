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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

final class TableFormatterTest {
    
    private TableFormatterTest() {}
    
    //Date class made for testing
    final static class Date {
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
        
        //Creating arbitrary data table for testing
        final List<String> randomTopics = List.of("Numbers", "Fruit", "Names");
        final List<List<?>> randomTopicData = List.of(
            List.of(-3, -2, -1, 0, 1, 2, 3),
            List.of("Apple", "Banana", "Cantaloupe", "Date", "Eggplant", "Fig", "Guava"),
            List.of("Adam", "Ben", "Craig", "Dan", "Edward", "Fred", "Gary")
        );
        
        Assertions.assertAll(
            //Testing for empty table
            () -> Assertions.assertEquals(
                Collections.emptyList(),
                tableFormatter.objColTableToStrRowTable(Collections.emptyList(), Collections.emptyList(), 0, 0)
            ),
            //Testing for single item table
            () -> Assertions.assertEquals(
                List.of(Collections.singletonList("Candidates"), Collections.singletonList("Nikunj (Test)")),
                tableFormatter.objColTableToStrRowTable(
                    Collections.singletonList("Candidates"),
                    Collections.singletonList(Collections.singletonList(new Candidate("Nikunj", "Test"))),
                    2,
                    1
                )
            ),
            //Testing arbitrary tables
            () -> Assertions.assertEquals(
                List.of(
                    List.of("Numbers", "Fruit", "Names"),
                    List.of("-3", "Apple", "Adam"),
                    List.of("-2", "Banana", "Ben"),
                    List.of("-1", "Cantaloupe", "Craig"),
                    List.of("0", "Date", "Dan"),
                    List.of("1", "Eggplant", "Edward"),
                    List.of("2", "Fig", "Fred"),
                    List.of("3", "Guava", "Gary")
                ),
                tableFormatter.objColTableToStrRowTable(
                    randomTopics,
                    randomTopicData,
                    1 + randomTopicData.get(0).size(),
                    randomTopics.size()
                )
            )
        );
    }
    
    @Test
    void testGetColumnLengths() {
        final TableFormatter tableFormatter = new TableFormatter('+', '-', '|');
        
        //Creating arbitrary data table for testing
        final List<String> randomTopics = List.of("Numbers", "Fruit", "Names");
        
        Assertions.assertAll(
            //Testing for empty table
            () -> Assertions.assertArrayEquals(
                new int[] {},
                tableFormatter.getColumnLengths(Collections.emptyList(), 0)
            ),
            //Testing for single item table
            () -> Assertions.assertArrayEquals(
                new int[] {"Nikunj Chawla".length()},
                tableFormatter.getColumnLengths(
                    List.of(Collections.singletonList("Name"), Collections.singletonList("Nikunj Chawla")),
                    1
                )
            ),
            //Testing arbitrary table
            () -> Assertions.assertArrayEquals(
                new int[] {"Numbers".length(), "Cantaloupe".length(), "Edward".length()},
                tableFormatter.getColumnLengths(
                    List.of(
                        List.of("Numbers", "Fruit", "Names"),
                        List.of("-3", "Apple", "Adam"),
                        List.of("-2", "Banana", "Ben"),
                        List.of("-1", "Cantaloupe", "Craig"),
                        List.of("0", "Date", "Dan"),
                        List.of("1", "Eggplant", "Edward"),
                        List.of("2", "Fig", "Fred"),
                        List.of("3", "Guava", "Gary")
                    ),
                    randomTopics.size()
                )
            )
        );
    }
    
    @Test
    void testGetTableFormat() {
        final TableFormatter tableFormatter = new TableFormatter('+', '-', '|');
        
        Assertions.assertAll(
            //Testing for empty table
            () -> Assertions.assertEquals(
                "|  |",
                tableFormatter.getTableFormat(Collections.emptyList(), new int[] {}, 0)
            ),
            //Testing for a left-aligned single column
            () -> Assertions.assertEquals(
                "| %-42s |",
                tableFormatter.getTableFormat(Collections.singletonList(Alignment.LEFT), new int[] {42}, 1)
            ),
            //Testing for a right-aligned single column
            () -> Assertions.assertEquals(
                "| %42s |",
                tableFormatter.getTableFormat(Collections.singletonList(Alignment.RIGHT), new int[] {42}, 1)
            ),
            //Testing for five arbitrary columns with left and right aligning
            () -> Assertions.assertEquals(
                "| %-42s | %24s | %-5s | %-2s | %3s |",
                tableFormatter.getTableFormat(
                    List.of(Alignment.LEFT, Alignment.RIGHT, Alignment.LEFT, Alignment.LEFT, Alignment.RIGHT),
                    new int[] {42, 24, 5, 2, 3},
                    5
                )
            )
        );
    }
    
    @Test
    void testGetHorizontalDivider() {
        final TableFormatter tableFormatter = new TableFormatter('+', '-', '|');
        
        Assertions.assertAll(
            //Testing for empty table
            () -> Assertions.assertEquals(
                "|--|",
                tableFormatter.getHorizontalDivider(new int[] {}, 0, "|")
            ),
            //Testing one zero-width column
            () -> Assertions.assertEquals(
                "|--|",
                tableFormatter.getHorizontalDivider(new int[] {0}, 1, "|")
            ),
            //Testing multiple zero-width columns
            () -> Assertions.assertEquals(
                "|--+--+--|",
                tableFormatter.getHorizontalDivider(new int[] {0, 0, 0}, 3, "|")
            ),
            //Testing for the failure of negative column widths
            () -> Assertions.assertThrows(IllegalArgumentException.class, () ->
                tableFormatter.getHorizontalDivider(new int[] {-1}, 1, "|")
            ),
            //Arbitrary column sizes test
            () -> Assertions.assertEquals(
                "|--+---+----+-----+------|",
                tableFormatter.getHorizontalDivider(new int[] {0, 1, 2, 3, 4}, 5, "|")
            )
        );
    }
    
    @Test
    void testFormatAsTable() {
        final TableFormatter tableFormatter = new TableFormatter('+', '-', '|');
        
        //Creating each of the test table suppliers
        
        //Testing NullPointerException thrown for null parameters
        final Supplier<String> nullHeader = () -> tableFormatter.formatAsTable(null, new ArrayList<>(), new ArrayList<>());
        final Supplier<String> nullTable = () -> tableFormatter.formatAsTable(new ArrayList<>(), null, new ArrayList<>());
        final Supplier<String> nullAlignments = () -> tableFormatter.formatAsTable(new ArrayList<>(), new ArrayList<>(), null);
        
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
        final List<String> randomTopics = List.of("Numbers", "Fruit", "Names");
        final List<List<?>> randomTopicData = List.of(
            List.of(-3, -2, -1, 0, 1, 2, 3),
            List.of("Apple", "Banana", "Cantaloupe", "Date", "Eggplant", "Fig", "Guava"),
            List.of("Adam", "Ben", "Craig", "Dan", "Edward", "Fred", "Gary")
        );
        
        //Testing left alignment with arbitrary data
        final Supplier<String> allLeft = () -> tableFormatter.formatAsTable(
            randomTopics,
            randomTopicData,
            List.of(Alignment.LEFT, Alignment.LEFT, Alignment.LEFT)
        );
        
        //Testing right alignment with arbitrary data
        final Supplier<String> allRight = () -> tableFormatter.formatAsTable(
            randomTopics,
            randomTopicData,
            List.of(Alignment.RIGHT, Alignment.RIGHT, Alignment.RIGHT)
        );
        
        //Testing left and right alignments with arbitrary data
        final Supplier<String> alternatingLeftRight = () -> tableFormatter.formatAsTable(
            randomTopics,
            randomTopicData,
            List.of(Alignment.LEFT, Alignment.RIGHT, Alignment.LEFT)
        );
        
        //Testing table with alternate symbols
        final TableFormatter tableFormatter2 = new TableFormatter('┼', '─', '│');
        
        final Supplier<String> randomCandidateData = () -> tableFormatter2.formatAsTable(
            randomTopics,
            randomTopicData,
            List.of(Alignment.RIGHT, Alignment.LEFT, Alignment.LEFT)
        );
        
        //Testing for empty table
        final Supplier<String> emptyTable = () ->
            tableFormatter.formatAsTable(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        
        //Testing for single-item table
        final Supplier<String> oneItem = () -> tableFormatter.formatAsTable(
            Collections.singletonList("Name"),
            Collections.singletonList(Collections.singletonList("Nikunj Chawla")),
            Collections.singletonList(Alignment.LEFT)
        );
        
        Assertions.assertAll(
            //Testing NullPointerException thrown for null parameters
            () -> Assertions.assertThrows(NullPointerException.class, nullHeader::get),
            () -> Assertions.assertThrows(NullPointerException.class, nullTable::get),
            () -> Assertions.assertThrows(NullPointerException.class, nullAlignments::get),
            
            //Testing for incorrect differences in sizes between headers, columns, and alignments
            () -> Assertions.assertThrows(IllegalArgumentException.class, undersizedHeader::get),
            () -> Assertions.assertThrows(IllegalArgumentException.class, oversizedHeader::get),
            () -> Assertions.assertThrows(IllegalArgumentException.class, undersizedNumColumns::get),
            () -> Assertions.assertThrows(IllegalArgumentException.class, oversizedNumColumns::get),
            () -> Assertions.assertThrows(IllegalArgumentException.class, undersizedAlignments::get),
            () -> Assertions.assertThrows(IllegalArgumentException.class, oversizedAlignments::get),
            
            //Testing left alignment with arbitrary data
            () -> Assertions.assertEquals(
                "+---------+------------+--------+\n"
                    + "| Numbers | Fruit      | Names  |\n"
                    + "|---------+------------+--------|\n"
                    + "| -3      | Apple      | Adam   |\n"
                    + "|---------+------------+--------|\n"
                    + "| -2      | Banana     | Ben    |\n"
                    + "|---------+------------+--------|\n"
                    + "| -1      | Cantaloupe | Craig  |\n"
                    + "|---------+------------+--------|\n"
                    + "| 0       | Date       | Dan    |\n"
                    + "|---------+------------+--------|\n"
                    + "| 1       | Eggplant   | Edward |\n"
                    + "|---------+------------+--------|\n"
                    + "| 2       | Fig        | Fred   |\n"
                    + "|---------+------------+--------|\n"
                    + "| 3       | Guava      | Gary   |\n"
                    + "+---------+------------+--------+",
                allLeft.get()
            ),
            
            //Testing right alignment with arbitrary data
            () -> Assertions.assertEquals(
                "+---------+------------+--------+\n"
                    + "| Numbers |      Fruit |  Names |\n"
                    + "|---------+------------+--------|\n"
                    + "|      -3 |      Apple |   Adam |\n"
                    + "|---------+------------+--------|\n"
                    + "|      -2 |     Banana |    Ben |\n"
                    + "|---------+------------+--------|\n"
                    + "|      -1 | Cantaloupe |  Craig |\n"
                    + "|---------+------------+--------|\n"
                    + "|       0 |       Date |    Dan |\n"
                    + "|---------+------------+--------|\n"
                    + "|       1 |   Eggplant | Edward |\n"
                    + "|---------+------------+--------|\n"
                    + "|       2 |        Fig |   Fred |\n"
                    + "|---------+------------+--------|\n"
                    + "|       3 |      Guava |   Gary |\n"
                    + "+---------+------------+--------+",
                allRight.get()
            ),
            
            //Testing left and right alignment with arbitrary data
            () -> Assertions.assertEquals(
                "+---------+------------+--------+\n"
                    + "| Numbers |      Fruit | Names  |\n"
                    + "|---------+------------+--------|\n"
                    + "| -3      |      Apple | Adam   |\n"
                    + "|---------+------------+--------|\n"
                    + "| -2      |     Banana | Ben    |\n"
                    + "|---------+------------+--------|\n"
                    + "| -1      | Cantaloupe | Craig  |\n"
                    + "|---------+------------+--------|\n"
                    + "| 0       |       Date | Dan    |\n"
                    + "|---------+------------+--------|\n"
                    + "| 1       |   Eggplant | Edward |\n"
                    + "|---------+------------+--------|\n"
                    + "| 2       |        Fig | Fred   |\n"
                    + "|---------+------------+--------|\n"
                    + "| 3       |      Guava | Gary   |\n"
                    + "+---------+------------+--------+",
                alternatingLeftRight.get()
            ),
            
            //Testing table with alternate symbols
            () -> Assertions.assertEquals(
                "┼─────────┼────────────┼────────┼\n"
                    + "│ Numbers │ Fruit      │ Names  │\n"
                    + "│─────────┼────────────┼────────│\n"
                    + "│      -3 │ Apple      │ Adam   │\n"
                    + "│─────────┼────────────┼────────│\n"
                    + "│      -2 │ Banana     │ Ben    │\n"
                    + "│─────────┼────────────┼────────│\n"
                    + "│      -1 │ Cantaloupe │ Craig  │\n"
                    + "│─────────┼────────────┼────────│\n"
                    + "│       0 │ Date       │ Dan    │\n"
                    + "│─────────┼────────────┼────────│\n"
                    + "│       1 │ Eggplant   │ Edward │\n"
                    + "│─────────┼────────────┼────────│\n"
                    + "│       2 │ Fig        │ Fred   │\n"
                    + "│─────────┼────────────┼────────│\n"
                    + "│       3 │ Guava      │ Gary   │\n"
                    + "┼─────────┼────────────┼────────┼",
                randomCandidateData.get()
            ),
            
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
            )
        );
    }
    
}
