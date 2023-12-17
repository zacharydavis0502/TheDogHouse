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

import com.zdavis.thedoghouse.databinding.ActivityAdminHomesBinding;
import com.zdavis.thedoghouse.databinding.ActivityAdminUsersBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.HouseDAO;
import com.zdavis.thedoghouse.db.UserDAO;

import java.util.List;

public class AdminHomesActivity extends AppCompatActivity {

    private static final String DOG_HOUSE_ADMIN = "com.zdavis.thedoghouse.DOG_HOUSE_ALL_HOMES_ADMIN";

    TextView mAdminHomesAppNameTextView;
    TextView mAdminHomesTitleTextView;

    ScrollView mAdminHomesScrollView;
    LinearLayout mAdminHomesLinearLayout;
    ActivityAdminHomesBinding mAdminHomesBinding;
    Button mAdminHomesBackButton;

    HouseDAO mAdminHouseDAO;

    int adminUserId;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homes);

        sharedPref = getSharedPreferences(MainActivity.DOG_HOUSE_SHAREDPREF_FILE, 0);
        editor = sharedPref.edit();

        mAdminHomesBinding = ActivityAdminHomesBinding.inflate(getLayoutInflater());
        setContentView(mAdminHomesBinding.getRoot());

        adminUserId = getIntent().getIntExtra(DOG_HOUSE_ADMIN, -1);

        if (adminUserId < 0) {
            Intent intent = MainActivity.getIntent(getApplicationContext());
            editor.remove(MainActivity.DOG_HOUSE_USER_PREFERENCE);
            editor.apply();
            startActivity(intent);
        }

        mAdminHouseDAO = AppDatabase.getInstance(this).getHouseDao();

        mAdminHomesAppNameTextView = mAdminHomesBinding.adminHomesAppNameTextview;
        mAdminHomesTitleTextView = mAdminHomesBinding.adminHomesTitleTextview;
        mAdminHomesScrollView = mAdminHomesBinding.adminHomesScrollview;
        mAdminHomesLinearLayout = mAdminHomesBinding.adminHomesLinearlayout;
        mAdminHomesBackButton = mAdminHomesBinding.adminHomesBackButton;

        List<House> allHomes = mAdminHouseDAO.getHouses();

        for (House currHouse : allHomes) {
            Button tempUserButton = new Button(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            tempUserButton.setText(currHouse.getName());
            tempUserButton.setId(currHouse.getHouseId());
            tempUserButton.setLayoutParams(lp);

            tempUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("INFO", currHouse.toString());
                    Intent intent = HomeActivity.getIntent(getApplicationContext(), adminUserId, currHouse.getHouseId(), true);
                    startActivity(intent);
                }
            });
            //add button to the layout
            mAdminHomesLinearLayout.addView(tempUserButton);
        }

        mAdminHomesBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AdminMenuActivity.getIntent(getApplicationContext(), adminUserId);
                startActivity(intent);
            }
        });

    }

    public static Intent getIntent(Context context, int id) {
        Intent intent = new Intent(context, AdminHomesActivity.class);
        intent.putExtra(DOG_HOUSE_ADMIN, id);
        return intent;
    }
}