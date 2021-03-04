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
            final String projectRootDir = new File("." + File.separator).getCanonicalPath();
            final String userHomeDir = System.getProperty("user.home");
            
            final Method getFullFilePath = VotingSystemRunner.class.getDeclaredMethod("getFullFilePath", String.class);
            getFullFilePath.setAccessible(true);
            
            final String fileSep = File.separator;
            
            Assertions.assertAll(
                //Testing paths relative to project directory
                () -> Assertions.assertEquals(
                    String.format("%s%stest.txt", projectRootDir, fileSep), getFullFilePath.invoke(VotingSystemRunner.class, "test.txt")),
                () -> Assertions.assertEquals(
                    String.format("%s%srandomFolder%Sa.txt", projectRootDir, fileSep, fileSep),
                    getFullFilePath.invoke(VotingSystemRunner.class, String.format("randomFolder%sa.txt", fileSep))
                ),
                //Testing paths relative to home directory
                () -> Assertions.assertEquals(
                    String.format("%s%stest.txt", userHomeDir, fileSep),
                    getFullFilePath.invoke(VotingSystemRunner.class, String.format("~%stest.txt", fileSep))
                ),
                () -> Assertions.assertEquals(
                    String.format("%s%srandomFolder%sa.txt", userHomeDir, fileSep, fileSep),
                    getFullFilePath.invoke(VotingSystemRunner.class, String.format("~%srandomFolder%sa.txt", fileSep, fileSep))
                ),
                //Testing absolute paths
                () -> Assertions.assertEquals(
                    String.format("%s%sDesktop%stest.txt", userHomeDir, fileSep, fileSep),
                    getFullFilePath.invoke(VotingSystemRunner.class, String.format("%s%sDesktop%stest.txt", userHomeDir, fileSep, fileSep))
                ),
                () -> Assertions.assertEquals(
                    String.format("%s%sDesktop%srandomDirectory%stest.txt", userHomeDir, fileSep, fileSep, fileSep),
                    getFullFilePath.invoke(VotingSystemRunner.class, String.format("%s%sDesktop%srandomDirectory%stest.txt", userHomeDir, fileSep,
                        fileSep, fileSep))
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
            
            Assertions.assertAll(
                //Testing valid file paths
                () -> Assertions.assertDoesNotThrow(() ->
                    getFileInputStream.invoke(
                        VotingSystem.class,
                        getFullFilePath.invoke(VotingSystemRunner.class, String.format("test-resources%svotingSystemRunnerTest%stest.txt",
                            File.separator, File.separator))
                    )
                ),
                () -> Assertions.assertDoesNotThrow(() ->
                    getFileInputStream.invoke(
                        VotingSystem.class,
                        getFullFilePath.invoke(VotingSystemRunner.class,
                            String.format("test-resources%svotingSystemRunnerTest%srandomDirectory%sa.txt", File.separator, File.separator,
                                File.separator))
                    )
                ),
                //Testing invalid file path
                () -> Assertions.assertEquals(
                    FileNotFoundException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getFileInputStream.invoke(
                            VotingSystem.class,
                            getFullFilePath.invoke(VotingSystemRunner.class, String.format("test-resources%svotingSystemRunnerTest%sb.txt",
                                File.separator, File.separator))
                        )
                    ).getCause().getClass()
                ),
                //Testing valid path but to directory
                () -> Assertions.assertEquals(
                    FileNotFoundException.class,
                    Assertions.assertThrows(InvocationTargetException.class, () ->
                        getFileInputStream.invoke(
                            VotingSystem.class,
                            getFullFilePath.invoke(VotingSystemRunner.class, String.format("test-resources%svotingSystemRunnerTest", File.separator))
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
