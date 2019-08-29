package com.baisi.whatsfreecall.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.baisi.whatsfreecall.entity.RecoderEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MnyZhao on 2017/12/30.
 */

public class DbManager {
    public Context context;
    public DbHelper dbHelper;
    public SQLiteDatabase sqLiteDatabase;

    public DbManager(Context context) {
        this.context = context;
        dbHelper = new DbHelper(context);
    }

    private void checkSqlDateBase() {
        sqLiteDatabase = null;
        sqLiteDatabase = dbHelper.getWritableDatabase();

    }

    private void closeSqlDateBase() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
    }

    /**
     * insert call recoder
     *
     * @param recoderEntity
     */
    public void insertInto(RecoderEntity recoderEntity) {
        checkSqlDateBase();
        try {
            String insertSql = "insert into " + DbConfig.TABLE_NAME + "(" + DbConfig.PHOTO + ","
                    + DbConfig.NAME + ","
                    + DbConfig.PHONE_NUMBER + ","
                    + DbConfig.COUNTRY_NAME + ","
                    + DbConfig.COUNTRY_CODE + ","
                    + DbConfig.COUNTRY_FLAG + ","
                    + DbConfig.TYPE + ","
                    + DbConfig.DATE
                    + ") values(?,?,?,?,?,?,?,?)";
            sqLiteDatabase.execSQL(insertSql, new Object[]{recoderEntity.getPhotoUri(),
                    recoderEntity.getName(),
                    recoderEntity.getPhoneNumber(),
                    recoderEntity.getCounryName(),
                    recoderEntity.getCountryCode(),
                    recoderEntity.getCountryFlag(),
                    recoderEntity.getType(),
                    recoderEntity.getDate()}
            );
        } catch (SQLException e) {

        } finally {
            closeSqlDateBase();
        }
    }

    /**
     * insert call recoder
     *
     * @param recoderEntity
     * @return insert->ok return true or false
     */
    public boolean insertIntoResult(RecoderEntity recoderEntity) {
        checkSqlDateBase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbConfig.PHOTO, recoderEntity.getPhotoUri());
            contentValues.put(DbConfig.NAME, recoderEntity.getName());
            contentValues.put(DbConfig.PHONE_NUMBER, recoderEntity.getPhoneNumber());
            contentValues.put(DbConfig.COUNTRY_NAME, recoderEntity.getCounryName());
            contentValues.put(DbConfig.COUNTRY_CODE, recoderEntity.getCountryCode());
            contentValues.put(DbConfig.COUNTRY_FLAG, recoderEntity.getCountryFlag());
            contentValues.put(DbConfig.TYPE, recoderEntity.getType());
            contentValues.put(DbConfig.DATE, recoderEntity.getDate());
            long rowId = sqLiteDatabase.insert(DbConfig.TABLE_NAME, null, contentValues);
            if (rowId != -1) {
                return true;
            }
        } catch (Exception e) {
        } finally {
            closeSqlDateBase();
        }
        return false;
    }

    /**
     * delete call recoder
     *
     * @param recoderEntity
     */
    public void deleteCallRecoder(RecoderEntity recoderEntity) {
        checkSqlDateBase();
        String deleteSql = "delete from " + DbConfig.TABLE_NAME + " where " + DbConfig.PHONE_NUMBER + "=" + recoderEntity.getPhoneNumber();
        try {
            sqLiteDatabase.execSQL(deleteSql);
        } catch (SQLException e) {

        } finally {
            closeSqlDateBase();
        }

    }

    /**
     * delete call recoder
     *
     * @param recoderEntity
     * @return delete scuudess retrurn true or false
     */
    public boolean deleteCallRecoderResult(RecoderEntity recoderEntity) {
        checkSqlDateBase();
        try {
            int row = sqLiteDatabase.delete(DbConfig.TABLE_NAME, DbConfig.PHONE_NUMBER + "=? ", new String[]{recoderEntity.getPhoneNumber()});
            return row == 1 ? true : false;
        } catch (Exception e) {
        } finally {
            closeSqlDateBase();
        }
        return false;
    }

    /**
     * 删除数据库
     */
    public void deleteTable() {
        checkSqlDateBase();
        try {
        } catch (Exception e) {
            sqLiteDatabase.execSQL("DROP TABLE " + DbConfig.TABLE_NAME);
        } finally {
            closeSqlDateBase();
        }
    }

    /**
     * update call recoder
     *
     * @param recoderEntity
     * @return update success return true or false
     */
    public boolean updateCallRecoderResult(RecoderEntity recoderEntity) {
        checkSqlDateBase();
        try {
            ContentValues contentValues = new ContentValues();
       contentValues.put(DbConfig.PHOTO,recoderEntity.getPhotoUri());
            contentValues.put(DbConfig.NAME,recoderEntity.getName());
            contentValues.put(DbConfig.PHONE_NUMBER,recoderEntity.getPhoneNumber());
            contentValues.put(DbConfig.COUNTRY_NAME,recoderEntity.getCounryName());
            contentValues.put(DbConfig.COUNTRY_CODE,recoderEntity.getCountryCode());
            contentValues.put(DbConfig.COUNTRY_FLAG,recoderEntity.getCountryFlag());
            contentValues.put(DbConfig.TYPE,recoderEntity.getType());
            contentValues.put(DbConfig.DATE, recoderEntity.getDate());
            int row = sqLiteDatabase.update(DbConfig.TABLE_NAME, contentValues, DbConfig.PHONE_NUMBER + "=?", new String[]{recoderEntity.getPhoneNumber()});
            return row != -1 ? true : false;
        } catch (Exception e) {
        } finally {
            closeSqlDateBase();
        }
        return false;
    }

    /**
     * query all data
     *
     * @return list<data>
     */
    public List<RecoderEntity> queryData() {
        List<RecoderEntity> list = new ArrayList<>();
        checkSqlDateBase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(DbConfig.TABLE_NAME, null, null, null, null, null, DbConfig.DATE + " desc");
            while (cursor.moveToNext()) {
                RecoderEntity recoderEntity = new RecoderEntity();
                recoderEntity.setPhotoUri(cursor.getString(cursor.getColumnIndex(DbConfig.PHOTO)));
                recoderEntity.setName(cursor.getString(cursor.getColumnIndex(DbConfig.NAME)));
                recoderEntity.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DbConfig.PHONE_NUMBER)));
                recoderEntity.setCounryName(cursor.getString(cursor.getColumnIndex(DbConfig.COUNTRY_NAME)));
                recoderEntity.setCountryCode(cursor.getInt(cursor.getColumnIndex(DbConfig.COUNTRY_CODE)));
                recoderEntity.setCountryFlag(cursor.getInt(cursor.getColumnIndex(DbConfig.COUNTRY_FLAG)));
                recoderEntity.setType(cursor.getInt(cursor.getColumnIndex(DbConfig.TYPE)));
                recoderEntity.setDate(cursor.getLong(cursor.getColumnIndex(DbConfig.DATE)));
                System.out.println("DbManager.queryData" + recoderEntity.toString());
                list.add(recoderEntity);
            }

        } catch (Exception e) {
        } finally {
            closeSqlDateBase();
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return list;
    }

    /**
     * check table is contain data
     *
     * @param recoderEntity
     * @return contain return true or false
     */
    public boolean checkContain(RecoderEntity recoderEntity) {
        checkSqlDateBase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery("select * from " + DbConfig.TABLE_NAME + " where " + DbConfig.PHONE_NUMBER + "=?", new String[]{recoderEntity.getPhoneNumber()});
            return cursor.getCount() > 0 ? true : false;
        } catch (Exception e) {
        } finally {
            closeSqlDateBase();
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return false;
    }

    /**
     * query all count
     * @return
     */
    public int getCount() {
        checkSqlDateBase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery("select * from " + DbConfig.TABLE_NAME, null);
            return cursor.getCount();
        } catch (Exception e) {
        } finally {
            closeSqlDateBase();
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return 0;
    }

    /**
     * query entity all count
     * @param recoderEntity
     * @return
     */
    public int getEntityCount(RecoderEntity recoderEntity) {
        checkSqlDateBase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery("select * from " + DbConfig.TABLE_NAME + " where " + DbConfig.PHONE_NUMBER + "=?", new String[]{recoderEntity.getPhoneNumber()});
            return cursor.getCount();
        } catch (Exception e) {
        } finally {
            closeSqlDateBase();
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }
}
