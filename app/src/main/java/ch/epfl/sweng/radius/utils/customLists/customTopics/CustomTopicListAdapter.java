package ch.epfl.sweng.radius.utils.customLists.customTopics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.radius.R;
import ch.epfl.sweng.radius.database.ChatLogs;
import ch.epfl.sweng.radius.database.Database;
import ch.epfl.sweng.radius.database.MLocation;
import ch.epfl.sweng.radius.database.UserInfo;
import ch.epfl.sweng.radius.utils.customLists.CustomListAdapter;
import ch.epfl.sweng.radius.utils.customLists.CustomListItem;
import ch.epfl.sweng.radius.utils.customLists.customGroups.CustomGroupListListeners;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomTopicListAdapter extends CustomListAdapter {

    private static final int TOPIC_ITEM = 1;
    private static final int TOPIC_CREATE_BUTTON = 2;

    public CustomTopicListAdapter(List<CustomListItem> items, Context context) {
        super(items, context);
        items.add(0, new CustomListItem("Dummy","Dummy","Dummy"));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TOPIC_CREATE_BUTTON;
        }
        return TOPIC_ITEM;
    }

    @NonNull
    @Override
    public CustomListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TOPIC_CREATE_BUTTON) {
            return new TopicCreateButtonHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.create_topic, null));
        }
        return new TopicItemHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_list_item_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case TOPIC_ITEM:
                TopicItemHolder topicItemHolder = (TopicItemHolder) viewHolder;
                Log.e("CustomTopicListAdapter", "Items topics size :" + items.size());
                topicItemHolder.textViewTitle.setText(items.get(position).getItemName());
                CustomListItem item = items.get(position);
                CustomTopicListListeners customListener = new CustomTopicListListeners(item.getItemId(),
                        item.getItemName(), item.getConvId());
                customListener.setCustomOnClick(topicItemHolder.textViewTitle, context);
                break;
            case TOPIC_CREATE_BUTTON:
        }
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class TopicItemHolder extends ViewHolder {
        TextView textViewTitle;
        ImageView imgViewIcon;

        TopicItemHolder(View itemLayoutView) {
            super(itemLayoutView);
            textViewTitle = itemLayoutView.findViewById(R.id.username);
            imgViewIcon = (CircleImageView) itemLayoutView.findViewById(R.id.profile_picture);
        }
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class TopicCreateButtonHolder extends ViewHolder {
        TextView textViewTitle;
        Button createTopicButton;

        TopicCreateButtonHolder(View itemLayoutView) {
            super(itemLayoutView);
            textViewTitle = itemLayoutView.findViewById(R.id.create_topic);

            createTopicButton = itemLayoutView.findViewById(R.id.create_topic_button);
            createTopicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createPrompt(view);
                }
            });
        }
    }

    private static void createPrompt(View view) {
        // get prompts.xml view
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        View promptsView = inflater.inflate(R.layout.topic_prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String topicName = userInput.getText().toString();
                                pushTopicToDatabase(topicName);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private static void pushTopicToDatabase(String topicName) {
        if (!topicName.isEmpty()) {
            MLocation newTopic = new MLocation(topicName);

            // new topic is set by user location values
            newTopic.setLocationType(2); // topic type
            newTopic.setLatitude(UserInfo.getInstance().getCurrentPosition().getLatitude());
            newTopic.setLongitude(UserInfo.getInstance().getCurrentPosition().getLongitude());
            newTopic.setRadius(UserInfo.getInstance().getCurrentPosition().getRadius());
            Database.getInstance().writeInstanceObj(newTopic, Database.Tables.LOCATIONS);

            ChatLogs topicChatLog = new ChatLogs(topicName);
            topicChatLog.addMembersId(UserInfo.getInstance().getCurrentUser().getID()); // creator is a member
            Database.getInstance().writeInstanceObj(topicChatLog, Database.Tables.CHATLOGS);
        }
    }

}