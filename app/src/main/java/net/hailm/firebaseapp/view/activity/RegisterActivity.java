package net.hailm.firebaseapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.base.BaseActivity;
import net.hailm.firebaseapp.listener.RegisterListener;
import net.hailm.firebaseapp.service.RegisterService;
import net.hailm.firebaseapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.edt_email_re)
    EditText edtEmail;
    @BindView(R.id.edt_password_re)
    EditText edtPassword;
    @BindView(R.id.edt_re_password_re)
    EditText edtRePassword;
    @BindView(R.id.layout)
    LinearLayout layout;


    private String email;
    private String password;
    private String rePassword;
    private RegisterService mRegisterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mRegisterService = new RegisterService(this);
    }

    @OnClick({R.id.btn_register_email, R.id.btn_back_login, R.id.btn_reset_password})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_register_email:
                registerAccountEmail();
                break;
            case R.id.btn_back_login:
                finish();
                break;
            case R.id.btn_reset_password:
                startActivity(new Intent(this, ResetPassActivity.class));
                break;
            default:
                break;
        }
    }

    private void registerAccountEmail() {
        if (checkInputData()) {
            showProgressDialog("Vui lòng đợi");
            mRegisterService.registerAccount(email, password, new RegisterListener() {
                @Override
                public void registerSuccess() {
                    hideProgressDialog();
                    edtEmail.setText("");
                    edtPassword.setText("");
                    edtRePassword.setText("");
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Thông báo!");
                    builder.setIcon(R.drawable.my_logo);
                    builder.setMessage(getResources().getString(R.string.verifiation));
                    builder.create().show();
                    LogUtils.d("Register success");
                }

                @Override
                public void registerFailure(String message) {
                    hideProgressDialog();
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    LogUtils.d("Register Failure");
                }
            });
        }
    }

    private boolean checkInputData() {
        if (Utils.isEmpty(edtEmail) && Utils.isEmpty(edtPassword) && Utils.isEmpty(edtRePassword)) {
            email = edtEmail.getText().toString().trim();
            password = edtPassword.getText().toString().trim();
            rePassword = edtRePassword.getText().toString().trim();
            if (!Utils.isEmailValid(email)) {
                edtEmail.requestFocus();
                edtEmail.setError(getResources().getString(R.string.email_error));
                return false;
            } else if (password.length() < 6) {
                edtPassword.requestFocus();
                edtPassword.setError(getResources().getString(R.string.pass_erro));
                return false;
            } else {
                if (!password.equals(rePassword)) {
                    edtRePassword.requestFocus();
                    edtRePassword.setError(getResources().getString(R.string.pass_no_duplicate));
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
