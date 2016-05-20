package com.ypunval.pcbang.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.model.Sync;
import com.ypunval.pcbang.update.JSONToRealm;
import com.ypunval.pcbang.update.PCBangHttpHelper;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ReviewActivity extends BaseRealmActivity {
    @Bind(R.id.et_review)
    EditText et_review;

    @Bind(R.id.tv_register)
    TextView tv_register;

    @Bind(R.id.ratingbar)
    RatingBar ratingBar;

    @Bind(R.id.tv_rating_point)
    TextView tv_rating_point;

    @Bind(R.id.tv_rating_text)
    TextView tv_rating_text;

    int pcBangId;

    @OnClick(R.id.ll_et_review)
    public void edit_review(View v) {
        et_review.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    @OnClick(R.id.tv_register)
    public void write_review(View v) {
        writeReview();
    }

    private static final String TAG = "ReviewActivity";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pcBangId = getIntent().getIntExtra("pcBangId", 0);

        PCBang pcBang = realm.where(PCBang.class).equalTo("id", pcBangId).findFirst();
        setTitle(pcBang.getName());


        et_review.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tv_register.setBackgroundResource(R.drawable.selector_register);
                    tv_register.setEnabled(true);
                } else {
                    tv_register.setBackgroundColor(Color.parseColor("#cccccc"));
                    tv_register.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                int i_rating = Math.round(rating * 2);

                tv_rating_point.setTextColor(Color.parseColor("#333333"));
                tv_rating_point.setText(String.valueOf(i_rating) + "점");

                tv_rating_text.setTextColor(Color.parseColor("#333333"));
                if (i_rating < 3) {
                    tv_rating_text.setText("별로에요. 개선이 시급합니다.");
                } else if (i_rating < 5) {
                    tv_rating_text.setText("조금 아쉽네요.");
                } else if (i_rating < 7) {
                    tv_rating_text.setText("그럭저럭 괜찮아요.");
                } else if (i_rating < 9) {
                    tv_rating_text.setText("기분좋게 이용했어요");
                } else {
                    tv_rating_text.setText("최고의 PC방 입니다.");
                }

            }
        });
    }

    private void writeReview() {
        PCBangListenerInterface.OnPostFinishListener listener = new PCBangListenerInterface.OnPostFinishListener() {
            @Override
            public void onPostSuccess(String responseString) {
                ReviewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ReviewActivity.this.dismissReviewDialog();
                    }
                });

                Log.i(TAG, "onPostSuccess");

                JSONToRealm jsonToRealm = new JSONToRealm(ReviewActivity.this);
                String status = jsonToRealm.updateResultToRealm(responseString);

                if (status.equals("success")) {

                } else if (status.equals("empty")) {

                } else if (status.equals("fail")) {

                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_review.getWindowToken(), 0);

                ReviewActivity.this.setResult(RESULT_OK, getIntent());
                finish();

            }

            @Override
            public void onPostFailure() {
                ReviewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ReviewActivity.this.dismissReviewDialog();
                    }
                });

                Log.i(TAG, "onFailureNetwork");
//            Todo: network fail 처리
            }
        };


        SharedPreferences pref = ReviewActivity.this.getSharedPreferences("myInfo", ReviewActivity.this.MODE_PRIVATE);
        String phoneNumber = pref.getString("phoneNumber", "010");

        String content = et_review.getText().toString();
        float rate = 2 * (ratingBar.getRating());
        Sync sync = realm.where(Sync.class).equalTo("id", 1).findFirst();
        String last_updated = sync.getUpdated();

        // TODO: 2016. 5. 20. last updated 가짜 데이터 진짜로 바꾸기
        RequestBody formBody = new FormBody.Builder()
//                .add("last_updated", "2016-05-11 10:00:00")
                .add("last_updated", sync.getUpdated())
                .add("pcbang", pcBangId+"")
                .add("content", content)
                .add("rate", rate+"")
                .add("phone_number", phoneNumber)

                .build();

        PCBangHttpHelper pcBangHttpHelper = new PCBangHttpHelper();
        pcBangHttpHelper.post(formBody, getResources().getString(R.string.url_review_write), listener);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}