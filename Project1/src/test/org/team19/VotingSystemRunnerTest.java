/*
 * File name:
 * VotingSystemRunnerTest.java
 *
 * Author:
 * Nikunj Chawla
 *
 * Purpose:
 * Tests the VotingSystemRunner class
 */

package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

final class VotingSystemRunnerTest {
    
    private VotingSystemRunnerTest() {}
    
    @Test
    void testGetFullFilePath() {
        try {
            final String projectRootDir = new File(".").getCanonicalPath();
            final String userHomeDir = System.getProperty("user.home");
            
            final Method getFullFilePath = VotingSystemRunner.class.getDeclaredMethod("getFullFilePath", String.class);
            getFullFilePath.setAccessible(true);
            
            final char fileSep = File.separatorChar;
            
            Assertions.assertAll(
                //Testing path relative to project directory
                () -> Assertions.assertEquals(
                    String.format("%s/test.txt".replace('/', fileSep), projectRootDir), getFullFilePath.invoke(VotingSystemRunner.class, "test.txt")
                ),
                //Testing path relative to home directory
                () -> Assertions.assertEquals(
                    String.format("%s/test.txt".replace('/', fileSep), userHomeDir),
                    getFullFilePath.invoke(VotingSystemRunner.class, "~/test.txt".replace('/', fileSep))
                ),
                //Testing absolute path
                () -> Assertions.assertEquals(
                    String.format("%s/Desktop/test.txt".replace('/', fileSep), userHomeDir),
                    getFullFilePath.invoke(VotingSystemRunner.class, String.format("%s/Desktop/test.txt".replace('/', fileSep), userHomeDir))
                )
            );
        }
        catch(IOException e) {
            Assertions.fail("Unable to retrieve the path for the project root directory");
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getFullFilePath method from VotingSystemRunner");
        }
    }
    
    @Test
    void testGetFileInputStream() {
        final Method getFileInputStream;
        try {
            getFileInputStream = VotingSystemRunner.class.getDeclaredMethod("getFileInputStream", String.class);
            getFileInputStream.setAccessible(true);
            
            final Method getFullFilePath = VotingSystemRunner.class.getDeclaredMethod("getFullFilePath", String.class);
            getFullFilePath.setAccessible(true);
            
            final char fileSep = File.separatorChar;
            
            Assertions.assertAll(
                //Testing valid file path
                () -> Assertions.assertDoesNotThrow(() ->
                    getFileInputStream.invoke(
                        VotingSystem.class,
                        getFullFilePath.invoke(
                            VotingSystemRunner.class,
                            "Project1/testing/test-resources/votingSystemRunnerTest/test.txt".replace('/', fileSep)
                        )
                    )
                ),
                //Testing invalid file path
                () -> Assertions.assertEquals(
                    FileNotFoundException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getFileInputStream.invoke(
                            VotingSystem.class,
                            getFullFilePath.invoke(
                                VotingSystemRunner.class,
                                "Project1/testing/test-resources/votingSystemRunnerTest/b.txt".replace('/', fileSep)
                            )
                        )
                    ).getCause().getClass()
                ),
                //Testing valid path but to directory
                () -> Assertions.assertEquals(
                    FileNotFoundException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getFileInputStream.invoke(
                            VotingSystem.class,
                            getFullFilePath.invoke(
                                VotingSystemRunner.class,
                                "Project1/testing/test-resources/votingSystemRunnerTest".replace('/', fileSep)
                            )
                        )
                    ).getCause().getClass()
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getFile method from VotingSystemRunner");
        }
    }
    
    @Test
    void testGenerateTimestampedFileName() {
        final Method generateTimestampedFileName;
        try {
            generateTimestampedFileName =
                VotingSystemRunner.class.getDeclaredMethod("generateTimestampedFileName", String.class, LocalDateTime.class);
            generateTimestampedFileName.setAccessible(true);
            
            //Testing various prefixes and time stamps for generateTimestampedFileName
            Assertions.assertAll(
                //Testing arbitrary date
                () -> Assertions.assertEquals("report_2016-08-14_14-55-47.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "report",
                    LocalDateTime.of(2016, 8, 14, 14, 55, 47)
                )),
                //Testing for proper zero padding
                () -> Assertions.assertEquals("potato_2021-09-09_09-09-09.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "potato",
                    LocalDateTime.of(2021, 9, 9, 9, 9, 9)
                )),
                //Edge case: Very low timestamp
                () -> Assertions.assertEquals("entrée_0-01-01_00-00-00.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "entrée",
                    LocalDateTime.of(0, 1, 1, 0, 0, 0)
                )),
                //Edge case: Very high timestamp
                () -> Assertions.assertEquals("vote_9999-12-31_23-59-59.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "vote",
                    LocalDateTime.of(9999, 12, 31, 23, 59, 59)
                ))
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the generateTimestampedFileName method from VotingSystemRunner");
        }
    }
    
    @Test
    void testGetFileOutputStream() {
        final Method getFileOutputStream;
        
        final char fileSep = File.separatorChar;
        
        try {
            //Retrieve getFileOutputStream using reflection due to it being private, and use reflection to make it accessible
            getFileOutputStream = VotingSystemRunner.class.getDeclaredMethod("getFileOutputStream", String.class);
            getFileOutputStream.setAccessible(true);
            
            Assertions.assertAll(
                //Testing existing file path
                () -> Assertions.assertDoesNotThrow(() ->
                    getFileOutputStream.invoke(
                        VotingSystem.class,
                        "Project1/testing/test-resources/votingSystemRunnerTest/test.txt".replace('/', fileSep)
                    )
                ),
                //Testing nonexistent file path
                () -> Assertions.assertDoesNotThrow(() ->
                    getFileOutputStream.invoke(
                        VotingSystem.class,
                        "Project1/testing/test-resources/votingSystemRunnerTest/b.txt".replace('/', fileSep)
                    )
                ),
                //Check that the nonexistent file was created for the output stream from the previous assertion
                () -> Assertions.assertTrue(
                    new File("Project1/testing/test-resources/votingSystemRunnerTest/b.txt".replace('/', fileSep)).exists()
                ),
                //Testing existing path but to directory
                () -> Assertions.assertEquals(
                    FileNotFoundException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getFileOutputStream.invoke(
                            VotingSystem.class,
                            "Project1/testing/test-resources/votingSystemRunnerTest".replace('/', fileSep)
                        )
                    ).getCause().getClass()
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getFile method from VotingSystemRunner");
        }
        finally {
            //Remove the created test file after the test is completed to reset to the initial state of files
            //noinspection ResultOfMethodCallIgnored
            new File("Project1/testing/test-resources/votingSystemRunnerTest/b.txt".replace('/', fileSep)).delete();
        }
    }
    
    @Test
    void testGenerateTimestampedFileName() {
        final Method generateTimestampedFileName;
        try {
            generateTimestampedFileName =
                VotingSystemRunner.class.getDeclaredMethod("generateTimestampedFileName", String.class, LocalDateTime.class);
            generateTimestampedFileName.setAccessible(true);
            
            //Testing various prefixes and time stamps for generateTimestampedFileName
            Assertions.assertAll(
                () -> Assertions.assertEquals("audit_2000-01-01_01-01-01.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "audit",
                    LocalDateTime.of(2000, 1, 1, 1, 1, 1)
                )),
                () -> Assertions.assertEquals("report_2016-08-14_14-55-47.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "report",
                    LocalDateTime.of(2016, 8, 14, 14, 55, 47)
                )),
                () -> Assertions.assertEquals("potato_2019-09-21_22-27-00.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "potato",
                    LocalDateTime.of(2019, 9, 21, 22, 27, 0)
                )),
                () -> Assertions.assertEquals("audit_2001-09-13_20-41-29.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "audit",
                    LocalDateTime.of(2001, 9, 13, 20, 41, 29)
                )),
                () -> Assertions.assertEquals("report_2003-12-17_05-41-08.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "report",
                    LocalDateTime.of(2003, 12, 17, 5, 41, 8)
                )),
                //Edge case: Very low timestamp
                () -> Assertions.assertEquals("entrée_0-01-01_00-00-00.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "entrée",
                    LocalDateTime.of(0, 1, 1, 0, 0, 0)
                )),
                //Edge case: Very high timestamp
                () -> Assertions.assertEquals("vote_9999-12-31_23-59-59.txt", generateTimestampedFileName.invoke(
                    VotingSystemRunner.class,
                    "vote",
                    LocalDateTime.of(9999, 12, 31, 23, 59, 59)
                ))
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the generateTimestampedFileName method from VotingSystemRunner");
        }
    }
    
    @Test
    void testGetFileOutputStream() {
        final Method getFileOutputStream;
        try {
            getFileOutputStream = VotingSystemRunner.class.getDeclaredMethod("getFileOutputStream", String.class);
            getFileOutputStream.setAccessible(true);
            
            Assertions.assertAll(
                //Testing existing file paths
                () -> Assertions.assertDoesNotThrow(() ->
                    getFileOutputStream.invoke(
                        VotingSystem.class,
                        String.format("test-resources%svotingSystemRunnerTest%stest.txt", File.separator, File.separator)
                    )
                ),
                () -> Assertions.assertDoesNotThrow(() ->
                    getFileOutputStream.invoke(
                        VotingSystem.class,
                        String.format("test-resources%svotingSystemRunnerTest%srandomDirectory%sa.txt", File.separator, File.separator,
                            File.separator)
                    )
                ),
                //Testing nonexistent file paths
                () -> Assertions.assertDoesNotThrow(() ->
                    getFileOutputStream.invoke(
                        VotingSystem.class,
                        String.format("test-resources%svotingSystemRunnerTest%srandomDirectory%sb.txt", File.separator, File.separator,
                            File.separator)
                    )
                ),
                //Check that the nonexistent file was created for the output stream
                () -> Assertions.assertTrue(
                    new File(String.format("test-resources%svotingSystemRunnerTest%srandomDirectory%sb.txt", File.separator, File.separator,
                        File.separator)).exists()),
                //Testing existing path but to directory
                () -> Assertions.assertEquals(
                    FileNotFoundException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getFileOutputStream.invoke(
                            VotingSystem.class,
                            String.format("test-resources%svotingSystemRunnerTest", File.separator)
                        )
                    ).getCause().getClass()
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getFile method from VotingSystemRunner");
        }
        finally {
            //Remove the created test files after the test is completed to reset to the initial state of files
            //noinspection ResultOfMethodCallIgnored
            new File(String.format("test-resources%svotingSystemRunnerTest%srandomDirectory%sb.txt", File.separator, File.separator,
                File.separator)).delete();
        }
    }
    
}
