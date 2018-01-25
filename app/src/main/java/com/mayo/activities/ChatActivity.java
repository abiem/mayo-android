package com.mayo.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.adapters.ChatListAdapter;
import com.mayo.models.Message;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressLint("Registered")
@EActivity(R.layout.activity_chat)
public class ChatActivity extends AppCompatActivity {

    FrameLayout mBackChatButton;

    @ViewById(R.id.messageChat)
    EditText mMessageChatText;

    @ViewById(R.id.messageSend)
    Button mMessageSend;

    @ViewById(R.id.chat_list_view)
    RecyclerView mChatRecyclerView;

    String mMessageText;
    Message mMessage;
    ChatListAdapter mChatAdapter;
    LinearLayoutManager mLayoutManager;


    private ArrayList<Message> mMessageList = new ArrayList<>();

    @AfterViews
    protected void init() {
        setActionBar();
        setRecyclerView();
    }

    private void setRecyclerView() {
        mChatAdapter = new ChatListAdapter(mMessageList, this);
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

    private void setActionBar() {
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
                mBackChatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
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
            sendMessage(mMessageText, Constants.UserType.OTHER);
            mMessageChatText.setText("");
            execSchedular();
        }
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
        mChatAdapter.notifyDataSetChanged();
    }

    private void execSchedular() {
        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

        exec.schedule(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.setText("\uD83D\uDE31" + getResources().getString(R.string.wooho)); // 10 spaces;
                message.setMessageFromLocalDevice(false);
                message.setDateCreated(CommonUtility.timeFormat(new Date().getTime()));
                message.setUserType(Constants.UserType.SELF);
                mMessageList.add(message);

                ChatActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mChatAdapter.notifyDataSetChanged();
                    }
                });


            }
        }, 1, TimeUnit.SECONDS);

    }

    public static String encode(String s) {
        return StringEscapeUtils.escapeJava(s);
    }

}
