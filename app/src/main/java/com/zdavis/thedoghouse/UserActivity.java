package com.zdavis.thedoghouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zdavis.thedoghouse.databinding.ActivityUserBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.UserDAO;

import java.util.List;

public class UserActivity extends AppCompatActivity {
    private static final String DOG_HOUSE_USER = "com.zdavis.thedoghouse.DOG_HOUSE_USER";

    TextView mAppNameTextView;
    TextView mUserWelcomeTextView;
    Button mUserCreateHomeButton;
    Button mUserAddHomeButton;
    Button mUserLogoutButton;

    Button mUserAdminMenuButton;

    ActivityUserBinding mUserBinding;

    UserDAO mUserDAO;

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
        mUserCreateHomeButton = mUserBinding.userCreateHomeButton;
        mUserAddHomeButton = mUserBinding.userAddHomeButton;
        mUserLogoutButton = mUserBinding.userLogoutButton;
        mUserAdminMenuButton = mUserBinding.userAdminMenuButton;

        mUserDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getUserDao();

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