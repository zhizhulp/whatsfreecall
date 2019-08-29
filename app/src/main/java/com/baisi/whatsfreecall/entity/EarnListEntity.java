package com.baisi.whatsfreecall.entity;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EarnListEntity implements Serializable {
    public List<EarnEntity> list;
    public static class EarnEntity implements Serializable{
        //本地属性
        public int resId;
        public int color;
        public int rightColor;
        public String desc;
        public int prtResColor;
        //网络属性
        @SerializedName("reward")
        public String reward;
        @SerializedName("total_wait_seconds")
        public volatile long totalTime;
        @SerializedName("wait_seconds")
        public volatile long leftTime;
        public int type;
        public List<Stars> stars;
        public Profile profile;
        public static class Stars implements Serializable {
            public int status;
        }
        public class Profile implements Serializable{
            public int credit_micro;
            public String name;
            public String picture;
            public String credit_string;
        }

        @Override
        public String toString() {
            return "EarnEntity{" +
                    "resId=" + resId +
                    ", color=" + color +
                    ", rightColor=" + rightColor +
                    ", desc='" + desc + '\'' +
                    ", reward='" + reward + '\'' +
                    ", totalTime=" + totalTime +
                    ", leftTime=" + leftTime +
                    ", type=" + type +
                    ", stars=" + stars +
                    ", profile=" + profile +
                    '}';
        }
    }
}
