package com.example.notificationtest.Receiver;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.support.api.push.PushReceiver;

public class HuaweiPushReceiver extends PushReceiver {

    private static final String TAG = "HuaweiPushReceiver";

    @Override
    public void onToken(Context context, String token, Bundle extras) {
        Log.e(TAG, "HuaweiPushRevicer onToken  token = " + token);
        // TODO: 拿到 token 后要连同用户登录标识(userId)发送给后台
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            Log.e(TAG, "HuaweiPushRevicer onPushMsg: " + new String(msg, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onEvent(final Context context, Event event, Bundle extras) {
        Log.e(TAG, "HuaweiPushRevicer onEvent with data: " + extras.toString());
    }

    @Override
    public void onPushState(Context context, boolean pushState) {
        Log.e(TAG, "HuaweiPushRevicer onPushState  pushState = " + pushState);
    }
}