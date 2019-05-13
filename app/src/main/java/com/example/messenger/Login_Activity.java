package com.example.messenger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.example.messenger.Activity_ChatRoomList.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.annotation.Nonnull;


public class Login_Activity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private Button mLoginButton;
    private Button mSubcriptionButton;
    private EditText email;
    private EditText password;

    private FirebaseAuth mAuth;

    private String mUser;
    int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialisation de l'objet FireBase mAuth
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            StartMainActivity();
            //StartLoadindActivity();
        }

        setContentView(R.layout.activity_login_);


        //Initialisation des Vues
        email = (EditText)findViewById(R.id.email_edit);
        password = (EditText)findViewById(R.id.password_edit);
        mLoginButton = (Button)findViewById(R.id.btn_login);
        mSubcriptionButton = (Button)findViewById(R.id.btn_subscription);

        //Relier button à un listener d'evenement
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

        //Relier bouton Subscription à un listener d'événemnet click
        mSubcriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LunchSubcriptionActivity();
            }
        });
    }

    private void StartLoadindActivity()
    {
        Intent intent = new Intent(Login_Activity.this, LoadingAppActivity.class);
        startActivity(intent);
    }

    private void StartMainActivity()
    {
        Intent intent = new Intent(Login_Activity.this, MainActivity.class);
        intent.putExtra("startCode", 1);
        startActivity(intent);
        finish();

    }

    private void LunchSubcriptionActivity()
    {
        Intent intent = new Intent(Login_Activity.this, Subscription_Activity.class);
        startActivity(intent);
        finish();
    }

    private void SignIn()
    {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@Nonnull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("Test", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            StartMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("Test", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login_Activity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
