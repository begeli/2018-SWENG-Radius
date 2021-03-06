package ch.epfl.sweng.radius.messages;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.radius.R;
import ch.epfl.sweng.radius.database.ChatLogs;
import ch.epfl.sweng.radius.database.ChatlogsUtil;
import ch.epfl.sweng.radius.database.DBLocationObserver;
import ch.epfl.sweng.radius.database.DBUserObserver;
import ch.epfl.sweng.radius.database.Database;
import ch.epfl.sweng.radius.database.MLocation;
import ch.epfl.sweng.radius.database.Message;
import ch.epfl.sweng.radius.database.OthersInfo;
import ch.epfl.sweng.radius.database.UserInfo;
import ch.epfl.sweng.radius.utils.MapUtility;
import ch.epfl.sweng.radius.utils.NotificationUtility;

/**
 * Activity that hosts messages between two users
 * MessageListActivity and MessageListAdapter and some layout files are inspired from https://blog.sendbird.com/android-chat-tutorial-building-a-messaging-ui
 */
public class MessageListActivity extends AppCompatActivity implements DBLocationObserver, DBUserObserver {
    private final int MAX_SIZE_MESSAGE = 512;
    private RecyclerView myMessageRecycler;
    private MessageListAdapter myMessageAdapter;
    private EditText messageZone;
    private Button sendButton;
    private ChatLogs chatLogs;
    private String chatId, otherUserId, myID;
    private int locType;
    private static HashMap<String, MessageListActivity> chatInstance = new HashMap<>();
    private static int UNIQUE_INT_PER_CALL = 0;
    private MLocation otherLoc;
    private ChatState isChatRunning = null;
    private Context context;
    public boolean uiReady = false;
    private boolean isEnabled = true;

    public MessageListActivity(){}

    public MessageListActivity(ChatLogs chatLogs, Context context, int locType){
        // Just create entry to avoid duplicate activities
        OthersInfo.getInstance().addLocationObserver(this);
        if(MessageListActivity.getChatInstance(chatLogs.getID()) == null){
            this.otherUserId = ChatLogs.getOtherID(chatLogs);
            this.chatId = chatLogs.getID();
            this.chatLogs = chatLogs;
            this.locType =locType;
            chatInstance.put(chatId, this);
            isChatRunning = new ChatState();
            isChatRunning.leaveActivity();
            this.context = context;
        }

    }

    public static MessageListActivity getChatInstance(String chatID){
        return chatInstance.get(chatID);
    }

    public void showNotification(String content, String senderId, String chatId) {
        // Setup Intent to end here in case of click
        Intent notifIntent = new Intent(context, MessageListActivity.class);
        Bundle b = new Bundle();b.putString("chatId", chatId);
        b.putString("otherId", otherUserId);b.putInt("locType", locType);
        notifIntent.putExtras(b);notifIntent.setAction(chatId);

        PendingIntent pi = PendingIntent.getActivity(context, 0,notifIntent, 0);
        // Build and show notification
        NotificationUtility.getInstance(null, null, null, null)
                .notifyNewMessage(senderId, content, pi);
    }
    /**
     * Get all infos needed to create the activity
     * We get the chatId and otherUserId from the parent fragment
     */
    private void setInfo() {
        Bundle b = getIntent().getExtras();

        //Get infos from parent fragment

        if (b != null) {
            chatId = b.getString("chatId"); locType = b.getInt("locType");
            chatInstance.put(chatId, this);

            chatLogs = ChatlogsUtil.getInstance().getChat(chatId, locType);
            otherUserId = ChatLogs.getOtherID(chatLogs);
            for(int i = 0; i < chatLogs.getMembersId().size(); i++){
                String s =chatLogs.getMembersId().get(i); addMembersInfo(s);
            }

        } else {
            throw new RuntimeException("MessagListActivity Intent created without bundle");
        }

    }

    public ChatState getIsChatRunning() {
        return isChatRunning;
    }

    /**
     * Set up the interface
     */
    private void setUpUI() {
        if(chatLogs == null) chatLogs = ChatlogsUtil.getInstance().getChat(chatId, locType);
        setContentView(R.layout.activity_message_list);
        getSupportActionBar().setTitle(NotificationUtility.getChatTitle(chatLogs, locType));
        messageZone = (EditText) findViewById(R.id.edittext_chatbox);
        myMessageAdapter = new MessageListAdapter(this, chatLogs.getMessages(),chatLogs.getMembersId());
        myMessageRecycler = findViewById(R.id.reyclerview_message_list);
        myMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        myMessageRecycler.setAdapter(myMessageAdapter);
        myMessageRecycler.scrollToPosition(chatLogs.getMessages().size()-1);

        uiReady = true;

    }


    /**
     * add a message in the chatlogs and notify the adapter
     *
     * @param message the new message
     */
    public void receiveMessage(Message message) {
        if(!uiReady) return;
        myMessageAdapter.setMessages(chatLogs.getMessages());
        myMessageRecycler.smoothScrollToPosition(chatLogs.getMessages().size());
        myMessageAdapter.notifyDataSetChanged();
    }

    public void addMembersInfo(String membersId){
        if(!chatLogs.getMembersId().contains(membersId)){
            chatLogs.addMembersId(membersId);
        }
        if(!uiReady) return;
        myMessageAdapter.setMembersIds(chatLogs.getMembersId());
        myMessageRecycler.smoothScrollToPosition(chatLogs.getMessages().size());
        myMessageAdapter.notifyDataSetChanged();
    }

    /**
     * push a message in the table
     *
     * @param senderId the senderId
     * @param message  the message
     * @param date     the date
     */
    private void sendMessage(String senderId, String message, Date date) {
        if(message.length()>MAX_SIZE_MESSAGE){
            message = message.substring(0,MAX_SIZE_MESSAGE);
        }
        if (!message.isEmpty()) {
            Message msg = new Message(senderId, message, date);
            chatLogs.addMessage(msg);

            List<Message> newList = chatLogs.getMessages();
            Database.getInstance().writeToInstanceChild(chatLogs, Database.Tables.CHATLOGS, "messages",
                    chatLogs.getMessages());

            messageZone.setText("");receiveMessage(msg);
        }

    }

    /**
     * If the button is clicked, add the message to the db
     */
    private void setUpSendButton() {
        sendButton = findViewById(R.id.button_chatbox_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageZone.getText().toString();
                sendMessage(myID, message, new Date());
            }
        });

    }

    private void toggleFlagAndSendingFields(boolean newState){
        if(newState != isEnabled) {
            setEnabled(newState); isEnabled = newState; }
    }

    private void compareLocation() {
        if(locType != 0) {// || chatLogs.getMembersId().size() != 2 ) {
            toggleFlagAndSendingFields(true);
        }
        else{
            if(otherUserId != null)
                handleUserChat();
            else
                toggleFlagAndSendingFields(false);
        }
    }

    private void handleUserChat() {
        boolean unBlockedAndVisible = OthersInfo.getInstance().getAllUserLocations().get(otherUserId).getVisible()
                    && !OthersInfo.getInstance().getUsers().get(otherUserId).getBlockedUsers().
                    contains(UserInfo.getInstance().getCurrentUser().getID());

        MapUtility mapUtility = new MapUtility();
        double latitude = OthersInfo.getInstance().getAllUserLocations().get(otherUserId).getLatitude();
        double longtitude = OthersInfo.getInstance().getAllUserLocations().get(otherUserId).getLongitude();
        boolean isInRadius = mapUtility.contains(latitude, longtitude);
        boolean deleted = OthersInfo.getInstance().getAllUserLocations().get(otherUserId).getDeleted();

        toggleFlagAndSendingFields(!deleted && unBlockedAndVisible && isInRadius);
    }

    public void setEnabled(boolean enableChat) {
        if(!uiReady) return;

        if (!enableChat) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageZone.setText(getString(R.string.chat_disabled));
                    sendButton.setEnabled(false);messageZone.setFocusable(false);

                }
            });
        }
        else{
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageZone.setText(""); messageZone.setFocusable(true);
                    sendButton.setEnabled(true);
                }
            });
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        String chatId = getIntent().getExtras().getString("chatId");
        if(chatId == null) return;

        isChatRunning = new ChatState();

        if(MessageListActivity.getChatInstance(chatId) == null){
            chatInstance.put(chatId, this);
            return;
        }
        NotificationUtility.clearSeenMsg(isChatRunning.getUnreadMsg());
        isChatRunning.clear();
    }

    @Override
    public void onPause(){
        super.onPause();
        isChatRunning.leaveActivity();
        OthersInfo.getInstance().removeLocationObserver(this);
        OthersInfo.getInstance().removeUserObserver(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OthersInfo.getInstance().addUserObserver(this);
        OthersInfo.getInstance().addLocationObserver(this);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightMode = settings.getBoolean("nightModeSwitch", false);

        String temp = getIntent().getExtras().getString("chatId");
            setTheme(R.style.LightTheme);

        if(MessageListActivity.getChatInstance(temp) != null){
            this.locType = MessageListActivity.getChatInstance(temp).locType;
            this.chatId = MessageListActivity.getChatInstance(temp).chatId;
            this.otherUserId = MessageListActivity.getChatInstance(temp).otherUserId;
            this.uiReady = MessageListActivity.getChatInstance(temp).uiReady;
            this.myMessageAdapter = MessageListActivity.getChatInstance(temp).myMessageAdapter;
            this.myMessageRecycler = MessageListActivity.getChatInstance(temp).myMessageRecycler;
        }
        this.context = this;

        if(isChatRunning == null) isChatRunning = new ChatState();
        myID = UserInfo.getInstance().getCurrentUser().getID();
        setContentView(R.layout.activity_message_list);
        messageZone = findViewById(R.id.edittext_chatbox);

        setInfo();setUpUI();setUpSendButton();

        compareLocation();
    }

    @Override
    protected void onStop() { super.onStop(); }

    @Override
    public void onLocationChange(String id) { if(chatId != null){ compareLocation(); } }

    @Override
    public void onUserChange(String id) { if(chatId != null) compareLocation(); }
}
