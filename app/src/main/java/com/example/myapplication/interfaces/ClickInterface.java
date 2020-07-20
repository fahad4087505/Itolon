package com.example.myapplication.interfaces;

import android.widget.ImageView;
import android.widget.TextView;

public interface ClickInterface {
    void click(String param, ImageView imageView, TextView textView);
    void showDialog(Boolean check, String audioUrl, String trackId, int position);
}