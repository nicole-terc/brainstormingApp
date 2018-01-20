package com.wizeline.brainstormingapp.VoteTinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipeDirectionalView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
import com.wizeline.brainstormingapp.App;
import com.wizeline.brainstormingapp.R;
import com.wizeline.brainstormingapp.champions.ChampionsActivity;
import com.wizeline.brainstormingapp.repository.Message;
import com.wizeline.brainstormingapp.repository.Repository;
import com.wizeline.brainstormingapp.repository.UserVote;
import com.wizeline.brainstormingapp.repository.Vote;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VoteActivity extends AppCompatActivity implements VoteHolder.Callback {
    private final static String ROOM_ID = "roomId";
    private SwipeDirectionalView mSwipeView;
    private Context mContext;
    private int mAnimationDuration = 300;
    //    private boolean isToUndo = false;
    private Point cardViewHolderSize;
    private List<UserVote> userVotes;

    private String roomId;
    private Repository repository;
    private View buttonsLayout;
    private View waitingMessage;

    public static Intent getIntent(Context context, String roomId) {
        Intent i = new Intent(context, VoteActivity.class);
        i.putExtra(ROOM_ID, roomId);
        return i;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        roomId = getIntent().getStringExtra(ROOM_ID);
        userVotes = new ArrayList<>();
        mSwipeView = findViewById(R.id.swipeView);
        waitingMessage = findViewById(R.id.waiting_label);
        buttonsLayout = findViewById(R.id.layout_btns);
        mContext = getApplicationContext();
        repository = ((App) mContext).getRepository();


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
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));


        cardViewHolderSize = new Point(windowSize.x, windowSize.y - bottomMargin);

        repository.getOtherMessages(roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Message>>() {
                    @Override
                    public void accept(List<Message> messages) throws Exception {
                        addMessagesToView(messages, cardViewHolderSize);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });


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

//        findViewById(R.id.undoBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSwipeView.undoLastSwipe();
//            }
//        });

        mSwipeView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
//                if (isToUndo) {
//                    isToUndo = false;
//                    mSwipeView.undoLastSwipe();
//                }
                if (count == 0) {
                    repository.vote(userVotes)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<Vote>>() {
                                @Override
                                public void accept(List<Vote> votes) throws Exception {
                                    startWaitingRoom();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            });

                }
            }
        });
    }

    private void startWaitingRoom() {
        //repository.votingFinished(roomId)
        Observable.just(Boolean.TRUE)
                .delay(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        buttonsLayout.setVisibility(View.INVISIBLE);
                        waitingMessage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(Boolean finished) {
                        if (finished) {
                            goToChampionsActivity();
                            onComplete();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void goToChampionsActivity() {
        Intent intent = new Intent(this, ChampionsActivity.class);
        intent.putExtra(ROOM_ID, roomId);
        startActivity(intent);
    }

    private void addMessagesToView(List<Message> messages, Point cardViewHolderSize) {
        for (Message message : messages) {
            mSwipeView.addView(new VoteHolder(message, cardViewHolderSize, this));
        }
    }

    @Override
    public void onVote(UserVote vote) {
        userVotes.add(vote);
    }
}
