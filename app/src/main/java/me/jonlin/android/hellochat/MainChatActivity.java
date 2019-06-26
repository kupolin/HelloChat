package me.jonlin.android.hellochat;

import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainChatActivity extends AppCompatActivity
{
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        setupDisplayName();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        //Send the message when the "enter" button is pressed
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                sendMessage();
                return true;
            }
        });

        //sendButton to send a message
        mSendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendMessage();
            }
        });
    }

    private void setupDisplayName()
    {
        SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS, MODE_PRIVATE);
        mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY, null);
        if(mDisplayName == null)
            mDisplayName = "Anonymous";
    }
    private void checkFireBaseConnection()
    {
        final String TAGS = this.getClass().getSimpleName();
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.d(TAGS, "connected");
                } else {
                    Log.d(TAGS, "not connected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAGS, "Listener was cancelled");
            }
        });
    }
    private void log(String str)
    {
        Log.d(this.getClass().getSimpleName(), str);
    }
    private void  sendMessage() {

        log("SendMessage called 77");
        // TODO: Grab the text the user typed in and push the message to Firebase
        String input = mInputText.getText().toString();
        if(!input.equals(""))
        {
            InstantMessage chat = new InstantMessage(input, mDisplayName);
            // find child location named messages
            log("displayName: "  + mDisplayName);
            log("sendMessage() " + chat);

            //chat history
            this.mDatabaseReference.child("messages").push().setValue(chat);
//            this.mDatabaseReference.child("messages").setValue(chat);
            /*
            DatabaseReference usersRef = this.mDatabaseReference.child("messages").push();
            usersRef.setValue(chat);
            checkFireBaseConnection();
            */
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d("mainchat", "OnStartCalled");
        mAdapter = new ChatListAdapter(this, mDatabaseReference, mDisplayName);
        mChatListView.setAdapter(mAdapter);
    }


    @Override
    public void onStop()
    {
        super.onStop();
        mAdapter.cleanUp();
    }

}
