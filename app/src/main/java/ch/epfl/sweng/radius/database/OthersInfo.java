package ch.epfl.sweng.radius.database;

import android.util.Log;

import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;

import ch.epfl.sweng.radius.utils.MapUtility;

public class OthersInfo extends DBObservable{

    private static OthersInfo othersInfo = null;
    private static final Database database = Database.getInstance();
    private static final MapUtility mapUtility = MapUtility.getMapInstance();
    private static final HashMap<String, MLocation> othersPos = new HashMap<>();


    public static OthersInfo getInstance(){
        if (othersInfo == null)
            othersInfo = new OthersInfo();
        return othersInfo;
    }

    private OthersInfo(){
        fetchUsersInMyRadius();
        fetchGroupsInMyRadius();
        fetchTopicsInMyRadius();
    }

    public HashMap<String, MLocation> getUsersInRadius(){
        return othersPos;
    }

    public void fetchUsersInMyRadius(){
        othersPos.clear();
        database.readAllTableOnce(Database.Tables.LOCATIONS, new CallBackDatabase() {
            @Override
            public void onFinish(Object value) {
                for (MLocation loc : (ArrayList<MLocation>) value) {
                    if(mapUtility.contains(loc.getLatitude(), loc.getLongitude())) {
                        Log.e("MapUtility", "Adder user " + loc.getID());
                        othersPos.put(loc.getID(), loc);
                    }
                }
            }

            @Override
            public void onError(DatabaseError error) {
                Log.e("FetchUserRadius", error.getMessage());
            }
        });
    }

    public void fetchGroupsInMyRadius(){

    }

    public void fetchTopicsInMyRadius(){

    }
}
