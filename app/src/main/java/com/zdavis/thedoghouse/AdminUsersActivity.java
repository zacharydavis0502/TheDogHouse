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

import com.zdavis.thedoghouse.databinding.ActivityAdminMenuBinding;
import com.zdavis.thedoghouse.databinding.ActivityAdminUsersBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.UserDAO;

import java.util.HashMap;
import java.util.List;

public class AdminUsersActivity extends AppCompatActivity {
    private static final String DOG_HOUSE_ADMIN = "com.zdavis.thedoghouse.DOG_HOUSE_ALL_USERS_ADMIN";

    TextView mAdminUsersAppNameTextView;
    TextView mAdminUsersTitleTextView;

    ScrollView mAdminUsersScrollView;
    LinearLayout mAdminUsersLinearLayout;
    ActivityAdminUsersBinding mAdminUsersBinding;
    Button mAdminUsersBackButton;

    UserDAO mAdminUserDAO;

    int adminUserId;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        sharedPref = getSharedPreferences(MainActivity.DOG_HOUSE_SHAREDPREF_FILE, 0);
        editor = sharedPref.edit();

        mAdminUsersBinding = ActivityAdminUsersBinding.inflate(getLayoutInflater());
        setContentView(mAdminUsersBinding.getRoot());

        adminUserId = getIntent().getIntExtra(DOG_HOUSE_ADMIN, -1);

        if (adminUserId < 0) {
            Intent intent = MainActivity.getIntent(getApplicationContext());
            editor.remove(MainActivity.DOG_HOUSE_USER_PREFERENCE);
            editor.apply();
            startActivity(intent);
        }

        mAdminUserDAO = AppDatabase.getInstance(this).getUserDao();

        mAdminUsersAppNameTextView = mAdminUsersBinding.adminUsersAppNameTextview;
        mAdminUsersTitleTextView = mAdminUsersBinding.adminUsersTitleTextview;
        mAdminUsersScrollView = mAdminUsersBinding.adminUsersScrollview;
        mAdminUsersLinearLayout = mAdminUsersBinding.adminUsersLinearlayout;
        mAdminUsersBackButton = mAdminUsersBinding.adminUsersBackButton;

        List<User> allUsers = mAdminUserDAO.getUsers();

        for (User currUser : allUsers) {
            if (currUser.getUserId() != adminUserId) {
                Button tempUserButton = new Button(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                tempUserButton.setText(currUser.getUsername());
                tempUserButton.setId(currUser.getUserId());
                tempUserButton.setLayoutParams(lp);

                tempUserButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.i("INFO", currUser.toString());
                        Intent intent = AdminViewUserActivity.getIntent(getApplicationContext(), adminUserId, currUser.getUserId());
                        startActivity(intent);
                    }
                });
                mAdminUsersLinearLayout.addView(tempUserButton);
            }
        }

        mAdminUsersBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AdminMenuActivity.getIntent(getApplicationContext(), adminUserId);
                startActivity(intent);
            }
        });

    }

    public static Intent getIntent(Context context, int id) {
        Intent intent = new Intent(context, AdminUsersActivity.class);
        intent.putExtra(DOG_HOUSE_ADMIN, id);
        return intent;
    }
}