package ch.epfl.sweng.radius.database;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class LocationVisibilityTest {

    private MLocation testUser;

    @Before
    public void setUp() {
        testUser = new MLocation();
    }

    @Test
    public void testInitialVisibility() {
        assertTrue(testUser.isVisible());
    }

    @Test
    public void testSetVisibility() {
        testUser.setVisibility(false);
        assertFalse(testUser.isVisible());
        testUser.setVisibility(true);
        assertTrue(testUser.isVisible());
    }

}