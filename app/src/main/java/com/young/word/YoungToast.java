package com.young.word;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class YoungToast {
    public static void showTextToast(String msg, Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.toast_young,null);
        TextView tvToast = view.findViewById(R.id.tv_toast);
        tvToast.setText(msg);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM,0,200);
        toast.setView(view);
        toast.show();
    }
}
