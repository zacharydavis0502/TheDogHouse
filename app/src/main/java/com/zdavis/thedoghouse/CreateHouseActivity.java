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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.zdavis.thedoghouse.databinding.ActivityAdminHomesBinding;
import com.zdavis.thedoghouse.databinding.ActivityCreateAccountBinding;
import com.zdavis.thedoghouse.databinding.ActivityCreateHouseBinding;
import com.zdavis.thedoghouse.databinding.ActivityMainBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.HouseDAO;
import com.zdavis.thedoghouse.db.UserDAO;

import java.util.List;

public class CreateHouseActivity extends AppCompatActivity {
    private static final String DOG_HOUSE_USER = "com.zdavis.thedoghouse.DOG_HOUSE_CREATE_HOUSE_USER";
    TextView mAppNameTextView;
    TextView mTitleTextView;
    EditText mHouseNameEdittext;
    EditText mInvitedUsersEdittext;
    Button mCreateHouseButton;
    HouseDAO mHouseDAO;
    UserDAO mUserDAO;
    ActivityCreateHouseBinding mCreateHomeBinding;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_house);

        mCreateHomeBinding = ActivityCreateHouseBinding.inflate(getLayoutInflater());
        View view = mCreateHomeBinding.getRoot();

        setContentView(view);
        mAppNameTextView = mCreateHomeBinding.createHouseAppNameTextview;
        mTitleTextView = mCreateHomeBinding.createHouseTitleTextview;
        mHouseNameEdittext = mCreateHomeBinding.createHouseNameEdittext;
        mInvitedUsersEdittext = mCreateHomeBinding.createHouseInvitedUsersEdittext;
        mCreateHouseButton = mCreateHomeBinding.createHouseButton;

        mHouseDAO = AppDatabase.getInstance(this).getHouseDao();
        mUserDAO = AppDatabase.getInstance(this).getUserDao();

        sharedPref = getSharedPreferences(MainActivity.DOG_HOUSE_SHAREDPREF_FILE, 0);
        editor = sharedPref.edit();

        userId = getIntent().getIntExtra(DOG_HOUSE_USER, -1);

        if (userId < 0) {
            Log.i("ERROR", "User activity wasnt passed a user id");
            editor.remove(MainActivity.DOG_HOUSE_USER_PREFERENCE);
            editor.apply();
            Snackbar.make(ActivityMainBinding.inflate(getLayoutInflater()).getRoot(), R.string.badId, Snackbar.LENGTH_SHORT).show();
            startActivity(MainActivity.getIntent(getApplicationContext()));
        }
        mCreateHouseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                List<User> foundUser = mUserDAO.getUsersById(userId);

                String name = mHouseNameEdittext.getText().toString();
                List<House> foundHouses = mHouseDAO.getByName(name);
                if (foundUser.size() == 1 && foundHouses.size() == 0) {

                    String invitedUsers = mInvitedUsersEdittext.getText().toString().strip();

                    House tempHouse = new House(name);
                    tempHouse.getUsers().add(userId);
                    if (!invitedUsers.isEmpty()) {
                        String[] invUserArray = invitedUsers.split(",");
                        for (String username : invUserArray) {
                            String strippedUsername = username.strip();
                            List<User> users = mUserDAO.getByUsername(strippedUsername);

                            for (User user: users) {
                                tempHouse.getUsers().add(user.getUserId());
                            }
                        }
                    }
                    mHouseDAO.insert(tempHouse);
                    List<House> houseCheck = mHouseDAO.getByName(name);
                    foundUser.get(0).getHomes().add(houseCheck.get(0).getHouseId());
                    mUserDAO.update(foundUser.get(0));
                    Log.d("HOUSE CHECK", houseCheck.toString());
                } else {
                    Log.e("Create House User ID Query Error",
                            "Couldn't find user with id " + userId);
                    Snackbar.make(ActivityMainBinding.inflate(getLayoutInflater()).getRoot(), R.string.noUserById, Snackbar.LENGTH_SHORT).show();
                }
                Intent intent = UserActivity.getIntent(getApplicationContext(), userId);
                startActivity(intent);
            }
        });
    }

    public static Intent getIntent(Context context, int id) {
        Intent intent = new Intent(context, CreateHouseActivity.class);
        intent.putExtra(DOG_HOUSE_USER, id);
        return intent;
    }
}