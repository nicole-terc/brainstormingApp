package com.wizeline.brainstormingapp.VoteTinder;

import android.graphics.Point;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mindorks.placeholderview.SwipeDirection;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInDirectional;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutDirectional;
import com.mindorks.placeholderview.annotations.swipe.SwipeTouch;
import com.mindorks.placeholderview.annotations.swipe.SwipeView;
import com.wizeline.brainstormingapp.repository.Message;
import com.wizeline.brainstormingapp.R;
import com.wizeline.brainstormingapp.repository.UserVote;

/**
 * Created by omarsanchez on 1/19/18.
 */
@Layout(R.layout.tinder_vote_card)
public class VoteHolder {

    @View(R.id.message_to_vote)
    private TextView messageText;

    @SwipeView
    private FrameLayout mSwipeView;

    private Message message;
    private Point mCardViewHolderSize;
    private Callback callback;

    public VoteHolder(Message message, Point mCardViewHolderSize, Callback callback) {
        this.message = message;
        this.mCardViewHolderSize = mCardViewHolderSize;
        this.callback = callback;
    }

    @Resolve
    private void onResolved() {
        messageText.setText(message.getText());
        mSwipeView.setAlpha(1);
    }

    @SwipeTouch
    private void onSwipeTouch(float xStart, float yStart, float xCurrent, float yCurrent) {

        float cardHolderDiagonalLength =
                (float) Math.sqrt(Math.pow(mCardViewHolderSize.x, 2) + (Math.pow(mCardViewHolderSize.y, 2)));
        float distance = (float) Math.sqrt(Math.pow(xCurrent - xStart, 2) + (Math.pow(yCurrent - yStart, 2)));

        float alpha = 1 - distance / cardHolderDiagonalLength;

        mSwipeView.setAlpha(alpha);
    }

    @SwipeCancelState
    private void onSwipeCancelState() {
        Log.d("DEBUG", "onSwipeCancelState");
        mSwipeView.setAlpha(1);
    }

    @SwipeOutDirectional
    private void onSwipeOutDirectional(SwipeDirection direction) {
        if(direction.getDirection() == SwipeDirection.RIGHT_TOP.getDirection()){
            callback.onVote(getUserVote(1));
        }else {
            callback.onVote(getUserVote(-1));
        }
    }

    @SwipeInDirectional
    private void onSwipeInDirectional(SwipeDirection direction) {
        callback.onVote(getUserVote(1));
    }

    @SwipeOut
    private void onSwipedOut(){
        callback.onVote(getUserVote(-1));
    }

    @SwipeIn
    private void onSwipeIn(){
        callback.onVote(getUserVote(1));
    }

    private UserVote getUserVote(int value) {
        return new UserVote(message.getId(), value);
    }

    public interface Callback {
        void onVote(UserVote vote);
    }
}
