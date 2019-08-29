package com.baisi.whatsfreecall.entity.httpbackentity;

/**
 * @author MnyZhao
 * @date 2017/12/29
 */

public class CallScrrenEntity {
    private int err_code;
    private String error_msg;
    private data data;

    public class data {
        private String locale;
        private String time;
        private float rate;
        private String rate_string;
        private float credit;
        private String credit_string;
        private String remain;

        @Override
        public String toString() {
            return "local" + getLocal() + "\ntime" + getTime() + "\nrate" + getRate() + "\ncredit" + getCredit() + "remamin" + getRemain();
        }

        public data(String locale, String time, float rate, String rate_string, String credit_string, float credit, String remain) {
            this.locale = locale;
            this.time = time;
            this.rate = rate;
            this.credit = credit;
            this.remain = remain;
            this.credit_string = credit_string;
            this.rate_string = rate_string;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public String getRate_string() {
            return rate_string;
        }

        public void setRate_string(String rate_string) {
            this.rate_string = rate_string;
        }

        public String getCredit_string() {
            return credit_string;
        }

        public void setCredit_string(String credit_string) {
            this.credit_string = credit_string;
        }

        public String getLocal() {
            return locale;
        }

        public void setLocal(String locale) {
            this.locale = locale;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public float getRate() {
            return rate;
        }

        public void setRate(float rate) {
            this.rate = rate;
        }

        public float getCredit() {
            return credit;
        }

        public void setCredit(float credit) {
            this.credit = credit;
        }

        public String getRemain() {
            return remain;
        }

        public void setRemain(String remain) {
            this.remain = remain;
        }
    }

    @Override
    public String toString() {
        return "error";
    }

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public CallScrrenEntity.data getData() {
        return data;
    }

    public void setData(CallScrrenEntity.data data) {
        this.data = data;
    }
    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
