package com.example.tableorderserver.Remote;


import com.example.tableorderserver.Module.MyResponse;
import com.example.tableorderserver.Module.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAa109HA4:APA91bHUyKw7G_6a1ZZ0IHkAze5Yma2wiPf7l9IoEZT6KfappOt2gRwCB0sLt3j_tW1PGNlaVW1MfXx89iIOD4dH58H2vAiV7KV2XsqunL7bZEIXz5Iu7jnJoxtHHiobbvP8HUZgGB7j"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
