package sweethome.sensors;

public class Utils {
    /**
     * Print an array of bytes in hex to standard out.
     *
     * @param  dataBuf data to print
     * @param  offset  offset into dataBuf to start
     * @param  len     length of data to print
     */
    public static String hexPrint (byte[] dataBuf, int offset, int len)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++)
        {
            if ((dataBuf [i + offset] & 0x000000FF) < 0x00000010)
            {
                sb.append("0");
                sb.append(Integer.toHexString(( int ) dataBuf [i + offset]
                        & 0x0000000F).toUpperCase());
            }
            else
                sb.append(Integer.toHexString(( int ) dataBuf [i + offset]
                        & 0x000000FF).toUpperCase());
        }
        return sb.toString();
    }

    /**
     * convert input to hexidecimal value
     *
     * @param  c  hex char to convert
     *
     * @return int representation of hex character
     */
    public static int hexDigitValue (char c)
    {
        int value = Character.digit(c, 16);

        if (value == -1)
        {
            throw new StringIndexOutOfBoundsException("Invalid Hex value: " + c);
        }

        return value;
    }

    /**
     * parse byte string into a byte array
     *
     * @param  str  String to parse
     *
     * @return byte array of data.
     */
    public static byte[] parseByteString (String str)
    {

        // data are entered in "xx xx xx xx" format
        String dataStr  = str.trim();
        int    dataLen  = dataStr.length();
        byte[] buf      = new byte [dataLen];
        int    bufLen   = 0;
        int    curPos   = 0;
        int    savedPos = 0;
        int    count    = 0;
        char   c;

        while (curPos < dataLen)
        {
            c = dataStr.charAt(curPos);

            if (!Character.isWhitespace(c))
            {
                savedPos = curPos;
                count    = 1;

                while ((curPos < dataLen - 1)
                        && (!Character.isWhitespace(dataStr.charAt(++curPos))))
                {
                    count++;
                }

                if (count > 2)
                    throw new StringIndexOutOfBoundsException(
                            "Invalid Byte String: " + str);

                if (curPos != dataLen - 1)
                    curPos--;

                if (count == 1)   // only 1 digit entered
                    buf [bufLen++] = ( byte ) hexDigitValue(c);
                else
                    buf [bufLen++] =
                            ( byte ) ((hexDigitValue(c) << 4)
                                    | ( byte ) hexDigitValue(dataStr.charAt(curPos)));
            }                    // if

            curPos++;
        }                       // while

        byte[] data = new byte [bufLen];

        System.arraycopy(buf, 0, data, 0, bufLen);

        return data;
    }
}
