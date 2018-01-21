package com.shubh.androidchatter.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shubh.androidchatter.R;
import com.shubh.androidchatter.adapters.MypagerAdapter;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class HomeActivity extends AppCompatActivity implements MaterialTabListener {

    private ViewPager mPager;
    private MaterialTabHost tabHost;
    private MypagerAdapter myadapter;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        toolbar=(Toolbar)findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Android Chatter");
        mPager=(ViewPager)findViewById(R.id.pager);
        myadapter= new MypagerAdapter(getSupportFragmentManager(),getResources());
        mPager.setAdapter(myadapter);
        tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);
            }
        });
        for (int i = 0; i < myadapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            //.setIcon(myadapter.geticon(i))
                            .setText(myadapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(HomeActivity.this,
                                        "You have been signed out.",
                                        Toast.LENGTH_LONG)
                                        .show();

                                // Close activity
                                finish();
                            }
                        });
                break;
            case R.id.menu_settings:
                Intent i=new Intent(this,SearchUserActivty.class);
                startActivity(i);
                break;
            case R.id.menu_chnage_profile:
                Intent p=new Intent(this,SetUpProfileActivity.class);
                startActivity(p);
                break;
            case R.id.searchBar:
                Intent search=new Intent(this,SearchUserActivty.class);
                startActivity(search);
                break;
        }
        return true;
    }
    @Override
    public void onTabSelected(MaterialTab tab) {
        mPager.setCurrentItem(tab.getPosition());
    }
    @Override
    public void onTabReselected(MaterialTab tab) {
    }
    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

}
