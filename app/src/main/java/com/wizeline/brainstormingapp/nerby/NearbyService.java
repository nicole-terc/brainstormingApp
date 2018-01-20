package com.wizeline.brainstormingapp.nerby;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;


/**
 * Created by Nicole Terc on 1/19/18.
 */

public class NearbyService {
    private static final String TAG = "";
    private Context context;
    MessageListener mMessageListener;
    Message mMessage = new Message("Hello World".getBytes());


    public NearbyService(Context context) {
        this.context = context;
    }

    public void start() {
        MessageListener mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d(TAG, "Found message: " + new String(message.getContent()));
                new AlertDialog.Builder(context)
                        .setTitle("Message received")
                        .setMessage(new String(message.getContent()))
                        .create()
                        .show();
            }

            @Override
            public void onLost(Message message) {
                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
                new AlertDialog.Builder(context)
                        .setTitle("Message lost")
                        .setMessage(new String(message.getContent()))
                        .create()
                        .show();
            }
        };
        Nearby.getMessagesClient(context).subscribe(mMessageListener);
        mMessage = new Message("Hello World".getBytes());
    }

    public void startBroadcasting() {
        Nearby.getMessagesClient(context).publish(mMessage);
    }

    public void stopBroadcasting(){
        Nearby.getMessagesClient(context).unpublish(mMessage);
    }

    public void stop(){
        Nearby.getMessagesClient(context).unsubscribe(mMessageListener);
    }


}
