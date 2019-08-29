package com.baisi.whatsfreecall.entity.httpbackentity;

/**
 * Created by MnyZhao on 2018/1/6.
 * @author Mny
 * 充值返回
 */

public class AdsEntity {
    private int err_code;
    private String err_msg;
    private data data;
    public class data{
        public String reward;
        public profile profile;
        public class profile{
            public int credit_micro;
            public String name;
            public String picture;
            public String credit_string;

            public String getCredit_string() {
                return credit_string;
            }

            public void setCredit_string(String credit_string) {
                this.credit_string = credit_string;
            }

            public int getCredit_micro() {
                return credit_micro;
            }

            public void setCredit_micro(int credit_micro) {
                this.credit_micro = credit_micro;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPicture() {
                return picture;
            }

            public void setPicture(String picture) {
                this.picture = picture;
            }


        }

        public String getReward() {
            return reward;
        }

        public void setReward(String reward) {
            this.reward = reward;
        }

        public AdsEntity.data.profile getProfile() {
            return profile;
        }

        public void setProfile(AdsEntity.data.profile profile) {
            this.profile = profile;
        }
    }

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public AdsEntity.data getData() {
        return data;
    }

    public void setData(AdsEntity.data data) {
        this.data = data;
    }
}
