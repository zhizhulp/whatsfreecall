package com.baisi.whatsfreecall.entity.httpbackentity;

/**
 * Created by MnyZhao on 2017/12/8.
 */

public class UserEntity {
    private String token;
    private profile profile;
    private static class profile{
        private int credit_micro;
        private String name;
        private String picture;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity.profile getProfile() {
        return profile;
    }

    public void setProfile(UserEntity.profile profile) {
        this.profile = profile;
    }
}
