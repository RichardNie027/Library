package com.nec.lib.android.httprequest.net.revert;

/**
 * 网络返回参数回调
 */
public interface OnBaseResponseListener<R extends BaseResponseEntity> {

  /**
   * 成功
   *
   * @param response 成功参数
   */
  void onSuccess(R response);

  /**
   * 失败
   *
   * @param response 失败参数
   */
  default void onFailing(R response) {
  }

  /**
   * 错误
   */
  default void onError() {
  }
}
