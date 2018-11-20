package ch.epfl.sweng.radius.database;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class GroupLocationTest {

    private MLocation testLocation;

    @Before
    public void setUp() {
        testLocation = new MLocation();
    }

    @Test
    public void testInitialState() {
        assertTrue(testLocation.isGroupLocation() == 0);
    }

    @Test
    public void testSetIsGroupLocation() {
        testLocation.setIsGroupLocation(1);
        assertTrue(testLocation.isGroupLocation() == 1);
        testLocation.setIsGroupLocation(0);
        assertTrue(testLocation.isGroupLocation() == 0);
    }

}
