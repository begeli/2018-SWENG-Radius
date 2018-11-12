package ch.epfl.sweng.radius.database;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FakeFirebaseUtility extends Database {
    private User currentUSer;
    private MLocation currentLoc;
    private HashMap<String, User> usersTable = new HashMap<>();
    private HashMap<String, ChatLogs> chatLogsTable = new HashMap<>();
    private HashMap<String, MLocation> locationsTable = new HashMap<>();

    private final double defaultLat = 46.5360698;
    private final double defaultLng = 6.5681216000000004;


    public FakeFirebaseUtility(){
        fillDatabase();
    }

    @Override
    public String getCurrent_user_id() {
        return currentUSer.getID();
    }

    @Override
    public void readObjOnce(final DatabaseObject obj,
                            final Tables tableName,
                            final CallBackDatabase callback) {
        DatabaseObject objRead = null;
        switch (tableName){
            case USERS:
                objRead = usersTable.get(obj.getID());
                break;
            case CHATLOGS:
                objRead = chatLogsTable.get(obj.getID());
                break;
            case LOCATIONS:
                objRead = locationsTable.get((obj.getID()));
                break;
        }

        if (objRead == null){
            writeInstanceObj(obj, tableName);
            callback.onFinish(obj);
        }
        else
            callback.onFinish(objRead);

    }

    @Override
    public void readObj(final DatabaseObject obj,
                        final Tables tableName,
                        final CallBackDatabase callback) {
    }

    @Override
    public void readListObjOnce(final List<String> ids,
                            final Tables tableName,
                            final CallBackDatabase callback) {
        ArrayList<DatabaseObject> objsRead = new ArrayList<>();

        HashMap<String, DatabaseObject> table = getTable(tableName);

        for (String id:ids) {
            DatabaseObject objRead = table.get(id);

            if(objRead != null)
                objsRead.add(objRead);
        }

        callback.onFinish(objsRead);
    }

    @Override
    public void readAllTableOnce(Tables tableName, CallBackDatabase callback) {
        ArrayList<DatabaseObject> objsRead = new ArrayList<>();

        int size = getTableSize(tableName);
        HashMap<String, DatabaseObject> table = getTable(tableName);

        Log.w("Map Test", "Size of table " + size);
        for (int i = 0; i < size; i++) {
            DatabaseObject objRead = table.get("testUser"+Integer.toString(i+1));

            if(objRead != null)
                objsRead.add(objRead);
        }
        Log.w("Map Test", "Size of objReads " + objsRead.size());

        callback.onFinish(objsRead);
    }

    @Override
    public void writeInstanceObj(final DatabaseObject obj, final Tables tableName){
        switch (tableName){
            case USERS:
                usersTable.put(obj.getID(), (User) obj);
                break;
            case CHATLOGS:
                chatLogsTable.put(obj.getID(), (ChatLogs) obj);
                break;
            case LOCATIONS:
                locationsTable.put(obj.getID(), (MLocation) obj);
                break;
        }
    }

    private void fillDatabase(){
        // Define Current user
        currentUSer = new User("testUser1");

        // Fill the users table
        usersTable.put("testUser1", currentUSer);
        usersTable.put("testUser2", new User("testUser2"));
        usersTable.put("testUser3", new User("testUser3"));
        usersTable.put("testUser4", new User("testUser4"));

        // TODO: Fill the chatLogs table
        currentLoc = new MLocation("testUser1", defaultLng, defaultLat);

        // Fill the users table
        locationsTable.put("testUser1", currentLoc);
        locationsTable.put("testUser2", new MLocation("testUser2", defaultLng + 0.01,
                defaultLat + 0.01));
        locationsTable.put("testUser3", new MLocation("testUser3", defaultLng - 0.02,
                defaultLat + 0.02));
        locationsTable.put("testUser4", new MLocation("testUser4",
                defaultLng - 0.01, defaultLat - 0.01));
    }

    private int getTableSize(Tables tableName){

        return tableName == Tables.USERS ? usersTable.size() :
                tableName == Tables.CHATLOGS ? chatLogsTable.size() : locationsTable.size();
    }

    private HashMap<String,DatabaseObject> getTable(Tables tableName){

        return (HashMap<String, DatabaseObject>) (tableName == Tables.USERS ? usersTable :
                        tableName == Tables.CHATLOGS ? chatLogsTable : locationsTable);
    }
}