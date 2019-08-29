package com.baisi.whatsfreecall.entity.httpbackentity;

/**
 * Created by MnyZhao on 2017/12/11.
 */

public class LoginConsumeBackEntity {
    private int err_code;
    private String err_msg;
    private data data;

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

    public LoginConsumeBackEntity.data getData() {
        return data;
    }

    public void setData(LoginConsumeBackEntity.data data) {
        this.data = data;
    }

    public class data{
        private String token;
        private profile profile;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public LoginConsumeBackEntity.data.profile getProfile() {
            return profile;
        }

        public void setProfile(LoginConsumeBackEntity.data.profile profile) {
            this.profile = profile;
        }

        public class profile{
            private int credit_micro;
            private String name;
            private String picture;
            private String credit_string;

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

            @Override
            public String toString() {
                return "name:"+getName()+"pic:"+getPicture()+"balance:"+getCredit_micro();
            }
        }
    }

}
