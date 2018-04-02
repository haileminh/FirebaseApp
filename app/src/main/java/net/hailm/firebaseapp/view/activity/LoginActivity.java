package net.hailm.firebaseapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import net.hailm.firebaseapp.MainActivity;
import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.base.BaseActivity;
import net.hailm.firebaseapp.listener.LoginListener;
import net.hailm.firebaseapp.service.LoginService;
import net.hailm.firebaseapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements FirebaseAuth.AuthStateListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 1000;
    @BindView(R.id.edt_username)
    EditText edtUsername;
    @BindView(R.id.edt_password)
    EditText edtPass;

    private String email;
    private String password;

    private LoginService mLoginService;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions mSignInOptions;
    private GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeComponents();
    }

    private void initializeComponents() {
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        mLoginService = new LoginService();



        mSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .build();

        mApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mSignInOptions)
                .build();
    }

    @OnClick({R.id.btn_login_email, R.id.btn_login_google, R.id.btn_register, R.id.tv_lost_pass})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_login_email:
                loginAccountEmail();
                break;
            case R.id.btn_login_google:
                Intent iLoginGoogle = Auth.GoogleSignInApi.getSignInIntent(mApiClient);
                startActivityForResult(iLoginGoogle, RC_SIGN_IN);
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.tv_lost_pass:

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                String idToken = result.getSignInAccount().getIdToken();
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        LogUtils.d("Login google success....");
                    }
                });
            }
        }
    }

    /**
     * loginAccountEmail
     */
    private void loginAccountEmail() {
        if (checkInputData()) {
            showProgressDialog("Đăng nhập ...");
            mLoginService.loginAccountEmail(email, password, new LoginListener() {
                @Override
                public void loginSuccess() {
                    hideProgressDialog();
//                    Toast.makeText(LoginActivity.this, getString(R.string.msg_login_success), Toast.LENGTH_SHORT).show();
                    LogUtils.d("Login success");
                }

                @Override
                public void loginFailure(String message) {
                    hideProgressDialog();
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    LogUtils.d("Login failure");
                }
            });
        }
    }

    /**
     * checkInputData
     *
     * @return
     */
    private boolean checkInputData() {
        if (Utils.isEmpty(edtUsername) && Utils.isEmpty(edtPass)) {
            email = edtUsername.getText().toString().trim();
            password = edtPass.getText().toString().trim();
            if (!Utils.isEmailValid(email)) {
                edtUsername.requestFocus();
                edtUsername.setError(getResources().getString(R.string.email_error));
                return false;
            } else {
                if (password.length() < 6) {
                    edtPass.requestFocus();
                    edtPass.setError(getResources().getString(R.string.pass_erro));
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        mAuth.removeAuthStateListener(this);
        super.onStop();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Kiem tra da active hay chua
            if (user.isEmailVerified()) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                mAuth.signOut();
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Thông báo");
                builder.setMessage(getResources().getString(R.string.verifiation));
                builder.setIcon(R.drawable.my_logo);
                builder.create().show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
