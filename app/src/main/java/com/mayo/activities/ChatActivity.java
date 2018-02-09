package com.mayo.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.adapters.ChatListAdapter;
import com.mayo.application.MayoApplication;
import com.mayo.models.Message;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressLint("Registered")
@EActivity(R.layout.activity_chat)
public class ChatActivity extends AppCompatActivity {

    FrameLayout mBackChatButton;
    TextView mActionBarMessage;

    @ViewById(R.id.messageChat)
    EditText mMessageChatText;

    @ViewById(R.id.messageSend)
    Button mMessageSend;

    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;

    @ViewById(R.id.chat_list_view)
    RecyclerView mChatRecyclerView;

    @App
    MayoApplication mMayoApplication;

    String mMessageText;
    Message mMessage;
    ChatListAdapter mChatAdapter;
    LinearLayoutManager mLayoutManager;

    private ArrayList<Message> mMessageList = new ArrayList<>();
    Bundle pBundle;

    @AfterViews
    protected void init() {
        pBundle = getIntent().getExtras();
        if (pBundle != null) {
            setActionBar(pBundle.getString(Constants.sPostMessage));
        } else {
            setActionBar(Constants.sConstantString);
        }
        setRecyclerView();
    }

    private void setRecyclerView() {
        mChatAdapter = new ChatListAdapter(mMessageList, this, pBundle);
        mLayoutManager = new LinearLayoutManager(this);
        mChatRecyclerView.setLayoutManager(mLayoutManager);
        setDividerDecoration();
        mChatRecyclerView.setAdapter(mChatAdapter);
    }

    private void setDividerDecoration() {
        DividerItemDecoration myDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        myDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.chat_divider));
        mChatRecyclerView.addItemDecoration(myDivider);
    }

    private void setActionBar(String pMessage) {
        if (getSupportActionBar() != null) {
            LayoutInflater inflater = (LayoutInflater) getSupportActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                View view = inflater.inflate(R.layout.actionbar_chatlayout, null);
                ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
                getSupportActionBar().setCustomView(view, params);
                getSupportActionBar().setDisplayShowCustomEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
                mBackChatButton = (FrameLayout) view.findViewById(R.id.parentBackChatButton);
                mActionBarMessage = (TextView) view.findViewById(R.id.actionBarMessage);
                mBackChatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        mMayoApplication.hideKeyboard(getCurrentFocus());
                    }
                });
                mActionBarMessage.setText(pMessage);
                if (pMessage.equals(Constants.sConstantString)) {
                    mActionBarMessage.setText(getResources().getString(R.string.ai_message));
                }
                textAdjustment();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Click(R.id.messageSend)
    protected void sendMessageFromUser() {
        if (!mMessageChatText.getText().toString().trim().isEmpty()) {
            mMessageText = mMessageChatText.getText().toString().trim();
            if (pBundle != null) {
                mMessageText = Constants.sSmileCode + Constants.sConstantSpaceString +
                        mMessageChatText.getText().toString().trim();
            }
            sendMessage(mMessageText, Constants.UserType.OTHER);
            mMessageChatText.setText("");
            if (pBundle == null) {
                execSchedular();
            }
        }
        mChatRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
    }


    @TextChange(R.id.messageChat)
    void onTextChangeOfMessage() {
        mMessageSend.setTextColor(getResources().getColor(R.color.ColorBlackOpacity));
        if (!mMessageChatText.getText().toString().trim().isEmpty()) {
            mMessageSend.setTextColor(getResources().getColor(R.color.colorDarkSkyBlue));
        }
    }

    private void sendMessage(String pMessageText, Constants.UserType userType) {
        mMessage = new Message();
        mMessage.setText(pMessageText);
        mMessage.setDateCreated(CommonUtility.timeFormat(new Date().getTime()));
        mMessage.setSenderId(userType.name());
        mMessage.setMessageFromLocalDevice(true);
        mMessage.setUserType(userType);
        mMessageList.add(mMessage);

    }

    private void execSchedular() {
        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

        exec.schedule(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.setText(Constants.sSmileCode + Constants.sConstantSpaceString + getResources().getString(R.string.wooho)); // 10 spaces;
                message.setMessageFromLocalDevice(false);
                message.setDateCreated(CommonUtility.timeFormat(new Date().getTime()));
                message.setUserType(Constants.UserType.SELF);
                mMessageList.add(message);

                ChatActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mChatAdapter.notifyDataSetChanged();
                        new CountDown(4000, 1000);
                        CommonUtility.progressDialogTransparent(ChatActivity.this);
                        mMessageChatText.setCursorVisible(false);
                        mMayoApplication.hideKeyboard(getCurrentFocus());
                        CommonUtility.setHandsAnimationShownOnMap(true, ChatActivity.this);
                    }
                });


            }
        }, 0, TimeUnit.SECONDS);

    }


    private class CountDown extends CountDownTimer {
        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            start();
        }

        @Override
        public void onFinish() {
            CommonUtility.progressDialogTransparentDismiss();
            mMayoApplication.hideKeyboard(getCurrentFocus());
            finish();
        }

        @Override
        public void onTick(long duration) {
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    void textAdjustment() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mActionBarMessage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mBackChatButton.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mActionBarMessage.getLayoutParams();
        if (mActionBarMessage.getMeasuredWidth() > ((size.x) - mBackChatButton.getMeasuredWidth())) {
            params.addRule(RelativeLayout.END_OF, R.id.parentBackChatButton);
        } else {
            params.removeRule(RelativeLayout.END_OF);
        }
        mActionBarMessage.setLayoutParams(params);
    }

}
