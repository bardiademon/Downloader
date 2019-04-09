class GetSize
{
    static String Get (long Byte)
    {
        float kb = (float) Byte / 1024;
        if (kb >= 1024)
        {
            float mb = (kb / 1024);
            if (mb >= 1024)
            {
                float gb = mb / 1024;
                return String.format ("%s GB" , toString (gb));
            }
            else
            {
                return String.format ("%s MB" , toString (mb));
            }
        }
        else return toString (kb);
    }

    private static String toString (double size)
    {
        return String.format ("%.3f" , size);
    }
}
