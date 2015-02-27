package com.codepath.apps.simpletwitterclient.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class TwitterHelpers {
    public static boolean checkForInternetConnectivity(Context context) {
        if (!isNetworkAvailable(context)) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Unable to connect to the internet.  Operating on offline mode.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return true;
        }
        return false;
    }

    private static Boolean isNetworkAvailable(Context context) {
        return true;
        /*
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting(); */
    }
}
