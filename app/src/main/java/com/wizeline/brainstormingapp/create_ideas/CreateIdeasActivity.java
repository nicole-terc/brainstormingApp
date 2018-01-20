package com.wizeline.brainstormingapp.create_ideas;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipeDirectionalView;
import com.wizeline.brainstormingapp.App;
import com.wizeline.brainstormingapp.R;
import com.wizeline.brainstormingapp.VoteTinder.VoteActivity;
import com.wizeline.brainstormingapp.repository.Message;
import com.wizeline.brainstormingapp.repository.Repository;
import com.wizeline.brainstormingapp.repository.Room;
import com.wizeline.brainstormingapp.util.ParserUtil;
import com.wizeline.brainstormingapp.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CreateIdeasActivity extends AppCompatActivity implements CreateIdeaHolder.Callback {
    private final int mAnimationDuration = 300;
    CountDownTimer countDownTimer;
    private Long remainingTime;
    private Long actualTime;
    private Long finalTime;
    private Repository repository;
    ////
    private SwipeDirectionalView mSwipeView;
    private Point cardViewHolderSize;
    private Room room;
    private List<String> messages;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ideas);
        repository = ((App) getApplicationContext()).getRepository();
        String roomJson = getIntent().getExtras().getString("room");
        this.room = ParserUtil.Companion.jsonToRoom(roomJson);
        finalTime = room.getStartTime() + 15000L;
        mSwipeView = findViewById(R.id.swipeView);
        messages = new ArrayList<>();
        toolbar = getSupportActionBar();
        int bottomMargin = Utils.dpToPx(160);
        Point windowSize = Utils.getDisplaySize(getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setIsUndoEnabled(true)
                .setSwipeVerticalThreshold(Utils.dpToPx(50))
                .setSwipeHorizontalThreshold(Utils.dpToPx(50))
                .setHeightSwipeDistFactor(10)
                .setWidthSwipeDistFactor(5)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setSwipeAnimTime(mAnimationDuration)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view2)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view2));
        cardViewHolderSize = new Point(windowSize.x, windowSize.y - bottomMargin);

        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });
        for (int i = 0; i < 5; i++) {
            mSwipeView.addView(new CreateIdeaHolder(cardViewHolderSize, this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualTime = Calendar.getInstance().getTimeInMillis();
        remainingTime = finalTime - actualTime;
        if (remainingTime > 0) {
            startCountDown();
        } else {
            sendMessages();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(remainingTime, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (toolbar != null) {
                    int sec = (int) (millisUntilFinished / 1000);
                    toolbar.setTitle(sec + " sec remainig");
                }
            }

            @Override
            public void onFinish() {
                sendMessages();
            }
        }.start();
    }

    private void goForward() {
        startActivity(VoteActivity.getIntent(this, room.getId()));
    }

    @Override
    public void onIdeaCreated(String idea) {
        mSwipeView.addView(new CreateIdeaHolder(cardViewHolderSize, this));
        hideKeyboard();
        messages.add(idea);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void sendMessages() {
        if (messages.size() > 0) {
            repository.createMessage(room.getId(), messages)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<Message>>() {
                        @Override
                        public void accept(List<Message> messages) throws Exception {
                            goForward();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            goForward();
                        }
                    });
        } else {
            goForward();
        }
    }

    @Override
    public void onCanceled() {
        mSwipeView.addView(new CreateIdeaHolder(cardViewHolderSize, this));
        hideKeyboard();

    }
}
