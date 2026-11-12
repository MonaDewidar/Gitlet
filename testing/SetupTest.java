package testing;

import gitlet.Utils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;



public class SetupTest {
    @Test
    public void junitWorks() {

        assertEquals(2, 1 + 1);
    }
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();


    @Test
    public void writeThenReadString() {
        File f = Utils.join(tmp.getRoot(), "a.txt");
        Utils.writeContents(f, "hello");
        assertEquals("hello", Utils.readContentsAsString(f));
    }
}
