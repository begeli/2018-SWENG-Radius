package ch.epfl.sweng.radius.utils.customLists.customUsers;

import android.content.Context;

import java.util.List;

import ch.epfl.sweng.radius.database.MLocation;
import ch.epfl.sweng.radius.database.OthersInfo;
import ch.epfl.sweng.radius.utils.customLists.CustomListAdapter;
import ch.epfl.sweng.radius.utils.customLists.CustomListItem;

public class CustomUserListAdapter extends CustomListAdapter {

    public CustomUserListAdapter(List<CustomListItem> items, Context context) {
        super(items, context);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        CustomListItem item = items.get(position);
        MLocation itemUser = OthersInfo.getInstance().getAllUserLocations().get(item.getItemId());

        viewHolder.txtViewTitle.setText(item.getItemName());
        viewHolder.txtViewStatus.setText(itemUser.getMessage());

        setIcon(viewHolder,position,itemUser);
        final String clickedId = item.getItemId();
        //Log.e("CustomUserListAdapter", "item.getItemId() :" + item.getItemId());
        CustomUserListListeners customListener = new CustomUserListListeners(item.getProfilePic(), clickedId,item.getItemName());
        customListener.setCustomOnClick(viewHolder.imgViewIcon, context);
        customListener.setCustomOnClick(viewHolder.linearLayout_name, context,clickedId,item.getConvId());
    }


}
