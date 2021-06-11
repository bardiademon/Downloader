package com.bardiademon.Downloder;

import com.bardiademon.Downloder.Bardiademon.Default;

public final class About
{
    public About ()
    {
        print (String.format ("%s - Version %s\n" , Default.APP_NAME , Default.VERSION));
        print (String.format ("Powered by %s\n" , Default.DEVELOPER_NAME));
        print ("========================================\n");
    }

    private void print (final String str)
    {
        System.out.print (str);
    }
}
