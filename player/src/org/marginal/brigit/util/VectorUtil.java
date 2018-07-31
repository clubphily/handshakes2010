package org.marginal.brigit.util;

import java.util.Enumeration;
import java.util.Vector;

/*------------------------------------------------------------------------------
class
VectorUtils

------------------------------------------------------------------------------*/
/**
 * Convenience methods for working with Vectors.. provides efficient Union,
 * Intersection, and Difference methods.
 */
public class VectorUtil {

    public final static int TO_INTEGER = 0;
    public final static int TO_STRING = 1;

    /**
     * This method returns a Vector containing the union of the objects
     * contained in vectA and vectB.  The resulting Vector will not
     * contain any duplicates, even if vectA or vectB themselves contain
     * repeated items.
     *
     * This method will always return a new, non-null Vector, even if
     * vectA and/or vectB are null.
     */
    public static Vector union(Vector vectA, Vector vectB) {
        Vector result = new Vector();

        if (vectA != null) {
            Enumeration enumV = vectA.elements();
            while (enumV.hasMoreElements()) {
                result.addElement(enumV.nextElement());
            }
        }

        if (vectB != null) {
            Enumeration enumV = vectB.elements();
            while (enumV.hasMoreElements()) {
                Object obj = enumV.nextElement();
                if (!result.contains(obj)) {
                    result.addElement(obj);
                }
            }
        }

        result.trimToSize();

        return result;
    }

    /**
     * This method adds obj to vect if and only if vect does not
     * already contain obj.
     */
    public static void unionAdd(Vector vect, Object obj) {
        if (obj == null) {
            return;
        }

        if (vect.contains(obj)) {
            return;
        }

        vect.addElement(obj);
    }

    /**
     * Returns true if vectA and vectB have any elements in
     * common.
     */
    public static boolean overlaps(Vector vectA, Vector vectB) {
        if (vectA == null || vectB == null || vectA.size() == 0 || vectB.size() == 0) {
            return false;
        }

        if (vectA.size() > vectB.size()) {
            for (int i = 0; i < vectA.size(); i++) {
                if (vectB.contains(vectA.elementAt(i))) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < vectB.size(); i++) {
                if (vectA.contains(vectB.elementAt(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * This method returns a Vector containing the intersection of the
     * objects contained in vectA and vectB.
     *
     * This method will always return a new, non-null Vector, even if
     * vectA and/or vectB are null.
     */
    public static Vector intersection(Vector vectA, Vector vectB) {
        Vector resultSet = new Vector();

        Enumeration enumV = vectA.elements();
        while (enumV.hasMoreElements()) {
            Object obj = enumV.nextElement();
            if (vectB.contains(obj)) {
                resultSet.addElement(obj);
            }
        }
        enumV = vectB.elements();
        Object obj = null;
        while (enumV.hasMoreElements()) {
            obj = enumV.nextElement();
            if (vectA.contains(obj)) {
                resultSet.addElement(obj);
            }
        }

        return resultSet;
    }

    /**
     * This method returns a Vector containing the set of objects
     * contained in vectA that are not contained in vectB.
     *
     * This method will always return a new, non-null Vector, even if
     * vectA and/or vectB are null.
     */
    public static Vector difference(Vector vectA, Vector vectB) {
        Vector result = new Vector();
        
        if (vectA == null) {
            return result;
        }

        if (vectB == null) {
            return vectA;
        }

        Enumeration enumV = vectA.elements();
        Object obj = null;
        while (enumV.hasMoreElements()) {
            obj = enumV.nextElement();
            if (!vectB.contains(obj)) {
                result.addElement(obj);
            }
        }

        return result;
    }

    /**
     * This method returns a Vector of items that appeared in the
     * vector parameter more than once.
     *
     * If no duplicates are found or if vector is null, this method
     * returns null.
     */
    public static Vector duplicates(Vector vector) {
        if (vector == null) {
            return null;
        }

        Vector result = null;
        Vector found = new Vector();


        Enumeration enumV = vector.elements();
        Object item = null;
        while (enumV.hasMoreElements()) {
            item = enumV.nextElement();
            if (found.contains(item)) {
                if (result == null) {
                    result = new Vector();
                }

                unionAdd(result, item);
            }

            found.addElement(item);
        }

        return result;
    }

    /**
     * This method returns a Vector containing the elements of vectA minus
     * the elements of vectB.  If vectA has an element in the Vector 5 times
     * and vectB has it 3 times, the result will have it two times.
     *
     * This method will always return a new, non-null Vector, even if
     * vectA and/or vectB are null.
     */
    public static Vector minus(Vector vectA, Vector vectB) {
        if (vectA == null) {
            return new Vector();	// empty
        }

        Vector result = vectA;

        if (vectB != null) {
            Enumeration enumV = vectB.elements();
            while (enumV.hasMoreElements()) {
                result.removeElement(enumV.nextElement());
            }
        }

        return result;
    }

    /**
     * This method returns a string containing all the elements in vec
     * concatenated together, comma separated.
     */
    public static String vectorString(Vector vec) {
        return VectorUtil.vectorString(vec, ",");
    }

    /**
     * This method returns a string containing all the elements in vec
     * concatenated together, comma separated.
     */
    public static String vectorString(Vector vec, String separator) {
        if (vec == null) {
            return "";
        }

        StringBuffer temp = new StringBuffer();

        boolean first = true;
        Enumeration enumV = vec.elements();
        Object elem = null;
        while (enumV.hasMoreElements()) {
            elem = enumV.nextElement();
            if (!first) {
                temp.append(separator);
            }

            temp.append(elem);

            first = false;
        }

        return temp.toString();
    }

        /**
     * This method returns a string containing all the elements in vec
     * concatenated together, comma separated.
     */
    public static Vector stringToIntVector(String input) {
        return VectorUtil.stringVector(input, TO_INTEGER, ",");
    }

    /**
     * This method takes a sepChars-separated string and converts it to
     * a vector of fields.  i.e., "gomod,jonabbey" -> a vector whose
     * elements are "gomod" and "jonabbey".
     *
     * NOTE: this method will omit 'degenerate' fields from the output
     * vector.  That is, if input is "gomod,,,  jonabbey" and sepChars
     * is ", ", then the result vector will still only have "gomod"
     * and "jonabbey" as elements, even though one might wish to
     * explicitly know about the blanks between commas.  This method
     * is intended mostly for creating email list vectors, rather than
     * general file-parsing vectors.
     *
     * @param input the sepChars-separated string to test.
     *
     * @param sepChars a string containing a list of characters which
     * may occur as field separators.  Any two fields in the input may
     * be separated by one or many of the characters present in sepChars.
     */
    public static Vector stringVector(String input, int convertTo, String sepChars) {
        Vector results = new Vector();
        int index = 0;
        int oldindex = 0;
        String temp;
        char inputAry[] = input.toCharArray();

        /* -- */

        while (index != -1) {
            // skip any leading field-separator chars

            for (; oldindex < input.length(); oldindex++) {
                if (sepChars.indexOf(inputAry[oldindex]) == -1) {
                    break;
                }
            }

            if (oldindex == input.length()) {
                break;
            }

            index = findNextSep(input, oldindex, sepChars);

            if (index == -1) {
                temp = input.substring(oldindex);
            } else {
                temp = input.substring(oldindex, index);
                oldindex = index + 1;
            }
            switch (convertTo) {
                case TO_INTEGER:
                    results.addElement(new Integer(Integer.parseInt(temp)));
                    break;
                case TO_STRING:
                default:
                    results.addElement(temp);
                    break;
            }

        }

        return results;
    }

    /**
     * findNextSep() takes a string, a starting position, and a string of
     * characters to be considered field separators, and returns the
     * first index after startDex whose char is in sepChars.
     *
     * If there are no chars in sepChars past startdex in input, findNextSep()
     * returns -1.
     */
    private static int findNextSep(String input, int startDex, String sepChars) {
        int currentIndex = input.length();
        char sepAry[] = sepChars.toCharArray();
        boolean foundSep = false;

        /* -- */

        // find the next separator

        for (int i = 0; i < sepAry.length; i++) {
            int tempdex = input.indexOf(sepAry[i], startDex);

            if (tempdex > -1 && tempdex <= currentIndex) {
                currentIndex = tempdex;
                foundSep = true;
            }
        }

        if (foundSep) {
            return currentIndex;
        } else {
            return -1;
        }
    }
}

