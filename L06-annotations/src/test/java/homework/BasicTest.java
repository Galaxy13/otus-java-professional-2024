package homework;

import com.galaxy13.unittest.annotaions.Test;

import static com.galaxy13.unittest.Assert.assertEquals;
import static com.galaxy13.unittest.Assert.assertTrue;

@SuppressWarnings("java:S2187")
public class BasicTest {

    @Test
    public void test() {
        assertTrue();
    }

    @Test
    public void testAddition() throws Exception {
        assertEquals(2 + 2, 4);
    }

    @Test
    public void testAdditionFail() throws Exception {
        assertEquals(2 + 1, 4);
    }

    @Test
    public void testStrings() throws Exception {
        assertEquals("Hello World", "Hello World");
    }
}
