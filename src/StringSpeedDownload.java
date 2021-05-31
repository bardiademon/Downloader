
@bardiademon
class StringSpeedDownload
{
    @bardiademon
    static String Get (long Byte)
    {
        double kb = (double) Byte / 1024;
        if (kb >= 1024)
        {
            double mb = (kb / 1024);
            return String.format ("%.3f MB/s" , mb);
        }
        else return String.format ("%.3f KB/s" , kb);
    }
}
