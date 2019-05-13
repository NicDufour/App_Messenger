package com.example.messenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.messenger.Activity_ChatRoomList.MainActivity;
import com.example.messenger.DbHelper.UserHelper;
import com.example.messenger.BaseActivities.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import javax.annotation.Nonnull;

import static com.example.messenger.BaseActivities.Validation.EmailValidator;
import static com.example.messenger.BaseActivities.Validation.PasswordValidator;
import static com.example.messenger.BaseActivities.Validation.TextValidator;

public class Subscription_Activity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;

    private Button btnSubcribe;
    private Button btnLogin;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_);

        //Initialisation de l'objet FireBase mAuth
        mAuth = FirebaseAuth.getInstance();


        name = findViewById(R.id.name_edit);
        email = findViewById(R.id.email_edit);
        password = findViewById(R.id.passeword_form_edit);
        confirmPassword = findViewById(R.id.passeword_confirm_form_edit);

        btnLogin =findViewById(R.id.btn_login);
        btnSubcribe = findViewById(R.id.btn_subscribe);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);


        btnSubcribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UserValidation())
                {
                    CreateAccount();
                    Log.i("validation", "Utilisateur valide");

                }
                else
                {
                    Log.i("validation", "Utilisateur invalide");
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartLoginActivity();
            }
        });

    }

    private void CreateUserInDataBase()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user !=null)
        {
            String uid = user.getUid();
            String _name = name.getText().toString();
            String mail = user.getEmail();
            String genre = GetSelectedSexe();

            UserHelper.createUser(uid, _name, mail, genre);
        }


    }

    private void StartLoginActivity()
    {
        Intent intent = new Intent(Subscription_Activity.this, Login_Activity.class);
        startActivity(intent);
        finish();
    }

    private void UpdateUserProfil()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@Nonnull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Test", "User profile updated.");
                        }
                    }
                });
    }

    private void CreateAccount()
    {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@Nonnull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            UpdateUserProfil();
                            CreateUserInDataBase();
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("Test", "createUserWithEmail:success");

                            //Si un utilisateur est déja connecté
                            //Fin de l'activité
                            finish();

                            //Ouverture de l'Activité principale de l'Application
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("Test", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Subscription_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String GetSelectedSexe()
    {
        String Sexe;

        mRadioButton = (RadioButton)findViewById(mRadioGroup.getCheckedRadioButtonId());

        if(mRadioButton.getId() == R.id.radio_men)
        {
            Sexe = "M";
        }
        else if (mRadioButton.getId() == R.id.radio_women)
        {
            Sexe = "W";
        }
        else
        {
            Sexe = "O";
        }

        return Sexe;
    }

    private boolean UserValidation()
    {
        boolean valid = true;

        if(!ValidationChamps())
        {
            Toast.makeText(this, R.string.toast_invalid_field, Toast.LENGTH_LONG).show();
            password.setText("");
            confirmPassword.setText("");
            valid = false;
        }
        else if(!Validation.ValidationPassWord(password.getText().toString(), confirmPassword.getText().toString()))
        {
            Log.i("validation", "Les mots de passe ne concorde pas.");
            password.setText("");
            confirmPassword.setText("");
            Toast.makeText(this, R.string.toast_password_matches, Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }

    private boolean ValidationChamps()
    {
        boolean valid = true;

        if(!TextValidator(name.getText().toString()))
        {
            Log.i("validation", "Nom Invalide");
            valid = false;
            name.setText("");

        }
        if(!EmailValidator(email.getText().toString()))
        {
            Log.i("validation", "Email invalide");
            valid = false;
            password.setText("");
        }
        if(!PasswordValidator(password.getText().toString()))
        {
            Log.i("validation", "1er mots de passe invalide");
            valid = false;
            password.setText("");
        }
        if(!PasswordValidator(confirmPassword.getText().toString())) {
            Log.i("validation", "2e mots de passe invalide");
            valid = false;
            confirmPassword.setText("");
        }
        return valid;
    }
}
