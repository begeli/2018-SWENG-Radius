package ch.epfl.sweng.radius.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.radius.AccountActivity;
import ch.epfl.sweng.radius.R;
import ch.epfl.sweng.radius.database.Database;
import ch.epfl.sweng.radius.database.FakeFirebaseUtility;
import ch.epfl.sweng.radius.database.MLocation;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

class RelaunchActivityRule<T extends Activity> extends ActivityTestRule<T> {

    public RelaunchActivityRule(Class<T> activityClass) {
        super(activityClass,false);
    }

    /*
    public RelaunchActivityRule(Class<T> activityClass, boolean initialTouchMode) {
        super(activityClass, initialTouchMode,true);
    }

    public RelaunchActivityRule(Class<T> activityClass, boolean initialTouchMode,
                                boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
    }
    */

    @Override protected void afterActivityFinished() {
        super.afterActivityFinished();
        launchActivity(getActivityIntent());
    }

    public void finish() {
        finishActivity();
    }

    public void relaunchActivity() {
        finishActivity();
        launchActivity();
    }

    public void launchActivity() {
        launchActivity(getActivityIntent());
    }
}

//Inner class to test User Profile Picture
class ColorMatcher extends TypeSafeMatcher<View> {

    private final int color;

    ColorMatcher(int color) {
        super(View.class);
        this.color = color;
    }

    @Override
    protected boolean matchesSafely(View target) {
        if (!(target instanceof ImageView)) {
            return false;
        }
        ImageView imageView = (ImageView) target;

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        return bitmap.getPixel(0, 0) == color;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("exactly matches the color value " + color);
    }
}

public class ProfileFragmentTest  extends ActivityInstrumentationTestCase2<AccountActivity> {

    @Rule
    public ActivityTestRule<AccountActivity> mblActivityTestRule = new ActivityTestRule<AccountActivity>(AccountActivity.class);
    @Rule
    public final GrantPermissionRule mPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public final RelaunchActivityRule<AccountActivity> mRelaunchRule = new RelaunchActivityRule<>(AccountActivity.class);

    private AccountActivity mblAccountActivity;
    //private FrameLayout fcontainer;
    private ProfileFragment fragment;
    /*
    public ProfileFragmentTest(Class<AccountActivity> activityClass) {
        super(activityClass);
    }
    */

    public ProfileFragmentTest() {
        super(AccountActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Database.activateDebugMode();
        ((FakeFirebaseUtility) Database.getInstance()).fillDatabase();

        MLocation testUser = new MLocation("testId");
        testUser.setTitle("testNickname");
        testUser.setMessage("testStatus");
        testUser.setInterests("testInterests");
        testUser.setSpokenLanguages("English");
        //1 pixel green picture in base 64
        testUser.setUrlProfilePhoto("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M/wHwAEBgIApD5fRAAAAABJRU5ErkJggg==");
        Intent intent = new Intent();
        mblAccountActivity = mblActivityTestRule.launchActivity(intent);
    }

    @Test
    public void testLaunch() {
        FrameLayout fcontainer = mblAccountActivity.findViewById(R.id.fcontainer);
        assertNotNull(fcontainer);

        Fragment fragment = new ProfileFragment();
        mblAccountActivity.getSupportFragmentManager().beginTransaction()
                .add(fcontainer.getId(), fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();

        View view = fragment.getView().findViewById(R.id.profileLayout); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.userPhoto); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.userNickname); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.userStatus); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.nicknameInput); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.statusInput); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.radiusLabel); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.radiusValue); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.radiusBar); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.languagesButton); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.spokenLanguages); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.saveButton); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.userInterests); assertNotNull(view);
        view = fragment.getView().findViewById(R.id.interestsInput); assertNotNull(view);
    }

    @Test
    public void testChangeNicknameAndStatus() {
        Espresso.onView(withId(R.id.navigation_profile)).perform(click());
        Espresso.onView(withId(R.id.nicknameInput)).perform(typeText("User Nickname"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.statusInput)).perform(typeText("User Status"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.saveButton)).perform(scrollTo(),click());
        Espresso.onView(withId(R.id.userNickname)).equals("User Nickname");
        Espresso.onView(withId(R.id.userStatus)).equals("User Status");
    }

    @Test
    public void testChangeInterests(){
        Espresso.onView(withId(R.id.navigation_profile)).perform(click());
        Espresso.onView(withId(R.id.interestsInput)).perform(typeText("User Interests"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.saveButton)).perform(scrollTo(),click());
        Espresso.onView(withId(R.id.userInterests)).equals("User Interests");
    }

    @Test
    public void testSaveInstanceState() {
        Espresso.onView(withId(R.id.navigation_home)).perform(click());
        Espresso.onView(withId(R.id.navigation_profile)).perform(click());
        Espresso.onView(withId(R.id.statusInput)).perform(typeText("User Status"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.nicknameInput)).perform(typeText("User Nickname"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.navigation_home)).perform(click());
        Espresso.onView(withId(R.id.navigation_profile)).perform(click());
    }

    @Test
    public void testProfileImage() {
        Espresso.onView(withId(R.id.navigation_profile)).perform(click());
        Espresso.onView(withId(R.id.userPhoto)).check(ViewAssertions.matches(isDisplayed()));
        //Espresso.onView(withId(R.id.userPhoto)).check(ViewAssertions.matches(new ColorMatcher(Color.GREEN)));
        //Espresso.onView(withId(R.id.userPhoto)).check(ViewAssertions.matches(not(new ColorMatcher(Color.RED))));
        
        // It seems that Travis doesnt support pressBack
        //Espresso.onView(withId(R.id.userPhoto)).perform(click());
        //UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressBack();
    }

   @Test
    public void testLanguageButton() {
       Espresso.onView(withId(R.id.navigation_profile)).perform(click());
       Espresso.onView(withId(R.id.languagesButton)).perform(scrollTo(),click());
       Espresso.onView(withText("OK"))
               .inRoot(RootMatchers.isDialog())
               .check(ViewAssertions.matches(isDisplayed()))
               .perform(click());

       Espresso.onView(withId(R.id.languagesButton)).perform(click());
       Espresso.onView(withText("DISMISS"))
               .inRoot(RootMatchers.isDialog())
               .check(ViewAssertions.matches(isDisplayed()))
               .perform(click());

       Espresso.onView(withId(R.id.languagesButton)).perform(click());
       Espresso.onView(withText("CLEAR ALL"))
               .inRoot(RootMatchers.isDialog())
               .check(ViewAssertions.matches(isDisplayed()))
               .perform(click());

       Espresso.onView(withId(R.id.languagesButton)).perform(click());
       Espresso.onView(withText("German"))
               .inRoot(RootMatchers.isDialog())
               .check(ViewAssertions.matches(isDisplayed()))
               .perform(click());
       Espresso.onView(withText("OK"))
               .inRoot(RootMatchers.isDialog())
               .check(ViewAssertions.matches(isDisplayed()))
               .perform(click());

   }

    @Test
    public void testSeekBar() {
        Espresso.onView(withId(R.id.navigation_profile)).perform(click());
        Espresso.onView(withId(R.id.radiusBar)).perform(setProgress(10));

        final Intent intent = new Intent();
        Uri uri = new Uri.Builder().appendEncodedPath("test").build();
        intent.setData(uri);

        fragment = new ProfileFragment();
        getActivity().runOnUiThread(new Runnable(){
            public void run(){
                fragment.onActivityResult(1, -1, intent);
            }
        });

    }

    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(progress);
            }
            @Override
            public String getDescription() {
                return "Set a progress on a SeekBar";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }

    @After
    public void tearDown() {
        mblAccountActivity = null;
    }
}