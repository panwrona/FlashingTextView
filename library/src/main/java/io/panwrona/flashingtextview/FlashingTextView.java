package io.panwrona.flashingtextview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;

public class FlashingTextView extends TextView {

    private int mInterval = 0;
    private int mFromColor = 0;
    private int mToColor = 0;

    private boolean isFlashing;

    private State mState;
    private AnimatorSet mAnimatorSet;
    private ValueAnimator mSecondAnimation;
    private ValueAnimator mFirstAnimation;

    public boolean isFlashing() {
        return isFlashing;
    }

    private enum State {
        READY, START, STOP
    }

    public FlashingTextView(Context context) {
        super(context);
        init(context, null);
    }

    public FlashingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlashingTextView, 0, 0);
        try {
            mInterval = array.getInteger(R.styleable.FlashingTextView_ftv_interval, 0);
            mFromColor = array.getColor(R.styleable.FlashingTextView_ftv_fromColor, 0);
            mToColor = array.getColor(R.styleable.FlashingTextView_ftv_toColor, 0);
        } finally {
            array.recycle();
        }
        prepareAnimations();
    }

    private void prepareAnimations() {
        if(mInterval == 0 || mFromColor == 0 || mToColor == 0)
            throw new IllegalArgumentException("Define all of the attributes");

        mFirstAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), mFromColor, mToColor);
        mFirstAnimation.setDuration(mInterval / 2);
        mFirstAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setTextColor((Integer) animation.getAnimatedValue());
            }
        });

        mSecondAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), mToColor, mFromColor);
        mSecondAnimation.setDuration(mInterval / 2);
        mSecondAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setTextColor((Integer) animation.getAnimatedValue());
            }
        });

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playSequentially(mFirstAnimation, mSecondAnimation);
        mAnimatorSet.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mState = State.START;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mState == State.START) {
                    animation.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void start() {
        if(mAnimatorSet == null)
            throw new IllegalStateException("Animations are not prepared");
        else
            mAnimatorSet.start();
    }

    public void stop() {
        if(mAnimatorSet == null)
            throw new IllegalStateException("Animations are not prepared");
        else {
            mState = State.STOP;
            mAnimatorSet.end();
        }
    }

    public void end() {
        if(mAnimatorSet == null)
            throw new IllegalStateException("Animations are not prepared");
        else {
            mState = State.READY;
        }
    }

    public void setFromColor(@ColorInt int color) {
        mFromColor = color;
        prepareAnimations();
    }

    public void setToColor(@ColorInt int color) {
        mToColor = color;
        prepareAnimations();
    }

    public void setInterval(int interval) {
        mInterval = interval;
        prepareAnimations();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.isFlashing = mState == State.START;
        savedState.mmCurrentPlayTime = mFirstAnimation.getCurrentPlayTime() > 0 ? mFirstAnimation.getCurrentPlayTime() : mSecondAnimation.getCurrentPlayTime() ;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            isFlashing = savedState.isFlashing;
            super.onRestoreInstanceState(savedState.getSuperState());
            if(isFlashing) {
                continueFlashing(savedState.mmCurrentPlayTime, savedState.mmCurrentAnimation);
            } else if(mState == State.STOP) {
                setStoppedFlashingPosition(savedState.mmCurrentPlayTime, savedState.mmCurrentAnimation);
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private void setStoppedFlashingPosition(long mmCurrentPlayTime, int mmCurrentAnimation) {
        if(mmCurrentAnimation == 0) {
            mFirstAnimation.setCurrentPlayTime(mmCurrentPlayTime);
        } else {
            mSecondAnimation.setCurrentPlayTime(mmCurrentPlayTime);
        }
    }

    private void continueFlashing(long mmCurrentPlayTime, int mmCurrentAnimation) {
        if(mmCurrentAnimation == 0) {
            mFirstAnimation.setCurrentPlayTime(mmCurrentPlayTime);
        } else {
            mSecondAnimation.setCurrentPlayTime(mmCurrentPlayTime);
        }
        mAnimatorSet.start();
    }

    static class SavedState extends BaseSavedState {

        private boolean isFlashing;
        private boolean isConfigurationChanged;
        private long mmCurrentPlayTime;
        private int mmCurrentAnimation;

        public SavedState(Parcel source) {
            super(source);
            isFlashing = source.readInt() == 1;
            isConfigurationChanged = source.readInt() == 1;
            mmCurrentPlayTime = source.readLong();
            mmCurrentAnimation = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isFlashing ? 1 : 0);
            dest.writeInt(isConfigurationChanged ? 1 : 0);
            dest.writeLong(mmCurrentPlayTime);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
