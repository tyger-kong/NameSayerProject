package namesayer.backend;

import java.util.Comparator;

/**
 * Class used to sort the names in the database alphabetically, ignoring case
 */
public class SortIgnoreCase implements Comparator<Object> {
    public int compare(Object o1, Object o2) {
        String s1 = (String) o1;
        String s2 = (String) o2;
        return s1.toLowerCase().compareTo(s2.toLowerCase());
    }
}