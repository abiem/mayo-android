package com.mayo.classes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.activities.MapActivity;
import com.mayo.adapters.MapViewPagerAdapter;
import com.mayo.models.CardLatlng;
import com.mayo.models.GradientColor;
import com.mayo.models.MapDataModel;
import com.mayo.models.MarkerTag;
import com.mayo.models.Task;
import com.mayo.models.TaskLatLng;
import com.mayo.models.TaskLocations;

import java.util.ArrayList;
import java.util.Calendar;
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

    private ArrayList<MapDataModel> sortArray(ArrayList<MapDataModel> pArray) {
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
        return pArray;
    }

    private ArrayList<MapDataModel> mergeArray(ArrayList<MapDataModel> pOriginalArray, ArrayList<MapDataModel> pTempArray) {
        int secondArray = pTempArray.size();
        int tempValue = 0;
        while (tempValue < secondArray) {
            pOriginalArray.add(pTempArray.get(tempValue));
            tempValue++;
        }
        pTempArray.clear();
        return pOriginalArray;
    }

    public void sortExpiredCardViewList(ArrayList<MapDataModel> pArray) {
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
        if (mMapViewPagerAdapter != null) {
            mMapViewPagerAdapter.notifyDataSetChanged();
        }
    }

    public void sortNonExpiredCardViewList(ArrayList<MapDataModel> pArray) {
        Collections.sort(pArray, new Comparator<MapDataModel>() {
            public int compare(MapDataModel o1, MapDataModel o2) {
                if (o1.getTimeCreated() != null && o2.getTimeCreated() != null && !o1.isCompleted() && !o2.isCompleted()) {
                    Date date1 = CommonUtility.convertStringToDateTime(o1.getTimeCreated());
                    Date date2 = CommonUtility.convertStringToDateTime(o2.getTimeCreated());
                    if (date1 != null) {
                        return (-1) * date1.compareTo(date2);
                    }
                }
                return 0;
            }
        });
        if (mMapViewPagerAdapter != null) {
            mMapViewPagerAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<MapDataModel> sortExpiryCard(ArrayList<MapDataModel> pArray) {
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
        return pArray;
    }


    public ArrayList<MapDataModel> sortNonExpiryCard(ArrayList<MapDataModel> pArray) {
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
        return pArray;
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
        mapDataModel.setTaskLatLng(pTaskLatlng);
        Drawable drawable;
        Date saveUpdatedate;
        Calendar calendar = Calendar.getInstance();
        Calendar calendarNewInstance = Calendar.getInstance();
        saveUpdatedate = CommonUtility.convertStringToDateTime(pTaskLatlng.getTask().getTimeUpdated());
        if (saveUpdatedate != null) {
            calendar.setTime(saveUpdatedate);
            calendar.add(Calendar.HOUR, 1);
        }
        if (pTaskLatlng.getTask().isCompleted() || calendar.getTime().before(calendarNewInstance.getTime())) {
            gradientColor.setStartColor(CardColor.expireCard[0]);
            gradientColor.setEndColor(CardColor.expireCard[1]);
            drawable = CommonUtility.getGradientDrawable("#" + CardColor.expireCard[0],
                    "#" + CardColor.expireCard[1], mContext);
            mapDataModel.setBackgroundView(drawable);
            mapDataModel.getTaskLatLng().getTask().setCompleted(true);
            mapDataModel.setCompleted(true);
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
        mapDataModel.setTaskLatLng(pTaskLatlng);
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

    public void removeCardListFromView() {
        int count = 0;
        if (!CommonUtility.getFakeCardOne(mContext)) {
            count++;
        }
        if (!CommonUtility.getFakeCardTwo(mContext)) {
            count++;
        }
        if (!CommonUtility.getFakeCardThree(mContext)) {
            count++;
        }
        for (int i = 0; i < mMapDataModels.size(); i++) {
            if (mMapDataModels.get(i).getFakeCardPosition() == Constants.CardType.DEFAULT.getValue()) {
                CardLatlng cardLatlng = mMapDataModels.get(i).getCardLatlng();
                if (cardLatlng.getMarker() != null) {
                    cardLatlng.getMarker().remove();
                }
            }
        }
        mMapDataModels.subList(count + 1, mMapDataModels.size()).clear();
        if (mMapViewPagerAdapter != null) {
            mMapViewPagerAdapter.notifyDataSetChanged();
        }
    }

    public void setListOnUpdationOfTask(Task pTask, ArrayList<MapDataModel> pMapDataModel, TaskLocations pTaskLocations) {
        MapDataModel mapDataModel = null;
        for (int i = 0; i < mTasksArray.size(); i++) {
            if (mTasksArray.get(i).getTask().getTaskID().equals(pTask.getTaskID())) {
                if (pTask.isCompleted()) {
                    mTasksArray.get(i).setTask(pTask);
                    mapDataModel = getMapModelData(mTasksArray.get(i));
                }
                break;
            }
        }
        if (mapDataModel != null) {
            for (int i = 0; i < pMapDataModel.size(); i++) {
                if (pMapDataModel.get(i).getTimeCreated() != null && pMapDataModel.get(i).getTaskLatLng().getTask().getTaskID().
                        equals(mapDataModel.getTaskLatLng().getTask().getTaskID())) {
                    if (pMapDataModel.get(i).getCardLatlng() != null && pMapDataModel.get(i).getCardLatlng().getMarker() != null) {
                        pMapDataModel.get(i).getCardLatlng().getMarker().remove();
                    }
                    pMapDataModel.remove(i);
                    break;
                }
            }
            pMapDataModel.add(mapDataModel);
            sortTaskLists(pMapDataModel);
        } else {
            if (pTask.isCompleted() && pTask.isUserMovedOutside()) {
                boolean isAdded = true;
                for (int i = 0; i < pMapDataModel.size(); i++) {
                    if (pMapDataModel.get(i).getTaskLatLng() != null && pMapDataModel.get(i).getTaskLatLng().getTask().getTaskID().
                            equals(pTask.getTaskID())) {
                        isAdded = false;
                        break;
                    }
                }
                if (isAdded) {
                    LatLng latLng = new LatLng(pTaskLocations.getLatitude(), pTaskLocations.getLongitude());
                    TaskLatLng taskLatLng = setTaskLatlngModel(pTask, latLng);
                    mapDataModel = getMapModelData(taskLatLng);
                    pMapDataModel.add(mapDataModel);
                    sortTaskLists(pMapDataModel);
                }
            }
        }
    }

    public void sortTaskLists(ArrayList<MapDataModel> pMapDataModel) {
        ArrayList<MapDataModel> pNonExpiryCard = new ArrayList<>();
        for (MapDataModel mapDataModelNew : pMapDataModel) {
            if (mapDataModelNew.getFakeCardPosition() == Constants.CardType.DEFAULT.getValue() && !mapDataModelNew.isCompleted()) {
                pNonExpiryCard.add(mapDataModelNew);
            }
        }
        pNonExpiryCard = sortNonExpiryCard(pNonExpiryCard);
        int counter = 4;
        for (int i = 0; i < pNonExpiryCard.size(); i++) {
            MarkerTag markerTag = new MarkerTag();
            markerTag.setId(String.valueOf(Constants.CardType.DEFAULT.getValue()));
            markerTag.setIdNew(String.valueOf(counter));
            Marker marker = pNonExpiryCard.get(i).getCardLatlng().getMarker();
            marker.setTag(markerTag);
            pNonExpiryCard.get(i).getCardLatlng().setMarker(marker);
            counter++;
        }
        ArrayList<MapDataModel> pExpiryCard = new ArrayList<>();
        for (MapDataModel mapDataModelNew : pMapDataModel) {
            if (mapDataModelNew.getFakeCardPosition() == Constants.CardType.DEFAULT.getValue() && mapDataModelNew.isCompleted()) {
                pExpiryCard.add(mapDataModelNew);
            }
        }
        pExpiryCard = sortExpiryCard(pExpiryCard);
        pMapDataModel = getDataModel();
        pNonExpiryCard = mergeArray(pNonExpiryCard, pExpiryCard);
        mergeLiveAndExpireCard(pMapDataModel, pNonExpiryCard);
    }

    private void mergeLiveAndExpireCard(ArrayList<MapDataModel> pOriginalArray, ArrayList<MapDataModel> pTempArray) {
        int secondArray = pTempArray.size();
        int tempValue = 0;
        while (tempValue < secondArray) {
            pOriginalArray.add(pTempArray.get(tempValue));
            tempValue++;
        }
        pTempArray.clear();
    }
}
