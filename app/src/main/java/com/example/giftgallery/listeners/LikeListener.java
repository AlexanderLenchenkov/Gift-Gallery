package com.example.giftgallery.listeners;

import android.view.View;

import com.example.giftgallery.models.Gift;

public interface LikeListener {
    void onLikeClicked(View view, Gift gift);
}
