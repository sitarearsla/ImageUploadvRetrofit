package com.sitare.imageproject;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface JiraAPI {

    @Multipart
    @POST("issue/{issueIdOrKey}/attachments")
    Call<ResponseBody> upload(@Path("issueIdOrKey") String issueId, @Part  MultipartBody.Part file);
}
