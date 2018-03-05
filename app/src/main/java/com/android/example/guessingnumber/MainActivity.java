package com.android.example.guessingnumber;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ArrayList<Integer> answerArr = new ArrayList<>();
    EditText editText;
    TextView logTextView;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        answerArr = generateAnswer();
        TextInputLayout inputLayout = findViewById(R.id.inputLayout);
        editText = inputLayout.getEditText();
        logTextView = findViewById(R.id.logTextView);
        scrollView = findViewById(R.id.scrollView2);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                if (keyCode == EditorInfo.IME_ACTION_DONE) {
                    int guessInt = Integer.parseInt(editText.getText().toString());
                    ArrayList<Integer> guess = new ArrayList<>();
                    for(int i = 0; i< 4; i++) {
                        guess.add(guessInt / 1000 % 10);
                        guessInt *= 10;
                    }
                    editText.setText(guess.get(0).toString() + guess.get(1).toString() + guess.get(2).toString() + guess.get(3).toString());
                    Set<Integer> set = new HashSet<>(guess);
                    if(set.size() < guess.size()) { //check whether number is repeated
                        Toast.makeText(MainActivity.this, R.string.not_repeated, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    StringBuilder builder = new StringBuilder("");
                    for(Integer digit:guess) {
                        builder.append(digit);
                    }
                    String result = checkCorrect(guess);
                    if (result.equals("4A0B")) {
                        editText.setEnabled(false);
                        editText.setText("");
                        logTextView.setText(getString(R.string.correct_message, logTextView.getText(), builder.toString()));
                    } else {
                        editText.setText("");
                        logTextView.setText(getString(R.string.log_message, logTextView.getText(), builder.toString(), result));
                    }
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
                return true; //make keyboard not disappear
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restartAction:
                answerArr = generateAnswer();
                editText.setEnabled(false);
                editText.setEnabled(true);
                InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                logTextView.setText("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        editText.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        editText.setEnabled(false);
    }

    private ArrayList<Integer> generateAnswer() {
        ArrayList<Integer> digits = new ArrayList<Integer>(){{
            for(int i = 0; i<10; i++) {
                add(i);
            }
        }};
        while(digits.get(0) == 0) {
            Collections.shuffle(digits);
        }
        System.err.println(digits);
        return new ArrayList<>(digits.subList(0, 4));
    }

    private String checkCorrect(ArrayList<Integer> guess) {
        int a = 0;
        int b = 0;
        for(Integer digit: guess) {
            if(answerArr.contains(digit)) {
                if(answerArr.indexOf(digit) == guess.indexOf(digit)) {
                    a++;
                } else {
                    b++;
                }
            }
        }
        return a+"A"+b+"B";
    }
}
