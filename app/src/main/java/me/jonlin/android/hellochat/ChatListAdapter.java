package me.jonlin.android.hellochat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter
{
    private Activity mActivity;
    private DatabaseReference mDatabasereference;
    private String mDisplayName;
    // dadtabase table like snapshot. Immutable getter.
    private ArrayList<DataSnapshot> mSnapshotList;

    private ChildEventListener mListener = new ChildEventListener()
    {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
        {
            Log.d("mainchat", "onChildAdded called");
            //dataSnapshot data ("message") is in json
            mSnapshotList.add(dataSnapshot);
            //need listview to refresh itself for new data.
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
        {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
        {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
        {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError)
        {

        }
    };

    public ChatListAdapter(Activity activity, DatabaseReference ref, String name)
    {
        Log.d("mainchat", "ChatListAdapter constructer");
        this.mActivity = activity;
        this.mDatabasereference = ref.child("messages");
        this.mDisplayName = name;
        this.mSnapshotList = new ArrayList<>();
        this.mDatabasereference.addChildEventListener(mListener);
    }

    static class ViewHolder
    {
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount()
    {
        return mSnapshotList.size();
    }

    // getting data for the list view item.
    @Override
    public InstantMessage getItem(int i)
    {
        Log.d("mainchat", "getItem CALLED ");

        //ChildEventListener. gets notified when a change in database
        DataSnapshot snapShot = mSnapshotList.get(i);
        //Log.d("mainchat", "getItem: " + snapShot.getValue(String.class));
          Log.d("mainchat", "snap" + snapShot.getValue(InstantMessage.class));
        //converts the Json from the snapshot to an InstantMessage object
        InstantMessage test = snapShot.getValue(InstantMessage.class);
        return snapShot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int i)
    {
        Log.d("mainchat", "getItemId called");
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        Log.d(this.getClass().getSimpleName(), "getView called");
        //view is listItem to recycle
        //create new row if it is null
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.chat_msg_row, viewGroup, false);
            final ViewHolder holder = new ViewHolder();
            holder.authorName = (TextView) view.findViewById(R.id.author);
            holder.body = (TextView) view.findViewById(R.id.message);
            holder.params= (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();
            //temporary store view holder inside in the created view so findviewbyid does not need to be called again
            view.setTag(holder);
        }


        final InstantMessage message = getItem(i);
        Log.d(this.getClass().getSimpleName(), "messaghe: " + message.getMessage());

        final ViewHolder holder = (ViewHolder) view.getTag();

        boolean isUser = message.getAuthor().equals(mDisplayName);

        setChatRowAppearance(isUser, holder);

        holder.authorName.setText(message.getAuthor());
        holder.body.setText(message.getMessage());

        return view;
    }

    private void setChatRowAppearance(boolean isUser, ViewHolder holder)
    {
        //if the message is from user, align row to the right
        if(isUser)
        {
            holder.params.gravity = Gravity.END;
            //lime green
            holder.authorName.setTextColor(Color.rgb(50,205,50));
            holder.body.setBackgroundResource(R.drawable.bubble2);
        }
        else
        {
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);

        }

        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);
    }

    public void cleanUp()
    {
        mDatabasereference.removeEventListener(mListener);
    }
}
