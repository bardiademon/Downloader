import org.apache.commons.io.FilenameUtils;


import javax.swing.JFileChooser;
import java.awt.Desktop;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Download extends Thread
{
    @Override
    public void run ()
    {
        while (true)
        {
            try
            {
                BufferedReader readerLink = new BufferedReader (new InputStreamReader (System.in));

                System.out.print ("Link: ");
                String link = readerLink.readLine ();

                if (link.equals ("exit")) break;

                URL url = new URL (link);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection ();
                connection.connect ();
                long sizeFile = Long.parseLong (connection.getHeaderField ("Content-length"));

                System.out.format ("\nSize file : %s\n\n" , GetSize.Get (sizeFile));

                BufferedReader readerDownloadYesNo = new BufferedReader (new InputStreamReader (System.in));
                System.out.print ("Download this file (y,n)? ");
                String nameTypeFile = FilenameUtils.getName (url.getPath ());
                if (readerDownloadYesNo.readLine ().toLowerCase ().equals ("y"))
                {
                    System.out.print ("\nLocation save file: ");
                    JFileChooser chooser = new JFileChooser ();
                    chooser.setSelectedFile (new File (nameTypeFile));
                    File fileSave;
                    if (chooser.showSaveDialog (null) == JFileChooser.OPEN_DIALOG && (fileSave = chooser.getSelectedFile ()) != null && fileSave.getParentFile () != null)
                    {
                        System.out.println (fileSave.getPath ());
                        InputStream inputStream = connection.getInputStream ();

                        byte[] buffer = new byte[5120];

                        FileOutputStream fileOutputStream = new FileOutputStream (fileSave);

                        int lenHere = 0;
                        int len;
                        float min;
                        int max = 0;
                        String percentage;
                        StringBuilder progress;

                        System.out.print ("\n %0");
                        while ((len = inputStream.read (buffer)) > 0)
                        {
                            fileOutputStream.write (buffer , 0 , len);

                            lenHere += len;

                            min = ((float) lenHere / sizeFile) * 100;

                            if ((int) min > max)
                            {
                                percentage = String.format ("%S%%" , String.valueOf ((int) min));

                                progress = new StringBuilder ();
                                for (int i = 0; i < ((int) min); i++) progress.append ("=");

                                max = (int) min;
                                System.out.print (String.format ("\r%s %s" , progress.toString () , percentage));
                            }
                        }
                        fileOutputStream.flush ();
                        fileOutputStream.close ();
                        inputStream.close ();
                        connection.disconnect ();
                        System.out.print ("\nDownload complete.\n\n");
                        Desktop.getDesktop ().open (fileSave.getParentFile ());
                    }
                    else throw new IOException ("Error select path save file");
                }
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace ();
            }
            catch (IOException e)
            {
                System.err.println ("\nDownload error => " + e.getMessage ());
            }
        }
    }
}
