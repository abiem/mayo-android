package com.mayo.classes;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLng;
import com.mayo.R;
import com.mayo.Utility.CommonUtility;
import com.mayo.Utility.Constants;
import com.mayo.models.CardLatlng;
import com.mayo.models.GradientColor;
import com.mayo.models.MapDataModel;

import java.util.ArrayList;

/**
 * Created by Lakshmi on 09/02/18.
 */

public class FakeDataModel {
    Context mContext;
    ArrayList<MapDataModel> mMapDataModels;

    public FakeDataModel(Context pContext) {
        mContext = pContext;
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
            int generaetRandomPostionOfLatlng = ShownCardMarker.generateRandomLocationOfCard();
            cardLatlng.setLatLng(new LatLng(ShownCardMarker.CardMarkerChoices[generaetRandomPostionOfLatlng][0],
                    ShownCardMarker.CardMarkerChoices[generaetRandomPostionOfLatlng][1]));
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
                    mapDataModel.setCardLatlng(cardLatlng);
                    if (CommonUtility.getFakeCardOne(mContext)) {
                        continue;
                    }
                    break;
                case FAKECARDTWO:
                    mapDataModel.setTextMessage(mContext.getResources().getString(R.string.ai_message));
                    mapDataModel.setFakeCardPosition(Constants.CardType.FAKECARDTWO.getValue());
                    mapDataModel.setCardLatlng(cardLatlng);
                    if (CommonUtility.getFakeCardTwo(mContext)) {
                        continue;
                    }
                    break;
                case FAKECARDTHREE:
                    mapDataModel.setTextMessage(mContext.getResources().getString(R.string.need_help));
                    mapDataModel.setFakeCardPosition(Constants.CardType.FAKECARDTHREE.getValue());
                    mapDataModel.setCardLatlng(cardLatlng);
                    if (CommonUtility.getFakeCardThree(mContext)) {
                        continue;
                    }
                    break;
            }

            mMapDataModels.add(mapDataModel);
        }
        return mMapDataModels;
    }


}
