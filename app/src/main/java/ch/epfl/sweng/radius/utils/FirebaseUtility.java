/** Represents an interface to communicate with the Firebase Realtime Database
 * @usage : 1 . Instantiate class by providing the object to be exchanged with the database
 *          2 . Add a listener to this object by calling listen[User, Message, ChatLogs]
 *          3 . Update the value on the database by first updating the object and then call
 *                  write[User, Message, ChatLogs]
 *              Write a new value in the database by calling
 *                  write[User, Message, ChatLogs]([User,Message, ChatLogs] new_value)
 * TODO : Add methods to add more than one listener for each instance
 * TODO : Add method to check if user is new
 */
package ch.epfl.sweng.radius.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Semaphore;

import ch.epfl.sweng.radius.database.ChatLogs;
import ch.epfl.sweng.radius.database.DatabaseObject;
import ch.epfl.sweng.radius.database.Message;
import ch.epfl.sweng.radius.database.User;

public class FirebaseUtility {

    private FirebaseDatabase fireDB;
    private FirebaseAuth     auth;

    private DatabaseReference   database;

    private DatabaseObject obj;
    private User        user;
    private Message     msg;
    private ChatLogs    chatLogs;
    private Semaphore   semaphore;

    /**
     *
     * @param user User to listen/update
     */
    public FirebaseUtility(User user){

        // Instanciate References Object
        initDB();
        this.user      = user;
        this.database  = fireDB.getReference("users");

    }

    public FirebaseUtility(DatabaseObject obj, String ref){
        initDB();
        this.obj = obj;
        this.database = fireDB.getReference(ref);
    }

    /**
     *
     * @param dataType Message listen/update
     */
    public FirebaseUtility(Message dataType){

        // Instanciate References Object
        initDB();
        this.msg        = dataType;
        this.database   = fireDB.getReference("messages");

    }

    /**
     *
     * @param dataType ChatLogs to listen/update
     */
    public FirebaseUtility(ChatLogs dataType){

        // Instanciate References Object
        initDB();
        this.chatLogs    = dataType;
        this.database    = fireDB.getReference("chatlogs");

    }

    private void initDB(){
        this.auth       = FirebaseAuth.getInstance();
        this.fireDB     = FirebaseDatabase.getInstance();
        this.semaphore = new Semaphore(0);
    }
    // TODO : #Salezer, must be fixed

    /**
     * Checks whether the current user is a new user. If that's the case, it creates a new user
     *      and pushes it to the database
     * @return Whether the user is new or not
     */
    public boolean isNew(){

        final boolean[] newUser = new boolean[1];
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(user.getUserID())) {
                    System.out.println("is not new");

                    newUser[0] = false;
                }
                else
                    newUser[0] = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        while(newUser == null);

        return newUser[0];
    }


    public void readObj() throws InterruptedException {

        database.child(obj.getID()).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void  onDataChange(@NonNull DataSnapshot dataSnapshot) {
                obj = dataSnapshot.getValue(obj.getClass());
                semaphore.release();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "Failed to read user", databaseError.toException());

            }
        });
        semaphore.acquire();

    }

    public void listenInstanceObject() throws InterruptedException {

        database.child(obj.getID()).addValueEventListener( new ValueEventListener() {
            @Override
            public void  onDataChange(@NonNull DataSnapshot dataSnapshot) {
                obj = dataSnapshot.getValue(obj.getClass());
                semaphore.release();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "Failed to read user", databaseError.toException());

            }
        });
        semaphore.acquire();

    }

    public DatabaseObject readOtherObject(DatabaseObject otherObj) throws InterruptedException {

        final DatabaseObject[] ret = new DatabaseObject[1];
        database.child(otherObj.getID()).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void  onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ret[0] = dataSnapshot.getValue(obj.getClass());
                semaphore.release();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "Failed to read object", databaseError.toException());

            }
        });
        semaphore.acquire();

        return ret[0];
    }

    public void writeInstanceObj(){ database.child(obj.getID()).setValue(obj); }

    public void writeOtherObj(DatabaseObject otherObj){ database.child(otherObj.getID()).setValue(otherObj);    }

    public void listenUser() throws InterruptedException {

        ValueEventListener  listener;

        database.child(user.getUserID()).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void  onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Log.w("Firebase", "User data has been read.");
                Log.w("Firebase", getUser().getStatus());
                semaphore.release();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "Failed to read user", databaseError.toException());

            }
        });
        semaphore.acquire();

    }

    public void writeUser(){

        database.child(user.getUserID()).setValue(user);

        return;

    }

    public void removeUser(){

        database.child(user.getUserID()).setValue(null);

        return;

    }

    public void writeUser(User new_user) {

        database.child(new_user.getUserID()).setValue(new_user);

        return;

    }

    public void listenChatLogs() throws InterruptedException {

        ValueEventListener  listener;

        listener = new ValueEventListener() {
            @Override
            public void  onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatLogs = dataSnapshot.getValue(ChatLogs.class);
                semaphore.release();
                Log.e("Firebase", "Chatlogs data has been read.");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read Chatlogs", databaseError.toException());

            }
        };
        // TODO Fix ID For Chatlogs
        database.child(chatLogs.getChatLogsId()).addListenerForSingleValueEvent(listener);
        semaphore.acquire();
        return;

    }

    public void writeChatLogs()  {

        database.child(chatLogs.getChatLogsId()).setValue(msg);
        return;

    }

    public void writeChatLogs(ChatLogs new_chatlogs) {
        database.child(new_chatlogs.getMembersId().get(0))
                .setValue(new_chatlogs);
        return;

    }

    public User getUser() {

        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}


