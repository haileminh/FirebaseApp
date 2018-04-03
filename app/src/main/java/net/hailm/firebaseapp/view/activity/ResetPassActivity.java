package net.hailm.firebaseapp.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.base.BaseActivity;
import net.hailm.firebaseapp.service.RegisterService;

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
        if (!TextUtils.isEmpty(email)) {
            mRegisterService.resetPass(email);
        } else {
            edtEmail.requestFocus();
            edtEmail.setError("Bạn chưa nhập email");
        }
    }
}
