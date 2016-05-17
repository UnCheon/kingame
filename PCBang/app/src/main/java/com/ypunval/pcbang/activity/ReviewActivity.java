package com.ypunval.pcbang.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rey.material.widget.FrameLayout;
import com.ypunval.pcbang.R;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.model.Sync;
import com.ypunval.pcbang.update.JSONToRealm;
import com.ypunval.pcbang.update.PCBangHttpHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class ReviewActivity extends BaseActivity {
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
    String last_updated;
    String nickname;

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

    private static final String TAG = "REVIEWACTIVITY";

    public void onFinishWriteReview() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_review.getWindowToken(), 0);
        finish();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pcBangId = getIntent().getIntExtra("pcBangId", 0);
        Realm realm = Realm.getDefaultInstance();
        PCBang pcBang = realm.where(PCBang.class).equalTo("id", pcBangId).findFirst();
        Sync sync = realm.where(Sync.class).equalTo("id", 1).findFirst();
        last_updated = sync.getUpdated();

        setTitle(pcBang.getName());

        SharedPreferences pref = getSharedPreferences("myInfo", MODE_PRIVATE);
        nickname = pref.getString("nickname", "");
        // TODO: 16. 5. 5. 닉네임 서버 만들어지면 if절 확인
        if (false) {
//        if (nickname == "") {
            final PCBangListenerInterface.OnNicknameListener listener = new PCBangListenerInterface.OnNicknameListener() {
                @Override
                public void onSuccessNickname(String responseString) {
                    JSONToRealm jsonToRealm = new JSONToRealm(ReviewActivity.this);
                    String status = jsonToRealm.nicknameResultToRealm(responseString);
                    if (status.equals("success")) {
                        SharedPreferences pref = ReviewActivity.this.getSharedPreferences("myInfo", ReviewActivity.this.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("nickname", nickname);
                    }
                }

                @Override
                public void onFailureNetwork() {

                }
            };


            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            final EditText et_nickname = new EditText(this);

            dialog.setView(et_nickname);


            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nickname = et_nickname.getText().toString();
                    Log.i(TAG, "onClick: " + nickname);
                    SharedPreferences pref = ReviewActivity.this.getSharedPreferences("myInfo", ReviewActivity.this.MODE_PRIVATE);
                    String phoneNumber = pref.getString("phoneNumber", "");
                    String regId = pref.getString("regId", "");

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("phoneNumber", phoneNumber);
                        jsonObject.put("nickname", nickname);
                        jsonObject.put("regId", regId);

                        PCBangHttpHelper httpHelper = new PCBangHttpHelper();
                        httpHelper.nickname(jsonObject, listener);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ReviewActivity.this.finish();
                }
            });

            dialog.setMessage("사용하실 닉네임을 입력하세요.");
            dialog.setCancelable(false);
            dialog.show();

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(20, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 0, 10, 0);
            et_nickname.setLayoutParams(lp);

        }


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
        SharedPreferences pref = ReviewActivity.this.getSharedPreferences("myInfo", ReviewActivity.this.MODE_PRIVATE);
        String nickname = pref.getString("nickname", "");
        String phoneNumber = pref.getString("phoneNumber", "");
        String regId = pref.getString("regId", "");

        String content = et_review.getText().toString();
        float rate = 2 * (ratingBar.getRating());
        Realm realm = Realm.getDefaultInstance();
        Sync sync = realm.where(Sync.class).equalTo("id", 1).findFirst();
        String last_updated = sync.getUpdated();

//        if (nickname != "" && phoneNumber != "" && content != "" && pcBangId != 0){
        // TODO: 16. 5. 5. 리뷰작성시 if절 확인 (닉네임 저장된후)
        if (true) {

            PCBangListenerInterface.OnUpdateListener listener = new PCBangListenerInterface.OnUpdateListener() {
                @Override
                public void onSuccessUpdate(String responseString) {
                    ReviewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ReviewActivity.this.dismissReviewDialog();
                        }
                    });
                    Log.i(TAG, "onSuccessUpdate");
                    JSONToRealm jsonToRealm = new JSONToRealm(ReviewActivity.this);
                    String status = jsonToRealm.updateResultToRealm(responseString);
                    if (status.equals("success")){
                        ReviewActivity.this.finish();

                    }else if (status.equals("empty")){

                    }else if (status.equals("fail")){

                    }
                }

                @Override
                public void onFailureNetwork() {
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




            JSONObject jsonObject = new JSONObject();
            JSONObject jsonReview = new JSONObject();
            try {
                // TODO: 16. 5. 5. 닉네임값 넣어주기
                jsonReview.put("nickname", "유소니");
                jsonReview.put("phoneNumber", phoneNumber);
                jsonReview.put("pcbang", regId);
                jsonReview.put("content", content);
                jsonReview.put("pcbang", pcBangId);
                jsonReview.put("rate", rate);

                jsonObject.put("last_updated", last_updated);
                jsonObject.put("review", jsonReview);
                ReviewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ReviewActivity.this.showReviewDialog();
                    }
                });

                PCBangHttpHelper httpHelper = new PCBangHttpHelper();
                httpHelper.update_or_writeReview(jsonObject, listener);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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