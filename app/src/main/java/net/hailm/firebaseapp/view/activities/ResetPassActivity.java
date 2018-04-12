package net.hailm.firebaseapp.view.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.base.BaseActivity;
import net.hailm.firebaseapp.service.RegisterService;
import net.hailm.firebaseapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPassActivity extends BaseActivity {
    @BindView(R.id.txt_email_reset)
    EditText edtEmail;
    private RegisterService mRegisterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        ButterKnife.bind(this);
        mRegisterService = new RegisterService(this);
    }

    @OnClick(R.id.btn_send_email)
    public void resetPass() {
        String email = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtEmail.requestFocus();
            edtEmail.setError(getResources().getString(R.string.email_null));
        } else if (!Utils.isEmailValid(email)) {
            edtEmail.requestFocus();
            edtEmail.setError(getResources().getString(R.string.email_error));
        } else {
            mRegisterService.resetPass(email);
        }
    }
}
