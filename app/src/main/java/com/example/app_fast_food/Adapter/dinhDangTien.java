package com.example.app_fast_food.Adapter;

import java.text.DecimalFormat;

public class dinhDangTien {
    public static String dinhdang(double c) {
        DecimalFormat decimalFormat = null;
        if (c >= 100000000) {
            decimalFormat = new DecimalFormat("###,###,###");
        } else if (c >= 10000000) {
            decimalFormat = new DecimalFormat("##,###,###");
        } else if (c >= 1000000) {
            decimalFormat = new DecimalFormat("#,###,###");
        } else if (c >= 100000) {
            decimalFormat = new DecimalFormat("###,###");
        } else if (c >= 10000) {
            decimalFormat = new DecimalFormat("##,###");
        } else if (c >= 1000) {
            decimalFormat = new DecimalFormat("#,###");
        }

        if (decimalFormat == null) {
            return c + "đ";
        }

        return decimalFormat.format(c) + "đ";
    }
}

