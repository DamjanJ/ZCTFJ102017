package com.funny.bjokes;

/**
 * Created by CJH on 2016.01.14.
 */
public class Constants {

    public static final String API_URL__FEED = "http://simp.iokilwa.com/api/v1/quotes?";
    public static final String API_URL__RATING = "http://simp.iokilwa.com/api/v1/<rating>?";
    public static final String API_URL__ADMOB = "http://simp.iokilwa.com/api/v1/admob";
    public static final String API_URL__PUSH = "http://simp.iokilwa.com/api/v1/push/register";
    public static final String API_URL__BASEPATH = "http://simp.iokilwa.com/resource/upload/";
    public static final String API_URL__PROMOTION = "http://simp.iokilwa.com/api/v1/promo";

    /*
    Test possibly
    public static final String API_URL__PROMOTION = "http://simp.iokilwa.com/api/v1/app_icon";
    */
    /*
    Edit by Damjan 13.10.2017. API change

    public static final String API_URL__FEED = "http://bro.irobot.tn/api/v1/quotes?";
    public static final String API_URL__RATING = "http://bro.irobot.tn/api/v1/<rating>?";
    public static final String API_URL__ADMOB = "http://bro.irobot.tn/api/v1/admob";
    public static final String API_URL__PUSH = "http://bro.irobot.tn/api/v1/push/register";
    public static final String API_URL__BASEPATH = "http://bro.irobot.tn/resource/upload/";
    public static final String API_URL__PROMOTION = "http://bro.irobot.tn/api/v1/app_icon";
    */


    /*
    public static final String API_URL__FEED = "http://funny.alexisavila.com/api/v1/quotes?";
    public static final String API_URL__RATING = "http://funny.alexisavila.com/api/v1/<rating>?";
    public static final String API_URL__ADMOB = "http://funny.alexisavila.com/api/v1/admob";
    public static final String API_URL__PUSH = "http://funny.alexisavila.com/api/v1/push/register";
    public static final String API_URL__BASEPATH = "http://funny.alexisavila.com/resource/upload/";
    public static final String API_URL__PROMOTION = "http://jokes-davidwilson1582.c9users.io/api/v1/app_icon";
    */

    public static final int PAGE_SIZE = 21;

    public static final String API_FIELD__ID = "id";
    public static final String API_FIELD__RV = "rv";
    public static final String API_FIELD__URL = "url";
    public static final String API_FIELD_PRO_ICON = "app_icon";
    public static final String API_FIELD_PRO_URL = "app_link";
    public static final String API_FIELD_DESC = "desc";

    public static final String BUNDLE_KEY__NEW_TAB = "new";

    public static final int TAB_COUNT = 4;

    public static final String BUNDLE_KEY__POSITION = "pos";
    public static final String BUNDLE_KEY__PAGE = "page";
    public static final String BUNDLE_KEY__IMAGE_LIST = "jsonImg";
    public static final String BUNDLE_KEY__TAB_INDEX = "tabnum";

    public static final String PREFERENCE_KEY = "FunnyQuote";
    public static final String PREFERENCE_KEY__TAB_SELECTED = "tab_selected";
    public static final String PREFERENCE_KEY__DEVICE_ID = "DeviceID";
    public static final String PREFERENCE_KEY__NEW_FEED = "new_feed";
    public static final long ANIM_ADS_DELAY = 60 * 1000; // 1 min delay

    public static boolean HOT_REFRESH = false;
    public static boolean[] NEED_REFRESH = new boolean[TAB_COUNT];

    public static void initRefresh() {
        HOT_REFRESH = false;
        for (int i = 0; i < TAB_COUNT; i++)
            NEED_REFRESH[i] = false;
    }

    public static int PAGE_VIEWED = 1;

    public static boolean ADS_FIRST_TIME = false;

    public static boolean GOOGLE_ADS = false;
    public static int ADS_LIMIT_REVIEW__GOOGLE = 6;

    public static boolean ADCOLONY_ADS = false;
    public static int ADS_LIMIT_REVIEW__ADCOLONY = 6;
    public static String ADCOLONY_ADS__APP_ID = "appcc8a61ffe1b543fe8e";
    public static String ADCOLONY_ADS__ZONE_ID = "vz173954b1d5564dc6a3";
//    public static String ADCOLONY_ADS__ZONE_ID = "vz84b74e96b60e41448b";

    public static final int SHARE_REQUEST_CODE = 1;
    public static final int DOWNLOAD_REQUEST_CODE = 2;

    public static final long DELAY_CLOSE_OPTION = 3000; // 2s
    public static final String FCM_TOPIC = "ImagesUpdated"; // topic that all users are subscribed to
}
