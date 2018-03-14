package com.mayo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mayo.R;
import com.mayo.Utility.Constants;
import com.mayo.classes.ChatBubbleColors;
import com.mayo.interfaces.OnItemClickListener;
import com.mayo.models.Message;

import java.util.ArrayList;

/**
 * Created by Lakshmi on 19/02/18.
 */

public class ThanksChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Message> mChatMessages;
    private OnItemClickListener mListener;

    public ThanksChatAdapter(ArrayList<Message> pChatMessages, Context pContext, OnItemClickListener pOnItemClickListener) {
        this.mChatMessages = pChatMessages;
        this.mContext = pContext;
        this.mListener = pOnItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thanks_dialog_list, parent, false);
        return new SenderList(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SenderList helpMessagesList = (SenderList) holder;
        if (mChatMessages.get(position).getMessageFromLocalDevice().equals(Constants.MessageFromLocalDevice.no)) {
            helpMessagesList.messageText.setText(mChatMessages.get(position).getText());
            setHelpMessageColor(helpMessagesList, position);
        }
    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }


    public class SenderList extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageText;
        View viewColor;
        LinearLayout messageBackground;

        SenderList(View view) {
            super(view);
            view.setOnClickListener(this);
            messageText = (TextView) view.findViewById(R.id.help_messages_list);
            viewColor = view.findViewById(R.id.backgroundChatColor);
            messageBackground = (LinearLayout) view.findViewById(R.id.messageBackground);
        }

        @Override
        public void onClick(View v) {
            boolean checkSelected;
            if (messageBackground.getBackground().getConstantState() == mContext.getResources().
                    getDrawable(R.drawable.background_white).getConstantState()) {
                messageBackground.setBackground(mContext.getResources().getDrawable(R.drawable.background_white_transparent));
                checkSelected = false;
            } else {
                messageBackground.setBackground(mContext.getResources().getDrawable(R.drawable.background_white));
                checkSelected = true;
            }
            mListener.onItemClick(v, getAdapterPosition(), checkSelected);
        }
    }

    private void setHelpMessageColor(SenderList pSenderView, int position) {
        GradientDrawable drawable = (GradientDrawable) pSenderView.viewColor.getBackground();
        drawable.setColor(Color.parseColor(ChatBubbleColors.colors[Integer.parseInt(mChatMessages.get(position).getColorIndex())]));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            pSenderView.viewColor.setBackground(drawable);
        } else {
            pSenderView.viewColor.setBackgroundDrawable(drawable);
        }
    }
}
