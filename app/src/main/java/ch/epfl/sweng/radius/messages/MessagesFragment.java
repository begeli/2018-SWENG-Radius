package ch.epfl.sweng.radius.messages;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.radius.R;
import ch.epfl.sweng.radius.browseProfiles.ChatListItem;
import ch.epfl.sweng.radius.browseProfiles.CustomAdapter;
import ch.epfl.sweng.radius.database.CallBackDatabase;
import ch.epfl.sweng.radius.database.ChatLogs;
import ch.epfl.sweng.radius.database.Database;
import ch.epfl.sweng.radius.database.Message;
import ch.epfl.sweng.radius.database.User;
import java.util.Date;



public class MessagesFragment extends Fragment {
    List<ChatListItem> chatList;
    ListView listView;
    HashMap<String, String> chatLogsId;

    public MessagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagesFragment newInstance(String param1, String param2) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w("MessageFragment" , "Just got onCreateViewed0");

        User user = new User(Database.getInstance().getCurrent_user_id());
        Date date = new Date();
        ChatLogs chat = new ChatLogs("chatTest0");
        chat.addMessage(new Message("userTest0", "Hellow", new Date()));
        Database.getInstance().writeInstanceObj(chat, Database.Tables.CHATLOGS);
        user.addChat("userTest0", "chatTest0");

        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        Database.getInstance().readObjOnce(user,
                Database.Tables.USERS, new CallBackDatabase<Object>() {
            @Override
            public void onFinish(Object value) {
                User user = (User) value;
                chatLogsId = (HashMap<String, String>) user.getChatList();
            }

            @Override
            public void onError(DatabaseError error) {

            }
        });

        chatList = new ArrayList<>();
        listView = view.findViewById(R.id.listView);

        chatList.add(new ChatListItem(R.drawable.image1, new User()));
        chatList.add(new ChatListItem(R.drawable.image2, new User()));
        chatList.add(new ChatListItem(R.drawable.image3, new User()));
        chatList.add(new ChatListItem(R.drawable.image4, new User()));
        chatList.add(new ChatListItem(R.drawable.image5, new User()));

        CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.chat_list_view, chatList);
        listView.setAdapter(adapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Object objecty = listView.getItemAtPosition(position);
                    /*
                    write you handling code like...
                    String st = "sdcard/";
                    File f = new File(st+o.toString());
                    // do whatever u want to do with 'f' File object
                    */
                String nextChatID = chatLogsId.get("userTest0");
                if(nextChatID == null){
                    chatLogsId.put("otherUserId", "chatTest0");
                    nextChatID = "chatTest0";
                }
                Intent intent = new Intent(getActivity(), MessageListActivity.class);
                Bundle b = new Bundle();
                b.putString("otherUserId", "userTest0");
                b.putString("chatId", nextChatID);
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
            }
        });

        return view;
    }
}
