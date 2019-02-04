package com.codedevstudio.orders.utils;

import android.content.res.Resources;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.activities.App;
import com.codedevstudio.orders.models.Order;

import static android.provider.Settings.Global.getString;

/**
 * Created by fabius on 25/03/2018.
 */

public class StatusDisplayName {
    public static String getStatusName(Order.Status status){
        String out = "";
        Resources res =  App.getAppContext().getResources();
        switch (status) {
            case WAITFORTHEACCEPT:
                out = res.getString(R.string.status_waitforaccept);
                break;
            case INDONE:
                out = res.getString(R.string.status_indone);
                break;
            case OPEN:
                out = res.getString(R.string.status_open);
                break;
            case SUCCESFULLYDONE:
                out = res.getString(R.string.status_done);
                break;
            case EXPIRED:
                out = res.getString(R.string.status_expired);
                break;
        }
        return out;
    }
}
