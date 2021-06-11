package com.bardiademon.Downloder.Download;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.bardiademon.Downloder.About;
import com.bardiademon.Downloder.ConvertTime;
import com.bardiademon.Downloder.GetSize;
import com.bardiademon.Downloder.StringSpeedDownload;
import com.bardiademon.Downloder.bardiademon;
import org.apache.commons.io.FilenameUtils;

@bardiademon
public final class Download extends Thread
{
    // FIEC => File Is Exists Command
    public static final int FIEC_RESUME = 1, FIEC_DELETE = 2, FIEC_RENAME = 3, FIEC_CANCEL = 4;

    private static final int PROGRESS_SHOW = 5; // Progress / PROGRESS_SHOW;
    private static final String VIEW_PROGRESS = "█";
    private static final String START_SHOW_PROGRESS = "[";
    private static final String END_SHOW_PROGRESS = "]";
    private BufferedReader readerLink;
    private int sec, min, h;
    private boolean download;

    private String argLink, argLocationSave;
    private boolean argMkDir;
    private boolean argLastLocIsFile;
    private final boolean downloadQuestion;
    private final boolean manuallyEnterTheAddress;

    private String link;

    private Timer timer;

    private File fileSave;
    private long downloadedSize = 0;
    private boolean pause = false;
    private boolean stopDownload = false;
    private boolean forPause = false;
    private FileOutputStream fileOutputStream;
    private InputStream inputStream;
    private HttpURLConnection connection;

    private final On on;
    private final OnInfoLink onInfoLink;

    private final boolean getInfo;

    private final boolean printable;

    private static final String EXIT = ":exit";

    private boolean run = true;

    private boolean compulsoryStop = false;

    public Download (final String Link , final String LocationSave , final boolean MkDir , final boolean LastLocIsFile , final boolean DownloadQuestion , final boolean ManuallyEnterTheAddress)
    {
        this (Link , LocationSave , MkDir , LastLocIsFile , DownloadQuestion , ManuallyEnterTheAddress , null);
    }

    public Download (final String Link , final String LocationSave , final boolean MkDir , final boolean LastLocIsFile , final boolean DownloadQuestion , final boolean ManuallyEnterTheAddress , final On _On)
    {
        this (Link , LocationSave , MkDir , LastLocIsFile , DownloadQuestion , ManuallyEnterTheAddress , _On , null);
    }

    public Download (final String Link , final OnInfoLink _On)
    {
        this (Link , null , false , false , true , true , null , _On);
    }

    public Download (final String Link , final String LocationSave , final boolean MkDir , final boolean LastLocIsFile , final boolean DownloadQuestion , final boolean ManuallyEnterTheAddress , final On _On , final OnInfoLink _OnInfoLink)
    {
        new About ();
        this.argLink = Link;
        this.argLocationSave = LocationSave;
        this.argMkDir = MkDir;
        this.argLastLocIsFile = LastLocIsFile;
        this.downloadQuestion = DownloadQuestion;
        this.manuallyEnterTheAddress = ManuallyEnterTheAddress;
        this.onInfoLink = _OnInfoLink;
        this.on = _On;
        this.printable = (this.on == null && this.onInfoLink == null);
        this.getInfo = (this.on == null && this.onInfoLink != null);
        start ();
    }

    @Override
    public void run ()
    {
        runClass ();
    }

    private void runClass ()
    {
        stopDownload = false;
        pause = false;

        do
        {
            try
            {
                String link = getLink ();
                if (link.equals ("exit"))
                    break;
                else if (link.equals ("list"))
                    listDownload ();
                else
                {
                    boolean okArgLocationSave;
                    if ((okArgLocationSave = isOkArgLocationSave ()))
                        print ("File saving path: " + argLocationSave + "\n");
                    download = true;
                    time ();

                    if (okArgLocationSave) download (link , true , new File (argLocationSave));
                    else download (link , true , null);

                    download = false;
                    print (" " + getTime () + " | " + okTime () + "\n\n");
                }
            }
            catch (final Exception e)
            {
                if (!printable)
                {
                    if (getInfo) onInfoLink.OnError (e);
                    else on.OnError (e);

                    if (e.getMessage () != null && e.getMessage ().toLowerCase (Locale.ROOT).contains ("cancel download"))
                        on.OnCancelDownload ();
                }
                print ("\nDownload error => " + e.getMessage () + "\n");
            }
        } while (printable && run);
    }

    private String getLink () throws IOException
    {
        print ("Link: ");
        if (isOkArgLink ())
        {
            print (argLink + "\n");
            return argLink;
        }
        else
        {
            if (printable)
            {
                readerLink = new BufferedReader (new InputStreamReader (System.in));
                return readerLink.readLine ();
            }
            else
            {
                if (getInfo) return onInfoLink.OnEnterLink ();
                else return on.OnEnterLink ();
            }
        }
    }

    private boolean isOkArgLink ()
    {
        return (argLink != null && !argLink.isEmpty ());
    }

    private boolean isOkArgLocationSave ()
    {
        if (argLocationSave != null && !argLocationSave.isEmpty ())
            return (pathValidation (new File (argLocationSave) , "") != null);

        return false;
    }

    private File pathValidation (final File file , final String nameTypeFile)
    {
        final boolean enterFilename = !(FilenameUtils.getExtension (file.getName ())).isEmpty ();

        if ((enterFilename && file.getParentFile ().exists ()) || (!enterFilename && file.exists ()))
        {
            if (enterFilename) return file;
            else return (new File (file.getPath () + File.separator + nameTypeFile));
        }
        else
        {
            if (!enterFilename && argLastLocIsFile && file.getParentFile ().exists ()) return file;

                /*
                 * (argMkDir && ((enterFilename && file.getParentFile ().mkdirs ()) || (!enterFilename && ((argLastLocIsFile && file.getParentFile ().mkdirs ()) || (!argLastLocIsFile && file.mkdirs ())))) && file.exists ())
                 * in if => agar if bala nabod inja aval mige agar gofte bod karbar dir ro besaz , besazesh ama =>
                 *
                 * aval agar toye path vared shode .type bod parent file ro begir besaz agar in nashod va .type nabod va akharin path file bode yani .type nadare in file parent ro begir dir ro besaz
                 * agar file nist ke adi besaz yani parent ro nemikhad begiri
                 */
            else if (argMkDir && ((enterFilename && file.getParentFile ().mkdirs ()) || (!enterFilename && ((argLastLocIsFile && file.getParentFile ().mkdirs ()) || (!argLastLocIsFile && file.mkdirs ())))))
            {
                if (enterFilename || argLastLocIsFile) return file;
                else return (new File (file.getPath () + File.separator + nameTypeFile));
            }

            else
            {
                if (!printable)
                    on.OnErrorPath (new Exception ("Invalid path") , file , nameTypeFile);

                print ("Invalid path!\n");
            }
        }

        return null;
    }

    public void compulsoryStop ()
    {
        stopDownload = true;
        run = false;
        compulsoryStop = true;
        try
        {
            closeStream ();
        }
        catch (final IOException e)
        {
            if (!printable && on != null) on.OnCompulsoryStopCloseStreamError (e);
        }
        close ();

        if (!printable && on != null) on.OnCompulsoryStop ();
    }

    @bardiademon
    private void listDownload () throws Exception
    {
        final List <String> links = new ArrayList <> ();
        String link;
        int counter = 1;
        print ("Link " + (counter++) + ": ");
        while (!(link = readerLink.readLine ()).equals ("flush"))
        {
            links.add (link);
            print ("Link " + (counter++) + ": ");
        }
        List <String> listName = new ArrayList <> ();
        print ("\n");
        long size = 0;
        for (final String oneLink : links)
        {
            final URL url = new URL (oneLink);
            String name = FilenameUtils.getName (url.getPath ());
            listName.add (name);
            print (name + ": " + GetSize.Get ((size = Long.parseLong (url.openConnection ().getHeaderField ("Content-length")))) + "\n");
        }
        print ("\n");
        print ("Start Download " + links.size () + " Files (y/n): ");
        if ((printable && readerLink.readLine ().equalsIgnoreCase ("y")) || on.OnConnectedList (size))
        {
            final File fileSave = getLocation (null , true);
            if (fileSave != null)
            {
                print ("\n");
                String oneLink, name;
                for (int i = 0; i < links.size (); i++)
                {
                    oneLink = links.get (i);
                    name = listName.get (i);
                    print ("=================================\n");
                    print ("Start download " + name + " || " + getTime () + "\n");

                    download = true;
                    time ();

                    download (oneLink , false , new File (fileSave.getPath () + File.separator + name));
                    print (" " + getTime () + " | " + okTime () + "\n");
                    print ("=================================\n\n");

                    download = false;
                }
                Desktop.getDesktop ().open (fileSave.getParentFile ());
                runClass ();
            }
            else
                throw new IOException ("Cancel download!");
        }
        else throw new IOException ("Error select path save file");
    }

    @bardiademon
    private String getTime ()
    {
        ConvertTime convertTime = new ConvertTime (String.valueOf (System.currentTimeMillis () / 1000));
        return String.format ("%s:%s:%s" , convertTime.hour24 () , convertTime.minutes () , convertTime.second ());
    }

    @bardiademon
    private String progress (final int progress)
    {
        int progressForShow = (progress / PROGRESS_SHOW);
        StringBuilder finalProgress = new StringBuilder ();
        finalProgress.append (START_SHOW_PROGRESS);
        finalProgress.append (VIEW_PROGRESS.repeat (Math.max (0 , progressForShow)));
        int spaceToFinal = Math.abs ((100 / PROGRESS_SHOW) - progressForShow);
        finalProgress.append (" ".repeat (Math.max (0 , spaceToFinal - 1)));
        finalProgress.append (END_SHOW_PROGRESS);
        finalProgress.append (String.format (" %d%%" , progress));
        return finalProgress.toString ();
    }

    @bardiademon
    private void time ()
    {
        sec = 0;
        min = 0;
        h = 0;
        (timer = new Timer ()).schedule (new TimerTask ()
        {
            @Override
            public void run ()
            {
                sec++;
                if (sec > 59)
                {
                    min++;
                    sec = 0;
                }
                if (min > 59)
                {
                    h++;
                    min = 0;
                    sec = 0;
                }

                if (!download) timer.cancel ();
            }
        } , 1000 , 1000);
    }

    @bardiademon
    private String okTime ()
    {
        return String.format ("%s:%s:%s" , zeroPlus (h) , zeroPlus (min) , zeroPlus (sec));
    }

    private String zeroPlus (final int num)
    {
        return ((num < 10) ? "0" + num : String.valueOf (num));
    }

    @bardiademon
    private void download (final String link , final boolean question , File fileSave) throws Exception
    {
        this.link = link;

        print ("Connecting...");

        final URL url = new URL (link);

        connection = (HttpURLConnection) url.openConnection ();

        if (downloadedSize > 0)
        {
            print (String.format ("\rContinue downloading from: %s\n" , GetSize.Get (downloadedSize)));
            connection.setRequestProperty ("Range" , "bytes=" + downloadedSize + "-");
        }

        connection.connect ();

        long filesize = Long.parseLong (connection.getHeaderField ("Content-length"));

        if (downloadedSize > 0) filesize += downloadedSize;

        print (String.format ("\rFile size : %s\n" , GetSize.Get (filesize)));

        boolean download;

        if (!downloadQuestion && question && (downloadedSize == 0))
        {
            print ("Download this file (y,n)? ");
            download = (printable && (new BufferedReader (new InputStreamReader (System.in))).readLine ().equalsIgnoreCase ("y")) || ((getInfo && onInfoLink.OnConnected (filesize , fileSave)) || on.OnConnected (filesize , fileSave));
        }
        else
            download = (printable || ((getInfo && onInfoLink.OnConnected (filesize , fileSave)) || on.OnConnected (filesize , fileSave)));

        if (download)
        {
            String filename = FilenameUtils.getName (link);

            if (FilenameUtils.getExtension (filename).isEmpty ())
            {
                final String contentDisposition = connection.getHeaderField ("Content-Disposition");
                if (contentDisposition != null)
                {
                    final String filenameEquals = "filename=";
                    filename = contentDisposition.substring (contentDisposition.indexOf (filenameEquals) + filenameEquals.length ());
                    if (filename.isEmpty ()) filename = FilenameUtils.getName (link);
                }

            }

            print ("Filename: " + filename + "\n");

            if (!printable)
            {
                if (getInfo) onInfoLink.OnFilename (filename);
                else on.OnFilename (filename);
            }

            if (getInfo) return;

            // agar download resume nashode bashe
            if (downloadedSize == 0)
            {
                if (fileSave == null)
                {
                    if (!manuallyEnterTheAddress) print ("File saving path: ");
                    fileSave = getLocation (filename , false);
                }
                else if (fileSave.isDirectory ())
                    fileSave = new File (fileSave + File.separator + filename);
            }
            else print ("File saving path: " + ((fileSave == null) ? this.fileSave : fileSave));

            if (fileSave != null)
            {
                // barasi in ke file vojod dare ya na , agar dare download na tamom ast ya na

                if (fileSave.exists () && downloadedSize == 0)
                {
                    final boolean fullNotDownloaded = (filesize > fileSave.length ());
                    print (String.format ("\nThis file<%s> is exists.\n" , filename));

                    if (fullNotDownloaded)
                    {
                        print ("Full not downloaded\n");
                        print ("1.Resume\n");
                    }

                    print ("2.Delete previous file and download again\n");
                    print ("3.Rename download file\n");
                    print ("4.Cancel\n");

                    final BufferedReader numRead = new BufferedReader (new InputStreamReader (System.in));

                    boolean breakWhile = false;
                    while (!breakWhile)
                    {
                        print ("Enter number: ");
                        final String strNum = (printable) ? numRead.readLine () : String.valueOf (on.OnExistsFile (fullNotDownloaded));

                        int num;
                        try
                        {
                            num = Integer.parseInt (strNum);
                        }
                        catch (final Exception e)
                        {
                            print ("Please enter just a number!");
                            continue;
                        }

                        switch (num)
                        {
                            case 1:
                                if (fullNotDownloaded)
                                {
                                    downloadedSize = fileSave.length ();
                                    download (link , question , fileSave);
                                    return;
                                }
                                else
                                {
                                    print ("Error number!");
                                    break;
                                }
                            case 2:
                                if (fileSave.delete ())
                                {
                                    print ("File deleted!");
                                    breakWhile = true;
                                    break;
                                }
                                else
                                {
                                    print ("Delete file error!");
                                    return;
                                }
                            case 3:
                                fileSave = new File (fileSave.getParent () + File.separator + getNewFilename () + "." + FilenameUtils.getExtension (filename));
                                download (link , false , fileSave);
                                return;
                            case 4:
                                print ("Cancel download.");
                                return;
                        }
                    }
                }

                this.fileSave = fileSave;

                inputStream = connection.getInputStream ();

                final byte[] buffer = new byte[5120];

                if (downloadedSize == 0 || fileOutputStream == null)
                    fileOutputStream = new FileOutputStream (fileSave , (downloadedSize > 0));

                int len;
                long lenInSec = 0;
                float min;
                int max = 0;
                long startRead, endRead;
                double second, secTemp = 0;
                String strSpeedDownload = "0 KB/s";
                String downloaded;
                String progress;

                forPause ();
                while (!stopDownload && run && !compulsoryStop)
                {
                    if (!pause)
                    {
                        try
                        {
                            startRead = System.nanoTime ();

                            len = inputStream.read (buffer);

                            if (len <= 0)
                            {
                                stopDownload = true;
                                pause = true;
                                break;
                            }

                            endRead = System.nanoTime ();

                            second = (endRead - startRead) / 1_000_000_000.0;

                            secTemp += second;
                            fileOutputStream.write (buffer , 0 , len);

                            downloadedSize += len;
                            lenInSec += len;

                            min = Math.abs (((float) downloadedSize / filesize) * 100);

                            if ((int) min > max)
                                max = (int) min;

                            if (secTemp >= 1)
                            {
                                strSpeedDownload = StringSpeedDownload.Get (lenInSec);
                                lenInSec = 0;
                                secTemp = 0;
                            }

                            downloaded = GetSize.Get (downloadedSize);
                            progress = progress (max);

                            print (String.format ("\r %s || %s || %s || %s " , progress , downloaded , strSpeedDownload , okTime ()));

                            if (!printable)
                                on.OnDownloading (max , progress , downloaded , downloadedSize , strSpeedDownload , okTime ());
                        }
                        catch (final Exception e)
                        {
                            on.OnErrorDownloading (e);
                            print ("\rDownload Error <" + e.getMessage () + ">\n");
                            pause ();
                        }
                    }
                }

                if (compulsoryStop) return;

                pause = false;
                print ("\n");
                closeStream ();

                print ("\nDownload complete.\n");
                if (printable) Desktop.getDesktop ().open (fileSave.getParentFile ());
                else on.OnDownloaded (fileSave);

                argLastLocIsFile = false;
                argLocationSave = null;
                argMkDir = false;
                argLink = null;

                System.gc ();
                if (printable) runClass ();

            }
            else
                throw new IOException ("Error select path save file");
        }
        else
        {
            if (!printable) on.OnCancelDownload ();

            print ("Cancel download.\n");
            close ();
        }
    }

    private String getNewFilename ()
    {
        print ("\nExit = " + EXIT + "\n");
        while (true)
        {
            try
            {
                print ("\nEnter new name: ");
                final String name = (printable) ? (new BufferedReader (new InputStreamReader (System.in))).readLine () : on.OnRenameFileExists (fileSave.getName ());
                if (name != null && !name.isEmpty ())
                {
                    if (name.equals (EXIT)) System.exit (0);

                    if (name.matches ("[-_.A-Za-z0-9]*")) return name;
                    else throw new IOException ("Invalid name!");
                }
                else throw new IOException ("Name is empty!");
            }
            catch (final IOException e)
            {
                if (!printable) on.OnErrorRenameFileExists (e);
                print ("Error enter name <" + e.getMessage () + ">");
            }
        }
    }

    private void closeStream () throws IOException
    {
        if (fileOutputStream != null)
        {
            fileOutputStream.flush ();
            fileOutputStream.close ();
        }
        if (inputStream != null) inputStream.close ();
        if (connection != null) connection.disconnect ();
    }

    private void forPause ()
    {
        forPause = true;
        print ("\nPress Enter to pause the download\n");
        new Thread (() ->
        {
            final BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));
            while (run)
            {
                if (forPause)
                {
                    try
                    {
                        reader.readLine ();
                        if (forPause) pause ();
                    }
                    catch (final IOException ignored)
                    {
                    }
                }
            }
        }).start ();
    }

    private void pause ()
    {
        forPause = false;
        new Thread (() ->
        {
            final BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));
            print ("\nDownload is pause, continue ? (y,n): ");
            pause = true;
            try
            {
                final String continueDownload;

                if (printable) continueDownload = reader.readLine ();
                else continueDownload = (on.OnPause () ? "y" : "n");

                if (continueDownload != null && continueDownload.toLowerCase (Locale.ROOT).equals ("y"))
                    pause = false;
                else
                    stopDownload = true;


                synchronized (Download.this)
                {
                    Download.this.notify ();
                    Download.this.notifyAll ();
                }
            }
            catch (final IOException e)
            {
                System.out.println (e.getMessage ());
            }
        }).start ();

        synchronized (Download.this)
        {
            try
            {
                Download.this.wait ();
            }
            catch (InterruptedException e)
            {
                on.OnErrorPause (e , false);
            }
        }

        if (!stopDownload && !pause && run)
        {
            try
            {
                download (link , true , fileSave);
            }
            catch (final Exception e)
            {
                on.OnErrorPause (e , true);
                pause ();
            }
        }
        else close ();
    }

    @bardiademon
    private File getLocation (final String nameTypeFile , final boolean justDir)
    {
        if (isOkArgLocationSave ()) return (new File (argLocationSave));
        else if (manuallyEnterTheAddress)
        {
            print ("\nExit = " + EXIT + "\n");
            while (true)
            {
                try
                {
                    print ("Enter path <" + nameTypeFile + "> " + ((justDir) ? "Enter dir " : "") + ": ");
                    final String path = (printable) ? (new BufferedReader (new InputStreamReader (System.in))).readLine () : (on.OnEnterPath (nameTypeFile , justDir));
                    if (path != null && !path.isEmpty ())
                    {
                        if (path.equals (EXIT))
                        {
                            if (printable) System.exit (0);
                            else return null;
                        }

                        else
                        {
                            final File file = pathValidation (new File (path) , nameTypeFile);
                            if (file != null) return file;
                        }
                    }
                }
                catch (final IOException e)
                {
                    print ("Error reader <" + e.getMessage () + ">");

                    //  assert printable == true;
                    System.exit (0);
                }
            }
        }
        else
        {
            if (printable)
            {
                final JFileChooser chooser = new JFileChooser ();

                // justDir , For Download List
                if (justDir)
                    chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
                else
                    chooser.setSelectedFile (new File (nameTypeFile));

                final AtomicInteger openDialogResult = new AtomicInteger ();
                SwingUtilities.invokeLater (() ->
                {
                    openDialogResult.set (chooser.showSaveDialog (null));
                    synchronized (Download.this)
                    {
                        Download.this.notify ();
                        Download.this.notifyAll ();
                    }
                });

                synchronized (Download.this)
                {
                    try
                    {
                        Download.this.wait ();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace ();
                    }
                }

                File fileSave;
                if (openDialogResult.get () == JFileChooser.OPEN_DIALOG && (fileSave = chooser.getSelectedFile ()) != null && fileSave.getParentFile () != null)
                {
                    print (fileSave.getPath () + "\n");
                    return fileSave;
                }
                return null;
            }
            else return new File (on.OnEnterPath (nameTypeFile , justDir));

        }
    }

    private void close ()
    {
        System.gc ();
        if (printable) System.exit (0);
    }

    private void print (String str)
    {
        if (!pause && printable) System.out.print (str);
    }

}
