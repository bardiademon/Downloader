
@bardiademon
class StringSpeedDownload
{
    @bardiademon
    static String Get (long Byte)
    {
        float kb = (float) Byte / 1024;
        if (kb >= 1024)
        {
            float mb = (kb / 1024);
            return String.format ("%d MB/s" , (int) mb);
        }
        else return String.format ("%d KB/s" , (int) kb);
    }
}
