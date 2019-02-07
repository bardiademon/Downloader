public class GetSize
{
    public static String Get (long Byte)
    {
        float mb = (((float) Byte / 1024) / 1024);
        if (mb >= 1024) return String.format("%s GB" , String.valueOf ((int) (mb / 1024)));
        else return String.format("%s MB" , String.valueOf ((int) mb));
    }
}
