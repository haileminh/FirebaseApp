package net.hailm.firebaseapp.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.base.BaseActivity;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.LoginListener;
import net.hailm.firebaseapp.model.dbhelpers.LoginService;
import net.hailm.firebaseapp.utils.Utils;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements FirebaseAuth.AuthStateListener,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_CODE_SIGN_IN = 1000;
    private static int CHECK_PROVIDE_LOGIN = 0;
    @BindView(R.id.edt_username)
    EditText edtUsername;
    @BindView(R.id.edt_password)
    EditText edtPass;
    @BindView(R.id.btn_login_facebook)
    LoginButton btnLoginFacebook;

    boolean flag = false;
    private String email;
    private String password;

    private LoginService mLoginService;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions mSignInOptions;
    private GoogleApiClient mApiClient;
    private CallbackManager mCallbackManager;

    private SharedPreferences mSharedPreferences;
//    private LoginManager mLoginManager;
//    private List<String> permissionFacebook = Arrays.asList("email", "public_profile");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initializeComponents();
        getKeyHash();
    }

    private void getKeyHash() {
        try {
            PackageInfo info = null;
            try {
                info = getPackageManager().getPackageInfo(
                        "net.hailm.firebaseapp",
                        PackageManager.GET_SIGNATURES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void initializeComponents() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        mLoginService = new LoginService();
        mCallbackManager = CallbackManager.Factory.create();
        mSharedPreferences = getSharedPreferences(Constants.LOCATION, MODE_PRIVATE);

        // Khoi tao client cho login google
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

    @OnClick({R.id.btn_login_email, R.id.btn_login_google, R.id.btn_register, R.id.tv_lost_pass, R.id.btn_login_facebook})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_login_email:
                loginAccountEmail();
                break;
            case R.id.btn_login_google:
                loginGoogle();
                break;
            case R.id.btn_login_facebook:
                flag = true;
                loginFacebook();
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.tv_lost_pass:
                startActivity(new Intent(this, ResetPassActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                String idToken = result.getSignInAccount().getIdToken();
                credentialLoginFirebase(idToken);
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void credentialLoginFirebase(String idToken) {
        if (CHECK_PROVIDE_LOGIN == 1) {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            LogUtils.d("Login google success....");
                        }
                    });
        } else if (CHECK_PROVIDE_LOGIN == 2) {
            AuthCredential credential = FacebookAuthProvider.getCredential(idToken);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                LogUtils.d("Login facebook success...");
                            } else {
                                LogUtils.d("Login facebook failure...");
                                showDialog1(getString(R.string.ton_tai), LoginActivity.this);
                            }
                        }
                    });
        }
    }

    private void loginGoogle() {
        CHECK_PROVIDE_LOGIN = 1;
        Intent iLoginGoogle = Auth.GoogleSignInApi.getSignInIntent(mApiClient);
        startActivityForResult(iLoginGoogle, REQUEST_CODE_SIGN_IN);
    }

    /**
     * loginFacebook
     */
    private void loginFacebook() {
//        mLoginManager = LoginManager.getInstance();
//        mLoginManager.logInWithReadPermissions(this, permissionFacebook);
        btnLoginFacebook.setReadPermissions("email", "public_profile");
        btnLoginFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                CHECK_PROVIDE_LOGIN = 2;
                String tokenID = loginResult.getAccessToken().getToken();
                credentialLoginFirebase(tokenID);
//
//                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                finish();
                result();
            }

            @Override
            public void onCancel() {
                LogUtils.d("Login facebook cancel...");
            }

            @Override
            public void onError(FacebookException error) {
                LogUtils.d("Login facebook error...");
            }
        });
    }

    private void result() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        LogUtils.d("JSON", response.getJSONObject().toString());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name, email, first_name");
        request.setParameters(parameters);
        request.executeAsync();
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
        LoginManager.getInstance().logOut();
        if (mAuth != null) {
            mAuth.addAuthStateListener(this);
        }
    }

    @Override
    protected void onStop() {
        if (mAuth != null) {
            mAuth.removeAuthStateListener(this);
        }
        super.onStop();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Kiem tra da active hay chua
            if (user.isEmailVerified() || flag == true) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(Constants.UID, String.valueOf(user.getUid()));
                editor.putString(Constants.USER_NAME, String.valueOf(user.getDisplayName()));
                editor.putString(Constants.EMAIL, String.valueOf(user.getEmail()));
                editor.commit();
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
