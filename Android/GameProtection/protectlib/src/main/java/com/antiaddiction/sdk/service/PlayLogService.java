package com.antiaddiction.sdk.service;

import android.content.Intent;

import com.antiaddiction.sdk.AntiAddictionCore;
import com.antiaddiction.sdk.AntiAddictionKit;
import com.antiaddiction.sdk.AntiAddictionPlatform;
import com.antiaddiction.sdk.Callback;
import com.antiaddiction.sdk.entity.User;
import com.antiaddiction.sdk.net.HttpUtil;
import com.antiaddiction.sdk.net.NetUtil;
import com.antiaddiction.sdk.utils.LogUtil;
import com.antiaddiction.sdk.utils.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlayLogService {
    static void handlePlayLog(long startTime,long endTime,User user,Callback callback){
        LogUtil.logd("handlePlayLog startTime = " + startTime + " endTime = " + endTime +
                " user = " + user.toJsonString());
         checkUserState(startTime,endTime,user,callback);
    }

    public static void checkUserState(long startTime, long endTime, User user, Callback callback){
        if(AntiAddictionKit.getFunctionConfig().getSupportSubmitToServer()){
             checkUserStateByServer(startTime, endTime, user,callback,false);
        }else{
            if(callback != null){
                user.updateOnlineTime((int) (endTime - startTime));
                AntiAddictionCore.saveUserInfo();
                callback.onSuccess(checkUserStateByLocal(user, false));
            }

        }
    }

    public static void checkUserStateByLogin(long startTime,long endTime,User user,Callback callback){
        if(AntiAddictionKit.getFunctionConfig().getSupportSubmitToServer()){
            checkUserStateByServer(startTime, endTime, user, callback,false);
        }else{
            if(callback != null){
                user.updateOnlineTime((int) (endTime - startTime));
                AntiAddictionCore.saveUserInfo();
                callback.onSuccess(checkUserStateByLocal(user, true));
            }

        }
    }

    private static JSONObject checkUserStateByLocal(User user,boolean isLogin){
        LogUtil.logd("checkUserStateByLocal");
        long currentTime = new Date().getTime();
        JSONObject response = new JSONObject();
        int restrictType = 0; //1 宵禁 2 在线时长限制
        int remainTime = 0;
        String description = "";
        String title = "";
        try {
            if (null == user) {
                return null;
            }
            if(user.getAccountType() != AntiAddictionKit.USER_TYPE_ADULT){
                int toNightTime = TimeUtil.getTimeToNightStrict();
                int toLimitTime = TimeUtil.getTimeToStrict(user);
                if(user.getAccountType() <= AntiAddictionKit.USER_TYPE_UNKNOWN){
                    remainTime = toLimitTime;
                    restrictType = 2;
                }else {
                    remainTime = toLimitTime > toNightTime ? toNightTime : toLimitTime;
                    restrictType = toLimitTime > toNightTime ? 1 : 2;
                }
                if(restrictType == 2){
                    title = "健康游戏提示";
                    if(user.getAccountType() <= AntiAddictionKit.USER_TYPE_UNKNOWN){
                        if((!isLogin && remainTime <= 3 * 60) || remainTime <= 0 ) {
                            description = AntiAddictionKit.getCommonConfig().getUnIdentifyLimit();
                        }else{
                            if(remainTime == AntiAddictionKit.getCommonConfig().getGuestTime() && (user.getSaveTimeStamp() <= 0 || (currentTime - user.getSaveTimeStamp() < 1000))){
                                description = AntiAddictionKit.getCommonConfig().getUnIdentifyFirstLogin();
                            }else {
                                description = AntiAddictionKit.getCommonConfig().getUnIdentifyRemain(remainTime / 60 > 0 ? (remainTime / 60) : 1);
                            }
                        }
                    }else{
                        if((!isLogin && remainTime <= 3 * 60) || remainTime <= 0 ) {
                            int min = TimeUtil.isHoliday() ? AntiAddictionKit.getCommonConfig().getChildHolidayTime() : AntiAddictionKit.getCommonConfig().getChildCommonTime();
                            description = AntiAddictionKit.getCommonConfig().getIdentifyLimit(min / 60);
                        }
                    }
                }else {
                    title = "健康游戏提示";
                    int startHour =  AntiAddictionKit.getCommonConfig().getNightStrictStart() / 3600 ;
                    int endHour = AntiAddictionKit.getCommonConfig().getNightStrictEnd() / 3600;
                    description = AntiAddictionKit.getCommonConfig().getNightStrictLimit(startHour, endHour);
                }
            }
            response.put("restrictType",restrictType);
            response.put("remainTime",remainTime);

            Intent intent = new Intent("antisdk.time.click");
            intent.putExtra("time",remainTime);
            AntiAddictionPlatform.getActivity().sendBroadcast(intent);

            response.put("description",description);
            response.put("title",title);
            LogUtil.logd(" timeResult = " + response);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static void checkUserStateByServer(long startTime, long endTime, User user, final Callback callback,boolean isSync){
        LogUtil.logd(" checkUserStateByServer ");
        JSONArray timeArray = new JSONArray();
        JSONArray timesArray = new JSONArray();
        timeArray.put(startTime);
        timeArray.put(endTime);
        timesArray.put(timeArray);
        String params = null;
        JSONObject timeJson = new JSONObject();
        try {
            timeJson.put("server_times",timesArray);
            timeJson.put("local_times",timesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params = "play_logs="+timeJson;
        NetUtil.NetCallback netCallback =  new NetUtil.NetCallback() {
            @Override
            public void onSuccess(String response) {
                LogUtil.logd(" checkUserStateByServer success response = " + response);

                try {
                    JSONObject result = new JSONObject(response);
                    if(result.getInt("code") == 200){
                        Intent intent = new Intent("antisdk.time.click");
                        intent.putExtra("time",result.getInt("remainTime"));
                        AntiAddictionPlatform.getActivity().sendBroadcast(intent);
                        callback.onSuccess(result);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFail("");
                }
            }

            @Override
            public void onFail(int code, String message) {
                LogUtil.logd(" checkUserStateByServer fail code = " + code + " msg = " + message);
                callback.onFail(message);
            }
        };
      Map<String,String> head = new HashMap<>();
        try {
            head.put("Authorization","Bearer " + URLEncoder.encode(user.getUserId(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(isSync){
          NetUtil.postSyncWithHead(ServerApi.PLAY_LOG.getApi(),params,head,netCallback);
      }else{
          HttpUtil.postAsyncWithHead(ServerApi.PLAY_LOG.getApi(),params,head,netCallback);
      }
    }

    //游客登录时，生成对应提示文案，因为最后3分钟时，文案会改为已达时长文案
    public static String generateGuestLoginTip(int remainTime){
        //处理游客登录提示
        String tip = AntiAddictionKit.getCommonConfig().getUnIdentifyFirstLogin();
        if(remainTime < AntiAddictionKit.getCommonConfig().getGuestTime()){
            tip = AntiAddictionKit.getCommonConfig().getUnIdentifyRemain();
        }
        int min = remainTime / 60;
        min = min <= 0 ? 1 : min;
        StringBuilder stringBuilder = new StringBuilder(tip);
        stringBuilder.insert(stringBuilder.indexOf("#")+1,min);
        return stringBuilder.toString();
    }


}
