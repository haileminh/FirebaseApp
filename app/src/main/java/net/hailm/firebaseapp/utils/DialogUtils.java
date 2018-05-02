package net.hailm.firebaseapp.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.view.activities.RegisterActivity;

public class DialogUtils {

    /**
     * Show dialog message
     *
     * @param msg
     * @param context
     */
    public static void showMessage(String msg, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thông báo!");
        builder.setIcon(R.drawable.my_logo);
        builder.setMessage(msg);
        builder.create().show();
    }
}
