package com.rthc.freshair.net;

/**
 * Created by Administrator on 2016/6/30.
 */
public class OpenApi {

    public interface NetResult{
        public void onFail(okhttp3.Call call, Exception e, int id);
        public void onSuccess(String response, int id);
    }
}
