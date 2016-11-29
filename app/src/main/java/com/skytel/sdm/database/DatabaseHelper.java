package com.skytel.sdm.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.skytel.sdm.entities.CardType;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "sdm.db";
    // any time you make changes to your database objects, you may have to
    // increase the database version
    private static final int DATABASE_VERSION = 1;

    // DAO objects
    private Dao<CardType, Integer> cardTypeDAO = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should
     * call createTable statements here to create the tables that will store
     * your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, CardType.class);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher
     * version number. This allows you to adjust the various data to match the
     * new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, CardType.class, true);

            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It
     * will create it or just give the cached value.
     */

    public Dao<CardType, Integer> getCardTypeDao() throws SQLException {
        if (cardTypeDAO == null) {
            cardTypeDAO = getDao(CardType.class);
        }
        return cardTypeDAO;
    }

    /**
     * Reset CardType Table
     * @return
     */
    public boolean resetCardTypes() {
        try {
            TableUtils.dropTable(connectionSource, CardType.class, true);
            TableUtils.createTable(connectionSource, CardType.class);
            return true;
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop database", e);
            return false;
        }
    }


    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        cardTypeDAO = null;
    }
}
