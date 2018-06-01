package com.dtapps.sugarcop.common;

import com.dtapps.sugarcop.R;

import java.math.BigDecimal;

/**
 * Created by jon.durao on 1/29/2018.
 */

public class CommonData {
    private static final String FOOD_BUNDLE_KEY = "FoodBundle";
    private static final int LIST_RECENT_LIMIT = 20;
    private static final String ADD_ID = "ca-app-pub-1585893575818828~2897881882";
    private static final String EXTRA_CALLER = "Caller";
    private static final String SQL_LIKE = " LIKE ? ";

    private static final BigDecimal CATEGORY_0_Q = BigDecimal.valueOf(15.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_1_Q = BigDecimal.valueOf(130.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_2_Q = BigDecimal.valueOf(28.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_3_Q = BigDecimal.valueOf(250.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_4_Q = BigDecimal.valueOf(85.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_5_Q = BigDecimal.valueOf(40.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_6_Q = BigDecimal.valueOf(300.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_7_Q = BigDecimal.valueOf(60.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_8_Q = BigDecimal.valueOf(30.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_9_Q = BigDecimal.valueOf(25.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_10_Q = BigDecimal.valueOf(90.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_11_Q = BigDecimal.valueOf(28.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_12_Q = BigDecimal.valueOf(10.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_13_Q = BigDecimal.valueOf(60.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_14_Q = BigDecimal.valueOf(80.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_15_Q = BigDecimal.valueOf(125.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_16_Q = BigDecimal.valueOf(20.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_17_Q = BigDecimal.valueOf(50.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_18_Q = BigDecimal.valueOf(70.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_19_Q = BigDecimal.valueOf(125.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_20_Q = BigDecimal.valueOf(180.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_21_Q = BigDecimal.valueOf(5.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_22_Q = BigDecimal.valueOf(25.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);
    private static final BigDecimal CATEGORY_23_Q = BigDecimal.valueOf(300.0).setScale(1,BigDecimal.ROUND_HALF_EVEN);

    private static final BigDecimal[] CATEGORYARRAY = {CATEGORY_0_Q, CATEGORY_1_Q, CATEGORY_2_Q,
            CATEGORY_3_Q, CATEGORY_4_Q, CATEGORY_5_Q, CATEGORY_6_Q, CATEGORY_7_Q, CATEGORY_8_Q,
            CATEGORY_9_Q, CATEGORY_10_Q, CATEGORY_11_Q, CATEGORY_12_Q, CATEGORY_13_Q, CATEGORY_14_Q,
            CATEGORY_15_Q, CATEGORY_16_Q, CATEGORY_17_Q, CATEGORY_18_Q, CATEGORY_19_Q, CATEGORY_20_Q,
            CATEGORY_21_Q, CATEGORY_22_Q, CATEGORY_23_Q,
    };

    private static final int ADD_COLOR = R.color.colorAccentDark;
    private static final int ALL_COLOR = R.color.colorPrimaryExtraDark;
    private static final int DETAILS_COLOR = R.color.colorPrimary;
    private static final int HISTORY_COLOR = R.color.colorPrimary;
    private static final int INTRO_1_COLOR = R.color.bg_slider_screen1;
    private static final int INTRO_2_COLOR = R.color.bg_slider_screen2;
    private static final int INTRO_3_COLOR = R.color.bg_slider_screen3;
    private static final int MAIN_COLOR = R.color.colorPrimary;
    private static final int SETTINGS_COLOR = R.color.colorAccentExtraDark;

    public static BigDecimal[] getCategoryArray() {return CATEGORYARRAY;}

    public static int getAddColor() {return ADD_COLOR;}

    public static int getAllColor() {return ALL_COLOR;}

    public static int getDetailsColor() {return DETAILS_COLOR;}

    public static int getHistoryColor() {return HISTORY_COLOR;}

    public static int getMainColor() {return MAIN_COLOR;}

    public static int getSettingsColor() {return SETTINGS_COLOR;}

    public static String getAddId() {return ADD_ID;}

    public static String getFoodBundleKey() {return FOOD_BUNDLE_KEY;}

    public static int getListRecentLimit() {return LIST_RECENT_LIMIT;}

    public static String getExtraCaller() {return EXTRA_CALLER;}

    public static String getSqlLike() {return SQL_LIKE;}
}
