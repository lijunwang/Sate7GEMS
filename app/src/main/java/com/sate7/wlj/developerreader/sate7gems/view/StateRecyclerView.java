package com.sate7.wlj.developerreader.sate7gems.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.sate7.wlj.developerreader.sate7gems.util.XLog;

public class StateRecyclerView extends RecyclerView implements ValueAnimator.AnimatorUpdateListener {
    public StateRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public StateRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (getVisibility() == VISIBLE) {
            state = State.OPENED;
        } else {
            state = State.CLOSED;
        }
    }

    private int heightBeforeClose = -1;
    private boolean isOpened = false;
    private final int MSG_OPEN = 0x10;
    private final int MSG_CLOSE = 0x11;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_OPEN:
                    open();
                    break;
                case MSG_CLOSE:
                    close();
                    break;
            }
        }
    };

    public enum State {
        CLOSED,
        CLOSING,
        OPENED,
        OPENING
    }

    private StateListener stateListener;

    public void setStateListener(StateListener listener) {
        stateListener = listener;
    }

    public interface StateListener {
        void onOpened();

        void onClosed();
    }

    private State state;

    public void toggle() {
        XLog.dReport("StateRecyclerView nani .... " + state);
        if (state == State.OPENING || state == State.CLOSING) {
            return;
        }
        if (getVisibility() == INVISIBLE) {
            //open it
            heightBeforeClose = getHeight();
            getLayoutParams().height = 0;
            requestLayout();
            setVisibility(View.VISIBLE);
            handler.removeMessages(MSG_OPEN);
            handler.sendEmptyMessageDelayed(MSG_OPEN, 10);
        }
        if (isOpened) {
            handler.removeMessages(MSG_CLOSE);
            handler.sendEmptyMessageDelayed(MSG_CLOSE, 10);
        } else {
            handler.removeMessages(MSG_OPEN);
            handler.sendEmptyMessageDelayed(MSG_OPEN, 10);
        }
    }

    public void open() {
        XLog.dReport("StateRecyclerView open ..." + heightBeforeClose);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, heightBeforeClose);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(this);
        valueAnimator.addListener(openListener);
        valueAnimator.start();
        isOpened = true;
    }

    public void close() {
        XLog.dReport("StateRecyclerView close ...");
        int height = getHeight();
        heightBeforeClose = height;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(height, 1);
        valueAnimator.addUpdateListener(this);
        valueAnimator.addListener(closeListener);
        valueAnimator.start();
        isOpened = false;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        getLayoutParams().height = (int) animation.getAnimatedValue();
        requestLayout();
    }

    private Animator.AnimatorListener openListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            state = State.OPENING;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            state = State.OPENED;
            if (stateListener != null) {
                stateListener.onOpened();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private Animator.AnimatorListener closeListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            state = State.CLOSING;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            state = State.CLOSED;
            if (stateListener != null) {
                stateListener.onClosed();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
}
