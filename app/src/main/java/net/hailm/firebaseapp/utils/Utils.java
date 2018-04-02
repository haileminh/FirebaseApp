package net.hailm.firebaseapp.utils;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hai_l on 25/03/2018.
 */

public class Utils {
    public static boolean isEmpty(EditText edtText) {
        if (edtText.getText().toString().trim().length() > 0) {
            return true;
        } else {
            edtText.requestFocus();
            edtText.setError("Vui lòng điền thông tin");
            return false;
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "[a-zA-Z0-9._-]+@[a-z]+(\\.+[a-z]+)+";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
