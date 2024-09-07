package homework;

import com.galaxy13.galaxytest.annotaions.Before;
import com.galaxy13.galaxytest.annotaions.GalaxyTest;
import com.galaxy13.galaxytest.annotaions.Test;

import static com.galaxy13.galaxytest.Assert.assertArraysEquals;

// S2187 - linter insists on using JUnit, unused - Idea analyzer
@SuppressWarnings({"java:S2187", "unused"})
@GalaxyTest
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
    public void arrEqTest() {
        int[] arr2 = new int[10];
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = i;
        }
        assertArraysEquals(arr, arr2);
    }

    //    @Test
    public void arrNotEqTest() {
        int[] arr2 = new int[10];
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = i + 1;
        }
        assertArraysEquals(arr, arr2);
    }
}
