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

import com.zdavis.thedoghouse.databinding.ActivityHomeBinding;
import com.zdavis.thedoghouse.databinding.ActivityUserBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.HouseDAO;
import com.zdavis.thedoghouse.db.PetDAO;
import com.zdavis.thedoghouse.db.UserDAO;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String DOG_HOUSE_USER = "com.zdavis.thedoghouse.DOG_HOUSE_HOME_USER_ID";
    private static final String DOG_HOUSE_HOME = "com.zdavis.thedoghouse.DOG_HOUSE_HOME_ID";
    private static final String DOG_HOUSE_ACCESS = "com.zdavis.thedoghouse.DOG_HOUSE_HOME_IS_ADMIN";
    TextView mHomeAppNameTextView;
    TextView mHomeWelcomeTextView;
    TextView mHomeNameTextView;
    ScrollView mHomePetsScrollView;
    LinearLayout mHomePetsLinearLayout;
    Button mHomeNewPetButton;
    Button mHomeBackButton;
    Button mHomeDeleteButton;
    ActivityHomeBinding mHomeBinding;
    UserDAO mUserDAO;
    HouseDAO mHouseDAO;
    PetDAO mPetDAO;
    int userId;
    int houseId;
    boolean adminAccess;
    Intent logoutIntent;
    User user;
    House currHome;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logoutIntent = MainActivity.getIntent(getApplicationContext());

        mHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(mHomeBinding.getRoot());

        mHomeAppNameTextView = mHomeBinding.homeAppNameTextview;
        mHomeWelcomeTextView = mHomeBinding.homeWelcomeTextview;
        mHomeNameTextView = mHomeBinding.homeNameTextview;
        mHomePetsScrollView = mHomeBinding.homePetsScrollview;
        mHomePetsLinearLayout = mHomeBinding.homePetsLinearlayout;
        mHomeNewPetButton = mHomeBinding.homeNewPetButton;
        mHomeBackButton = mHomeBinding.homeBackButton;
        mHomeDeleteButton = mHomeBinding.homeDeleteButton;

        mUserDAO = AppDatabase.getInstance(this).getUserDao();
        mHouseDAO = AppDatabase.getInstance(this).getHouseDao();
        mPetDAO = AppDatabase.getInstance(this).getPetDao();

        sharedPref = getSharedPreferences(MainActivity.DOG_HOUSE_SHAREDPREF_FILE, 0);
        editor = sharedPref.edit();

        userId = getIntent().getIntExtra(DOG_HOUSE_USER, -1);
        houseId = getIntent().getIntExtra(DOG_HOUSE_HOME, -1);
        adminAccess = getIntent().getBooleanExtra(DOG_HOUSE_ACCESS, false);

        if (adminAccess) {
            mHomeDeleteButton.setVisibility(View.VISIBLE);
            mHomeDeleteButton.setClickable(true);
        }

        if (userId < 0) {
            Log.i("ERROR", "User id extra wasn't included in intent!");
            editor.remove(MainActivity.DOG_HOUSE_USER_PREFERENCE);
            editor.apply();
            startActivity(logoutIntent);
        } else if  (houseId < 0){
            back(userId);
        } else {
            List<User> users = mUserDAO.getUsersById(userId);
            if (users.size() > 1 || users.isEmpty()) {
                Log.i("ERROR", "No user or multiple users of id " + userId);
                editor.remove(MainActivity.DOG_HOUSE_USER_PREFERENCE);
                editor.apply();
                startActivity(logoutIntent);
            } else {
                user = users.get(0);
                mHomeWelcomeTextView.setText("Welcome " + user.getUsername() + "!");
            }
        }

        List<House> foundHome = mHouseDAO.getHouseById(houseId);

        if (foundHome.size() != 1) {
            back(userId);
        } else {
            currHome = foundHome.get(0);
            mHomeNameTextView.setText(currHome.getName());

            for (int petId : currHome.getPets()) {
                List<Pet> foundPet = mPetDAO.getPetById(petId);
                if (foundPet.size() > 0) {
                    Pet pet = foundPet.get(0);
                    Button tempPetButton = new Button(this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
                    tempPetButton.setText(pet.getName());
                    tempPetButton.setId(pet.getPetId());
                    tempPetButton.setLayoutParams(lp);

                    tempPetButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("INFO", pet.toString());
                            Intent petIntent = PetActivity.getIntent(getApplicationContext(),userId, houseId, petId, adminAccess);
                            startActivity(petIntent);
                        }
                    });
                    //add button to the layout
                    mHomePetsLinearLayout.addView(tempPetButton);
                } else {
                    Log.e("No Such House", "No such house with Id " + houseId);
                }
            }
        }

        mHomeBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                back(userId);
            }
        });

        mHomeNewPetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = NewPetActivity.getIntent(getApplicationContext(), userId, houseId);
                startActivity(intent);
            }
        });

        mHomeDeleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mHouseDAO.delete(currHome);
                back(userId);
            }
        });
    }

    public void back(int userId) {
        Intent intent;
        if (adminAccess) {
            intent = AdminHomesActivity.getIntent(getApplicationContext(), userId);
        } else {
            intent = UserActivity.getIntent(getApplicationContext(), userId);
        }
        startActivity(intent);
    }

    public static Intent getIntent(Context context, int userId, int houseId, boolean isAdmin) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(DOG_HOUSE_USER, userId);
        intent.putExtra(DOG_HOUSE_HOME, houseId);
        intent.putExtra(DOG_HOUSE_ACCESS, isAdmin);
        return intent;
    }
}