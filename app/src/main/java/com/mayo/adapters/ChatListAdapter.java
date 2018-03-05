package com.mayo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.models.Message;

import java.util.ArrayList;


public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Message> mMessages;
    private Context mContext;
    private Bundle mBundle;

    public ChatListAdapter(ArrayList<Message> pChatMessages, Context pContext, Bundle pBundle) {
        this.mMessages = pChatMessages;
        this.mContext = pContext;
        this.mBundle = pBundle;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Constants.UserType whichView = Constants.UserType.values()[viewType];
        switch (whichView) {
            case SELF: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_receiver, parent, false);
                return new Receiver(view);
            }
            case OTHER: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_sender, parent, false);
                view.findViewById(R.id.parentBlueLayout).setVisibility(View.VISIBLE);
                if (mBundle != null) {
                    view.findViewById(R.id.parentGreyLayout).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.parentBlueLayout).setVisibility(View.GONE);
                }
                return new Sender(view);
            }
            default: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_receiver, parent, false);
                return new Receiver(view);
            }
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Constants.UserType whichView = Constants.UserType.values()[holder.getItemViewType()];
        switch (whichView) {
            case SELF: {
                Receiver receiverViewholder = (Receiver) holder;
                receiverViewholder.messageTextViewReceiver.setText(mMessages.get(position).getText());
                break;

            }
            case OTHER: {
                Sender senderViewholder = (Sender) holder;
                senderViewholder.messageTextViewSender.setText(mMessages.get(position).getText());
                if (mBundle != null) {
                    senderViewholder.messageTextViewSenderGreyColor.setText(mMessages.get(position).getText());
                }
                break;

            }
            default: {
                Receiver receiverViewholder = (Receiver) holder;
                receiverViewholder.messageTextViewReceiver.setText(mMessages.get(position).getText());
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class Sender extends RecyclerView.ViewHolder {
        TextView messageTextViewSender, messageTextViewSenderGreyColor;

        Sender(View view) {
            super(view);
            messageTextViewSender = (TextView) view.findViewById(R.id.textview_message_sender);
            messageTextViewSenderGreyColor = (TextView) view.findViewById(R.id.textview_grey_message_sender);
        }
    }

    public static class Receiver extends RecyclerView.ViewHolder {
        TextView messageTextViewReceiver;

        Receiver(View view) {
            super(view);
            messageTextViewReceiver = (TextView) view.findViewById(R.id.textview_message_reciever);
        }
    }


    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        if (message.getMessageFromLocalDevice().name().equals(Constants.sYes)) {
            return message.getUserType().ordinal(); //sender
        }
        return message.getUserType().ordinal();  //receiver
    }
}
