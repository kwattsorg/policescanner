package net.kwatts.android.NetScanner;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.google.android.libraries.maps.model.BitmapDescriptor;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Util {

    public static String fetchURL(String url)
    {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        URL fetchurl;

        try {
            try {
                fetchurl = new URL(url);
            } catch (MalformedURLException e) {
                return "";
            }
            connection = (HttpURLConnection) fetchurl.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer sb = new StringBuffer();
            String line ="";
            while((line= bufferedReader.readLine())!=null){
                sb.append(line);
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();

            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";
    }


    public static String getRedirectUrl(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setInstanceFollowRedirects(false);
            con.connect();
            return con.getHeaderField("Location");
        } catch (Exception e) {
            return url;
        }
    }

    public static BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(App.INSTANCE.getResources(), id, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}
