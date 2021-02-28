package org.team19;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class VotingSystemRunnerTest {
    
    private VotingSystemRunnerTest() {}
    
    @Test
    void testGetFullFilePath() {
        try {
            final String projectRootDir = new File("./").getCanonicalPath();
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
    void testGetFile() {
        final Method getFile;
        try {
            getFile = VotingSystemRunner.class.getDeclaredMethod("getFile", String.class);
            getFile.setAccessible(true);
            
            final Method getFullFilePath = VotingSystemRunner.class.getDeclaredMethod("getFullFilePath", String.class);
            getFullFilePath.setAccessible(true);
            
            Assertions.assertAll(
                //Testing valid file paths
                () -> Assertions.assertDoesNotThrow(() ->
                    getFile.invoke(
                        VotingSystem.class,
                        getFullFilePath.invoke(VotingSystemRunner.class, "test-resources/votingSystemRunnerTest/test.txt")
                    )
                ),
                () -> Assertions.assertDoesNotThrow(() ->
                    getFile.invoke(
                        VotingSystem.class,
                        getFullFilePath.invoke(VotingSystemRunner.class, "test-resources/votingSystemRunnerTest/randomDirectory/a.txt")
                    )
                ),
                //Testing invalid file path
                () -> Assertions.assertThrows(InvocationTargetException.class, () ->
                    getFile.invoke(
                        VotingSystem.class,
                        getFullFilePath.invoke(VotingSystemRunner.class, "test-resources/votingSystemRunnerTest/b.txt")
                    )
                ),
                //Testing valid path but to directory
                () -> Assertions.assertThrows(InvocationTargetException.class, () ->
                    getFile.invoke(
                        VotingSystem.class,
                        getFullFilePath.invoke(VotingSystemRunner.class, "test-resources/votingSystemRunnerTest/")
                    )
                )
            );
        }
        catch(NoSuchMethodException e) {
            Assertions.fail("Unable to retrieve the getFile method from VotingSystemRunner");
        }
    }
    
}
