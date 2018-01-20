package com.wizeline.brainstormingapp.create_ideas;

import android.graphics.Point;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;

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
import com.wizeline.brainstormingapp.R;

/**
 * Created by omarsanchez on 1/20/18.
 */
@Layout(R.layout.tinder_create_card)
public class CreateIdeaHolder {

    @View(R.id.create_message)
    private EditText messageText;

    @SwipeView
    private FrameLayout mSwipeView;

    private Point mCardViewHolderSize;
    private Callback callback;
    private CharSequence lastText;

    public CreateIdeaHolder(Point mCardViewHolderSize, Callback callback) {
        this.mCardViewHolderSize = mCardViewHolderSize;
        this.callback = callback;
    }

    @Resolve
    private void onResolved() {
        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastText = s;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        if (direction.getDirection() == SwipeDirection.RIGHT_TOP.getDirection()) {
            callback.onIdeaCreated(lastText.toString());
        } else {
            callback.onCanceled();
        }
    }

    @SwipeInDirectional
    private void onSwipeInDirectional(SwipeDirection direction) {
        callback.onIdeaCreated(lastText.toString());
    }

    @SwipeOut
    private void onSwipedOut() {
        callback.onCanceled();
    }

    @SwipeIn
    private void onSwipeIn() {
        callback.onIdeaCreated(lastText.toString());
    }


    public interface Callback {
        void onIdeaCreated(String idea);

        void onCanceled();
    }
}
