package com.baisi.whatsfreecall.utils.utilcall;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

/**
 * Created by MnyZhao on 2017/12/7.
 */

public class callUtils {
    public callUtils() {
    }

    /**
     * 通过号码获取联系人名称
     */
    public String getContactNameByNumber(String number, Context context) {
        String contactName = "";
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number)), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        if (contactLookupCursor.getCount() > 0) {
            while (contactLookupCursor.moveToNext()) {
                contactName = contactLookupCursor.getString(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
        }
        contactLookupCursor.close();

        return contactName;
    }

    /**
     * 通过号码获取联系人头像
     */
    public String getContactPhotoUrlByNumber(String number, Context context) {
        String contactName = "";
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number)), new String[]{ContactsContract.PhoneLookup.PHOTO_URI, ContactsContract.PhoneLookup._ID}, null, null, null);
        if (contactLookupCursor.getCount() > 0) {
            while (contactLookupCursor.moveToNext()) {
                contactName = contactLookupCursor.getString(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_URI));
            }
        }
        contactLookupCursor.close();

        return contactName;
    }

    /**
     * 根据类型id获取类型
     * @param typeId
     * @return
     */
    public String getPhoneTypeNameById(int typeId) {
        switch (typeId) {
            case Phone.TYPE_HOME:
                return "home";
            case Phone.TYPE_MOBILE:
                return "mobile";
            case Phone.TYPE_WORK:
                return "work";
            case Phone.TYPE_FAX_WORK:
                return "fax work";
            default:
                return "none";
        }
    }
}
