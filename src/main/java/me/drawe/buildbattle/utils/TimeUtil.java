package me.drawe.buildbattle.utils;

import me.drawe.buildbattle.managers.GameManager;
import me.drawe.buildbattle.objects.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String formatTimeMMSS(int secs) {
        int minutes = (secs % 3600) / 60;
        int seconds = secs % 60;

        String timeString = String.format("%02d:%02d", minutes, seconds);

        if(secs > GameManager.getDefaultGameTime() / 2) {
            return "§a" + timeString;
        } else if(secs > GameManager.getDefaultGameTime() / 10) {
            return "§e" + timeString;
        } else {
            return "§c" + timeString;
        }
    }

    public static String ordinal(int i) {
        String[] sufixes = new String[]{Message.ORDINAL_TH.getMessage(), Message.ORDINAL_ST.getMessage(), Message.ORDINAL_ND.getMessage(), Message.ORDINAL_RD.getMessage(), Message.ORDINAL_TH.getMessage(), Message.ORDINAL_TH.getMessage(), Message.ORDINAL_TH.getMessage(), Message.ORDINAL_TH.getMessage(), Message.ORDINAL_TH.getMessage(), Message.ORDINAL_TH.getMessage()};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + Message.ORDINAL_TH.getMessage();
            default:
                return i + sufixes[i % 10];

        }
    }

    public static String getCurrentDateTime() {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(d);
    }
}