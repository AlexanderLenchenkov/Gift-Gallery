package com.example.giftgallery.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftgallery.listeners.LikeListener;
import com.example.giftgallery.models.Gift;
import com.example.giftgallery.databinding.CatalogItemBinding;

import java.util.List;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {

    private final List<Gift> gifts;
    private final LikeListener likeListener;

    public GiftAdapter(List<Gift> gifts, LikeListener likeListener) {
        this.gifts = gifts;
        this.likeListener = likeListener;
    }

    @NonNull
    @Override
    public GiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CatalogItemBinding catalogItemBinding = CatalogItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new GiftViewHolder(catalogItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GiftViewHolder holder, int position) {
        holder.setGiftData(gifts.get(position));
    }


    @Override
    public int getItemCount() {
        return gifts.size();
    }

    class GiftViewHolder extends RecyclerView.ViewHolder {

        CatalogItemBinding binding;

        public GiftViewHolder(CatalogItemBinding catalogItemBinding) {
            super(catalogItemBinding.getRoot());
            binding = catalogItemBinding;
        }

        void setGiftData(Gift gift) {
            binding.textProductName.setText(gift.name);
            binding.textProductDescr.setText(gift.description);
            binding.textCountLikes.setText(String.valueOf(gift.countLikes));
            if(gift.isLiked) {
                binding.imageLike.setColorFilter(Color.RED);
            }
            binding.imageLike.setOnClickListener(v -> likeListener.onLikeClicked(v, gift));

        }
    }

}
