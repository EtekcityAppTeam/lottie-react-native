package com.airbnb.android.react.lottie;

import android.os.Handler;
import android.os.Looper;

import android.support.v4.view.ViewCompat;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

class LottieAnimationViewManager extends SimpleViewManager<LottieAnimationView> {
    private static final String TAG = LottieAnimationViewManager.class.getSimpleName();
    private static final String REACT_CLASS = "LottieAnimationView";
    private static final int VERSION = 1;
    private static final int COMMAND_PLAY = 1;
    private static final int COMMAND_RESET = 2;

    @Override
    public Map<String, Object> getExportedViewConstants() {
        return MapBuilder.<String, Object>builder()
                .put("VERSION", VERSION)
                .build();
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public LottieAnimationView createViewInstance(ThemedReactContext context) {
        return new LottieAnimationView(context);
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "play", COMMAND_PLAY,
                "reset", COMMAND_RESET
        );
    }

    @Override
    public void receiveCommand(final LottieAnimationView view, int commandId, ReadableArray args) {
        switch (commandId) {
            case COMMAND_PLAY: {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (ViewCompat.isAttachedToWindow(view)) {
                            view.setProgress(0f);
                            view.playAnimation();
                        }
                    }
                });
            }
            break;
            case COMMAND_RESET: {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (ViewCompat.isAttachedToWindow(view)) {
                            view.cancelAnimation();
                            view.setProgress(0f);
                        }
                    }
                });
            }
            break;
        }
    }

    // TODO: cache strategy

    @ReactProp(name = "sourceName")
    public void setSourceName(LottieAnimationView view, String name) {
        view.setAnimation(name);
    }

    @ReactProp(name = "sourceJson")
    public void setSourceJson(LottieAnimationView view, ReadableMap json) {
        try {
//            view.setAnimation(new JSONObject(json.toHashMap()));
            view.setAnimation(toJsonObject(json));
        } catch (Exception e) {
            // TODO: expose this to the user better. maybe an `onError` event?
            Log.e(TAG, "setSourceJsonError", e);
        }
    }

    private JSONObject toJsonObject(ReadableMap readableMap) throws JSONException {
        JSONObject object = new JSONObject();
        ReadableMapKeySetIterator iter = readableMap.keySetIterator();
        while (iter.hasNextKey()) {
            String key = iter.nextKey();
            ReadableType type = readableMap.getType(key);
            switch (type) {
                case Boolean:
                    object.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    object.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    object.put(key, readableMap.getString(key));
                    break;
                case Map:
                    object.put(key, toJsonObject(readableMap.getMap(key)));
                    break;
                case Array:
                    object.put(key, toJsonArray(readableMap.getArray(key)));
                    break;
            }
        }
        return object;
    }

    private JSONArray toJsonArray(ReadableArray readableArray) throws JSONException {
        JSONArray array = new JSONArray();
        for (int idx = 0; idx < readableArray.size(); idx++) {
            ReadableType type = readableArray.getType(idx);
            switch (type) {
                case Boolean:
                    array.put(readableArray.getBoolean(idx));
                    break;
                case Number:
                    array.put(readableArray.getDouble(idx));
                    break;
                case String:
                    array.put(readableArray.getString(idx));
                    break;
                case Map:
                    array.put(toJsonObject(readableArray.getMap(idx)));
                    break;
                case Array:
                    array.put(toJsonArray(readableArray.getArray(idx)));
                    break;
            }
        }
        return array;
    }

    @ReactProp(name = "progress")
    public void setProgress(LottieAnimationView view, float progress) {
        view.setProgress(progress);
    }

    @ReactProp(name = "speed")
    public void setSpeed(LottieAnimationView view, double speed) {
        // TODO?
    }

    @ReactProp(name = "loop")
    public void setLoop(LottieAnimationView view, boolean loop) {
        view.loop(loop);
    }

    @ReactProp(name = "imageAssetsFolder")
    public void setImageAssetsFolder(LottieAnimationView view, String imageAssetsFolder) {
        view.setImageAssetsFolder(imageAssetsFolder);
    }
}
