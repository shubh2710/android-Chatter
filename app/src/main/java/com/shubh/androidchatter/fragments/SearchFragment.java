package com.shubh.androidchatter.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.Activity.UserChatActivity;
import com.shubh.androidchatter.informationModels.Chats;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.informationModels.User;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;

import java.util.ArrayList;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;
import static com.shubh.androidchatter.extra_classes.DbKeys.PREFNAME;
import static com.shubh.androidchatter.extra_classes.DbKeys.PREFPROFILE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FirebaseListAdapter<User> adapter;
    FirebaseDatabase database = new FirebaseInctence().getinstence();
    DatabaseReference chat=database.getReference("chatList");
    DatabaseReference msg=database.getReference("masseges");
    DatabaseReference groups=database.getReference("groups");
    DatabaseReference users=database.getReference("users");
    private ArrayList<User> data=new ArrayList<User>();
    private boolean ex=false;
    private  ListView listOfUser;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_search, container, false);
        listOfUser = (ListView)layout.findViewById(R.id.list_of_Users);
        makeArray();
        showUsers();


        return layout;
    }
    private void showUsers() {

        adapter = new FirebaseListAdapter<User>(getActivity(), User.class,
                R.layout.user_details_row, users) {
            @Override
            protected void populateView(View v, User model, int position) {
                // Get references to the views of message.xml
                TextView userName = (TextView)v.findViewById(R.id.tv_user_name);
                TextView userEmail = (TextView)v.findViewById(R.id.tv_user_email);

                userName.setText(model.getName());
                userEmail.setText(model.getEmail());
            }
        };
        listOfUser.setAdapter(adapter);
        listOfUser.setOnItemClickListener(this);
    }
    private void makeArray() {

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
                    data.add(new User(post.getName(),post.getEmail(),post.isProfile(),post.getProfileUrl(),post.getLastseen(),post.getStatus()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User u=data.get(position);

        Intent i=new Intent(getActivity(),UserChatActivity.class);
        if(!u.getEmail().equals(readPrefes(getActivity(),PREFEMAIL,null))){
            i.putExtra("sendTo",u.getEmail());
            i.putExtra("ChatName",u.getName());
            checkIfExiste(u,readPrefes(getActivity(),PREFEMAIL,null));
            startActivity(i);
        }
    }
    private void checkIfExiste(final User u, final String myemail) {

        DatabaseReference chatList =chat.child("chats").child(TohashCode(u.getEmail()));
        chatList.keepSynced(true);
        chatList.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean e=false;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
                if(snapshot.getChildrenCount()==0){
                    e=false;
                }
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Chats post = postSnapshot.getValue(Chats.class);
                    Log.d(myemail, post.getChatFor()+" ");
                    if(post.getChatFor().toString().equals(myemail)){
                        e=true;
                        Log.e(myemail,post.getChatFor());
                        break;
                    }
                }
                setcreateUser(e,u,myemail);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }
    void setcreateUser(Boolean e,User u, String myemail)
    {
        if(!e) {
            Chats c = new Chats(readPrefes(getActivity(), PREFNAME, null), readPrefes(getActivity(), PREFEMAIL, null),false, readPrefes(getActivity(), PREFPROFILE, null));
            chat.child("chats").child(TohashCode(u.getEmail())).push().setValue(c);
            Chats c2 = new Chats(u.getName(), u.getEmail(),true,u.getProfileUrl());
            chat.child("chats").child(TohashCode(readPrefes(getActivity(), PREFEMAIL, null))).push().setValue(c2);
        }
    }
    private String TohashCode(String code){
        String str1=code;
        Log.d("hashcode1",str1);
        int hash1=(str1.hashCode());
        Log.d("hashcode1",hash1+"");
        return hash1+"";
    }
    private String encry(String chatTo) {
        String str1=chatTo;
        String str2=readPrefes(getActivity(),PREFEMAIL,null);
        if(str2!=null){
            int hash1=(str1.hashCode());
            int hash2=(str2.hashCode());
            return (hash1+hash2)+"";
        }else return null;
    }
    public static String readPrefes(Context context, String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }

}
