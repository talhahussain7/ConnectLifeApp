package com.example.connectlife.notificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAqVWzaqs:APA91bFGkuyaXAT0Roi_bv9ubSQ4zLPLCg5kd-ts_VkVdK8CozMxpH6SF3LP8r690YXQ6E51u3h2E7lJqECBeYj_VvGDvM2aGXsNzuta5y-klEdaoz4yilg8X71uygq3fC9oEcAUmjCs" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

