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

import com.zdavis.thedoghouse.databinding.ActivityNewPetBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.HouseDAO;
import com.zdavis.thedoghouse.db.PetDAO;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class NewPetActivity extends AppCompatActivity {
    private static final String DOG_HOUSE_USER = "com.zdavis.thedoghouse.DOG_HOUSE_NEW_PET_USER_ID";
    private static final String DOG_HOUSE_HOME = "com.zdavis.thedoghouse.DOG_HOUSE_NEW_PET_HOME_ID";
    private static final String DOG_HOUSE_ACCESS = "com.zdavis.thedoghouse.DOG_HOUSE_NEW_PET_ACCESS";
    ActivityNewPetBinding mNewPetBinding;
    TextView mNewPetAppNameTextView;
    TextView mNewPetTitleTextView;
    EditText mNewPetNameEditText;
    EditText mNewPetTypeEditText;
    EditText mNewPetColorEditText;
    EditText mNewPetBirthdateEditText;
    Button mNewPetAddPetButton;

    HouseDAO mHouseDAO;
    PetDAO mPetDAO;
    int userId;
    int houseId;
    boolean isAdmin;
    House currHome;

    Intent logoutIntent;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pet);

        mNewPetBinding = ActivityNewPetBinding.inflate(getLayoutInflater());
        setContentView(mNewPetBinding.getRoot());

        logoutIntent = MainActivity.getIntent(getApplicationContext());

        mNewPetAppNameTextView = mNewPetBinding.newPetAppNameTextview;
        mNewPetTitleTextView = mNewPetBinding.newPetTitleTextview;
        mNewPetNameEditText = mNewPetBinding.newPetNameEdittext;
        mNewPetTypeEditText = mNewPetBinding.newPetTypeEdittext;
        mNewPetColorEditText = mNewPetBinding.newPetColorEdittext;
        mNewPetBirthdateEditText = mNewPetBinding.newPetBirthdateEdittext;
        mNewPetAddPetButton = mNewPetBinding.newPetAddPetButton;

        mHouseDAO = AppDatabase.getInstance(this).getHouseDao();
        mPetDAO = AppDatabase.getInstance(this).getPetDao();

        sharedPref = getSharedPreferences(MainActivity.DOG_HOUSE_SHAREDPREF_FILE, 0);
        editor = sharedPref.edit();

        userId = getIntent().getIntExtra(DOG_HOUSE_USER, -1);
        houseId = getIntent().getIntExtra(DOG_HOUSE_HOME, -1);
        isAdmin = getIntent().getBooleanExtra(DOG_HOUSE_ACCESS, false);

        if (userId < 0) {
            Log.i("ERROR", "User id extra wasn't included in intent!");
            editor.remove(MainActivity.DOG_HOUSE_USER_PREFERENCE);
            editor.apply();
            startActivity(logoutIntent);
        } else if  (houseId < 0){
            back(userId, houseId);
        } else {
            List<House> currHouseQuery = mHouseDAO.getHouseById(houseId);
            if (currHouseQuery.size() == 1) {
                currHome = currHouseQuery.get(0);
            } else {
                back(userId, houseId);
            }
        }

        mNewPetAddPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNewPetNameEditText.getText().toString().strip();
                String type = mNewPetTypeEditText.getText().toString().strip();
                String color = mNewPetColorEditText.getText().toString().strip();
                LocalDate birthdate = LocalDate.parse(mNewPetBirthdateEditText.getText().toString());

                Pet tempPet = new Pet(name, type, color, birthdate);
                long petid = mPetDAO.insert(tempPet);
                currHome.getPets().add((int) petid);
                mHouseDAO.update(currHome);
                back(userId, houseId);
            }
        });
    }

    public void back(int userId, int houseId) {
        Intent backIntent = HomeActivity.getIntent(getApplicationContext(), userId, houseId, isAdmin);
        startActivity(backIntent);
    }
    public static Intent getIntent(Context context, int userId, int houseId) {
        Intent intent = new Intent(context, NewPetActivity.class);
        intent.putExtra(DOG_HOUSE_USER, userId);
        intent.putExtra(DOG_HOUSE_HOME, houseId);
        return intent;
    }
}