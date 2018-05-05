package net.hailm.firebaseapp.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.view.activities.RegisterActivity;

public class DialogUtils {
    public static ProgressDialog mProgressDialog;

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


    public static void showProgressDialog(String message, Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    public static void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
