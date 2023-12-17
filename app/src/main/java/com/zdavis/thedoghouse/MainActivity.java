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

import com.zdavis.thedoghouse.databinding.ActivityMainBinding;
import com.zdavis.thedoghouse.databinding.ActivityPetBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.UserDAO;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String DOG_HOUSE_USER_PREFERENCE = "com.zdavis.thedoghouse.DOG_HOUSE_USERID";
    public static final String DOG_HOUSE_SHAREDPREF_FILE = "com.zdavis.thedoghouse.DOG_HOUSE_SHAREDPREF_FILE";


    EditText mLoginUsernameEdittext;
    EditText mLoginPasswordEdittext;
    TextView mLoginStatusTextView;
    Button mLoginLoginButton;
    Button mLoginCreateAccountButton;

    UserDAO mUserDAO;

    ActivityMainBinding mMainBinding;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String loginStatus = "Please log in!";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mMainBinding.getRoot());
        mLoginUsernameEdittext = mMainBinding.loginUsernameEdittext;
        mLoginPasswordEdittext = mMainBinding.loginPasswordEdittext;
        mLoginStatusTextView = mMainBinding.loginStatusTextview;
        mLoginLoginButton = mMainBinding.loginLoginButton;
        mLoginCreateAccountButton = mMainBinding.loginCreateAccountButton;

        mUserDAO = AppDatabase.getInstance(this).getUserDao();

        sharedPref = getSharedPreferences(DOG_HOUSE_SHAREDPREF_FILE, 0);
        editor = sharedPref.edit();

        mLoginStatusTextView.setText(loginStatus);

        mLoginLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i("Available Users", mUserDAO.getUsers().toString());
                String username = mLoginUsernameEdittext.getText().toString();
                String password = mLoginPasswordEdittext.getText().toString();

                List<User> users = mUserDAO.authenticateUser(username, password);
                if (users.size() == 1) {
                    User user = users.get(0);

                    editor.putInt(DOG_HOUSE_USER_PREFERENCE, user.getUserId());
                    editor.apply();

                    Intent intent = UserActivity.getIntent(getApplicationContext(), user.getUserId());
                    startActivity(intent);
                } else {
                    Log.i("LOGIN STATUS", mLoginStatusTextView.getText().toString());
                    loginStatus = "Login Failed!";
                    updateStatus();
                }

            }
        });

        mLoginCreateAccountButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = CreateAccountActivity.getIntent(getApplicationContext());
                startActivity(intent);
            }
        });

        int prefCheck = sharedPref.getInt(DOG_HOUSE_USER_PREFERENCE, -1);
        if (prefCheck > 0) {
            Intent intent = UserActivity.getIntent(getApplicationContext(), prefCheck);
            startActivity(intent);
        }
    }

    private void updateStatus() {
        if (loginStatus.equals("Login Failed!")) {
            mLoginStatusTextView.setTextColor(Color.RED);
        }
        mLoginStatusTextView.setText(loginStatus);
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }
}