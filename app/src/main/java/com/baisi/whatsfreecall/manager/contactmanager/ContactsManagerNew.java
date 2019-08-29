package com.baisi.whatsfreecall.manager.contactmanager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.baisi.whatsfreecall.entity.ContactsEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by MnyZhao on 2017/12/5.
 */

public class ContactsManagerNew {
    private Context mContext;
    private String PHONEBOOK_LABEL;
    private static final int CONTACT_ID = 0;
    private static final int DISPLAY_NAME = 1;
    private static final int NUMBER = 2;
    private static final int PHOTO_URI = 3;
    private static final int TYPE = 4;
    private static final int PHONEBOOK_LABELINT = 5;

    private static String[] PHONES_PROJECTION;

    public ContactsManagerNew(Context mContext) {
        this.mContext = mContext;
        //如果android操作系统版本4.4或4.4以上就要用phonebook_label而不是sort_key字段
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            PHONEBOOK_LABEL = "phonebook_label";
        } else {
            PHONEBOOK_LABEL = Phone.SORT_KEY_PRIMARY;
        }
        PHONES_PROJECTION = new String[]{
                Phone.CONTACT_ID,
                Phone.DISPLAY_NAME,
                Phone.NUMBER,
                Phone.PHOTO_URI,
                Phone.TYPE,
                PHONEBOOK_LABEL};
    }

    /**
     * 获取联系人信息
     *
     * @return
     */
    public List<ContactsEntity> getAllContacts() {
        List<ContactsEntity> lists = new ArrayList<ContactsEntity>();
        LinkedHashMap<String, ContactsEntity> LinkedHashMap = new LinkedHashMap<>();
        ContentResolver resolver = mContext.getContentResolver();
        ContactsEntity bean = new ContactsEntity();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
                PHONES_PROJECTION, null, null, Phone.SORT_KEY_PRIMARY);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                // 得到联系人ID
                long contactId = phoneCursor.getLong(CONTACT_ID);
                String name = phoneCursor.getString(DISPLAY_NAME);
                String phontType = "";
                String number = "";
                if (LinkedHashMap.containsKey(name)) {
                    int phoneType = phoneCursor.getInt(TYPE);
                    phontType = bean.getType() + ";" + getPhoneTypeNameById(phoneType);
                    number = bean.getPhone() + ";" + phoneCursor.getString(NUMBER);
                } else {
                    int phoneType = phoneCursor.getInt(TYPE);
                    phontType = getPhoneTypeNameById(phoneType);
                    number = phoneCursor.getString(NUMBER);

                }
                ContactsEntity contactsEntity = new ContactsEntity();
                contactsEntity.setId(contactId);
                contactsEntity.setName(name);
                contactsEntity.setType(phontType);
                try {
                    contactsEntity.setPhone(number.replace(" ", ""));
                } catch (NullPointerException e) {
                    contactsEntity.setPhone(number);
                }
                try{
                    contactsEntity.setFirstLater(phoneCursor.getString(PHONEBOOK_LABELINT));
                }catch (IllegalArgumentException e){
                    contactsEntity.setFirstLater("#");
                }
                contactsEntity.setIconUri(phoneCursor.getString(PHOTO_URI));
                bean = contactsEntity;
                LinkedHashMap.put(contactsEntity.getName(), contactsEntity);
            }
            Collection<ContactsEntity> cl = LinkedHashMap.values();
            Iterator<ContactsEntity> it = cl.iterator();
            while (it.hasNext()) {
                lists.add(it.next());
            }
            phoneCursor.close();
        }
        return lists;
    }

    /**
     * 获取联系人信息速率最慢 多个cursor查询
     *
     * @return
     */
    public List<ContactsEntity> getAllContactsLow() {
        List<ContactsEntity> lists = new ArrayList<ContactsEntity>();
        ContentResolver resolver = mContext.getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, null, null, null, Phone.SORT_KEY_PRIMARY);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                // 得到联系人ID
                long contactId = phoneCursor.getLong(phoneCursor.getColumnIndex(Phone.CONTACT_ID));
                String phoneType = "";
                String phoneNumber = "";
                String photoUri = "";
                Cursor numberCursor = resolver.query(Phone.CONTENT_URI,
                        null,
                        Phone.CONTACT_ID + "="
                                + contactId, null, null);
                if (numberCursor != null) {
                    while (numberCursor.moveToNext()) {
                        photoUri = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                        System.out.println("ContactsManager.getAllContacts>>>" + photoUri);
                        int phoneTypeint = numberCursor.getInt(numberCursor.getColumnIndex(Phone.TYPE));

                        phoneType += getPhoneTypeNameById(phoneTypeint) + ";";
                        phoneNumber += numberCursor.getString(numberCursor.getColumnIndex(Phone.NUMBER)) + ";";
                    }
                    numberCursor.close();
                }
                ContactsEntity contactsEntity = new ContactsEntity();
                contactsEntity.setId(contactId);
                contactsEntity.setName(phoneCursor.getString(phoneCursor.getColumnIndex(Phone.DISPLAY_NAME)));
                contactsEntity.setType(phoneType);
                contactsEntity.setPhone(phoneNumber.replace(" ", ""));
                contactsEntity.setFirstLater(phoneCursor.getString(phoneCursor.getColumnIndex(PHONEBOOK_LABEL)));
                contactsEntity.setIconUri(photoUri);
                lists.add(contactsEntity);
            }
            removeDuplicateWithOrder(lists);
            phoneCursor.close();

        }
        return lists;
    }

    /**
     * 根据类型id获取类型
     *
     * @param typeId
     * @return
     */
    private String getPhoneTypeNameById(int typeId) {
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

    /*无序*/
    public void removeDuplicate(List arlList) {
        HashSet h = new HashSet(arlList);
        arlList.clear();
        arlList.addAll(h);
    }

    /*不改变原来顺序*/
    public static void removeDuplicateWithOrder(List<ContactsEntity> arlList) {
        Set set = new HashSet();
        List newList = new ArrayList();
        Iterator iter = arlList.iterator();
        while (iter.hasNext()) {
            ContactsEntity element = (ContactsEntity) iter.next();
            if (set.add(element.getId())) {
                newList.add(element);
            }
        }
        arlList.clear();
        arlList.addAll(newList);
    }

    /**
     * object类型必须重写equals方法和hashcode方法 再调用下面object方法
     * public class user{
     * String name;
     * int age;
     *
     * @param arlList
     * @Override public boolean equals(Object o){
     * if(o=this){return true};
     * if(!(o instanceof User)){
     * return false;
     * }
     * UserEntity user=(UserEntity)o;
     * //有几个比较几个
     * return UserEntity.name.equals(name)&&UserEntity.age==age;
     * }
     * @Override public int hashCode(){
     * //随便写
     * int result=11;
     * result=22*result+name.hashcode();
     * result=22*result+age;
     * return result;
     * }
     * }
     */
    public static void removeDuplicateWithOrderObject(List arlList) {
        Set set = new HashSet();
        List newList = new ArrayList();
        Iterator iter = arlList.iterator();
        while (iter.hasNext()) {
            Object element = iter.next();
            if (set.add(element) && set.contains(element)) {
                newList.add(element);
            }
        }
        arlList.clear();
        arlList.addAll(newList);
    }
}
