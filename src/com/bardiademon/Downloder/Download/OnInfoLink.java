package com.bardiademon.Downloder.Download;

import java.io.File;

public interface OnInfoLink
{
    String OnEnterLink ();

    boolean OnConnected (final long Size , final File Path);

    void OnFilename (final String Filename);

    void OnError (final Exception E);

    void OnPrint (final String Message);

    void OnCancelDownload ();

    /*
     * redirect
     */
    void OnNewLink (final String Link);
}
