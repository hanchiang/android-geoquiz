package com.example.han.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.han.geoquiz.answer_is_true";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String EXTRA_ANSWER_SHOWN = "com.example.han.geoquiz.answer_shown";

    // Layout views
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;
    private Button mCheatButton;

    // Activity variables
    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    private boolean[] mQuestionAnswered = new boolean[mQuestionBank.length];
    private boolean[] mQuestionCheated = new boolean[mQuestionBank.length];

    private int mCurrentIndex = 0;
    private int numQuestionsAnswered = 0;
    private int numQuestionsCorrect = 0;
    private double score = 0.0;

    // Methods
    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }

    private void updateQuestion() {
        Log.d(TAG, "Updating question text");
        // Log.d(TAG, "Updating question text", new Exception());
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        numQuestionsAnswered ++;

        if (mQuestionCheated[mCurrentIndex]) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                numQuestionsCorrect++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();

        if (numQuestionsAnswered  == mQuestionBank.length) {
            score = (double) numQuestionsCorrect / numQuestionsAnswered  * 100;
            Toast.makeText(this, String.format("Your score: %.2f%%", score), Toast.LENGTH_SHORT).show();
        }
    }

    private void setButtonsStatus() {
        if (mQuestionAnswered[mCurrentIndex]) {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
            mCheatButton.setEnabled(false);
        } else {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
            mCheatButton.setEnabled(true);
        }
    }


    /* Lifecycle methods */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuestionAnswered[mCurrentIndex] = true;
                setButtonsStatus();
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuestionAnswered[mCurrentIndex] = true;
                setButtonsStatus();
                checkAnswer(false);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
                setButtonsStatus();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.previous_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex--;
                if (mCurrentIndex == -1) {
                    mCurrentIndex = mQuestionBank.length - 1;
                }
                updateQuestion();
                setButtonsStatus();
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CheatActivity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = QuizActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mQuestionCheated[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
        }
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
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
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
