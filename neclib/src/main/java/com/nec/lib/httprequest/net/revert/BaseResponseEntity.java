package com.nec.lib.httprequest.net.revert;

import com.nec.lib.httprequest.utils.ApiConfig;

import java.io.Serializable;

public class BaseResponseEntity implements Serializable {

    public int code = -1;   //如需重命名，在子类重定义变量且加注释@SerializedName("error_code")
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
