package com.example.han.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CheatActivity extends AppCompatActivity {
    private static final String TAG = "CheatActivity";
    private static final String ANSWER_TRUE_KEY = "answer_true_key";
    private static final String ANSWER_SHOWN_KEY  = "answer_shown_key";
    private static final String CHEAT_COUNT = "com.example.han.geoquiz.cheat_count";
    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.han.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.example.han.geoquiz.answer_shown";
    private static final int MAX_CHEAT = 3;

    // Layout views
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private TextView mDeviceAPILevel;
    private TextView mCheatRemainingTextView;

    private boolean mAnswerIsTrue;
    private boolean mAnswerShown;
    private int cheatCount;

    // Intent related functions
    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int cheatCount) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(CHEAT_COUNT, cheatCount);
        return intent;
    }

    private void setAnswerShownResult() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ANSWER_SHOWN, mAnswerShown);
        setResult(RESULT_OK, intent);
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    // Show answer
    private void showAnswer() {
        if(mAnswerIsTrue) {
            mAnswerTextView.setText(R.string.true_button);
        } else {
            mAnswerTextView.setText(R.string.false_button);
        }
    }

    private void updateCheatRemaining(int cheatCount) {
        mCheatRemainingTextView.setText("Cheats remaining: " + (MAX_CHEAT - cheatCount));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mCheatRemainingTextView = (TextView) findViewById(R.id.cheat_remaining_text_view);
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        cheatCount = getIntent().getIntExtra(CHEAT_COUNT, 0);

        if (savedInstanceState != null) {
            mAnswerIsTrue = savedInstanceState.getBoolean(ANSWER_TRUE_KEY, false);
            mAnswerShown = savedInstanceState.getBoolean(ANSWER_SHOWN_KEY, false);
        }

        if (cheatCount == MAX_CHEAT) {
            mShowAnswerButton.setEnabled(false);
        } else if (mAnswerShown) {
            showAnswer();
            setAnswerShownResult();
        }

        updateCheatRemaining(cheatCount);


        mDeviceAPILevel = (TextView) findViewById(R.id.device_api_level);
        mDeviceAPILevel.setText("API Level " + Integer.toString(Build.VERSION.SDK_INT));


        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnswerShown = true;
                showAnswer();
                setAnswerShownResult();
                cheatCount++;
                updateCheatRemaining(cheatCount);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putBoolean(ANSWER_TRUE_KEY, mAnswerIsTrue);
        savedInstanceState.putBoolean(ANSWER_SHOWN_KEY, mAnswerShown);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
