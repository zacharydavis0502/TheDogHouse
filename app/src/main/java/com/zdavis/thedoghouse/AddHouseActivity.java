package com.zdavis.thedoghouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zdavis.thedoghouse.databinding.ActivityAddHouseBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.HouseDAO;
import com.zdavis.thedoghouse.db.UserDAO;

import java.util.List;

public class AddHouseActivity extends AppCompatActivity {
    private static final String DOG_HOUSE_USER = "com.zdavis.thedoghouse.DOG_HOUSE_ADD_HOME_USER_ID";
    TextView mAddHomeAppNameTextView;
    TextView mAddHomeTitleTextView;
    TextView mAddHomeStatusTextView;
    EditText mAddHomeNameEditText;
    Button mAddHomeButton;
    Button mAddHomeBackButton;
    ActivityAddHouseBinding mAddHouseBinding;
    UserDAO mUserDAO;
    HouseDAO mHouseDAO;
    User currUser;
    int userId;
    Intent logoutIntent;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String addStatus = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house);

        logoutIntent = MainActivity.getIntent(getApplicationContext());

        mAddHouseBinding = ActivityAddHouseBinding.inflate(getLayoutInflater());
        setContentView(mAddHouseBinding.getRoot());

        mAddHomeAppNameTextView = mAddHouseBinding.addHomeAppNameTextview;
        mAddHomeTitleTextView = mAddHouseBinding.addHomeTitleTextview;
        mAddHomeStatusTextView = mAddHouseBinding.addHomeStatusTextview;
        mAddHomeNameEditText = mAddHouseBinding.addHomeNameEdittext;
        mAddHomeButton = mAddHouseBinding.addHomeButton;
        mAddHomeBackButton = mAddHouseBinding.homeBackButton;

        mUserDAO = AppDatabase.getInstance(this).getUserDao();
        mHouseDAO = AppDatabase.getInstance(this).getHouseDao();

        sharedPref = getSharedPreferences(MainActivity.DOG_HOUSE_SHAREDPREF_FILE, 0);
        editor = sharedPref.edit();

        mAddHomeStatusTextView.setText(addStatus);

        userId = getIntent().getIntExtra(DOG_HOUSE_USER, -1);

        if (userId < 0) {
            Log.i("ERROR", "User id extra wasn't included in intent!");
            editor.remove(MainActivity.DOG_HOUSE_USER_PREFERENCE);
            editor.apply();
            startActivity(logoutIntent);
        } else {
            List<User> users = mUserDAO.getUsersById(userId);
            if (users.size() > 1 || users.isEmpty()) {
                Log.i("ERROR", "No user or multiple users of id " + userId);
                editor.remove(MainActivity.DOG_HOUSE_USER_PREFERENCE);
                editor.apply();
                startActivity(logoutIntent);
            } else {
                currUser = users.get(0);
            }
        }
        mAddHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reqHouseName = mAddHomeNameEditText.getText().toString().strip();
                List<House> reqHouse = mHouseDAO.getByName(reqHouseName);

                if (reqHouse.size() != 1 || !reqHouse.get(0).getUsers().contains(currUser.getUserId())) {
                    addStatus = "Requested house doesn't exist or you're not invited!";
                    updateStatus();
                } else {
                    currUser.getHomes().add(reqHouse.get(0).getHouseId());
                    mUserDAO.update(currUser);
                    back();
                }
            }
        });
        mAddHomeBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    private void updateStatus() {
        mAddHomeStatusTextView.setTextColor(Color.RED);
        mAddHomeStatusTextView.setText(addStatus);
    }
    public void back() {
        Intent backIntent = UserActivity.getIntent(getApplicationContext(), userId);
        startActivity(backIntent);
    }
    public static Intent getIntent(Context context, int userId) {
        Intent intent = new Intent(context, AddHouseActivity.class);
        intent.putExtra(DOG_HOUSE_USER, userId);;
        return intent;
    }


}