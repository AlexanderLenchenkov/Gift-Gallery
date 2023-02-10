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
import com.example.giftgallery.models.Like;
import com.example.giftgallery.models.User;
import com.example.giftgallery.utilities.Constants;
import com.example.giftgallery.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.protobuf.Value;

import org.checkerframework.checker.units.qual.A;
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
    private ArrayList<Like> likes;
    private FirebaseFirestore database;
    private ArrayList<String> selectedTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCatalogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        loadUserDetails();
        setListeners();
        selectedTags = preferenceManager.getStringArrayList("selectedTags");
        listenGifts();
        listenLikes();
    }

    private void init() {
        gifts = new ArrayList<>();
        likes = new ArrayList<>();
        giftAdapter = new GiftAdapter(gifts, this);
        binding.productsRecyclerView.setAdapter(giftAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v -> signOut());
        binding.imageFilter.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
    }

    private void listenLikes() {
        database.collection(Constants.KEY_COLLECTION_LIKES)
                .whereEqualTo(Constants.KEY_USER_ID, database.document("/users/" + preferenceManager.getString(Constants.KEY_USER_ID)))
                .addSnapshotListener(eventLikesListener);
    }

    private void listenGifts() {
        database.collection(Constants.KEY_COLLECTION_GIFTS)
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventLikesListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String likeId = documentChange.getDocument().getId();
                    Like like = new Like();
                    like.id = likeId;
                    like.userId = documentChange.getDocument().getDocumentReference(Constants.KEY_USER_ID).getId();
                    like.giftId = documentChange.getDocument().getDocumentReference(Constants.KEY_GIFT_ID).getId();
                    likes.add(like);
                } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                    for (int i = 0; i < likes.size(); i++) {
                        String likeId = documentChange.getDocument().getId();
                        if (likes.get(i).id.equals(likeId)) {
                            likes.remove(i);
                        }
                    }
                }
            }
            for (int i = 0; i < likes.size(); i++) {
                for (int j = 0; j < gifts.size(); j++) {
                    if(likes.get(i).giftId.equals(gifts.get(j).id)) {
                        gifts.get(j).isLiked = true;
                    }
                }
            }
        }
    };

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
                    gift.tags = (ArrayList<String>)documentChange.getDocument().get("tags");
                    gift.name = documentChange.getDocument().getString(Constants.KEY_NAME);
                    gift.image = documentChange.getDocument().getString(Constants.KEY_IMAGE);
                    gift.description = documentChange.getDocument().getString(Constants.KEY_DESCRIPTION);
                    gift.countLikes = documentChange.getDocument().get(Constants.KEY_COUNT_LIKES, Integer.class);
                    gifts.add(gift);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < gifts.size(); i++) {
                        String giftId = documentChange.getDocument().getId();
                        if (gifts.get(i).id.equals(giftId)) {
                            gifts.get(i).name = documentChange.getDocument().getString(Constants.KEY_NAME);
                            gifts.get(i).image = documentChange.getDocument().getString(Constants.KEY_IMAGE);
                            gifts.get(i).tags = (ArrayList<String>)documentChange.getDocument().get("tags");
                            gifts.get(i).description = documentChange.getDocument().getString(Constants.KEY_DESCRIPTION);
                            gifts.get(i).countLikes = documentChange.getDocument().get(Constants.KEY_COUNT_LIKES, Integer.class);
                        }
                    }
                }
            }
            if(!selectedTags.contains("Empty")){
                Log.d("shop-catalog",selectedTags.toString());
                ArrayList<Gift> filteredGifts =  new ArrayList<>();
                for(Gift gift :gifts){
                    for(String tag:selectedTags){
                            if(tag.equals("0 - 18")){
                                if(gift.tags.contains("0 - 18")){
                                    filteredGifts.add(gift);
                                }
                                break;
                            }else if(tag.equals("19 - 35")){
                                if(gift.tags.contains("19 - 35")){
                                    filteredGifts.add(gift);
                                }
                                break;
                            }else if(tag.equals("36 - 60")){
                                if(gift.tags.contains("36 - 60")){
                                    filteredGifts.add(gift);
                                }
                                break;
                            }else if(tag.equals("60+")){
                                if(gift.tags.contains("60+")){
                                    filteredGifts.add(gift);
                                }
                                break;
                            }else if(tag.equals("Мужской")){
                                if(gift.tags.contains("Мужской")){
                                    filteredGifts.add(gift);
                                }
                                break;
                            }else if(tag.equals("Женский")){
                                if(gift.tags.contains("Женский")){
                                    filteredGifts.add(gift);
                                }
                                break;
                            }else if(gift.tags.contains(tag)) {
                                filteredGifts.add(gift);
                            }
                    }
                }
                giftAdapter.setGifts(filteredGifts);
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

        String currentLikeId = null;

        for (int i = 0; i < likes.size(); i++) {
            if (gift.id.equals(likes.get(i).giftId)) {
                currentLikeId = likes.get(i).id;
                gift.isLiked = true;
            }
        }

        if (currentLikeId == null) {
            addLike(gift);
        } else {
            removeLike(gift, currentLikeId);
        }
    }

    public void addLike(Gift gift) {
        HashMap<String, Object> currentGift = new HashMap<>();
        HashMap<String, Object> currentLike = new HashMap<>();

        currentGift.put(Constants.KEY_COUNT_LIKES, gift.countLikes + 1);

        currentLike.put(Constants.KEY_USER_ID, database.document("/users/" + preferenceManager.getString(Constants.KEY_USER_ID)));
        currentLike.put(Constants.KEY_GIFT_ID, database.document("/gifts/" + gift.id));

        database.collection(Constants.KEY_COLLECTION_LIKES).add(currentLike).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Error!");
            }
        });
    }

    public void removeLike(Gift gift, String likeId) {
        if (likeId == null) {
            return;
        }
        gift.isLiked = false;
        HashMap<String, Object> currentGift = new HashMap<>();

        currentGift.put(Constants.KEY_COUNT_LIKES, gift.countLikes - 1);

        database.collection(Constants.KEY_COLLECTION_LIKES).document(likeId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                database.collection(Constants.KEY_COLLECTION_GIFTS)
                        .document(gift.id)
                        .update(currentGift)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                showToast("Like deleted!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast("Error!");
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Error!");
            }
        });
    }
}