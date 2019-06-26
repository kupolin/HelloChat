package me.jonlin.android.hellochat;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.login_email);
        mPasswordView = (EditText) findViewById(R.id.login_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {

                log("passView " + "setOnEditorActionListener - id: " + id + "IME_NULL: "+ EditorInfo.IME_NULL );

                if (id == R.integer.login || id == EditorInfo.IME_NULL || id == EditorInfo.IME_ACTION_DONE)
                {
                    log("attempt login in login_pass view");
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
        attemptLogin();

    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    private void log(String str)
    {
        Log.d(this.getClass().getSimpleName() + "d", str);
    }

    private void toast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /*
https://stackoverflow.com/questions/43444945/firebase-auth-cant-instantiate-authcredential
For email :

AuthCredential credential = EmailAuthProvider
.getCredential("user@example.com", "password1234");

AuthCredential credential = GoogleAuthProvider
.getCredential(acct.getIdToken(), null);
For facebook you need facebook AccessToken getToken() docs

AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
 */
    private void attemptLogin()
    {
        log("AttemptLogin() called");

        String emailStr = mEmailView.getText().toString();
        String passStr = this.mPasswordView.getText().toString();

        Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);

//        emailStr = "qwerty@test.com";
//        passStr = "qwertyu";

//        emailStr = "test@gmail.com";
//        passStr = "test123";

        if (emailStr.isEmpty() || passStr.isEmpty())
            return;

        mAuth.signInWithEmailAndPassword(emailStr, passStr)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    log("signInWithEmail() onComplete: " + task.isSuccessful());

                    if (!task.isSuccessful())
                    {
                        log("Problem signing in: " + task.getException().getMessage());
                        showErrorDialog(task.getException().getLocalizedMessage());
                    } else
                    {
                        Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }
            });
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