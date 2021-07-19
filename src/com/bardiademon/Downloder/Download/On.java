package com.bardiademon.Downloder.Download;

import java.io.File;

public interface On extends OnInfoLink
{
    void OnDownloading (final int Percent , final String Progress , final String DownloadedString , final long DownloadedLong , final String Speed , final String Time);

    boolean OnConnectedList (final long Size);

    int OnExistsFile (final boolean FullNotDownloaded);

    void OnExistsFileError (final Exception E);

    void OnExistsFileErrorDeleteFile (final Exception E , final File _File);

    String OnEnterPath (final String Filename , final boolean JustDir);

    void OnErrorPath (final Exception E , final File _File , final String Filename);

    void OnDownloaded (final File Path);

    void OnErrorDownloading (final Exception E);

    // for continue
    void OnPause (final Download.ResumeDownload ResumeDownload);

    void OnErrorPause (final Exception E , final boolean Pause);

    String OnRenameFileExists (final String Filename);

    void OnErrorRenameFileExists (final Exception E);

    void OnCompulsoryStop ();

    void OnCompulsoryStopCloseStreamError (final Exception E);

}
