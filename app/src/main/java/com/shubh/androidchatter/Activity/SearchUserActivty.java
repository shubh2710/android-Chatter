package com.shubh.androidchatter.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.informationModels.Chats;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.informationModels.User;
import com.shubh.androidchatter.adapters.SearchActivityAdapter;
import com.shubh.androidchatter.database.ChatsDbHelper;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;

import java.util.ArrayList;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;
import static com.shubh.androidchatter.extra_classes.DbKeys.PREFNAME;
import static com.shubh.androidchatter.extra_classes.DbKeys.PREFPROFILE;

public class SearchUserActivty extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private SearchActivityAdapter adapter;
    FirebaseDatabase database = new FirebaseInctence().getinstence();
    DatabaseReference chat=database.getReference("chatList");
    DatabaseReference users=database.getReference("users");
    private ArrayList<User> data=new ArrayList<User>();
    SearchView editsearch;
    private boolean ex=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_activty);
        editsearch = (SearchView) findViewById(R.id.searchView);
        editsearch.setFocusable(true);
        editsearch.setIconified(false);
        editsearch.requestFocusFromTouch();
        editsearch.setQueryHint("type key to search products");
        editsearch.setOnQueryTextListener(this);
        //showUsers();
        displayUsers();
    }
    private void displayUsers() {
        ListView listOfUser = (ListView)findViewById(R.id.list_of_Users);
        adapter =new SearchActivityAdapter(this,data);
        listOfUser.setAdapter(adapter);
        listOfUser.setOnItemClickListener(this);
    }

    private void makeArray(final String search) {
        data.clear();
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean e=false;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
                if(snapshot.getChildrenCount()==0){
                    e=false;
                }
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    User post = postSnapshot.getValue(User.class);
                    Log.e("Get Data", post.getEmail());
                    if(post.getEmail().equals(search) || post.getName().contains(search) )
                    data.add(new User(post.getName(),post.getEmail(),post.isProfile(),post.getProfileUrl(),post.getLastseen(),post.getStatus()));
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      /*  User u=data.get(position);
            Intent i=new Intent(this,UserChatActivity.class);
        if(!u.getEmail().equals(readPrefes(this,PREFEMAIL,null))){
            i.putExtra("sendTo",u.getEmail());
            i.putExtra("ChatName",u.getName());
            checkIfExiste(u,readPrefes(this,PREFEMAIL,null));
            startActivity(i);
            }*/
    }
    public static String readPrefes(Context context, String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }
    public void searchUser(String search){
        for(User u:data){
            if(u.getEmail().equals(search) || u.getName().contains(search) ){

            }
        }
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        makeArray(query);
        adapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        makeArray(newText);
        adapter.notifyDataSetChanged();
        return false;
    }
}