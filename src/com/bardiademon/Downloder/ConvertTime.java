package com.bardiademon.Downloder;

// Created by bardiademon on 01/03/2018_12:01.


import java.sql.Date;
import java.text.SimpleDateFormat;

public class ConvertTime
{

    private String[] dateIr;
    private Date date;

    private String weekFa;
    private String weekInt;

    @bardiademon
    public ConvertTime (String time)
    {
        date = new Date(Long.parseLong(time) * 1000L);

        int y = Integer.parseInt(new SimpleDateFormat("y").format(date));
        int m = Integer.parseInt(new SimpleDateFormat("M").format(date));
        int d = Integer.parseInt(new SimpleDateFormat("d").format(date));

        dateIr = gregorian_to_jalali(y, m, d);

        weekFa(new SimpleDateFormat("E").format(date));
    }


    public String pm_am ()
    {
        return new SimpleDateFormat("a").format(date);
    }

    public String hour12 ()
    {
        int h = Integer.parseInt(new SimpleDateFormat("h").format(date));
        return String.valueOf((h < 10) ? "0" + h : h);
    }

    @bardiademon
    public String hour24 ()
    {
        int H = Integer.parseInt(new SimpleDateFormat("H").format(date));
        return String.valueOf((H < 10) ? "0" + H : H);
    }

    @bardiademon
    public String minutes ()
    {
        int m = Integer.parseInt(new SimpleDateFormat("m").format(date));
        return String.valueOf((m < 10) ? "0" + m : m);
    }

    @bardiademon
    public String second ()
    {
        int s = Integer.parseInt(new SimpleDateFormat("s").format(date));
        return String.valueOf((s < 10) ? "0" + s : s);

    }

    @bardiademon
    public String dayInt ()
    {
        return weekInt;
    }

    @bardiademon
    public String nameDay ()
    {
        return weekFa;
    }

    @bardiademon
    public String year ()
    {
        return dateIr[0];
    }

    @bardiademon
    public String monInt ()
    {
        return dateIr[1];
    }

    @bardiademon
    public String monthName ()
    {
        return monFa(Integer.parseInt(monInt()));
    }

    @bardiademon
    public String dayOnMonth ()
    {
        return dateIr[2];
    }

    @bardiademon
    public String weekOnMonth ()
    {
        return new SimpleDateFormat("W").format(date);
    }

    @bardiademon
    public String weekOnYear ()
    {
        return new SimpleDateFormat("w").format(date);
    }

    @bardiademon
    private void weekFa (String week)
    {
        switch (week)
        {
            case "Sat":
                weekInt = "0";
                weekFa = "shanbeh";
                break;

            case "Sun":
                weekInt = "1";
                weekFa = "yek shanbeh";
                break;

            case "Mon":
                weekInt = "2";
                weekFa = "do shanbeh";
                break;

            case "Tue":
                weekInt = "3";
                weekFa = "se shanbeh";
                break;

            case "Wed":
                weekInt = "4";
                weekFa = "chahar shanbeh";
                break;

            case "Thu":
                weekInt = "5";
                weekFa = "panj shanbeh";
                break;

            case "Fri":
                weekInt = "6";
                weekFa = "jome";
                break;

            default:
                weekInt = "-1";
                weekFa = "";
                break;

        }
    }

    @bardiademon
    private String monFa (int mon)
    {
        switch (mon)
        {
            case 1:
                return "farvardin";

            case 2:
                return "ordibehesht";

            case 3:
                return "khordad";

            case 4:
                return "tir";

            case 5:
                return "mordad";

            case 6:
                return "shahrivar";

            case 7:
                return "mehr";

            case 8:
                return "aban";

            case 9:
                return "azar";

            case 10:
                return "dey";

            case 11:
                return "bahman";
            case 12:
                return "esfand";

            default:
                return "";
        }
    }

    @bardiademon
    private String[] gregorian_to_jalali (int gy, int gm, int gd)
    {
        int[] g_d_m = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
        int jy;
        if (gy > 1600)
        {
            jy = 979;
            gy -= 1600;
        }
        else
        {
            jy = 0;
            gy -= 621;
        }
        int gy2 = (gm > 2) ? (gy + 1) : gy;
        int days = (365 * gy) + ((int) ((gy2 + 3) / 4)) - ((int) ((gy2 + 99) / 100)) + ((int) ((gy2 + 399) / 400)) - 80 + gd + g_d_m[gm - 1];
        jy += 33 * ((int) (days / 12053));
        days %= 12053;
        jy += 4 * ((int) (days / 1461));
        days %= 1461;
        if (days > 365)
        {
            jy += (int) ((days - 1) / 365);
            days = (days - 1) % 365;
        }
        int jm = (days < 186) ? 1 + (int) (days / 31) : 7 + (int) ((days - 186) / 30);
        int jd = 1 + ((days < 186) ? (days % 31) : ((days - 186) % 30));

        String[] data = new String[3];
        data[0] = String.valueOf(jy);
        data[1] = String.valueOf(jm);
        data[2] = String.valueOf(jd);

        return data;
    }
}
