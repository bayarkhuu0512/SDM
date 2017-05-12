package com.skytel.sdm.database;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.skytel.sdm.entities.CardType;
import com.skytel.sdm.enums.PackageTypeEnum;
import com.skytel.sdm.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class DataManager implements Constants {
    String TAG = DataManager.class.getName();

    private Context context;
    private DatabaseHelper databaseHelper = null;
    private Dao<CardType, Integer> cardTypeDAO;

    public DataManager(Context context) {
        this.context = context;
        // getHelper();
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class);
        }
        return databaseHelper;
    }

    // Get All CardType by PackageType
    public List<CardType> getCardTypeByPackageType(PackageTypeEnum packageTypeEnum) {
        try {
            cardTypeDAO = getHelper().getCardTypeDao();
            QueryBuilder<CardType, Integer> cardTypeQb = cardTypeDAO
                    .queryBuilder();
            Where where = cardTypeQb.where();
            where.eq("packageTypeEnum", packageTypeEnum);
            return cardTypeQb.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get All CardTypes
    public List<CardType> getCardTypes() {
        try {
            cardTypeDAO = getHelper().getCardTypeDao();
            QueryBuilder<CardType, Integer> cardTypeQb = cardTypeDAO
                    .queryBuilder();
            return cardTypeQb.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Get Selected Card Type
    public CardType getCardType(int id) {
        try {
            cardTypeDAO = getHelper().getCardTypeDao();
            QueryBuilder<CardType, Integer> messageQb = cardTypeDAO
                    .queryBuilder();
            Where where = messageQb.where();
            where.eq("id", id);
            return messageQb.query().get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Set All CardTypes
    public void setCardTypes() {
        try {
            String mCardTypes = "card_type.json";
            String mCardTypesJson = null;
            InputStream is = context.getAssets().open(mCardTypes);
            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            mCardTypesJson = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(mCardTypesJson);
            JSONArray mCardTypesArray = obj.getJSONArray("card_type");
            for (int i = 0; i < mCardTypesArray.length(); i++) {
                JSONObject jO = mCardTypesArray.getJSONObject(i);

                final CardType cardType = new CardType();
                cardType.setId(jO.getInt("id"));
                cardType.setName(jO.getString("name"));
                cardType.setDesciption(jO.getString("description"));

                switch (jO.getInt("package_type")) {
                    case CONST_SKYTEL_CARD_PACKAGE:
                        cardType.setPackageTypeEnum(PackageTypeEnum.SKYTEL_CARD_PACKAGE);
                        break;
                    case CONST_SKYTEL_DATA_PACKAGE:
                        cardType.setPackageTypeEnum(PackageTypeEnum.SKYTEL_DATA_PACKAGE);
                        break;
                    case CONST_SKYMEDIA_IP76_PACKAGE:
                        cardType.setPackageTypeEnum(PackageTypeEnum.SKYMEDIA_IP76_PACKAGE);
                        break;
                    case CONST_SMART_PACKAGE:
                        cardType.setPackageTypeEnum(PackageTypeEnum.SMART_PACKAGE);
                        break;
                }
                try {
                    cardTypeDAO = getHelper().getCardTypeDao();
                    cardTypeDAO.create(cardType);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean resetCardTypes() {
        return getHelper().resetCardTypes();
    }
}
