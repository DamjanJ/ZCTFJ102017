package com.funny.bjokes.utils;

import com.funny.bjokes.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CJH on 2016.01.14.
 */
public class ImageUtils {

    public static List<Item> ImgFromJson(final String s) {
        ArrayList<Item> array = new ArrayList<Item>();
        try {
            JSONArray result = new JSONArray(s);
            for (int i=0; i<result.length(); i++) {
                JSONObject obj = result.getJSONObject(i);
                Item item = new Item();
                item.id = Utils.checkNull(obj.getString(Constants.API_FIELD__ID), 0);
                item.rv = Utils.checkNull(obj.getString(Constants.API_FIELD__RV), 0);
                item.url = Utils.checkNull(obj.getString(Constants.API_FIELD__URL));
                array.add(item);
            }
        }catch (JSONException ee) {
            ee.printStackTrace();
        }

        return array;
    }

    public static String ImgToJson(final List<Item> list) {
        try{
            JSONArray result = new JSONArray();
            for (Item item : list) {
                JSONObject obj = new JSONObject();
                obj.put(Constants.API_FIELD__ID, item.id);
                obj.put(Constants.API_FIELD__RV, item.rv);
                obj.put(Constants.API_FIELD__URL, item.url);
                result.put(obj);
            }

            return result.toString();
        }catch (Exception ee) {
            ee.printStackTrace();
        }

        return "";
    }

    public static void copyFile(final File file, final File file2) throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        final FileOutputStream fileOutputStream = new FileOutputStream(file2);
        final byte[] array = new byte[1024];
        while (true) {
            final int read = fileInputStream.read(array);
            if (read <= 0) {
                break;
            }
            fileOutputStream.write(array, 0, read);
        }
        fileInputStream.close();
        fileOutputStream.close();
    }

    public static boolean getRandomBoolean(int paramInt) {
        return Math.random() < 1 / paramInt;
    }

    public static class Item {
        public String desc;
        public int id;
        public int rv;
        public String url;

        public Item(){
            id = 0;
            rv = 0;
            url = "";
            desc = "";
        }
    }

}
