package com.baisi.whatsfreecall.retrofit_services;

import com.baisi.whatsfreecall.configs.UrlConfig;
import com.baisi.whatsfreecall.entity.EarnListEntity;
import com.baisi.whatsfreecall.entity.InviteCode;
import com.baisi.whatsfreecall.entity.httpbackentity.AdsEntity;
import com.baisi.whatsfreecall.entity.httpbackentity.HttpEntity;
import com.baisi.whatsfreecall.entity.requestentity.AdsCharge;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NetService {

    @POST(UrlConfig.MISSION_LIST)
    Call<HttpEntity<EarnListEntity>> missionList(@Header ("Authorization") String token);

    @POST(UrlConfig.MISSION_STARS)
    Call<HttpEntity<EarnListEntity.EarnEntity>> applyStar(@Header("Authorization") String token, @Body AdsCharge body);

    @POST(UrlConfig.MISSION_VIDEO)
    Call<HttpEntity<EarnListEntity.EarnEntity>> applyVideo(@Header("Authorization") String token, @Body AdsCharge body);

    @GET(UrlConfig.MISSION_INVITE)
    Call<HttpEntity<InviteCode>> getInviteCode(@Header ("Authorization") String token);

    @POST(UrlConfig.MISSION_INVITE)
    Call<HttpEntity<InviteCode>> postInviteCode(@Header ("Authorization") String token,@Body InviteCode code);
}
