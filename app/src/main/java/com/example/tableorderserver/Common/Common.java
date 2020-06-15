package com.example.tableorderserver.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.tableorderserver.Module.Request;
import com.example.tableorderserver.Module.User;
import com.example.tableorderserver.Remote.APIService;
import com.example.tableorderserver.Remote.FCMRetrofitClient;
import com.example.tableorderserver.Remote.GeoCoordinates;
import com.example.tableorderserver.Remote.RetroClient;

import java.util.BitSet;

import retrofit2.Retrofit;

public class Common {
    public static User currentUser;
    public  static Request currentRequest;

    public static String UPDATE = "Update";
    public static String DELETE = "Delete";

    public static final int PICK_IMAGE_REQUEST =71;

    public static final String baseUrl = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com";

    public  static String convertCodeeToStatus(String code){
        if (code.equals("0")){
            return "Placed";
        }
        else if (code.equals("1")){
            return "On the way";

        }
        else
            return "delivered";
    }

    public static GeoCoordinates getGeoCodeService(){
        return RetroClient.getClient(baseUrl).create(GeoCoordinates.class);
    }

    public static APIService getFCMClient(){
        return FCMRetrofitClient.getClient(fcmURL).create(APIService.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/bitmap.getWidth();
        float scaleY = newHeight/bitmap.getHeight();
        float pivotX=0,pivotY=0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }
}
