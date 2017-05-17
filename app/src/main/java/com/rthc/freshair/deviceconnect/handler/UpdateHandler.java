package com.rthc.freshair.deviceconnect.handler;

/**
 * Created by Administrator on 2016/8/26.
 *
 * 设备固件升级回调
 *
 * @author wdjxysc
 */
public interface UpdateHandler {
    void success();
    void failed(String massage);
    void timeout();
    void progress(float progress);
}
