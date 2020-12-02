package com.foodies.hangrymatesrider.Constants;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Dinosoftlabs on 12/12/2017.
 */

public class AllConstants {

    public static int width=0;
    public static int height=0;

    public static String verdana = "verdana.ttf";
    public static String arial = "arial.ttf";

    public static final String CALCULATION = "CalculationAndroid";
    public static final String TRACKING = "tracking";

    public static final String FIREBASE_TOKEN = "firebaseTocken";

    public static String textTypeFaceBold = "fonts/OpenSans-Bold.ttf";
    public static String textTypeFaceSemiBold = "fonts/OpenSans-SemiBold.ttf";

    public static SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);

    public static SimpleDateFormat df2 =
            new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);


    public static String tag="rider";
}
