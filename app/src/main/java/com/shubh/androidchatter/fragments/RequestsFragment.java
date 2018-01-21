package com.shubh.androidchatter.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubh.androidchatter.MyApplication;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.adapters.RecycleViewAdapterRequestList;
import com.shubh.androidchatter.extra_classes.FirebaseInctence;
import com.shubh.androidchatter.informationModels.MessagesModel;
import com.shubh.androidchatter.informationModels.Requests;

import java.util.ArrayList;

import static com.shubh.androidchatter.extra_classes.DbKeys.PREFEMAIL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerview;
    public RecycleViewAdapterRequestList adaptor;
    ArrayList<Requests> RequestList;
    FirebaseDatabase database = new FirebaseInctence().getinstence();
    DatabaseReference reqList=database.getReference("requests");
    DatabaseReference msg=database.getReference("masseges");
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public RequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestsFragment newInstance(String param1, String param2) {
        RequestsFragment fragment = new RequestsFragment();
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
        View layout= inflater.inflate(R.layout.activity_request_list, container, false);
        recyclerview=(RecyclerView)layout.findViewById(R.id.list_of_Requests);
        RequestList=new ArrayList<>();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback( ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.e("MOVE SWIPE","onMove");
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        removeRequest(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerview);
        adaptor=new RecycleViewAdapterRequestList(getActivity(),RequestList,getResources());
        recyclerview.setAdapter(adaptor);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        getRequestList();
        checkMyDb();
        return layout;
    }
    public void updateMyLogicOrView(){
        Log.e("on onrefresh view","reqlist");
    }


    private void removeRequest(int pos) {
      Requests req=  RequestList.get(pos);
        updateRequest(req.getEmail());
        RequestList.remove(pos);
        removeMsg(req.getEmail());
    }

    private void removeMsg(String email) {

            final DatabaseReference updateMsg =msg.child("convensation").child(TohashCode(readPrefes(getActivity(),PREFEMAIL,null)));
            updateMsg.keepSynced(true);
            Query query = updateMsg.orderByChild("messageUser").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        MessagesModel post = postSnapshot.getValue(MessagesModel.class);
                                Log.e("message seen",post.getMessageSyncState()+"");
                                updateMsg.child(postSnapshot.getRef().getKey()).removeValue();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    private void updateRequest(final String email) {
        final DatabaseReference RequestListRef=reqList.child(TohashCode(readPrefes(getActivity(),PREFEMAIL,null)));
        RequestListRef.keepSynced(true);
        RequestListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("read Requests" ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Requests req=postSnapshot.getValue(Requests.class);
                    Log.e("Get Data check sycn", req.getName());
                    if(req.getEmail().equals(email)){
                        RequestListRef.child(postSnapshot.getRef().getKey()).removeValue();
                        Toast.makeText(getActivity(),"Request removed",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }

    private void getRequestList() {
        final DatabaseReference RequestListRef=reqList.child(TohashCode(readPrefes(getActivity(),PREFEMAIL,null)));
        RequestListRef.keepSynced(true);
        RequestListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("read Requests" ,""+snapshot.getChildrenCount());
                RequestList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Requests friend=postSnapshot.getValue(Requests.class);
                    Log.e("Get Data check sycn", friend.getName());
                    if(!friend.getSyncStatus())
                    RequestList.add(friend);
                }
                adaptor.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }
    public void checkMyDb(){
        final DatabaseReference RequestListRef=reqList.child(TohashCode(readPrefes(getActivity(),PREFEMAIL,null)));
        RequestListRef.keepSynced(true);
        RequestListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getRequestList();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static String readPrefes(Context context, String prefesName, String defaultValue){
        SharedPreferences sharedPrefs= MyApplication.preferences;
        return sharedPrefs.getString(prefesName,defaultValue);
    }
    private String TohashCode(String code){
        String str1=code;
        Log.d("hashcode2",str1);
        int hash1=(str1.hashCode());
        Log.d("hashcode2",hash1+"");
        return hash1+"";
    }

}
