package ch.epfl.sweng.radius.utils.customLists.customGroups;

import android.content.Context;
import android.util.Log;

import java.util.List;

import ch.epfl.sweng.radius.database.MLocation;
import ch.epfl.sweng.radius.database.OthersInfo;
import ch.epfl.sweng.radius.utils.customLists.CustomListAdapter;
import ch.epfl.sweng.radius.utils.customLists.CustomListItem;

public class CustomGroupListAdapter extends CustomListAdapter{

    public CustomGroupListAdapter(List<CustomListItem> items, Context context) {
        super(items,context);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {


        Log.e("CustomGroupListAdapter", "Items groups size :" + items.size());



        CustomListItem item = items.get(position);
        final String groupId = item.getItemId();
        final String groupName = item.getItemName();
        final String convId = item.getConvId();

        MLocation itemGroup = OthersInfo.getInstance().getGroupsPos().get(item.getItemId());

        if (viewHolder.txtViewTitle != null) {
            viewHolder.txtViewTitle.setText(groupName);
        }
        if (viewHolder.txtViewStatus != null) {
            viewHolder.txtViewStatus.setText("");
        }
        setIcon(viewHolder,position,itemGroup);

        CustomGroupListListeners customListener = new CustomGroupListListeners(groupId,groupName,convId);
        customListener.setCustomOnClick(viewHolder.linearLayout_name, context);
    }

}
