import org.apache.commons.io.FilenameUtils;

import javax.swing.JFileChooser;

import java.awt.Desktop;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Download extends Thread
{
    private static final int PROGRESS_SHOW = 5; // Progress / PROGRESS_SHOW;
    private static final String VIEW_PROGRESS = "█";
    private static final String START_SHOW_PROGRESS = "[";
    private static final String END_SHOW_PROGRESS = "]";
    private BufferedReader readerLink;
    private int sec, min, h;
    private boolean download;

    @Override
    public void run ()
    {
        while (true)
        {
            try
            {
                readerLink = new BufferedReader (new InputStreamReader (System.in));
                System.out.print ("Link: ");
                String link = readerLink.readLine ();
                if (link.equals ("exit")) break;
                else if (link.equals ("list")) listDownload ();
                else
                {
                    download = true;
                    time ();
                    download (link , true , null);
                    download = false;
                    System.out.println (" " + getTime () + " | " + okTime () + "\n");
                }
            }
            catch (IOException | InterruptedException e)
            {
                System.err.println ("\nDownload error => " + e.getMessage ());
            }
        }
    }

    private void listDownload () throws IOException, InterruptedException
    {
        List <String> links = new ArrayList <> ();
        String link;
        int counter = 1;
        System.out.print ("Link " + (counter++) + ": ");
        while (!(link = readerLink.readLine ()).equals ("flush"))
        {
            links.add (link);
            System.out.print ("Link " + (counter++) + ": ");
        }
        List <String> listName = new ArrayList <> ();
        System.out.println ();
        for (String oneLink : links)
        {
            URL url = new URL (oneLink);
            String name = FilenameUtils.getName (url.getPath ());
            listName.add (name);
            System.out.println (name + ": " + GetSize.Get (Long.parseLong (url.openConnection ().getHeaderField ("Content-length"))));
        }
        System.out.println ();
        System.out.print ("Start Download " + links.size () + " Files (y/n): ");
        if (readerLink.readLine ().toLowerCase ().equals ("y"))
        {
            File fileSave = getLocation (null , true);
            if (fileSave != null)
            {

                System.out.println ();
                String oneLink, name;
                for (int i = 0; i < links.size (); i++)
                {
                    oneLink = links.get (i);
                    name = listName.get (i);
                    System.out.println ("=================================");
                    System.out.println ("Start download " + name + " || " + getTime ());

                    download = true;
                    time ();
                    try
                    {
                        download (oneLink , false , new File (fileSave.getPath () + File.separator + name));
                        System.out.println (" " + getTime () + " | " + okTime ());
                        System.out.println ("=================================\n");
                    }
                    catch (IOException e)
                    {
                        System.out.println ("Error download " + name + " => " + e.getMessage ());
                    }
                    download = false;
                    Thread.sleep (1000);
                }
                Desktop.getDesktop ().open (fileSave.getParentFile ());
            }
            else throw new IOException ("Cancel download!");
        }
        else throw new IOException ("Error select path save file");
    }

    private String getTime ()
    {
        ConvertTime convertTime = new ConvertTime (String.valueOf (System.currentTimeMillis () / 1000));
        return String.format ("%s:%s:%s" , convertTime.hour24 () , convertTime.minutes () , convertTime.second ());
    }

    private String progress (int progress)
    {
        int progressForShow = (progress / PROGRESS_SHOW);
        StringBuilder finalProgress;
        finalProgress = new StringBuilder ();
        finalProgress.append (START_SHOW_PROGRESS);
        for (int i = 0; i < progressForShow; i++) finalProgress.append (VIEW_PROGRESS);
        int spaceToFinal = Math.abs ((100 / PROGRESS_SHOW) - progressForShow);
        for (int i = 0; i < spaceToFinal - 1; i++) finalProgress.append (" ");
        finalProgress.append (END_SHOW_PROGRESS);
        finalProgress.append (String.format (" %d%%" , progress));
        return finalProgress.toString ();
    }

    private void time ()
    {
        new Thread (() ->
        {
            sec = 0;
            min = 0;
            h = 0;
            while (true)
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
                if (!download) return;
                try
                {
                    Thread.sleep (1000);
                }
                catch (InterruptedException ignored)
                {
                }
                if (!download) return;
            }
        }).start ();
    }

    private String okTime ()
    {
        return String.format (
                "%s:%s:%s" ,
                (h < 10) ? "0" + h : h ,
                (min < 10) ? "0" + min : min ,
                (sec < 10) ? "0" + sec : sec
        );
    }

    private void download (String link , boolean question , File fileSave) throws IOException
    {
        URL url = new URL (link);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection ();
        connection.connect ();
        long sizeFile = Long.parseLong (connection.getHeaderField ("Content-length"));

        System.out.format ("Size file : %s\n" , GetSize.Get (sizeFile));

        boolean download;
        if (question)
        {
            BufferedReader readerDownloadYesNo = new BufferedReader (new InputStreamReader (System.in));
            System.out.print ("Download this file (y,n)? ");
            download = readerDownloadYesNo.readLine ().toLowerCase ().equals ("y");
        }
        else download = true;
        if (download)
        {
            String nameTypeFile = FilenameUtils.getName (url.getPath ());
            if (fileSave == null) fileSave = getLocation (nameTypeFile , false);
            if (fileSave != null)
            {
                InputStream inputStream = connection.getInputStream ();

                byte[] buffer = new byte[5120];

                FileOutputStream fileOutputStream = new FileOutputStream (fileSave);

                int lenHere = 0;
                int len, lenInSec = 0, allDl = 0;
                float min;
                int max = 0;
                long startRead, endRead;
                double second, secTemp = 0;
                String strSpeedDownload = "0 KB/s";
                while (true)
                {
                    startRead = System.nanoTime ();
                    len = inputStream.read (buffer);
                    if (len <= 0) break;
                    endRead = System.nanoTime ();
                    second = (endRead - startRead) / 1_000_000_000.0;
                    secTemp += second;
                    fileOutputStream.write (buffer , 0 , len);

                    lenHere += len;
                    allDl += len;
                    lenInSec += len;

                    min = ((float) lenHere / sizeFile) * 100;

                    if ((int) min > max) max = (int) min;
                    if (secTemp >= 1)
                    {
                        strSpeedDownload = StringSpeedDownload.Get (lenInSec);
                        lenInSec = 0;
                        secTemp = 0;
                    }
                    System.out.printf ("\r %s || %s || %s || %s " , progress (max) , GetSize.Get (allDl) , strSpeedDownload , okTime ());
                }
                fileOutputStream.flush ();
                fileOutputStream.close ();
                inputStream.close ();
                connection.disconnect ();
                if (question)
                {
                    System.out.print ("\rDownload complete.");
                    Desktop.getDesktop ().open (fileSave.getParentFile ());
                }
                else
                    System.out.print ("\rDownloaded " + FilenameUtils.getName (fileSave.getPath ()) + ".");
            }
            else throw new IOException ("Error select path save file");
        }
    }

    private File getLocation (String nameTypeFile , boolean justDir)
    {
        System.out.print ("Location save file: ");
        JFileChooser chooser = new JFileChooser ();
        if (justDir) chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
        else chooser.setSelectedFile (new File (nameTypeFile));
        File fileSave;
        if (chooser.showSaveDialog (null) == JFileChooser.OPEN_DIALOG && (fileSave = chooser.getSelectedFile ()) != null && fileSave.getParentFile () != null)
        {
            System.out.println (fileSave.getPath ());
            return fileSave;
        }
        return null;
    }
}
