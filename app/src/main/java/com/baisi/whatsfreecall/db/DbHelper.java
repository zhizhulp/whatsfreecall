package com.baisi.whatsfreecall.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.baisi.whatsfreecall.entity.RecoderEntity;


/**
 * Created by MnyZhao on 2017/12/30.
 */

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context, "record.db", null, 1);
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table "+DbConfig.TABLE_NAME+"(_id integer primary key,"
                + DbConfig.PHOTO + " varchar(250),"
                + DbConfig.NAME + " varchar(250),"
                + DbConfig.PHONE_NUMBER + " varchar(250),"
                + DbConfig.COUNTRY_NAME + " varchar(250),"
                + DbConfig.COUNTRY_CODE + " integer,"
                + DbConfig.COUNTRY_FLAG + " integer,"
                + DbConfig.TYPE + " integer,"
                + DbConfig.DATE + " integer"
                + ")";
        db.execSQL(createTable);

        /*insertInto(db,new RecoderEntity("mny","17611232117",1,System.currentTimeMillis(),"https","CHina",86));

        insertInto(db,new RecoderEntity("mny","17611232117",1,System.currentTimeMillis(),"https","CHinas",876));

        insertInto(db,new RecoderEntity("mny","17611232117",1,System.currentTimeMillis(),"https","CHinas",8786));

        insertInto(db,new RecoderEntity("mny","17611232117",1,System.currentTimeMillis(),"https","CHinas",87869));

        insertInto(db,new RecoderEntity("mny","17611232117",1,System.currentTimeMillis(),"https","CHinas",787869));*/
    }

    public void insertInto(SQLiteDatabase sqLiteDatabase,RecoderEntity recoderEntity) {

        try {
            String insertSql = "insert into " + DbConfig.TABLE_NAME + "(" + DbConfig.PHOTO + ","
                    + DbConfig.NAME + ","
                    + DbConfig.PHONE_NUMBER + ","
                    + DbConfig.COUNTRY_NAME + ","
                    + DbConfig.COUNTRY_CODE + ","
                    + DbConfig.TYPE + ","
                    + DbConfig.DATE
                    + ") values(?,?,?,?,?,?,?)";
            sqLiteDatabase.execSQL(insertSql, new Object[]{recoderEntity.getPhotoUri(),
                    recoderEntity.getName(),
                    recoderEntity.getPhoneNumber(),
                    recoderEntity.getCounryName(),
                    recoderEntity.getCountryCode(),
                    recoderEntity.getType(),
                    recoderEntity.getDate()}
            );
        } catch (SQLException e) {

        } finally {

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
