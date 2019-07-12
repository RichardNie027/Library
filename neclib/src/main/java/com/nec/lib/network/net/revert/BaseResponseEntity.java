package com.nec.lib.network.net.revert;

import com.nec.lib.network.utils.ApiConfig;

import java.io.Serializable;

public class BaseResponseEntity implements Serializable {

    public int code = -1;
    public String msg = "";

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean success() {
        return ApiConfig.getSucceedCode() == code;
    }

    public boolean tokenInvalid() {
        return ApiConfig.getInvalidateToken() == code;
    }
}
