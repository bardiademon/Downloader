package com.bardiademon.Downloder.Download;

import java.io.File;

public interface On extends OnInfoLink
{
    void OnDownloading (final int Percent , final String Progress , final String DownloadedString , final long DownloadedLong , final String Speed , final String Time);

    boolean OnConnectedList (final long Size);

    int OnExistsFile (final boolean FullNotDownloaded);

    String OnEnterPath (final String Filename , final boolean JustDir);

    void OnErrorPath (final Exception E , final File _File , final String Filename);


    void OnDownloaded (final File Path);

    void OnErrorDownloading (final Exception E);

    // for continue
    boolean OnPause ();

    void OnErrorPause (final Exception E , final boolean Pause);

    String OnRenameFileExists (final String Filename);

    void OnErrorRenameFileExists (final Exception E);

    void OnCancelDownload ();

    void OnCompulsoryStop ();

    void OnCompulsoryStopCloseStreamError (final Exception E);
}
