package com.example.giftgallery.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Gift implements Serializable {
    public String id, name, description, image;
    public int countLikes;
    public ArrayList<String> tags;
    public Boolean isLiked = false;
}
