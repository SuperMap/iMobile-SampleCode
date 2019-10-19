package com.supermap.imobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Cookie;

import java.util.ArrayList;
import java.util.List;

public class ListDataSave {

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    private static Context mContext = null;
    private static ListDataSave mListDataSave = null;

    private ListDataSave(Context mContext) {
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static ListDataSave getInstance(String spName) {
        if (mListDataSave == null) {
            mListDataSave = new ListDataSave(mContext);
        }
        preferences = mContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
        editor = preferences.edit();
        return mListDataSave;
    }

    /**
     * 保存Cookie List
     *
     * @param tag
     * @param datalist
     */
    public void setDataList(String tag, List<Cookie> datalist) {
        if (null == datalist || datalist.size() <= 0){
            return;
        }
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();
    }

    /**
     * 获取Cookie List
     *
     * @param tag
     * @return
     */
    public List<Cookie> getDataList(String tag) {
        List<Cookie> datalist = new ArrayList<Cookie>();
        String strJson = preferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<Cookie>>() {
        }.getType());
        return datalist;
    }
}
