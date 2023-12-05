package com.zdavis.thedoghouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zdavis.thedoghouse.databinding.ActivityCreateAccountBinding;
import com.zdavis.thedoghouse.databinding.ActivityMainBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.UserDAO;

public class CreateAccountActivity extends AppCompatActivity {

    EditText mCreateAccountUsernameEdittext;
    EditText getmCreateAccountPasswordEdittext;
    TextView mCreateAccountStatusTextView;
    Button mCreateAccountButton;

    UserDAO mUserDAO;

    ActivityCreateAccountBinding mCreateAccountBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mCreateAccountBinding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        View view = mCreateAccountBinding.getRoot();

        setContentView(view);
        mCreateAccountUsernameEdittext = mCreateAccountBinding.createAccountUsernameEdittext;
        getmCreateAccountPasswordEdittext = mCreateAccountBinding.createAccountPasswordEdittext;
        mCreateAccountStatusTextView = mCreateAccountBinding.createAccountStatusTextview;
        mCreateAccountButton = mCreateAccountBinding.createAccountButton;

        mUserDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME)
                .allowMainThreadQueries()
                .build()
                .getUserDao();

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = mCreateAccountUsernameEdittext.getText().toString();
                String password = getmCreateAccountPasswordEdittext.getText().toString();
                if (verifyCredentials(username, password)) {
                    User user = new User(username, password, false);
                    mUserDAO.insert(user);
                    Log.i("Check DB", mUserDAO.getUsers().toString());
                    Intent intent = MainActivity.getIntent(getApplicationContext());
                    startActivity(intent);
                } else {
                    mCreateAccountStatusTextView.setText("Bad credentials!");
                }
            }
        });
    }
    private boolean verifyCredentials(String username, String password) {
        boolean result;
        if (mUserDAO.getByUsername(username).size() == 0) {
            result = true;
        } else {
            result =  false;
        }

        Log.i("Verify Cred Results", ""+result);
        return result;

    }


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CreateAccountActivity.class);
        return intent;
    }
}