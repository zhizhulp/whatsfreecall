/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baisi.whatsfreecall.utils.utilpay;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app billing purchase.
 */
public class Purchase implements Parcelable {
    String mItemType;  // ITEM_TYPE_INAPP or ITEM_TYPE_SUBS
    String mOrderId;
    String mPackageName;
    String mSku;
    long mPurchaseTime;
    int mPurchaseState;
    String mDeveloperPayload;
    String mToken;
    String mOriginalJson;
    String mSignature;
    boolean mIsAutoRenewing;

    public Purchase(String itemType, String jsonPurchaseInfo, String signature) throws JSONException {
        mItemType = itemType;
        mOriginalJson = jsonPurchaseInfo;
        JSONObject o = new JSONObject(mOriginalJson);
        mOrderId = o.optString("orderId");
        mPackageName = o.optString("packageName");
        mSku = o.optString("productId");
        mPurchaseTime = o.optLong("purchaseTime");
        mPurchaseState = o.optInt("purchaseState");
        mDeveloperPayload = o.optString("developerPayload");
        mToken = o.optString("token", o.optString("purchaseToken"));
        mIsAutoRenewing = o.optBoolean("autoRenewing");
        mSignature = signature;
    }

    public String getItemType() { return mItemType; }
    public String getOrderId() { return mOrderId; }
    public String getPackageName() { return mPackageName; }
    public String getSku() { return mSku; }
    public long getPurchaseTime() { return mPurchaseTime; }
    public int getPurchaseState() { return mPurchaseState; }
    public String getDeveloperPayload() { return mDeveloperPayload; }
    public String getToken() { return mToken; }
    public String getOriginalJson() { return mOriginalJson; }
    public String getSignature() { return mSignature; }
    public boolean isAutoRenewing() { return mIsAutoRenewing; }

    @Override
    public String toString() { return "PurchaseInfo(type:" + mItemType + "):" + mOriginalJson; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mItemType);
        dest.writeString(this.mOrderId);
        dest.writeString(this.mPackageName);
        dest.writeString(this.mSku);
        dest.writeLong(this.mPurchaseTime);
        dest.writeInt(this.mPurchaseState);
        dest.writeString(this.mDeveloperPayload);
        dest.writeString(this.mToken);
        dest.writeString(this.mOriginalJson);
        dest.writeString(this.mSignature);
        dest.writeByte(this.mIsAutoRenewing ? (byte) 1 : (byte) 0);
    }

    protected Purchase(Parcel in) {
        this.mItemType = in.readString();
        this.mOrderId = in.readString();
        this.mPackageName = in.readString();
        this.mSku = in.readString();
        this.mPurchaseTime = in.readLong();
        this.mPurchaseState = in.readInt();
        this.mDeveloperPayload = in.readString();
        this.mToken = in.readString();
        this.mOriginalJson = in.readString();
        this.mSignature = in.readString();
        this.mIsAutoRenewing = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Purchase> CREATOR = new Parcelable.Creator<Purchase>() {
        @Override
        public Purchase createFromParcel(Parcel source) {
            return new Purchase(source);
        }

        @Override
        public Purchase[] newArray(int size) {
            return new Purchase[size];
        }
    };
}
