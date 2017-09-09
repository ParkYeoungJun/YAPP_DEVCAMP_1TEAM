package com.example.park.yapp_1team.sql;

import android.util.Log;

import com.example.park.yapp_1team.items.SearchListItem;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HunJin on 2017-09-09.
 */

public class RealmRest {

    private static final String TAG = RealmRest.class.getSimpleName();

    private Realm realm;

    public RealmRest() {
        initRealm();
    }

    private void initRealm() {
        realm = Realm.getDefaultInstance();
    }

    public RealmResults<SearchListItem> getUserList(){
        return realm.where(SearchListItem.class).findAll();
    }

    public RealmResults<SearchListItem> getUserList(String location) {
        return realm.where(SearchListItem.class).equalTo("location",location).findAll();
    }

    public void insertUserData(String location){
        realm.beginTransaction();

        RealmResults<SearchListItem> tmpItem = getUserList(location);
        RealmResults<SearchListItem> tmpdelete = getUserList();

        if(tmpdelete.size() > 2)
        {
            Log.e("delete", "in");
            tmpdelete.first().deleteFromRealm();
        }

        if(tmpItem.size() == 0) {
            realm.createObject(SearchListItem.class, location);
        }

        realm.commitTransaction();
    }

    public void closeRealm() {
        realm.close();
    }


}