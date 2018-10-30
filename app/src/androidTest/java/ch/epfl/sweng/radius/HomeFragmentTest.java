package ch.epfl.sweng.radius;

import android.Manifest;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.FrameLayout;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;



public class HomeFragmentTest extends ActivityInstrumentationTestCase2<AccountActivity> {

    @Rule
    public ActivityTestRule<AccountActivity> mblActivityTestRule
            = new ActivityTestRule<AccountActivity>(AccountActivity.class);
    @Rule
    public final GrantPermissionRule mPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION);


    private FrameLayout fcontainer;private AccountActivity mblAccountActivity;
    private Fragment fragment;

    public HomeFragmentTest(Class<AccountActivity> activityClass) {
        super(activityClass);
    }

    public HomeFragmentTest(){
        super(AccountActivity.class);
    }

    /**
     * Set up the test.
     * */
    @Before
    public void setUp() throws Exception {
        super.setUp();

        Intent intent = new Intent();
        mblAccountActivity = mblActivityTestRule.launchActivity(intent);

    }

    @Test
    public void testMarkButton() {
        onView(withId(R.id.navigation_profile)).perform(click());
        onView(withId(R.id.navigation_home)).perform(click());
        onView(withId(R.id.testMark)).perform(click());
    }

    @Test
    public void testLaunch() {
        FrameLayout fcontainer = mblAccountActivity.findViewById(R.id.fcontainer);
        assertNotNull(fcontainer);

        Fragment fragment = new HomeFragment();

        mblAccountActivity.getSupportFragmentManager().beginTransaction()
                .add(fcontainer.getId(), fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();

        View view = fragment.getView().findViewById(R.id.map);
        assertNotNull(view);
    }

    @After
    public void tearDown() {
        mblAccountActivity = null;
    }
}