package com.zdavis.thedoghouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zdavis.thedoghouse.databinding.ActivityAdminHomesBinding;
import com.zdavis.thedoghouse.databinding.ActivityAdminViewUserBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.HouseDAO;
import com.zdavis.thedoghouse.db.UserDAO;

import java.util.List;

public class AdminViewUserActivity extends AppCompatActivity {

    private static final String DOG_HOUSE_ADMIN_ID = "com.zdavis.thedoghouse.DOG_HOUSE_ADMIN_VIEW_USER_ADMIN_ID";
    private static final String DOG_HOUSE_USER_ID = "com.zdavis.thedoghouse.DOG_HOUSE_ADMIN_VIEW_USER_USER_ID";

    TextView mAdminViewUserAppNameTextView;
    TextView mAdminViewUserTitleTextView;
    TextView mAdminViewUserUsernameTextView;
    TextView mAdminViewUserIdTextView;
    TextView mAdminViewUserHomesTextView;
    Button mAdminViewUserDeleteUserButton;
    Button mAdminViewUserBackButton;
    ActivityAdminViewUserBinding mAdminViewUserBinding;

    UserDAO mAdminUserDAO;

    int adminUserId;

    User viewedUser;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user);

        sharedPref = getSharedPreferences(MainActivity.DOG_HOUSE_SHAREDPREF_FILE, 0);
        editor = sharedPref.edit();

        mAdminViewUserBinding = ActivityAdminViewUserBinding.inflate(getLayoutInflater());
        setContentView(mAdminViewUserBinding.getRoot());

        adminUserId = getIntent().getIntExtra(DOG_HOUSE_ADMIN_ID, -1);
        int viewedUserId = getIntent().getIntExtra(DOG_HOUSE_USER_ID, -1);

        if (adminUserId < 0) {
            Intent intent = MainActivity.getIntent(getApplicationContext());
            editor.remove(MainActivity.DOG_HOUSE_USER_PREFERENCE);
            editor.apply();
            startActivity(intent);
        }

        if (viewedUserId < 0) {
           back();
        }

        mAdminUserDAO = AppDatabase.getInstance(this).getUserDao();

        mAdminViewUserAppNameTextView = mAdminViewUserBinding.adminViewUserAppNameTextview;
        mAdminViewUserTitleTextView = mAdminViewUserBinding.adminViewUserTitleTextview;
        mAdminViewUserUsernameTextView = mAdminViewUserBinding.adminViewUserUsernameTextview;
        mAdminViewUserIdTextView = mAdminViewUserBinding.adminViewUserIdTextview;
        mAdminViewUserHomesTextView = mAdminViewUserBinding.adminViewUserHomesTextview;
        mAdminViewUserBackButton = mAdminViewUserBinding.adminViewUserBackButton;
        mAdminViewUserDeleteUserButton = mAdminViewUserBinding.adminViewUserDeleteButton;

        List<User> userQuery = mAdminUserDAO.getUsersById(viewedUserId);
        if (userQuery.size() == 1) {
            viewedUser = userQuery.get(0);
        } else {
            back();
        }

        mAdminViewUserUsernameTextView.setText("Username: " + viewedUser.getUsername());
        mAdminViewUserIdTextView.setText("User Id: " + viewedUser.getUserId());
        mAdminViewUserHomesTextView.setText("Home Ids: " + viewedUser.getHomes());

        mAdminViewUserBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                back();
            }
        });

        mAdminViewUserDeleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewedUserId != adminUserId) {
                    mAdminUserDAO.delete(viewedUser);
                }
                back();
            }
        });
    }

    public static Intent getIntent(Context context, int adminId, int userId) {
        Intent intent = new Intent(context, AdminViewUserActivity.class);
        intent.putExtra(DOG_HOUSE_ADMIN_ID, adminId);
        intent.putExtra(DOG_HOUSE_USER_ID, userId);
        return intent;
    }

    public void back() {
        Intent intent = AdminUsersActivity.getIntent(getApplicationContext(), adminUserId);
        startActivity(intent);
    }
}