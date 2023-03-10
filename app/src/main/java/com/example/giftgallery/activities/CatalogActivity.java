package com.example.giftgallery.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.giftgallery.adapters.GiftAdapter;
import com.example.giftgallery.databinding.ActivityCatalogBinding;
import com.example.giftgallery.listeners.LikeListener;
import com.example.giftgallery.models.Gift;
import com.example.giftgallery.utilities.Constants;
import com.example.giftgallery.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.protobuf.Value;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CatalogActivity extends AppCompatActivity implements LikeListener {

    private ActivityCatalogBinding binding;
    private PreferenceManager preferenceManager;
    private GiftAdapter giftAdapter;
    private ArrayList<Gift> gifts;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCatalogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        loadUserDetails();
        setListeners();
        listenGifts();
    }

    private void init() {
        gifts = new ArrayList<>();
        giftAdapter = new GiftAdapter(gifts, this);
        binding.productsRecyclerView.setAdapter(giftAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v -> signOut());
    }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
    }

    private void listenGifts() {
        database.collection(Constants.KEY_COLLECTION_GIFTS)
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String giftId = documentChange.getDocument().getId();
                    Gift gift = new Gift();
                    gift.id = giftId;
                    gift.name = documentChange.getDocument().getString(Constants.KEY_NAME);
                    gift.description = documentChange.getDocument().getString(Constants.KEY_DESCRIPTION);
                    gift.countLikes = documentChange.getDocument().get(Constants.KEY_COUNT_LIKES, Integer.class);

                    gifts.add(gift);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < gifts.size(); i++) {
                        String giftId = documentChange.getDocument().getId();
                        if (gifts.get(i).id.equals(giftId)) {
                            gifts.get(i).name = documentChange.getDocument().getString(Constants.KEY_NAME);
                            gifts.get(i).description = documentChange.getDocument().getString(Constants.KEY_DESCRIPTION);
                            gifts.get(i).countLikes = documentChange.getDocument().get(Constants.KEY_COUNT_LIKES, Integer.class);
                        }
                    }
                }
            }
            Collections.sort(gifts, (obj1, obj2) -> Double.compare(obj2.countLikes, obj1.countLikes));
            giftAdapter.notifyDataSetChanged();
            binding.productsRecyclerView.smoothScrollToPosition(0);
            binding.productsRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void signOut() {
        showToast("Sign out...");
        preferenceManager.clear();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @Override
    public void onLikeClicked(View view, Gift gift) {

        HashMap<String, Object> currentGift = new HashMap<>();
        currentGift.put(Constants.KEY_COUNT_LIKES, gift.countLikes + 1);
        Log.d("firebase", gift.id);
        Log.d("firebase", String.valueOf(gift.countLikes));

        database.collection(Constants.KEY_COLLECTION_GIFTS)
                .document(gift.id)
                .update(currentGift)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast("Liked!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Error!");
                    }
                });


        AppCompatImageView likeImege = (AppCompatImageView) view;
        likeImege.setColorFilter(Color.RED);
    }
}