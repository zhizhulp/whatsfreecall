package com.bestgo.adsplugin.ads.logger;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import ly.count.android.sdk.Countly;

/**
 * Created by jikai on 1/26/18.
 */

public class EventLogger {
    public static EventLogger instance = new EventLogger();

    public void init(Context context, String appKey) {
        if (!TextUtils.isEmpty(appKey)) {
            Countly.sharedInstance().init(context, "http://log.uugame.info", appKey);
        }
    }

    public void onCreate(Activity activity) {
        if (Countly.sharedInstance().isInitialized()) {
            Countly.onCreate(activity);
        }
    }

    public void onStart(Activity activity) {
        try {
            if (Countly.sharedInstance().isInitialized()) {
                Countly.sharedInstance().onStart(activity);
            }
        } catch (Exception ex) {
        }
    }

    public void onStop() {
        try {
            if (Countly.sharedInstance().isInitialized()) {
                Countly.sharedInstance().onStop();
            }
        } catch (Exception ex) {
        }
    }

    public void logEvent(String category, String action, String value) {
        if (Countly.sharedInstance().isInitialized()) {
            Map<String, String> segments = new HashMap<>();
            segments.put(action, value);
            try {
                Countly.sharedInstance().recordEvent(category, segments, 1, 0, 0);
            } catch (Exception ex) {
            }
        }
    }
}
