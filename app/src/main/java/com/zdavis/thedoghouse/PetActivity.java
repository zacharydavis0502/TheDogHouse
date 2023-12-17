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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zdavis.thedoghouse.databinding.ActivityPetBinding;
import com.zdavis.thedoghouse.db.AppDatabase;
import com.zdavis.thedoghouse.db.HouseDAO;
import com.zdavis.thedoghouse.db.PetDAO;
import com.zdavis.thedoghouse.db.UserDAO;

import org.w3c.dom.Text;

import java.util.List;

public class PetActivity extends AppCompatActivity {
    private static final String DOG_HOUSE_USER = "com.zdavis.thedoghouse.DOG_HOUSE_PET_USER_ID";
    private static final String DOG_HOUSE_HOME = "com.zdavis.thedoghouse.DOG_HOUSE_PET_HOME_ID";
    private static final String DOG_HOUSE_PET = "com.zdavis.thedoghouse.DOG_HOUSE_PET_ID";
    private static final String DOG_HOUSE_ACCESS = "com.zdavis.thedoghouse.DOG_HOUSE_PET_ACCESS";
    ActivityPetBinding mPetBinding;
    TextView mPetNameTextView;
    TextView mPetTypeAndColorTextView;
    TextView mPetBirthdateTextView;

    ScrollView mPetNotesScrollView;
    LinearLayout mPetNotesLinearLayout;
    EditText mPetNotesEditTest;
    Button mPetBackButton;
    Button mPetRemoveButton;
    PetDAO mPetDAO;
    HouseDAO mHouseDAO;
    House currHouse;
    Pet currPet;
    int userId;
    int houseId;
    int petId;

    boolean adminAccess;
    Intent logoutIntent;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        logoutIntent = MainActivity.getIntent(getApplicationContext());

        mPetBinding = ActivityPetBinding.inflate(getLayoutInflater());
        setContentView(mPetBinding.getRoot());

        mPetNameTextView = mPetBinding.petNameTextview;
        mPetTypeAndColorTextView = mPetBinding.petTypeAndColorTextview;
        mPetBirthdateTextView = mPetBinding.petBirthdateTextview;
        mPetNotesScrollView = mPetBinding.petNotesScrollview;
        mPetNotesLinearLayout = mPetBinding.petNotesLinearlayout;
        mPetNotesEditTest = mPetBinding.petNoteMultilineText;
        mPetBackButton = mPetBinding.petBackButton;
        mPetRemoveButton = mPetBinding.petRemoveButton;

        mPetDAO = AppDatabase.getInstance(this).getPetDao();

        mHouseDAO = AppDatabase.getInstance(this).getHouseDao();

        sharedPref = getSharedPreferences(MainActivity.DOG_HOUSE_SHAREDPREF_FILE, 0);
        editor = sharedPref.edit();

        userId = getIntent().getIntExtra(DOG_HOUSE_USER, -1);
        houseId = getIntent().getIntExtra(DOG_HOUSE_HOME, -1);
        petId = getIntent().getIntExtra(DOG_HOUSE_PET, -1);
        adminAccess = getIntent().getBooleanExtra(DOG_HOUSE_ACCESS, false);

        if (userId < 0) {
            Log.i("ERROR", "User activity wasnt passed a user id");
            startActivity(logoutIntent);
        } else if (houseId < 0) {
            Intent backIntent = UserActivity.getIntent(getApplicationContext(), userId);
            startActivity(backIntent);
        } else if (petId < 0) {
            back();
        } else {
            List<Pet> foundPet = mPetDAO.getPetById(petId);
            List<House> foundHouse = mHouseDAO.getHouseById(houseId);
            if (foundPet.size() == 1 && foundHouse.size() == 1) {
                currPet = foundPet.get(0);
                currHouse = foundHouse.get(0);
                mPetNameTextView.setText(currPet.getName());
                mPetTypeAndColorTextView.setText(currPet.getColor() + " " + currPet.getType());
                mPetBirthdateTextView.setText("Birthday: " + currPet.getBirthdate().toString());
                mPetNotesEditTest.setText(currPet.getNotes());
            } else {
                back();
            }
        }

        mPetBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notes = mPetNotesEditTest.getText().toString();
                currPet.setNotes(notes);
                mPetDAO.update(currPet);
                back();
            }
        });

        mPetRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currHouse.getPets().remove(Integer.valueOf(petId));
                mHouseDAO.update(currHouse);
                mPetDAO.delete(currPet);
                back();
            }
        });
    }

    public void back() {
        Intent backIntent = HomeActivity.getIntent(getApplicationContext(), userId, houseId, adminAccess);
        startActivity(backIntent);
    }
    public static Intent getIntent(Context context, int userId, int houseId, int petId, boolean isAdmin) {
        Intent intent = new Intent(context, PetActivity.class);
        intent.putExtra(DOG_HOUSE_USER, userId);
        intent.putExtra(DOG_HOUSE_HOME, houseId);
        intent.putExtra(DOG_HOUSE_PET, petId);
        intent.putExtra(DOG_HOUSE_ACCESS, isAdmin);
        return intent;
    }
}