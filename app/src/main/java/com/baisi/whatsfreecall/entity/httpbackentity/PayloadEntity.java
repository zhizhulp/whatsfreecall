package com.baisi.whatsfreecall.entity.httpbackentity;

/**
 * Created by MnyZhao on 2017/12/11.
 */

public class PayloadEntity {
    private int err_code;
    private String err_msg;
    private data data;
    public class data {
        public String pay_id;

        public data(String pay_id) {
            this.pay_id = pay_id;
        }

        public String getPay_id() {
            return pay_id;
        }

        public void setPay_id(String pay_id) {
            this.pay_id = pay_id;
        }
    }

    public PayloadEntity.data getData() {
        return data;
    }

    public void setData(PayloadEntity.data data) {
        this.data = data;
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



}
