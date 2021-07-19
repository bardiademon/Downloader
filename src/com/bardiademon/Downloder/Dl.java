package com.bardiademon.Downloder;

import com.bardiademon.Downloder.Download.Download;

@bardiademon
public final record Dl(String[] args)
{
    private static final String ARG_LINK = "-l";
    private static final String ARG_LOCATION_SAVE = "-p";
    private static final String ARG_MK_DIR = "-m";
    private static final String ARG_LAST_LOC_IS_FILE = "-f";
    private static final String ARG_HELP = "-h";
    private static final String ARG_DOWNLOAD_QUESTION = "-q";
    private static final String ARG_MANUALLY_ENTER_THE_ADDRESS = "-ma";

    public Dl (final String[] args)
    {
        this.args = args;

        String link = null;
        String locationSave = null;
        boolean mkDir = false;
        boolean lastLocIsFile = false;
        boolean downloadQuestion = false;
        boolean manuallyEnterTheAddress = false;

        if (args.length > 0)
        {
            if (findSingle (ARG_HELP))
            {
                printHelp ();
                System.exit (0);
            }

            link = getArg (find (ARG_LINK));
            locationSave = getArg (find (ARG_LOCATION_SAVE));

            mkDir = findSingle (ARG_MK_DIR);
            lastLocIsFile = findSingle (ARG_LAST_LOC_IS_FILE);
            downloadQuestion = findSingle (ARG_DOWNLOAD_QUESTION);
            manuallyEnterTheAddress = findSingle (ARG_MANUALLY_ENTER_THE_ADDRESS);
        }

        // lastLocIsFile => yani agar file bedone pasvand bod ono dir dar nazar nagire
        new Download (link , locationSave , mkDir , lastLocIsFile , downloadQuestion , manuallyEnterTheAddress);
    }

    @bardiademon
    public static void main (final String[] args)
    {
        new About ();
        new Dl (args);
    }

    private String getArg (final int index)
    {
        if (index >= 0)
        {
            try
            {
                return args[index];
            }
            catch (final Exception ignored)
            {
            }
        }

        return null;
    }

    private int find (final String what)
    {
        for (int i = 0, len = args.length; i < len; i++)
        {
            try
            {
                if (args[i].equals (what)) return (i + 1);
            }
            catch (final Exception ignored)
            {
            }
        }
        return -1;
    }

    private boolean findSingle (final String what)
    {
        for (final String arg : args)
        {
            try
            {
                if (arg.equals (what)) return true;
            }
            catch (final Exception ignored)
            {
            }
        }

        return false;
    }

    private void printHelp ()
    {
        System.out.printf ("""

                        %s -> Get the download link , Sample ( java -jar download.jar %s "LINK" )

                        %s -> File saving path , Sample ( java -jar download.jar %s "PATH" )

                        %s -> If there is no path, create the path , Sample ( java -jar download.jar %s )

                        %s -> If the file has no extension, So it is not a folder , Sample ( java -jar download.jar %s )

                        %s -> If it is incorrect, the download will start without any questions , Sample ( java -jar download.jar %s )

                        %s -> If it is false, the file chooser window will open , Sample ( java -jar download.jar %s )
                                                
                        """ ,
                ARG_LINK , ARG_LINK ,
                ARG_LOCATION_SAVE , ARG_LOCATION_SAVE ,
                ARG_MK_DIR , ARG_MK_DIR ,
                ARG_LAST_LOC_IS_FILE , ARG_LAST_LOC_IS_FILE ,
                ARG_DOWNLOAD_QUESTION , ARG_DOWNLOAD_QUESTION ,
                ARG_MANUALLY_ENTER_THE_ADDRESS , ARG_MANUALLY_ENTER_THE_ADDRESS
        );
    }
}
