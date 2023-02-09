package com.example.giftgallery.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.example.giftgallery.R;
import com.example.giftgallery.databinding.ActivityMainBinding;
import com.example.giftgallery.utilities.PreferenceManager;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private CheckBox newYearCheckBox;
    private CheckBox christmasCheckBox;
    private CheckBox februaryOf23CheckBox;
    private CheckBox easterCheckBox;
    private CheckBox angelDayCheckBox;
    private CheckBox weddingCheckBox;
    private CheckBox bigPurchaseCheckBox;
    private CheckBox retirementCheckBox;
    private CheckBox birthdayCheckBox;
    private CheckBox apologyCheckBox;
    private PreferenceManager preferenceManager;
    private CheckBox respectCheckBox;
    private CheckBox christeningCheckBox;
    private CheckBox housewarmingCheckBox;
    private CheckBox promotionCheckBox;
    private CheckBox kidCheckBox;
    private CheckBox youthCheckBox;
    private CheckBox matureCheckBox;
    private CheckBox pensionerCheckBox;
    ArrayList<CheckBox> checkBoxes;
    ArrayList<String> selectedTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    public void init(){
        checkBoxes = new ArrayList<>();
        selectedTags = new ArrayList<>();
        maleRadioButton = binding.maleRadioButton;
        femaleRadioButton = binding.femaleRadioButton;
        newYearCheckBox = binding.newYearTag;
        christmasCheckBox = binding.christmasTag;
        februaryOf23CheckBox = binding.februaryOf23CheckBox;
        easterCheckBox = binding.easterTag;
        angelDayCheckBox = binding.angelDayTag;
        weddingCheckBox = binding.weddingDay;
        bigPurchaseCheckBox = binding.bigPurchaseTag;
        retirementCheckBox = binding.retirementTag;
        birthdayCheckBox = binding.birthdayTag;
        apologyCheckBox = binding.apologyTag;
        respectCheckBox = binding.respectTag;
        christeningCheckBox = binding.christeningTag;
        housewarmingCheckBox = binding.housewarmingTag;
        promotionCheckBox = binding.promotionTag;
        kidCheckBox = binding.kidTag;
        youthCheckBox = binding.youthTag;
        matureCheckBox = binding.matureTag;
        pensionerCheckBox = binding.pensionerTag;
        checkBoxes.add(newYearCheckBox);
        checkBoxes.add(christmasCheckBox);
        checkBoxes.add(februaryOf23CheckBox);
        checkBoxes.add(easterCheckBox);
        checkBoxes.add(angelDayCheckBox);
        checkBoxes.add(weddingCheckBox);
        checkBoxes.add(bigPurchaseCheckBox);
        checkBoxes.add(retirementCheckBox);
        checkBoxes.add(birthdayCheckBox);
        checkBoxes.add(apologyCheckBox);
        checkBoxes.add(respectCheckBox);
        checkBoxes.add(christeningCheckBox);
        checkBoxes.add(housewarmingCheckBox);
        checkBoxes.add(promotionCheckBox);
        checkBoxes.add(kidCheckBox);
        checkBoxes.add(youthCheckBox);
        checkBoxes.add(matureCheckBox);
        checkBoxes.add(pensionerCheckBox);
        maleRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    selectedTags.add((String) compoundButton.getText());
                }else{
                    selectedTags.remove((String) compoundButton.getText());
                }
            }
        });
        femaleRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    selectedTags.add((String) compoundButton.getText());
                }else{
                    selectedTags.remove((String) compoundButton.getText());
                }
            }
        });
        for (CheckBox box: checkBoxes) {
            box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        selectedTags.add((String) box.getText());
                    }else{
                        selectedTags.remove(box.getText());
                    }
                }
            });
        }

    }

    private void setListeners() {
        binding.buttonOpenCatalog.setOnClickListener(v -> {
            Log.d("shop", String.valueOf(selectedTags.size()));
            if(!selectedTags.isEmpty()){
                preferenceManager.putStringArrayList("selectedTags",selectedTags);
            }else{
                preferenceManager.putStringArrayList("selectedTags", selectedTags);
            }
            Intent intent = new Intent(getApplicationContext(), CatalogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}