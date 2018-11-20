package ch.epfl.sweng.radius.utils;

import android.support.v4.content.ContextCompat;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import ch.epfl.sweng.radius.database.Database;
import ch.epfl.sweng.radius.database.FakeFirebaseUtility;
import ch.epfl.sweng.radius.database.GroupLocationFetcher;
import ch.epfl.sweng.radius.database.MLocation;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseDatabase.class, ContextCompat.class})
public class GroupLocationFetcherTest {

    private GroupLocationFetcher fetcher;
    private final double RADIUS = 50;
    private final MLocation groupLocation = new MLocation();
    private final MLocation notGroupLocation = new MLocation();

    DatabaseReference mockedDb   = Mockito.mock(DatabaseReference.class);
    FirebaseDatabase mockedFb   = Mockito.mock(FirebaseDatabase.class);
    String curRef;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(FirebaseDatabase.class);

        Mockito.when(FirebaseDatabase.getInstance()).thenReturn(mockedFb);
        Mockito.when(mockedFb.getReference(any(String.class))).thenReturn(mockedDb);

        Mockito.when(mockedDb.child((String) Matchers.argThat(new ArgumentMatcher(){

            // Update current and print to console path to console
            @Override
            public boolean matches(Object argument) {
                curRef = (String) argument;
                return true;
            }

        }))).thenReturn(mockedDb);
        Database.activateDebugMode();
        ((FakeFirebaseUtility) Database.getInstance()).fillDatabase();

        fetcher = new GroupLocationFetcher(RADIUS);
        groupLocation.setIsGroupLocation(1);
    }

    @Test
    public void testGetGroupLocation(){
        //Database.getInstance().writeInstanceObj(groupLocation, Database.Tables.LOCATIONS);
        Database.getInstance().readAllTableOnce(Database.Tables.LOCATIONS, fetcher);
        HashMap<String, MLocation> map = fetcher.getGroupLocations();
        assertTrue(map.isEmpty());
    }

    @Test
    public void testOnError(){
        Throwable testThrow = new Throwable();
        DatabaseError testError = DatabaseError.fromException(testThrow);
        fetcher.onError(testError);
    }

    @After
    public void tearDown() {
        fetcher = null;
    }

}
