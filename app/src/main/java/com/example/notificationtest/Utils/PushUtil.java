package com.example.notificationtest.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.notificationtest.Service.PlayerService;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.xiaomi.mipush.sdk.MiPushClient;

public final class PushUtil {

    private static final String TAG = "PushUtil";
    private static HuaweiApiClient mClient;

    private static HuaweiApiClient init(Context context) {
        if (mClient == null) {
            synchronized (PushUtil.class) {
                if (mClient == null) {
                    mClient = new HuaweiApiClient.Builder(context)
                            .addApi(HuaweiPush.PUSH_API)
                            .addConnectionCallbacks(new HuaweiApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected() {
                                    Log.e(TAG, "HUAWEI onConnected, IsConnected: " + mClient.isConnected());
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            PendingResult<TokenResult> token = HuaweiPush.HuaweiPushApi.getToken(mClient);
                                            token.await();
                                        }
                                    }).start();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    Log.e(TAG, "HUAWEI onConnectionSuspended, cause: " + i + ", IsConnected:" + " " + mClient.isConnected());
                                }
                            })
                            .addOnConnectionFailedListener(new HuaweiApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(ConnectionResult connectionResult) {
                                    Log.e(TAG, "HUAWEI onConnectionFailed, ErrorCode: " + connectionResult.getErrorCode());
                                }
                            }).build();
                }
            }
        }
        return mClient;
    }

    public static void setup(Context context) {
        if (DeviceUtil.getRomType() == DeviceUtil.ROM_TYPE.EMUI) { // 华为推送
            init(context).connect();
            MiPushClient.unregisterPush(context);
        } else { // 小米推送
            String metaInfo = getMetaInfo(context, "MI_PUSH_INFO");
            if (metaInfo != null) {
                MiPushClient.registerPush(context, metaInfo.split(",")[0], metaInfo.split(",")[1]);
                // 非小米需要后台播放音乐保活进程接收推送通知
                if (DeviceUtil.getRomType() != DeviceUtil.ROM_TYPE.MIUI)
                    context.startService(new Intent(context, PlayerService.class));
            }
        }
    }

    public static String getMetaInfo(Context context, String key) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return applicationInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
