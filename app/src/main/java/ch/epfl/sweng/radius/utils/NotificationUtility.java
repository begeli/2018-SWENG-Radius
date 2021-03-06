package ch.epfl.sweng.radius.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ch.epfl.sweng.radius.R;
import ch.epfl.sweng.radius.database.ChatLogs;
import ch.epfl.sweng.radius.database.Message;
import ch.epfl.sweng.radius.database.OthersInfo;
import ch.epfl.sweng.radius.database.UserInfo;

public class NotificationUtility {

    private static int unseenMsg = 0;
    private static int unseenReq = 0;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder msgNotifBuilder;
    private NotificationCompat.Builder reqNotifBuilder;
    private NotificationCompat.Builder nearFriendNotifBuilder;
    private static NotificationUtility instance;


    public NotificationUtility(NotificationManager nm, NotificationCompat.Builder msgNotif, NotificationCompat.Builder reqNotif,
                               NotificationCompat.Builder nearFriendNotif) {
        this.notificationManager = nm;

        this.msgNotifBuilder = msgNotif
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Radius")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        this.reqNotifBuilder = reqNotif
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Radius")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        this.nearFriendNotifBuilder = nearFriendNotif
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Radius")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
    }

    public static NotificationUtility getInstance(NotificationManager nm, NotificationCompat.Builder msgNotif, NotificationCompat.Builder reqNotif,
                                                  NotificationCompat.Builder nearFriendNotif) {
        if (instance == null)
            instance = new NotificationUtility(nm, msgNotif, reqNotif, nearFriendNotif);
        return instance;
    }

    public void resetUnseenMsg() {
        unseenMsg = 0;
    }

    public void resetUnseenReq() {
        unseenMsg = 0;
    }

    public static void clearSeenMsg(int num) {
        if (unseenMsg >= num) unseenMsg -= num;
    }


    public void notifyNewMessage(String senderId, String content, PendingIntent pi) {
        msgNotifBuilder.setContentText("New Message : " + content)
                .setContentIntent(pi)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(senderId);
        unseenMsg++;
        notificationManager.notify(1, msgNotifBuilder.build());
        Log.d("NewMessageNotif", "New Message : " + content);
    }

    public void notifyNewFrienReq(String userID, String userNickname, PendingIntent pi) {
        unseenMsg++;
        reqNotifBuilder.setContentText("New Friend Request from " + userNickname + " (" + userID + ")")
                .setSmallIcon(android.R.drawable.alert_dark_frame)
                .setContentTitle("Radius Friend Request")
                .setContentIntent(pi);
        notificationManager.notify(2, reqNotifBuilder.build());
        Log.d("NewFriendReqNotif", "New Friend Request from " + userNickname + " (" + userID + ")");
    }

    public void notifyFriendIsNear(String userID, String userNickname, PendingIntent pi) {
        nearFriendNotifBuilder.setContentText("Your friend " + userNickname + " (" + userID + ")" + "is in the Radius!")
                .setSmallIcon(android.R.drawable.alert_dark_frame)
                .setContentTitle("Radius Friend Is Near")
                .setContentIntent(pi);
        notificationManager.notify(3, nearFriendNotifBuilder.build());
        Log.d("NearFriendNotif", "Your friend " + userNickname + " (" + userID + ")" + "is in the Radius!");
    }

    public static String getChatTitle(ChatLogs chatlogs, int chatType) {
        String ret = "Anonymous";
        switch (chatType) {
            case 0: // user
                ret = handleUserTitle(chatlogs);
                break;
            case 1: // group
                ret = chatlogs.getID();
                break;
            case 2: // topic
                ret = OthersInfo.getInstance().getTopicsPos().get(chatlogs.getID()).getTitle();
                break;
            default:
                break;
        }
        return ret;
    }

    public static String getChatTitleNotification(ChatLogs chatlogs, Message message, int chatType) {
        String ret = getChatTitle(chatlogs, chatType);
        switch (chatType) {
            case 1: // group
                ret = ret + " : " + OthersInfo.getInstance().getAllUserLocations().get(message.getSenderId()).getTitle();
                break;
            case 2: // topic
                ret = ret + " : " + OthersInfo.getInstance().getAllUserLocations().get(message.getSenderId()).getTitle();
                break;
            default:
                break;
        }
        return ret;
    }

    private static String handleUserTitle(ChatLogs chatlogs) {
        String currentUserId = UserInfo.getInstance().getCurrentUser().getID();
        String otherUserId = "";
        String ret = "Anonymous";
        for (String id : chatlogs.getMembersId()) {
            if (!id.equals(currentUserId)) {
                otherUserId = id;
            }
        }

        if (!otherUserId.isEmpty() && OthersInfo.getInstance().getAllUserLocations()
                .containsKey(otherUserId)) {
            ret = OthersInfo.getInstance().getAllUserLocations().get(otherUserId).getTitle();
        }

        return ret;
    }
}
