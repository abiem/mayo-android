package com.mayo.classes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.activities.MapActivity;
import com.mayo.adapters.MapViewPagerAdapter;
import com.mayo.models.CardLatlng;
import com.mayo.models.GradientColor;
import com.mayo.models.MapDataModel;
import com.mayo.models.Task;
import com.mayo.models.TaskLatLng;
import com.mayo.models.TaskLocations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Lakshmi on 09/02/18.
 */

public class CardsDataModel {
    private Context mContext;
    private ArrayList<MapDataModel> mMapDataModels;
    private ArrayList<TaskLatLng> mTasksArray;
    private MapViewPagerAdapter mMapViewPagerAdapter;
    private ArrayList<MapDataModel> mLocalExpiredCardArrayModel;

    public CardsDataModel(Context pContext, ArrayList<TaskLatLng> pTaskArray) {
        mContext = pContext;
        mTasksArray = pTaskArray;
    }

    public void setViewPagerAdapter(MapViewPagerAdapter pMapViewPagerAdapter) {
        mMapViewPagerAdapter = pMapViewPagerAdapter;
    }

    public ArrayList<MapDataModel> getDataModel() {
        mMapDataModels = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            MapDataModel mapDataModel = new MapDataModel();
            int color = CardColor.generateRandomColor();
            GradientColor gradientColor = new GradientColor();
            gradientColor.setStartColor(CardColor.choices[color][1]);
            gradientColor.setEndColor(CardColor.choices[color][0]);
            mapDataModel.setGradientColor(gradientColor);
            mapDataModel.setFakeCard(false);
            if (i > 0 & i < 4) {
                mapDataModel.setFakeCard(true);
            }
            Drawable drawable = CommonUtility.getGradientDrawable(CardColor.choices[color][0], CardColor.choices[color][1], mContext);
            mapDataModel.setBackgroundView(drawable);
            CardLatlng cardLatlng = new CardLatlng();
            Constants.CardType cardType = Constants.CardType.values()[i];
            switch (cardType) {
                case POST:
                    mapDataModel.setTextMessage(mContext.getResources().getString(R.string.help_message));
                    mapDataModel.setButtonMessage(mContext.getResources().getString(R.string.post));
                    mapDataModel.setFakeCardPosition(Constants.CardType.POST.getValue());
                    break;
                case FAKECARDONE:
                    mapDataModel.setTextMessage(mContext.getResources().getString(R.string.helping_message));
                    mapDataModel.setFakeCardPosition(Constants.CardType.FAKECARDONE.getValue());
                    cardLatlng.setLatLng(new LatLng(Constants.CardMarkerValues.sCardMarkerPositionMininum,
                            Constants.CardMarkerValues.sCardMarkerPositionConstant));
                    mapDataModel.setCardLatlng(cardLatlng);
                    mapDataModel.setCompleted(false);
                    if (CommonUtility.getFakeCardOne(mContext)) {
                        continue;
                    }
                    break;
                case FAKECARDTWO:
                    mapDataModel.setTextMessage(mContext.getResources().getString(R.string.ai_message));
                    mapDataModel.setFakeCardPosition(Constants.CardType.FAKECARDTWO.getValue());
                    cardLatlng.setLatLng(new LatLng(Constants.CardMarkerValues.sCardMarkerPositionConstantNew,
                            Constants.CardMarkerValues.sCardMarkerPositionMaximumNew));
                    mapDataModel.setCardLatlng(cardLatlng);
                    mapDataModel.setCompleted(false);
                    if (CommonUtility.getFakeCardTwo(mContext)) {
                        continue;
                    }
                    break;
                case FAKECARDTHREE:
                    mapDataModel.setTextMessage(mContext.getResources().getString(R.string.need_help));
                    mapDataModel.setFakeCardPosition(Constants.CardType.FAKECARDTHREE.getValue());
                    cardLatlng.setLatLng(new LatLng(Constants.CardMarkerValues.sCardMarkerPositionMininumNew,
                            Constants.CardMarkerValues.sCardMarkerPositionConstantNew));
                    mapDataModel.setCardLatlng(cardLatlng);
                    mapDataModel.setCompleted(false);
                    if (CommonUtility.getFakeCardThree(mContext)) {
                        continue;
                    }
                    break;
            }
            mMapDataModels.add(mapDataModel);
        }
        return mMapDataModels;
    }

    public void waitingTimeToFetchTaskArrayList(long pMillisInFuture, long pCountDownInterval) {
        mLocalExpiredCardArrayModel = new ArrayList<>();
        new WaitForFetchingNearByTask(pMillisInFuture, pCountDownInterval);
    }

    public class WaitForFetchingNearByTask extends CountDownTimer {
        WaitForFetchingNearByTask(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            start();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            for (int count = 0; count < mTasksArray.size(); count++) {
                setMapModelData(mTasksArray.get(count));
            }
            sortArray(mMapDataModels);
            sortArray(mLocalExpiredCardArrayModel);
            mergeArray(mMapDataModels, mLocalExpiredCardArrayModel);
            ((MapActivity) mContext).setViewPagerData();
        }
    }

    private void sortArray(ArrayList<MapDataModel> pArray) {
        Collections.sort(pArray, new Comparator<MapDataModel>() {
            public int compare(MapDataModel o1, MapDataModel o2) {
                if (o1.getTimeCreated() != null && o2.getTimeCreated() != null) {
                    Date date1 = CommonUtility.convertStringToDateTime(o1.getTimeCreated());
                    Date date2 = CommonUtility.convertStringToDateTime(o2.getTimeCreated());
                    if (date1 != null) {
                        return (-1) * date1.compareTo(date2);
                    }
                }
                return 0;
            }
        });
    }

    private void mergeArray(ArrayList<MapDataModel> pOriginalArray, ArrayList<MapDataModel> pTempArray) {
        int secondArray = pTempArray.size();
        int tempValue = 0;
        while (tempValue < secondArray) {
            pOriginalArray.add(pTempArray.get(tempValue));
            tempValue++;
        }
        pTempArray.clear();
    }

    public void sortCardViewList(ArrayList<MapDataModel> pArray) {
        Collections.sort(pArray, new Comparator<MapDataModel>() {
            public int compare(MapDataModel o1, MapDataModel o2) {
                if (o1.getTimeCreated() != null && o2.getTimeCreated() != null && o1.isCompleted() && o2.isCompleted()) {
                    Date date1 = CommonUtility.convertStringToDateTime(o1.getTimeCreated());
                    Date date2 = CommonUtility.convertStringToDateTime(o2.getTimeCreated());
                    if (date1 != null) {
                        return (-1) * date1.compareTo(date2);
                    }
                }
                return 0;
            }
        });
        mMapViewPagerAdapter.notifyDataSetChanged();
    }

    private void setMapModelData(TaskLatLng pTaskLatlng) {
        MapDataModel mapDataModel = new MapDataModel();
        GradientColor gradientColor = new GradientColor();
        mapDataModel.setGradientColor(gradientColor);
        mapDataModel.setFakeCard(false);
        mapDataModel.setTextMessage(pTaskLatlng.getTask().getTaskDescription());
        mapDataModel.setFakeCardPosition(Constants.CardType.DEFAULT.getValue());
        mapDataModel.setCompleted(pTaskLatlng.getTask().isCompleted());
        mapDataModel.setTimeCreated(pTaskLatlng.getTask().getTimeCreated());
        CardLatlng cardLatlng = new CardLatlng();
        cardLatlng.setLatLng(new LatLng(pTaskLatlng.getTaskLocations().getLatitude(),
                pTaskLatlng.getTaskLocations().getLongitude()));
        mapDataModel.setCardLatlng(cardLatlng);
        Drawable drawable;
        if (pTaskLatlng.getTask().isCompleted()) {
            gradientColor.setStartColor(CardColor.expireCard[0]);
            gradientColor.setEndColor(CardColor.expireCard[1]);
            drawable = CommonUtility.getGradientDrawable("#" + CardColor.expireCard[0],
                    "#" + CardColor.expireCard[1], mContext);
            mapDataModel.setBackgroundView(drawable);
            mLocalExpiredCardArrayModel.add(mapDataModel);
        } else {
            gradientColor.setStartColor(pTaskLatlng.getTask().getStartColor());
            gradientColor.setEndColor(pTaskLatlng.getTask().getEndColor());
            drawable = CommonUtility.getGradientDrawable("#" + pTaskLatlng.getTask().getEndColor(),
                    "#" + pTaskLatlng.getTask().getStartColor(), mContext);
            mapDataModel.setBackgroundView(drawable);
            mMapDataModels.add(mapDataModel);
        }
    }

    public MapDataModel getMapModelData(TaskLatLng pTaskLatlng) {
        MapDataModel mapDataModel = new MapDataModel();
        GradientColor gradientColor = new GradientColor();
        mapDataModel.setGradientColor(gradientColor);
        mapDataModel.setFakeCard(false);
        mapDataModel.setTextMessage(pTaskLatlng.getTask().getTaskDescription());
        mapDataModel.setFakeCardPosition(Constants.CardType.DEFAULT.getValue());
        mapDataModel.setCompleted(pTaskLatlng.getTask().isCompleted());
        mapDataModel.setTimeCreated(pTaskLatlng.getTask().getTimeCreated());
        CardLatlng cardLatlng = new CardLatlng();
        cardLatlng.setLatLng(new LatLng(pTaskLatlng.getTaskLocations().getLatitude(),
                pTaskLatlng.getTaskLocations().getLongitude()));
        mapDataModel.setCardLatlng(cardLatlng);
        Drawable drawable;
        if (pTaskLatlng.getTask().isCompleted()) {
            gradientColor.setStartColor(CardColor.expireCard[0]);
            gradientColor.setEndColor(CardColor.expireCard[1]);
            drawable = CommonUtility.getGradientDrawable("#" + CardColor.expireCard[0],
                    "#" + CardColor.expireCard[1], mContext);
            mapDataModel.setBackgroundView(drawable);
        } else {
            gradientColor.setStartColor(pTaskLatlng.getTask().getStartColor());
            gradientColor.setEndColor(pTaskLatlng.getTask().getEndColor());
            drawable = CommonUtility.getGradientDrawable("#" + pTaskLatlng.getTask().getEndColor(),
                    "#" + pTaskLatlng.getTask().getStartColor(), mContext);
            mapDataModel.setBackgroundView(drawable);
        }
        return mapDataModel;
    }

    public TaskLatLng setTaskLatlngModel(Task pTask, LatLng pLatlng) {
        TaskLatLng taskLatLng = new TaskLatLng();
        taskLatLng.setTask(pTask);
        TaskLocations taskLocations = new TaskLocations();
        taskLocations.setKey(Constants.sConstantEmptyString);
        taskLocations.setLatitude(pLatlng.latitude);
        taskLocations.setLongitude(pLatlng.longitude);
        taskLatLng.setTaskLocations(taskLocations);
        return taskLatLng;
    }
}
