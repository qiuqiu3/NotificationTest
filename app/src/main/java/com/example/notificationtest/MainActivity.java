package com.example.notificationtest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.notificationtest.Model.NotificationInfo;
import com.example.notificationtest.Utils.PushUtil;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageHelper;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPush();
        initIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initPush();
        initIntent(intent);
    }

    private void initPush() {
        PushUtil.setup(this);
    }

    private void initIntent(Intent intent) {
        NotificationInfo notificationInfo = null;
        Uri data = intent.getData();
        MiPushMessage miPushMessage = (MiPushMessage) intent.getSerializableExtra(PushMessageHelper.KEY_MESSAGE);

        if (data != null) { // 点击华为推送的信息后 intent 带过来的信息
            // json 是后台发送过来的字符串字段
            String huaweiJson = data.getQueryParameter("json");
            if (huaweiJson != null) {
                // 通过用 json 库转一下
//                notificationInfo = new Gson().fromJson(huaweiJson, NotificationInfo.class);
            }
        } else if (miPushMessage != null) { // 点击小米推送的信息后 intent 带过来的信息
            notificationInfo = new NotificationInfo();
            Map<String, String> extra = miPushMessage.getExtra();
            // 小米通过 getExtra 获取键值对
//            notificationInfo.setId(extra.get("id"));
//            notificationInfo.setType(extra.get("type"));
        }
    }
}
