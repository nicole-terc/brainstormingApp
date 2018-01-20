package com.wizeline.brainstormingapp.nerby;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;


/**
 * Created by Nicole Terc on 1/19/18.
 */

public class NearbyService {
    private static final String TAG = "NearbyService";
    private Context context;
    MessageListener messageListener;
    Message message;

    public NearbyService(Context context) {
        this.context = context;
    }

    public void startListening(MessageListener messageListener) {
        this.messageListener = messageListener;
        Nearby.getMessagesClient(context).subscribe(this.messageListener);
        Log.d(TAG, "Start listening for messages");
    }

    public void stopListening() {
        if (messageListener != null) {
            Nearby.getMessagesClient(context).unsubscribe(messageListener);
        }
        Log.d(TAG, "stopListening listening for messages");
    }

    public void startBroadcasting(String messageText) {
        if (message != null) {
            Log.d(TAG, "Already casting message: -" + new String(message.getContent()) + "-");
            return;
        }
        message = new Message(messageText.getBytes());
        Nearby.getMessagesClient(context).publish(message);
        Log.d(TAG, "Start cast of message: -" + new String(message.getContent()) + "-");

    }

    public void stopBroadcasting() {
        if (message != null) {
            Log.d(TAG, "Stop cast of message: -" + new String(message.getContent()) + "-");
            Nearby.getMessagesClient(context).unpublish(message);
            message = null;
        }
    }

    public void stop(){
        stopBroadcasting();
        stopListening();
    }
}