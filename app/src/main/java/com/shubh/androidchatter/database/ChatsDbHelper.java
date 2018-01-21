package com.shubh.androidchatter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.shubh.androidchatter.extra_classes.DbKeys;

import java.util.Date;

/**
 * Created by root on 17/6/17.
 */

public class ChatsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASEVERSION=1;
    // messages
    private static final String CREAT_TABLE= "create table "+ DbKeys.TABLE_NAME_MASSEGE+
            "(id integer primary key autoincrement,"
            +DbKeys.MSG_DATA+" text,"
            +DbKeys.SEND_TO+" text, "
            +DbKeys.SEND_FROM+" text,"
            +DbKeys.SEND_TO_NAME+" text, "
            +DbKeys.SEND_FROM_NAME+" text,"
            +DbKeys.MSG_TIME+" long,"
            +DbKeys.SYNC_STATUS+" integer);";
    private static final String DROP_TABLE="drop table if exists "+DbKeys.TABLE_NAME_MASSEGE;
// friendlist
    private static final String CREAT_TABLE_FRIENDSLIST= "create table "+ DbKeys.TABLE_NAME_FRIENDLIST+
            "(id integer primary key autoincrement,"
            +DbKeys.FRIENDEMAIL+" text);";
    private static final String DROP_TABLE_FRIENDSLIST="drop table if exists "+DbKeys.TABLE_NAME_FRIENDLIST;



    // chats
    private static final String CREAT_TABLE_CHATS= "create table "+ DbKeys.TABLE_NAME_CHATS+
            "(id integer primary key autoincrement,"
            +DbKeys.CHAT_TO+" text,"
            +DbKeys.SYNC_STATUS+" integer,"
            +DbKeys.CHAT_NAME+" text); ";
    private static final String DROP_TABLE_CHATS="drop table if exists "+DbKeys.TABLE_NAME_CHATS;


    public ChatsDbHelper(Context context) {
        super(context,DbKeys.DATABASE_NAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_TABLE);
        db.execSQL(CREAT_TABLE_CHATS);
        db.execSQL(CREAT_TABLE_FRIENDSLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        db.execSQL(DROP_TABLE_CHATS);
        db.execSQL(DROP_TABLE_FRIENDSLIST);
        onCreate(db);
    }
    public void saveMsgToLocalDb(String data,String sendTo,String sendFrom,String sendToName,String sendFromName,int syncStatus,long time,SQLiteDatabase db){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DbKeys.SEND_TO,sendTo);
        contentValues.put(DbKeys.MSG_DATA,data);
        contentValues.put(DbKeys.SEND_FROM_NAME,sendFromName);
        contentValues.put(DbKeys.SEND_TO_NAME,sendToName);
        contentValues.put(DbKeys.SEND_FROM,sendFrom);
        contentValues.put(DbKeys.SYNC_STATUS,syncStatus);
        contentValues.put(DbKeys.MSG_TIME,time);
        db.insert(DbKeys.TABLE_NAME_MASSEGE,null,contentValues);
    }
    public Cursor readMsgFromLocalDb(SQLiteDatabase db){
        String [] projection={DbKeys.MSG_DATA,DbKeys.SEND_TO,DbKeys.SEND_FROM,DbKeys.SYNC_STATUS,DbKeys.SEND_TO_NAME,DbKeys.SEND_FROM_NAME,DbKeys.MSG_TIME};
        return (db.query(DbKeys.TABLE_NAME_MASSEGE,projection,null,null,null,null,null));
    }
    public void updateMsgLocalDataBase(String msgFrom,String msgTo,long msgTime,int syncStatus,SQLiteDatabase db){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DbKeys.SYNC_STATUS,syncStatus);
        String selection=DbKeys.MSG_TIME+" = ? AND "+DbKeys.SEND_FROM+ "= ? AND " +DbKeys.SEND_TO+ "= ?";
        String[] selection_agrs={msgTime+"",msgFrom,msgTo};
        db.update(DbKeys.TABLE_NAME_MASSEGE,contentValues,selection,selection_agrs);
    }
    public void saveChatToLocalDb(String name,String chat_to,int sync,SQLiteDatabase db){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DbKeys.CHAT_TO,chat_to);
        contentValues.put(DbKeys.CHAT_NAME,name);
        contentValues.put(DbKeys.SYNC_STATUS,sync);
        db.insert(DbKeys.TABLE_NAME_CHATS,null,contentValues);
    }
    public Cursor readChatFromLocalDb(SQLiteDatabase db){
        String [] projection={DbKeys.CHAT_NAME,DbKeys.CHAT_TO, DbKeys.SYNC_STATUS};
        return (db.query(DbKeys.TABLE_NAME_CHATS,projection,null,null,null,null,"id DESC"));
    }
    public Cursor readFRiendsFromLocalDb(SQLiteDatabase db){
        String [] projection={DbKeys.FRIENDEMAIL};
        return (db.query(DbKeys.TABLE_NAME_FRIENDLIST,projection,null,null,null,null,"id DESC"));
    }
    public void deleteChat(String chatTo,SQLiteDatabase db){
        String selection=DbKeys.CHAT_TO+" = ?";
        String[] selection_agrs={chatTo};
        db.delete(DbKeys.TABLE_NAME_CHATS,selection,selection_agrs);
        deleteMsgOfSelectedUser(chatTo,db);
    }

    public void deleteMsgOfSelectedUser(String To,SQLiteDatabase db){
        String selection=DbKeys.SEND_FROM+ "= ? OR " +DbKeys.SEND_TO+ "= ?";
        String[] selection_agrs={To,To};
        db.delete(DbKeys.TABLE_NAME_MASSEGE,selection,selection_agrs);
        }

    public void deleteFriendsUser(SQLiteDatabase db){
        String selectQuery = "DELETE FROM "+DbKeys.TABLE_NAME_FRIENDLIST;
        db.execSQL(selectQuery);
    }

    public boolean CheckChatExist(String chatTo,SQLiteDatabase db){
        String selectQuery = "SELECT "+DbKeys.CHAT_NAME+" FROM "+DbKeys.TABLE_NAME_CHATS+" WHERE "+DbKeys.CHAT_TO+" = ?";
        Cursor c = db.rawQuery(selectQuery, new String[] { chatTo });
        if (c.moveToFirst()) {
            c.close();
            return false;
        }
        else {
            c.close();
            return true;
        }
    }


    public boolean CheckFriendExist(String email,SQLiteDatabase db){

        String selectQuery = "SELECT "+DbKeys.FRIENDEMAIL+" FROM "+DbKeys.TABLE_NAME_FRIENDLIST+" WHERE "+DbKeys.FRIENDEMAIL+" = ?";
        Cursor c = db.rawQuery(selectQuery, new String[] { email});
        if (c.moveToFirst()) {
            c.close();
            return true;
        }
        else {
            c.close();
            return false;
        }
        //temp_address = c.getString(c.getColumnIndex("lastchapter"));
    }
    public void saveFriendToLocalDb(String email,SQLiteDatabase db){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DbKeys.FRIENDEMAIL,email);;
        db.insert(DbKeys.TABLE_NAME_FRIENDLIST,null,contentValues);
    }
    public void removeFriendToLocalDb(String email,SQLiteDatabase db){
        String selection=DbKeys.FRIENDEMAIL+ "= ? ";
        String[] selection_agrs={email};
        db.delete(DbKeys.TABLE_NAME_FRIENDLIST,selection,selection_agrs);
    }
    public Boolean checkMsgExst(String time,String to,String from,SQLiteDatabase db) {
        String selectQuery = "SELECT "+ DbKeys.MSG_TIME +" FROM "+DbKeys.TABLE_NAME_MASSEGE+" WHERE "+DbKeys.SEND_TO+" = ? AND "+DbKeys.SEND_FROM+" = ? AND "+DbKeys.MSG_TIME+" = ?";
        Cursor c = db.rawQuery(selectQuery, new String[] {to,from,time});
        if (c.moveToFirst()) {
            c.close();
            Log.e("db msg","msg foound");
            return false;
        }
        else {
            c.close();
            Log.e("db msg","msg not found");
            return true;
        }
    }
}