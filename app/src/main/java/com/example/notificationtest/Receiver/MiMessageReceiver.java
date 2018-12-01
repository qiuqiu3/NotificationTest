package com.example.notificationtest.Receiver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

public class MiMessageReceiver extends PushMessageReceiver {
    private static final String TAG = "MiMessageReceiver";

    private String mRegId;
    private String mTopic;
    private String mAlias;

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.e(TAG,"onNotificationMessageClicked is called. " + message.toString());
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
            Log.e(TAG, mTopic);
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
            Log.e(TAG, mAlias);
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.e(TAG,"onNotificationMessageArrived is called. " + message.toString());
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.e(TAG,"onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                Log.e(TAG, "Register push success.");
            } else {
                Log.e(TAG, "Register push fail.");
            }
        } else {
            log = message.getReason();
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.e(TAG,"onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String mRegId = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                // TODO: 重点是这里，一般情况下我们要改成自己的用户登录标识(userId)
                MiPushClient.setUserAccount(context, "userId", null);
                Log.e(TAG, "Register push success.");
            } else {
                Log.e(TAG, "Register push fail.");
            }
        }
    }
}