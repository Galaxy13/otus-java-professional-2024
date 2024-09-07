package homework;

import com.galaxy13.galaxytest.annotaions.GalaxyTest;
import com.galaxy13.galaxytest.annotaions.Test;

import static com.galaxy13.galaxytest.Assert.*;

// S2187 - linter insists on using JUnit, unused - Idea analyzer
@SuppressWarnings({"java:S2187", "unused"})
@GalaxyTest
public class BasicTest {

    @Test
    public void test() {
        assertTrue();
    }

    public void testFail() {
        assertFalse();
    }

    @Test
    public void testAddition() {
        assertEquals(2 + 2, 4);
    }

    //    @Test
    public void testAdditionFail() {
        assertEquals(2 + 1, 4);
    }

    @Test
    public void testStrings() {
        assertEquals("Hello World", "Hello World");
    }
}
