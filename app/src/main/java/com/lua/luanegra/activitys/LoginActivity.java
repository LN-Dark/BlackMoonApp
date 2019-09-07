package com.lua.luanegra.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lamudi.phonefield.PhoneInputLayout;
import com.lua.luanegra.R;
import com.lua.luanegra.tools.DelayedProgressDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private EditText mvercode;
    private MaterialButton mVerifyCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String userVerificationID;
    private final DelayedProgressDialog progressDialog = new DelayedProgressDialog();
    private PhoneInputLayout phoneInputLayout;
    private String phoneNumber;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    public void setTheme(int resId) {
        SharedPreferences prefs = getSharedPreferences("AppTheme", Context.MODE_PRIVATE);
        String tema = " ";
        tema = prefs.getString("AppTheme", " ");
        if(tema != " "){
            if(tema.equals("light")){
                super.setTheme(R.style.AppTheme_Light);
            }else {
                super.setTheme(R.style.AppTheme);
            }
        }else {
            super.setTheme(R.style.AppTheme_Light);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + "LogIn");
        setSupportActionBar(toolbar);
        phoneInputLayout = findViewById(R.id.phone_input_layout);
        phoneInputLayout.setHint(R.string.escreve_numero_telemovel);
        phoneInputLayout.setDefaultCountry(Locale.getDefault().getCountry());

        phoneInputLayout.setHorizontalGravity(Gravity.CENTER);
        phoneInputLayout.setVerticalGravity(Gravity.CENTER);
        TextView termosdeutilizacao = findViewById(R.id.termosdeutilizacao);
        termosdeutilizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TermosDeUtilizacaoActivity.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        SignInButton googleSignInButton = findViewById(R.id.sign_in_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        userIsLogIn();
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });
        mvercode = findViewById(R.id.code);
        progressDialog.setCancelable(false);
        mVerifyCode = findViewById(R.id.verifycode);
        mvercode.setVisibility(View.INVISIBLE);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                String message = e.getMessage();
                progressDialog.cancel();
                Snackbar.make(findViewById(android.R.id.content), "Erro - " + message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                userVerificationID = s;
                if (phoneNumber.equals("+18885017745")) {
                    mvercode.setVisibility(View.VISIBLE);
                    progressDialog.cancel();
                } else {
                    mvercode.setVisibility(View.INVISIBLE);
                }

                mVerifyCode.setText(R.string.Verificar);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                progressDialog.cancel();
                Snackbar.make(findViewById(android.R.id.content), "Erro - " + s, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };
        mVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                if (phoneInputLayout.isValid()) {
                    phoneInputLayout.setError(null);
                } else {
                    phoneInputLayout.setError(getString(R.string.Invalido));
                    valid = false;
                }
                if (valid) {
                    phoneNumber = phoneInputLayout.getPhoneNumber();
                    progressDialog.show(getSupportFragmentManager(), "tag");
                    if (userVerificationID != null) {
                        verifyPhoneNumberWithCode();
                    } else {
                        StartPhoneNumberVerification();
                    }
                }


            }
        });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(android.R.id.content), "Erro - " + e.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        progressDialog.cancel();
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                             FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                                mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            Calendar CalForDate = Calendar.getInstance();
                                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                                            String currentDate = currentDateFormat.format(CalForDate.getTime());
                                            Calendar CalForTime = Calendar.getInstance();
                                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                                            currentTimeFormat.toLocalizedPattern();
                                            String currentTime = currentTimeFormat.format(CalForTime.getTime());
                                            Map<String, Object> userMap = new HashMap<>();
                                            userMap.put("profile_image", Uri.parse("https://firebasestorage.googleapis.com/v0/b/bdlua-29c71.appspot.com/o/Profile_Images%2F73R6I0KnEuhFwmSD7WpK3TlVon02%2F-LewX1rv1EreGkzlW1lF?alt=media&token=237cb7cb-48e5-4e10-b4cd-27f20096de6d").toString());
                                            userMap.put("name", "UserUnknown");
                                            userMap.put("bio", "Too lazy to write a Bio.");
                                            userMap.put("registerDate", currentDate);
                                            userMap.put("registerTime", currentTime);
                                            userMap.put("registerNick", "UserUnknown");
                                            userMap.put("patrono", "false");
                                            userMap.put("onlineDate", currentDate);
                                            userMap.put("onlineTime", currentTime);
                                            userMap.put("online", "true");
                                            userMap.put("opcoes/exitConf", "true");
                                            userMap.put("opcoes/sociavel", "true");
                                            userMap.put("opcoes/notificacoes", "true");
                                            userMap.put("notificationKey", "testeKey");
                                            mUserDB.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.cancel();
                                                    userIsLogIn();

                                                }
                                            });
                                        }else {
                                            progressDialog.cancel();
                                            userIsLogIn();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        progressDialog.cancel();
                                    }
                                });
                            }else {
                                userIsLogIn();
                                progressDialog.cancel();
                            }
                        } else {
                            String message = Objects.requireNonNull(task.getException()).getMessage();
                            Snackbar.make(findViewById(android.R.id.content), "Erro - " + message, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            progressDialog.cancel();
                        }
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                progressDialog.show(getSupportFragmentManager(), "tag");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                progressDialog.cancel();
            }
        }
    }

    private void verifyPhoneNumberWithCode(){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(userVerificationID, mvercode.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    Calendar CalForDate = Calendar.getInstance();
                                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                                    String currentDate = currentDateFormat.format(CalForDate.getTime());
                                    Calendar CalForTime = Calendar.getInstance();
                                    SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                                    currentTimeFormat.toLocalizedPattern();
                                    String currentTime = currentTimeFormat.format(CalForTime.getTime());
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("profile_image", Uri.parse("https://firebasestorage.googleapis.com/v0/b/bdlua-29c71.appspot.com/o/Profile_Images%2F73R6I0KnEuhFwmSD7WpK3TlVon02%2F-LewX1rv1EreGkzlW1lF?alt=media&token=237cb7cb-48e5-4e10-b4cd-27f20096de6d").toString());
                                    userMap.put("name", "UserUnknown");
                                    userMap.put("bio", "Too lazy to write a Bio.");
                                    userMap.put("registerDate", currentDate);
                                    userMap.put("registerTime", currentTime);
                                    userMap.put("registerNick", "UserUnknown");
                                    userMap.put("patrono", "false");
                                    userMap.put("onlineDate", currentDate);
                                    userMap.put("onlineTime", currentTime);
                                    userMap.put("online", "true");
                                    userMap.put("opcoes/exitConf", "true");
                                    userMap.put("opcoes/sociavel", "true");
                                    userMap.put("opcoes/notificacoes", "true");
                                    userMap.put("notificationKey", "testeKey");
                                    mUserDB.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.cancel();
                                            userIsLogIn();

                                        }
                                    });
                                }else {
                                    progressDialog.cancel();
                                    userIsLogIn();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressDialog.cancel();
                                Snackbar.make(findViewById(android.R.id.content), "Erro - " + databaseError.getMessage(), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                    }else {
                        userIsLogIn();
                        progressDialog.cancel();
                    }
                } else {
                    String message = Objects.requireNonNull(task.getException()).getMessage();
                    Snackbar.make(findViewById(android.R.id.content), "Erro - " + message, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    progressDialog.cancel();
                }
            }
        });
    }

    private void userIsLogIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            progressDialog.cancel();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }else {
            progressDialog.cancel();
        }
    }

    private void StartPhoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, mCallbacks);
    }

}
