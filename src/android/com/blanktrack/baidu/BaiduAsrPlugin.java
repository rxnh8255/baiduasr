package com.blanktrack.baidu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Blank on 2017-08-24.
 */

public class BaiduAsrPlugin extends CordovaPlugin {
    public static final String TAG = "BaiduAsrPlugin";
    private static CallbackContext pushContext;
    private String permission = Manifest.permission.RECORD_AUDIO;
    private EventManager asr;


    public static CallbackContext getCurrentCallbackContext() {
        return pushContext;
    }

    private Context getApplicationContext() {
        return this.cordova.getActivity().getApplicationContext();
    }

    protected void getMicPermission(int requestCode) {
        PermissionHelper.requestPermission(this, requestCode, permission);
    }
    /**
     * Called after plugin construction and fields have been initialized.
     * Prefer to use pluginInitialize instead since there is no value in
     * having parameters on the initialize() function.
     *
     * @param cordova
     * @param webView
     */
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
//        Context context = this.cordova.getActivity().getApplicationContext();

//        ApplicationInfo applicationInfo = null;
//        try {
//            applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        //SpeechUtility.createUtility(context, "appid="+applicationInfo.metaData.getString("com.blanktrack.appid"));

        asr = EventManagerFactory.create(getApplicationContext(), "asr");
        asr.registerListener(asrListener);

    }


    private void registerNotifyCallback(CallbackContext callbackContext) {

        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);

    }
    /**
     * Called when the system is about to start resuming a previous activity.
     *
     * @param multitasking Flag indicating if multitasking is turned on for app
     */
    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
    }

    EventListener asrListener = new EventListener() {
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            Log.d(TAG, String.format("event: name=%s, params=%s", name, params));

            if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)){
                // 引擎就绪，可以说话，一般在收到此事件后通过UI通知用户可以说话了
                sendEvent("asrBegin","ok");
            }
            if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)){
                // 识别结束
                sendEvent("asrFinish","ok");
            }
            if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)){
                sendEvent("asrText",params);
            }

            //唤醒成功
//            if(name.equals("wp.data")){
//                try {
//                    JSONObject json = new JSONObject(params);
//                    int errorCode = json.getInt("errorCode");
//                    if(errorCode == 0){
//                        //唤醒成功
//                        sendEvent("wakeup",json.toString());
//                        Log.i(TAG,"baidu唤醒成功了了了了");
//                    } else {
//                        //唤醒失败
//                        Log.i(TAG,"baidu唤醒失败l了了了了");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else if("wp.exit".equals(name)){
//                //唤醒已停止
//                Log.i(TAG,"baidu唤醒停止停止");
//                sendEvent("sleep","true");
//            }

        }
    };

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        //final JSONObject arg_object = args.getJSONObject(0);
        if ("begin".equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    promptForRecord();
                    callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.OK) );
                }
            });
        }
        else if ("stop".equals(action)) {
            Log.i(TAG, "stop voice");
            // 停止录音
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
                    callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.OK) );
                }
            });

        }  else if ("finish".equals(action)) {
            callbackContext.success();
        } else if ("registerNotify".equals(action)) {
            pushContext = callbackContext;
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    registerNotifyCallback(callbackContext);
                }
            });
        }
        else {
            Log.e(TAG, "Invalid action : " + action);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
            return false;
        }

        return true;
    }

    /**
     * The final call you receive before your activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != asr) {
            asr = null;
        }
    }

    private void sendEvent(String type, String msg) {
        JSONObject response = new JSONObject();
        try {
            response.put("type", type);
            response.put("message", msg);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, response);
            pluginResult.setKeepCallback(true);
            CallbackContext pushCallback = getCurrentCallbackContext();
            if (pushCallback != null) {
                pushCallback.sendPluginResult(pluginResult);
            }

        } catch (JSONException e) {
            sendError(e.getMessage());
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                sendError("用户未授权录音机");
                return;
            }
        }
        promptForRecord();
    }

    private void promptForRecord() {
        if (PermissionHelper.hasPermission(this, permission)) {
            Log.i(TAG,"开始识别");

            String json = "{\"accept-audio-data\":false,\"accept-audio-volume\":false,\"pid\":1536}";
            asr.send(SpeechConstant.ASR_START, json, null, 0, 0);

//            Map<String, Object> params = new LinkedHashMap<String, Object>();
//            params.put(com.baidu.speech.asr.SpeechConstant.APP_ID, "10099877");
//            params.put(com.baidu.speech.asr.SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
//            String json = null; // 这里可以替换成你需要测试的json
//            json = new JSONObject(params).toString();
//            wakeup.send(com.baidu.speech.asr.SpeechConstant.WAKEUP_START, json, null, 0, 0);

        } else {
            getMicPermission(0);
        }

    }

    public void sendError(String message) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, message);
        pluginResult.setKeepCallback(true);
        CallbackContext pushCallback = getCurrentCallbackContext();
        if (pushCallback != null) {
            pushCallback.sendPluginResult(pluginResult);
        }
    }

}
