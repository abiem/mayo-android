package com.mayo.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.adapters.ChatListAdapter;
import com.mayo.application.MayoApplication;
import com.mayo.firebase.database.FirebaseDatabase;
import com.mayo.models.MapDataModel;
import com.mayo.models.Message;
import com.mayo.models.Task;
import com.mayo.models.TaskLatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
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

    @ViewById(R.id.parentQuestCompleted)
    TextView mQuestCompletedTextView;

    @ViewById(R.id.parentMessageSend)
    LinearLayout mParentLayoutOfMessageSend;

    @App
    MayoApplication mMayoApplication;

    @Extra(Constants.sCardsData)
    MapDataModel mMapDataModel;

    String mMessageText;
    Message mMessage;
    ChatListAdapter mChatAdapter;
    LinearLayoutManager mLayoutManager;
    FirebaseDatabase mFirebaseDatabase;
    String mColorIndex, mTaskId, mTaskDecription;


    private ArrayList<Message> mMessageList = new ArrayList<>();
    Bundle pBundle;

    @AfterViews
    protected void init() {
        pBundle = getIntent().getExtras();
        mMayoApplication.setActivity(this);
        if (pBundle != null) {
            setActionBar(pBundle.getString(Constants.sPostMessage));
            if (pBundle.getBoolean(Constants.sQuestMessageShow)) {
                mQuestCompletedTextView.setVisibility(View.VISIBLE);
                mParentLayoutOfMessageSend.setVisibility(View.GONE);
            }
            if (pBundle.getString(Constants.Notifications.sChannelId) != null) {
                getFirebaseInstance();
                mFirebaseDatabase.processMessageNotification(pBundle.getString(Constants.Notifications.sChannelId));
            }
        } else {
            setActionBar(Constants.sConstantEmptyString);
        }
        setRecyclerView();
        getFirebaseInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMayoApplication.activityResumed();
    }

    private void setRecyclerView() {
        mChatAdapter = new ChatListAdapter(mMessageList, this, pBundle);
        mLayoutManager = new LinearLayoutManager(this);
        mChatRecyclerView.setLayoutManager(mLayoutManager);
        mChatRecyclerView.setAdapter(mChatAdapter);
    }

    private void setActionBar(String pMessage) {
        mTaskDecription = pMessage;
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
                if (pMessage != null && pMessage.equals(Constants.sConstantEmptyString)) {
                    mActionBarMessage.setText(getResources().getString(R.string.ai_message));
                }
                textAdjustment();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mFirebaseDatabase != null) {
            mFirebaseDatabase.removeMessageListener();
        }
        finish();
        super.onBackPressed();
    }

    @Click(R.id.messageSend)
    protected void sendMessageFromUser() {
        boolean isRecentActivity = true;
        if (!mMessageChatText.getText().toString().trim().isEmpty()) {
            // If we post a message in our created task then we need to add smily to it else post a message
            if (pBundle != null) {
                if (mMapDataModel != null && mMapDataModel.getTaskLatLng() != null) {
                    Task task = mMapDataModel.getTaskLatLng().getTask();
                    Task taskData = CommonUtility.getTaskData(this);
                    if (CommonUtility.getTaskApplied(this) && taskData.getTaskID().equals(task.getTaskID())) {
                        mColorIndex = "0";
                        mMessageText = Constants.sSmileCode + Constants.sConstantSpaceString +
                                mMessageChatText.getText().toString().trim();
                        isRecentActivity = false;
                    } else {
                        mMessageText = mMessageChatText.getText().toString().trim();
                    }
                    if (task != null) {
                        mFirebaseDatabase.setMessage(this, CommonUtility.getUserId(this),
                                mMessageText, task.getTaskID(),mTaskDecription);
                    }
                    if (task != null) {
                        if (isRecentActivity) {
                            task.setRecentActivity(true);
                        }
                        mFirebaseDatabase.updateTask(task.getTaskID(), task);
                        mFirebaseDatabase.writeTaskParticipated(CommonUtility.getUserId(this), task.getTaskID());
                    }
                }
                if (pBundle.getString(Constants.Notifications.sChannelId) != null) {
                    if (mTaskId != null) {
                        Task taskData = CommonUtility.getTaskData(this);
                        if (CommonUtility.getTaskApplied(this) && taskData != null && taskData.getTaskID().equals(mTaskId)) {
                            mColorIndex = "0";
                            mMessageText = Constants.sSmileCode + Constants.sConstantSpaceString +
                                    mMessageChatText.getText().toString().trim();
                        } else {
                            mMessageText = mMessageChatText.getText().toString().trim();
                        }
                        mFirebaseDatabase.setMessage(this, CommonUtility.getUserId(this),
                                mMessageText, mTaskId, mTaskDecription);
                    }
                }
            } else {
                mColorIndex = "0";
                mMessageText = mMessageChatText.getText().toString().trim();
                sendMessagesToUser(mColorIndex);
            }

            mMessageChatText.setText("");
            if (pBundle == null) {
                execSchedular();
            }
        }
        mChatRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
    }

    public void sendMessagesToUser(String pColorIndex) {
        sendMessage(mMessageText, Constants.UserType.OTHER, pColorIndex);
    }


    @TextChange(R.id.messageChat)
    void onTextChangeOfMessage() {
        mMessageSend.setTextColor(getResources().getColor(R.color.ColorBlackOpacity));
        if (!mMessageChatText.getText().toString().trim().isEmpty()) {
            mMessageSend.setTextColor(getResources().getColor(R.color.colorDarkSkyBlue));
        }
    }

    private void sendMessage(String pMessageText, Constants.UserType userType, String pColorIndex) {
        mMessage = new Message();
        mMessage.setText(pMessageText);
        mMessage.setDateCreated(CommonUtility.timeFormat(new Date().getTime()));
        mMessage.setSenderId(userType.name());
        mMessage.setMessageFromLocalDevice(Constants.MessageFromLocalDevice.yes);
        mMessage.setUserType(userType);
        mMessage.setColorIndex(pColorIndex);
        mMessageList.add(mMessage);

    }

    public void taskExpireAlert() {
        if (!isFinishing()) {
            final Dialog dialog = CommonUtility.showCustomDialog(this, R.layout.quest_completed);
            if (dialog != null) {
                dialog.findViewById(R.id.questCompleted).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        }
    }

    private void execSchedular() {
        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.schedule(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.setText(Constants.sSmileCode + Constants.sConstantSpaceString + getResources().getString(R.string.wooho)); // 10 spaces;
                message.setMessageFromLocalDevice(Constants.MessageFromLocalDevice.no);
                message.setDateCreated(CommonUtility.timeFormat(new Date().getTime()));
                message.setColorIndex("0");
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

    private void getFirebaseInstance() {
        if (mFirebaseDatabase == null) {
            mFirebaseDatabase = new FirebaseDatabase(this);
            mFirebaseDatabase.currentUserColorIndex = -1;
            if (mMapDataModel != null && mMapDataModel.getTaskLatLng() != null) {
                mFirebaseDatabase.getMessagesFromFirebase(mMapDataModel.getTaskLatLng().getTask().getTaskID(), mChatAdapter, mMessageList, mChatRecyclerView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMayoApplication.activityDestroyed();
    }

    public void setData(String pTaskDescription, boolean pIsCompleted, String pTaskId) {
        setActionBar(pTaskDescription);
        mTaskId = pTaskId;
        if (pIsCompleted) {
            mQuestCompletedTextView.setVisibility(View.VISIBLE);
            mParentLayoutOfMessageSend.setVisibility(View.GONE);
        } else {
            mQuestCompletedTextView.setVisibility(View.GONE);
            mParentLayoutOfMessageSend.setVisibility(View.VISIBLE);
        }
        if (mFirebaseDatabase != null)
            mFirebaseDatabase.getMessagesFromFirebase(pTaskId, mChatAdapter, mMessageList, mChatRecyclerView);
        if (pTaskId != null) {
            processMessageNotification(pTaskId);
        }
    }

    private void processMessageNotification(String pChannelId) {
        Intent i = new Intent("android.intent.action.MAIN");
        i.putExtra(Constants.Notifications.sChannelId, pChannelId);
        sendBroadcast(i);
    }
}
