package com.ttcnpm.nuntius;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class dashboard extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_dashboard);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        firebaseAuth = firebaseAuth.getInstance();

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        actionBar.setTitle("Home");
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();

    }
   private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
           new BottomNavigationView.OnNavigationItemSelectedListener() {
               @Override
               public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                   //chọn item được click
                   switch (menuItem.getItemId()){
                       case R.id.nav_home:
                           actionBar.setTitle("Home");
                           HomeFragment fragment1 = new HomeFragment();
                           FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                           ft1.replace(R.id.content,fragment1,"");
                           ft1.commit();
                           return true;
                      case R.id.nav_profile:
                           actionBar.setTitle("Profile");
                           ProfileFragment fragment2 = new ProfileFragment();
                           FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                           ft2.replace(R.id.content,fragment2,"");
                           ft2.commit();
                           return true;
                       case R.id.nav_users:
                           actionBar.setTitle("Users");
                           UsersFragment fragment3 = new UsersFragment();
                           FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                           ft3.replace(R.id.content,fragment3,"");
                           ft3.commit();
                           return true;
                       case R.id.nav_friends:
                           actionBar.setTitle("Friends");
                           FriendsFragment fragment4 = new FriendsFragment();
                           FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                           ft4.replace(R.id.content,fragment4,"");
                           ft4.commit();
                           return true;
                       case R.id.nav_request:
                           actionBar.setTitle("Requests");
                           RequestFragment fragment5 = new RequestFragment();
                           FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
                           ft5.replace(R.id.content,fragment5,"");
                           ft5.commit();
                           return true;

                   }


                   return false;
               }
           };

}
