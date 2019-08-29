package com.baisi.whatsfreecall.entity.checkin;

import java.util.List;

/**
 * Created by MnyZhao on 2018/2/6.
 */

public class CheckInEntity {
    public String reward;
    public profile profile;
    public List<tasks> tasks;


    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public CheckInEntity.profile getProfile() {
        return profile;
    }

    public void setProfile(CheckInEntity.profile profile) {
        this.profile = profile;
    }

    public List<CheckInEntity.tasks> getTasks() {
        return tasks;
    }

    public void setTasks(List<CheckInEntity.tasks> tasks) {
        this.tasks = tasks;
    }

    public static class profile {
        public int credit_micro;
        public String credit_string;
        public String name;
        public String picture;
    }

    public static class tasks {
        public int status;
        public String reward;

        public tasks(){}

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getReward() {
            return reward;
        }

        public void setReward(String reward) {
            this.reward = reward;
        }
    }


    @Override
    public String toString() {
        return reward+":"+ tasks.size();
    }
}
