package com.sitare.imageproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private final int PICK_IMAGE = 0x14;
    private final int PERMISSION = 0x12;
    private final String TAG = "ImageTest";
    String jiraBaseURL = "https://api.atlassian.com/ex/jira/your-cloud-id/rest/api/2/";

    String token = "your token";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.openGalleryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION) {
            if (permissions[0].equals(READ_EXTERNAL_STORAGE) && grantResults[0] == PERMISSION_GRANTED) {
                openGallery();
            } else {
                Log.d(TAG, "Permission not granted");
            }
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, PERMISSION);
        } else {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            MainActivity.this.startActivityForResult(i, PICK_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PICK_IMAGE || resultCode != Activity.RESULT_OK) return;

        Uri uri = data.getData();
        Log.v(TAG, "Uri path: " + uri.getPath());
        File file = new FileSaver().saveUriToFile(this, uri);
        Log.v(TAG, "File after save path: " + Uri.fromFile(file).getPath());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(jiraBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpCreator().create(token))
                .build();

        JiraAPI jiraAPI = retrofit.create(JiraAPI.class);

        RequestBody filePart = RequestBody.create(MediaType.parse("*/*"), file);

        MultipartBody.Part partfile = MultipartBody.Part.createFormData("file", "file", filePart);

        Call<ResponseBody> call = jiraAPI.upload("JIR-168", partfile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("imageProject", "on response");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("imageProject", "on failure");

            }
        });
    }
}