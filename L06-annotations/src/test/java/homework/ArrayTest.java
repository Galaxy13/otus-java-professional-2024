package homework;

import com.galaxy13.unittest.annotaions.Before;
import com.galaxy13.unittest.annotaions.Test;

import static com.galaxy13.unittest.Assert.assertArraysEquals;

@SuppressWarnings("java:S2187")
public class ArrayTest {
    private int[] arr;

    @Before
    public void setUp() {
        arr = new int[10];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
    }

    @Test
    public void arrEqTest() throws Exception {
        int[] arr2 = new int[10];
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = i;
        }
        assertArraysEquals(arr, arr2);
    }

    @Test
    public void arrNotEqTest() throws Exception {
        int[] arr2 = new int[10];
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = i + 1;
        }
        assertArraysEquals(arr, arr2);
    }
}
