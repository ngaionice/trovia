import org.junit.Assert;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DifferenceChecker {

    // not sure if it works, will need to wait for update to see
    public void UpdateChecker(String oldPath, String newPath) throws IOException {
        Assert.assertArrayEquals(oldPath + "Binary files differ",
                Files.readAllBytes(Paths.get(oldPath)),
                Files.readAllBytes(Paths.get(newPath)));
    }
}
