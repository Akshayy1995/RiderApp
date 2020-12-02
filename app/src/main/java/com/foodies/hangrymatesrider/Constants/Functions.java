package com.foodies.hangrymatesrider.Constants;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.foodies.hangrymatesrider.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by AQEEL on 3/26/2019.
 */

public class Functions {

    public static void Hide_keyboard(Activity activity){

        InputMethodManager imm = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static int dpToPx(final Activity context, final float dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density);
    }


    public static Dialog dialog;
    public static void Show_loader(Context context, boolean outside_touch, boolean cancleable) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_progress_dialog_layout);


        if(!outside_touch)
            dialog.setCanceledOnTouchOutside(false);

        if(!cancleable)
            dialog.setCancelable(false);

        dialog.show();

    }


    public static void cancel_loader(){
        if(dialog!=null){
            dialog.cancel();
        }
    }


    // this is the delete message diloge which will show after long press in chat message
    public static void Show_Options(Context context,CharSequence[] options,final  Callback callback) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context,R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                callback.Responce(""+options[item]);

            }

        });

        builder.show();

    }


}
