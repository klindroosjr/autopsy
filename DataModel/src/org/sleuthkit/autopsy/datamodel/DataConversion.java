/*
 * Autopsy Forensic Browser
 *
 * Copyright 2011 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.datamodel;

import java.awt.Font;
import java.util.Arrays;

/**
 * Helper methods for converting data.
 */
public class DataConversion {

    public static String byteArrayToHex(byte[] array, int length, long offset, Font font) {
        if (array == null) {
            return "";
        } else {
            String base = new String(array, 0, length);

            StringBuilder buff = new StringBuilder();
            int count = 0;
            int extra = base.length() % 16;
            String sub = "";
            char subchar;

            //commented out code can be used as a base for generating hex length based on
            //offset/length/file size
            //String hex = Long.toHexString(length + offset);
            //double hexMax = Math.pow(16, hex.length());
            double hexMax = Math.pow(16, 6);
            while (count < base.length() - extra) {
                buff.append("0x");
                buff.append(Long.toHexString((long) (offset + count + hexMax)).substring(1));
                buff.append(": ");
                for (int i = 0; i < 16; i++) {
                    buff.append(Integer.toHexString((((int) base.charAt(count + i)) & 0xff) + 256).substring(1).toUpperCase());
                    buff.append("  ");
                    if (i == 7) {
                        buff.append("  ");
                    }
                }
                sub = base.substring(count, count + 16);
                for (int i = 0; i < 16; i++) {
                    subchar = sub.charAt(i);
                    if (!font.canDisplay(subchar)) {
                        sub.replace(subchar, '.');
                    }

                    // replace all unprintable characters with "."
                    int dec = (int) subchar;
                    if (dec < 32 || dec > 126) {
                        sub = sub.replace(subchar, '.');
                    }
                }
                buff.append("  " + sub + "\n");
                count += 16;

            }
            if (base.length() % 16 != 0) {
                buff.append("0x" + Long.toHexString((long) (offset + count + hexMax)).substring(1) + ": ");
            }
            for (int i = 0; i < 16; i++) {
                if (i < extra) {
                    buff.append(Integer.toHexString((((int) base.charAt(count + i)) & 0xff) + 256).substring(1) + "  ");
                } else {
                    buff.append("    ");
                }
                if (i == 7) {
                    buff.append("  ");
                }
            }
            sub = base.substring(count, count + extra);
            for (int i = 0; i < extra; i++) {
                subchar = sub.charAt(i);
                if (!font.canDisplay(subchar)) {
                    sub.replace(subchar, '.');
                }
            }
            buff.append("  " + sub);
            return buff.toString();
        }
    }

    protected static String charArrayToByte(char[] array) {
        if (array == null) {
            return "";
        } else {
            String[] binary = new String[array.length];

            for (int i = 0; i < array.length; i++) {
                binary[i] = Integer.toBinaryString(array[i]);
            }
            return Arrays.toString(binary);
        }
    }

    /*
     * Gets only the printable string from the given characters
     *
     * The definition of printable are:
     *  -- All of the letters, numbers, and punctuation.
     *  -- space and tab
     *  -- It does NOT include newlines or control chars.
     *  -- When looking for ASCII strings, they evaluate each byte and when they find four or more printable characters they get printed out with a newline in between each string.
     *  -- When looking for Unicode strings, they evaluate each two byte sequence and look for four or more printable characters…
     *
     * @param args          the bytes that the string read from
     * @param len           length of text in the buffer to convert, starting at position 0
     * @param parameter     the "length" parameter for the string
     *
     * @author jantonius
     */
    public static String getString(byte[] args, int len, int parameter) {

        /*
        // these encoding might be needed for later
        // Note: if not used, can be deleted
        CharsetEncoder asciiEncoder =
        Charset.forName("US-ASCII").newEncoder(); // or "ISO-8859-1" for ISO Latin 1
        
        CharsetEncoder utf8Encoder =
        Charset.forName("UTF-8").newEncoder();
         */
        final StringBuilder result = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        int counter = 0;
        //char[] converted = new java.lang.System.Text.Encoding.ASCII.GetString(args).ToCharArray();

        final char NL = (char) 10; // ASCII char for new line
        final String NLS = Character.toString(NL);
        boolean isZero = false;
        for (int i = 0; i < len; i++) {
            char curChar = (char) args[i];

            if (curChar == 0 && isZero == false) {
                //allow to skip one 0
                isZero = true;
            } else {
                isZero = false;
            }
            //ignore non-printable ASCII chars
            //use 32-126 and not TAB ( 9)
            if (isUsableChar(curChar)) {
                temp.append(curChar);
                ++counter;
            } else if (!isZero) {
                if (counter >= parameter) {
                    // add to the result and also add the new line at the end
                    result.append(temp);
                    result.append(NLS);
                }
                // reset the temp and counter
                temp = new StringBuilder();
                counter = 0;

            }
        }

        result.append(temp);
        return result.toString();
    }

    private static boolean isUsableChar(char c) {
        return c >= 32 && c <= 126 && c != 9;
    }

    /**
     * Converts the given paths into the formatted path. This mainly used for
     * the paths for the "new directory table" and "new output view".
     * Will return path from beginIndex to ((length of paths) - endIndex)
     *
     * @param paths  the given paths
     * @param beginIndex  the starting index of the given paths
     * @param endIndex the ending index
     * @return path  the formatted path
     *
     * @author jantonius
     */
    public static String getformattedPath(String[] paths, int beginIndex, int endIndex) {
        String result = "";
        for (int i = beginIndex; i < (paths.length - endIndex); i++) {
            result = result + "\\" + paths[i];
        }
        return result;
    }

    public static String getformattedPath(String[] paths, int index) {
        return getformattedPath(paths, index, 0);
    }
}
