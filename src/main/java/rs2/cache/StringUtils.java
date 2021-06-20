package rs2.cache;

/**
 * @author Patrick
 */
public final class StringUtils {

    public static final char[] VALID_CHARS = {
            '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    public static String getStringFromLong(long l) {
        int index = 0;
        char[] characters = new char[32];
        while (l != 0L) {
            long l1 = l;
            l /= 37L;
            characters[11 - index++] = VALID_CHARS[(int) (l1 - l * 37L)];
        }
        return new String(characters, 12 - index, index);
    }


}
