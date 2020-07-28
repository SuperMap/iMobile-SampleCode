package com.supermap;

/**
 *
 */
public class GroupManager {

    private static final String TAG = "GroupManager";

    public static String mCurrentUserId = null;   //当前用户ID
    public static String mCurrentUserName = null; //当前用户昵称

    public static String getCurrentUserId() {
        return mCurrentUserId;
    }

    public static void setCurrentUserId(String currentUserId) {
        GroupManager.mCurrentUserId = currentUserId;
    }

    public static String getCurrentUserName() {
        return mCurrentUserName;
    }

    public static void setCurrentUserName(String mCurrentUserName) {
        GroupManager.mCurrentUserName = mCurrentUserName;
    }



}
