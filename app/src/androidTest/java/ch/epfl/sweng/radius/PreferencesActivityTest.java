package ch.epfl.sweng.radius;

import android.Manifest;
import android.content.Intent;
import android.preference.Preference;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.FrameLayout;

import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ch.epfl.sweng.radius.database.Database;
import ch.epfl.sweng.radius.database.MLocation;
import ch.epfl.sweng.radius.database.User;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class PreferencesActivityTest  extends ActivityInstrumentationTestCase2<PreferencesActivity> {

    @Rule
    public ActivityTestRule<PreferencesActivity> mblActivityTestRule
            = new ActivityTestRule<PreferencesActivity>(PreferencesActivity.class);

    @Rule
    public final GrantPermissionRule mPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION);

    private PreferencesActivity mblPreferenceActivity;
    private FrameLayout fcontainer;
    private PreferencesActivity.MyPreferenceFragment fragment;

    public PreferencesActivityTest(Class<PreferencesActivity> activityClass) {
        super(activityClass);
    }

    public PreferencesActivityTest() {
        super(PreferencesActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Database.activateDebugMode();

        Intent intent = new Intent();
        mblPreferenceActivity = mblActivityTestRule.launchActivity(intent);
        fragment =  (PreferencesActivity.MyPreferenceFragment) mblPreferenceActivity
                .getFragmentManager().findFragmentByTag("preferencesFragment");
    }

    @Test
    public void testLaunch() {
        assertNotNull(fragment);
        getInstrumentation().waitForIdleSync();

        Preference incognitoSwitch = fragment.findPreference("incognitoSwitch");
        Preference notifCheckBox = fragment.findPreference("notificationsCheckbox");
        Preference nightModeSwitch = fragment.findPreference("nightModeSwitch");
        Preference logoutButton = fragment.findPreference("logOutButton");
        Preference deleteAccount = fragment.findPreference("deleteAccount");

        assertNotNull(incognitoSwitch);
        assertNotNull(notifCheckBox);
        assertNotNull(nightModeSwitch);
        assertNotNull(logoutButton);
        assertNotNull(deleteAccount);
    }

    @Test
    public void testIncognitoMode(){

        Espresso.onView(AllOf.allOf(withText(R.string.incognitoTitle)))
                .perform(click());
    }

    @Test
    public void testNotifications(){
        Espresso.onView(AllOf.allOf(withText(R.string.notificationsTitle)))
                .perform(click());
    }

    @Test
    public void testNightMode(){
        Espresso.onView(AllOf.allOf(withText(R.string.nightModeTitle)))
                .perform(click());
    }

    @Test
    public void testLogOut(){
        Espresso.onView(AllOf.allOf(withText(R.string.logoutTitle)))
                .perform(click());
    }

    @Test
    public void testDeleteAccountDismiss(){
        Espresso.onView(AllOf.allOf(withText(R.string.deleteAccountTitle)))
                .perform(click());
        Espresso.onView(withText("Dismiss")).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteAccount(){
        Espresso.onView(AllOf.allOf(withText(R.string.deleteAccountTitle)))
                .perform(click());
        Espresso.onView(withText("Delete")).perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        restoreCurrentUser();
    }

    private void restoreCurrentUser() {

        User currentUSer = new User("testUser1");
        currentUSer.addChat("testUser2", "10");
        currentUSer.addChat("testUser3", "11");
        currentUSer.addChat("testUser5", "12");
        currentUSer.addFriendRequest(new User("testUser5"));
        ArrayList<String> blockedUser = new ArrayList<>();
        blockedUser.add("testUser3");currentUSer.setBlockedUsers(blockedUser);

        currentUSer.addFriendRequest(new User("testUser3"));

        Database.getInstance().writeInstanceObj(currentUSer, Database.Tables.USERS);

        MLocation currentLoc = new MLocation("testUser1");
        currentLoc.setUrlProfilePhoto("./app/src/androidTest/java/ch/epfl/sweng/radius/utils/default.png");
        currentLoc.setTitle("testUser1");
        currentLoc.setRadius(30000); currentLoc.setMessage("Being tested on");
        currentLoc.setInterests("Tests, mostly");

        Database.getInstance().writeInstanceObj(currentLoc, Database.Tables.LOCATIONS);

    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        this.mblPreferenceActivity = null;
    }
}
