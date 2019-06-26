package me.jonlin.android.hellochat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    // Constants
    public static final String CHAT_PREFS = "ChatPrefs";
    public static final String DISPLAY_NAME_KEY = "username";

    // TODO: Add member variables here:
    // UI references.
    // autocomplete edit text view
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;

    // Firebase instance variables
    private FirebaseAuth mAuth;

    private String emailStr;
    private String passwordStr;

    private void log(String str)
    {
        Log.d(this.getClass().getSimpleName(), str);
    }

    private void toast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_email);
        mPasswordView = (EditText) findViewById(R.id.register_password);
        mConfirmPasswordView = (EditText) findViewById(R.id.register_confirm_password);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.register_username);

        // Keyboard sign in action
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.register_form_finished || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    // Executed when Sign Up button is pressed.
    public void signUp(View v) {
        Toast.makeText(this, "hello!!!", Toast.LENGTH_SHORT).show();
        attemptRegistration();
    }

    private void attemptRegistration() {

        // Reset errors displayed in the form.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            toast("createFireBaseUser() called");
            createFirebaseUser();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    // 6 character minimum password
    private boolean isPasswordValid(String password)
    {
        String confPass = mConfirmPasswordView.getText().toString();
        log("password: " + password + "| confirm password: " + confPass );
        return password.length() > 6 && password.equals(confPass);
    }

    private void createFirebaseUser()
    {
        log("createFireBaseUser called 140");
        this.emailStr = this.mEmailView.getText().toString();
        this.passwordStr = this.mPasswordView.getText().toString();
        if (isPasswordValid(passwordStr))
        {
            // check for response
            mAuth.createUserWithEmailAndPassword(this.emailStr, passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    log("OnComplete called 151");
                    toast("onComplete, called");
                    //User account creation can fail if the account already exists or the password is invalid.
                    if(!task.isSuccessful())
                    {
                        Exception e = task.getException();
                        if(e != null)
                            showErrorDialog(e.getLocalizedMessage());

//                        log(task.getException().getLocalizedMessage());
//                        log(task.getException().getMessage());
//                        toast(task.getException().toString());
                        toast("user creation failed, called");
                        log("user creation failed");
                    }
                    else
                    {
                        //TODO: startActivityWithResult
                        saveDisplayName();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void saveDisplayName()
    {
        // save into current logged in user.
        // can store information into based on user. Normally used for profiles

        FirebaseUser user = mAuth.getCurrentUser();
        String displayName = mUsernameView.getText().toString();

        //user gotta be logged in because saveDisplayName called after creation of userName.
        if(user != null)
        {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

            user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                            Log.d("mainchat", "user name udpated");
                    }
                });
        }
        /*
        SharedPreferences.Editor pref = getSharedPreferences(CHAT_PREFS, MODE_PRIVATE).edit();
        pref.putString(DISPLAY_NAME_KEY, displayName)
            .apply();
            */
    }

    private void showErrorDialog(String message)
    {
        log("showErrorDialog called");
        log("msg" + message);
        new AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
}
