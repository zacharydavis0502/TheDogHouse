package com.zdavis.thedoghouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zdavis.thedoghouse.databinding.ActivityUserBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.HouseDAO;
import com.zdavis.thedoghouse.db.UserDAO;

import java.util.List;

public class UserActivity extends AppCompatActivity {
    private static final String DOG_HOUSE_USER = "com.zdavis.thedoghouse.DOG_HOUSE_USER";

    TextView mAppNameTextView;
    TextView mUserWelcomeTextView;

    ScrollView mUserHomeScrollView;

    LinearLayout mUserHomeLinearLayout;
    Button mUserCreateHomeButton;
    Button mUserAddHomeButton;
    Button mUserLogoutButton;

    Button mUserAdminMenuButton;

    ActivityUserBinding mUserBinding;

    UserDAO mUserDAO;
    HouseDAO mHouseDAO;

    int userId;

    User user;

    Intent logoutIntent;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        logoutIntent = MainActivity.getIntent(getApplicationContext());

        mUserBinding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(mUserBinding.getRoot());

        mAppNameTextView = mUserBinding.userAppNameTextview;
        mUserWelcomeTextView = mUserBinding.userWelcomeTextview;
        mUserHomeScrollView = mUserBinding.userHomesScrollview;
        mUserHomeLinearLayout = mUserBinding.userHomesLinearlayout;
        mUserCreateHomeButton = mUserBinding.userCreateHomeButton;
        mUserAddHomeButton = mUserBinding.userAddHomeButton;
        mUserLogoutButton = mUserBinding.userLogoutButton;
        mUserAdminMenuButton = mUserBinding.userAdminMenuButton;

        mUserDAO = AppDatabase.getInstance(this).getUserDao();
        mHouseDAO = AppDatabase.getInstance(this).getHouseDao();

        sharedPref = getSharedPreferences(MainActivity.DOG_HOUSE_SHAREDPREF_FILE, 0);
        editor = sharedPref.edit();

        userId = getIntent().getIntExtra(DOG_HOUSE_USER, -1);

        if (userId < 0) {
            Log.i("ERROR", "User activity wasnt passed a user id");
            startActivity(logoutIntent);
        } else {
            List<User> users = mUserDAO.getUsersById(userId);
            if (users.size() > 1 || users.isEmpty()) {
                Log.i("ERROR", "No user or multiple users of id " + userId);
                editor.remove(MainActivity.DOG_HOUSE_USER_PREFERENCE);
                editor.apply();
                startActivity(logoutIntent);
            } else {
                user = users.get(0);
                mUserWelcomeTextView.setText("Welcome " + user.getUsername() + "!");
            }
        }

        if (user.isAdmin()) {
            mUserAdminMenuButton.setVisibility(View.VISIBLE);
            mUserAdminMenuButton.setClickable(true);
        } else {
            mUserAdminMenuButton.setVisibility(View.INVISIBLE);
            mUserAdminMenuButton.setClickable(false);
        }

        for (int houseId : user.getHomes()) {
            List<House> foundHouse = mHouseDAO.getHouseById(houseId);
            if (foundHouse.size() >= 0) {
                House house = foundHouse.get(0);
                Button tempUserButton = new Button(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                tempUserButton.setText(house.getName());
                tempUserButton.setId(house.getHouseId());
                tempUserButton.setLayoutParams(lp);

                tempUserButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("INFO", house.toString());
                        Intent houseIntent = HomeActivity.getIntent(getApplicationContext(), userId, house.getHouseId(), false);
                        startActivity(houseIntent);
                    }
                });
                //add button to the layout
                mUserHomeLinearLayout.addView(tempUserButton);
            } else {
                Log.e("No Such House", "No such house with Id " + houseId);
            }

        }

        mUserLogoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.remove(MainActivity.DOG_HOUSE_USER_PREFERENCE);
                editor.apply();
                startActivity(logoutIntent);
            }
        });

        mUserAdminMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AdminMenuActivity.getIntent(getApplicationContext(), user.getUserId());
                startActivity(intent);
            }
        });

        mUserCreateHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CreateHouseActivity.getIntent(getApplicationContext(), userId);
                startActivity(intent);
            }
        });

        mUserAddHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddHouseActivity.getIntent(getApplicationContext(), userId);
                startActivity(intent);
            }
        });

    }

//    @Override
//    protected  void onResume() {
//        super.onResume();
//        SharedPreferences sharedPreferences = getSharedPreferences("UserSharedPreferences", MODE_PRIVATE);
//        String sharedWelcomeText = sharedPreferences.getString("welcomeText", mUserWelcomeTextView.getText().toString());
//        int sharedUserId = sharedPreferences.getInt("userId", userId);
//
//        try {
//            mUserWelcomeTextView.setText(sharedWelcomeText);
//            userId = sharedUserId;
//            user = mUserDAO.getUsersById(userId).get(0);
//        } catch (Exception e) {
//            Log.e("ERROR", e.getMessage());
//            startActivity(logoutIntent);
//        }
//
//
//    }
//
//    @Override
//    protected  void onPause() {
//        super.onPause();
//        SharedPreferences sharedPreferences = getSharedPreferences("UserSharedPreferences", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        editor.putString("welcomeText", mUserWelcomeTextView.getText().toString());
//        editor.putInt("userId", userId);
//        editor.apply();
//    }

    public static Intent getIntent(Context context, int id) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(DOG_HOUSE_USER, id);
        return intent;
    }
}