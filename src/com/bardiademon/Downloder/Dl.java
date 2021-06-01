package com.bardiademon.Downloder;

@bardiademon
public class Dl
{
    private static final int INDEX_ARG_LINK = 0, INDEX_ARG_LOCATION_SAVE = 1, INDEX_ARG_MK_DIR = 2, INDEX_ARG_LAST_LOC_IS_FILE = 3;

    @bardiademon
    public static void main (String[] args)
    {
        if (args.length > 4)
        {
            System.err.println ("Arg error!");
            return;
        }

        String link = null;
        String locationSave = null;
        String mkDirStr;
        boolean mkDir = false;

        String lastLocIsFileStr;
        boolean lastLocIsFile = false;


        if (args.length > 0)
        {
            link = args[INDEX_ARG_LINK];
            locationSave = ((args.length > 1) ? args[INDEX_ARG_LOCATION_SAVE] : null);

            if (args.length > 2 && ((mkDirStr = args[INDEX_ARG_MK_DIR]).equals ("-t") || mkDirStr.equals ("-f")))
                mkDir = (mkDirStr.equals ("-t"));


            if (args.length == 4 && ((lastLocIsFileStr = args[INDEX_ARG_LAST_LOC_IS_FILE]).equals ("-t") || lastLocIsFileStr.equals ("-f")))
                lastLocIsFile = (lastLocIsFileStr.equals ("-t"));
        }

        new Download (link , locationSave , mkDir , lastLocIsFile);
    }
}
