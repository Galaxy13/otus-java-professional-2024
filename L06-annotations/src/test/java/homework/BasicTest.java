package homework;

import com.galaxy13.unittest.annotaions.Test;

import static com.galaxy13.unittest.Assert.*;

// S2187 - linter insists on using JUnit, unused - Idea analyzer
@SuppressWarnings({"java:S2187", "unused"})
public class BasicTest {

    @Test
    public void test() {
        assertTrue();
    }

    public void testFail() throws Exception {
        assertFalse();
    }

    @Test
    public void testAddition() throws Exception {
        assertEquals(2 + 2, 4);
    }

    //    @Test
    public void testAdditionFail() throws Exception {
        assertEquals(2 + 1, 4);
    }

    @Test
    public void testStrings() throws Exception {
        assertEquals("Hello World", "Hello World");
    }
}
