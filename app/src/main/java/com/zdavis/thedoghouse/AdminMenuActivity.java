package com.zdavis.thedoghouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zdavis.thedoghouse.databinding.ActivityAdminMenuBinding;
import com.zdavis.thedoghouse.databinding.ActivityUserBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.UserDAO;

import java.util.List;

public class AdminMenuActivity extends AppCompatActivity {
    private static final String DOG_HOUSE_ADMIN = "com.zdavis.thedoghouse.DOG_HOUSE_ADMIN";
    TextView mAppNameTextView;
    TextView mAdminWelcomeTextView;
    Button mAdminBackButton;
    ActivityAdminMenuBinding mAdminMenuBinding;
    UserDAO mUserDAO;
    int userId;
    User adminUser;
    Intent logoutIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        logoutIntent = MainActivity.getIntent(getApplicationContext());

        mAdminMenuBinding = ActivityAdminMenuBinding.inflate(getLayoutInflater());
        setContentView(mAdminMenuBinding.getRoot());

        mAppNameTextView = mAdminMenuBinding.adminAppNameTextview;
        mAdminWelcomeTextView = mAdminMenuBinding.adminWelcomeTextview;
        mAdminBackButton = mAdminMenuBinding.adminBackButton;

        mUserDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getUserDao();

        userId = getIntent().getIntExtra(DOG_HOUSE_ADMIN, -1);

        if (userId < 0) {
            Log.i("ERROR", "User activity wasnt passed a user id");
            startActivity(logoutIntent);
        } else {
            List<User> users = mUserDAO.getUsersById(userId);
            if (users.size() > 1 || users.isEmpty()) {
                Log.i("ERROR", "No user or multiple users of id " + userId);
                startActivity(logoutIntent);
            } else {
                adminUser = users.get(0);
                mAdminWelcomeTextView.setText("Welcome " + adminUser.getUsername() + "!");
            }
        }

        mAdminBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = UserActivity.getIntent(getApplicationContext(), adminUser.getUserId());
                startActivity(intent);
            }
        });
    }

    public static Intent getIntent(Context context, int id) {
        Intent intent = new Intent(context, AdminMenuActivity.class);
        intent.putExtra(DOG_HOUSE_ADMIN, id);
        return intent;
    }
}