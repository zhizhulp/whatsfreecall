package com.baisi.whatsfreecall.utils.libphonenumberutils;

import android.content.Context;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import java.util.Locale;

public class GeoUtil {


    private static PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();
    private static PhoneNumberToCarrierMapper carrierMapper = PhoneNumberToCarrierMapper.getInstance();

    // 获取国家码 “CN”
    public static String getCurrentCountryIso(Context context) {
        // The {@link CountryDetector} should never return null so this is safe to return as-is.
        return CountryDetector.getInstance(context).getCurrentCountryIso();
    }

    // 获取国家码 “86”
    public static int getCurrentCountryIsoNumber(Context context) {
        // The {@link CountryDetector} should never return null so this is safe to return as-is.

        return mPhoneNumberUtil.getCountryCodeForRegion(CountryDetector.getInstance(context).getCurrentCountryIso());
    }

    /**
     * 根据电话号码获取国家代码
     *
     * @param context
     * @param phoneNumber
     * @return
     */
    public static int getCountryCode(Context context, String phoneNumber) {
        Phonenumber.PhoneNumber structuredNumber = getStructedNumber(context, phoneNumber);
        if (structuredNumber != null) {
            return structuredNumber.getCountryCode();
        }
        return 0;
    }

    //获取归属地信息
    public static String getGeocodedLocationFor(Context context, String phoneNumber) {

        final PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();


        Phonenumber.PhoneNumber structuredNumber = getStructedNumber(context, phoneNumber);
        Locale locale = context.getResources().getConfiguration().locale;
        return geocoder.getDescriptionForNumber(structuredNumber, locale);
    }

    //检查是否为 有效号码
    public static boolean checkPhoneNumber(Context context, String phoneNumber) {
        if (getStructedNumber(context, phoneNumber)!=null) {
            return mPhoneNumberUtil.isValidNumber(getStructedNumber(context, phoneNumber));
        }
       return false;
    }

    public static Phonenumber.PhoneNumber getStructedNumber(Context context, String phoneNumber) {
        try {
            final Phonenumber.PhoneNumber structuredNumber =
                    mPhoneNumberUtil.parse(phoneNumber, getCurrentCountryIso(context));
            return structuredNumber;
        } catch (NumberParseException e) {
            return null;
        }
    }

    //获取运营商信息
    public static String getCarrier(Context context, String phoneNumber) {

        Phonenumber.PhoneNumber structedNumber = getStructedNumber(context, phoneNumber);
        Locale locale = context.getResources().getConfiguration().locale;
        return carrierMapper.getNameForNumber(structedNumber, Locale.US);
    }

    /**
     * 根据国家代码和手机号判断归属地
     *
     * @param context
     * @param phoneNumber
     * @param countryCode
     * @return
     */
    public static String getGeo(Context context, String phoneNumber, int countryCode) {

        int ccode = countryCode;
        long phone = Long.parseLong(phoneNumber);

        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(ccode);
        pn.setNationalNumber(phone);
        PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();
        Locale locale = context.getResources().getConfiguration().locale;
        return geocoder.getDescriptionForNumber(pn, locale);

    }
}