package com.mayo.interfaces;

import android.view.View;

import com.mayo.models.Message;

/**
 * Created by Lakshmi on 14/03/18.
 */

public interface OnItemClickListener {
    void onItemClick(View view,int position,boolean isSelected);
}
