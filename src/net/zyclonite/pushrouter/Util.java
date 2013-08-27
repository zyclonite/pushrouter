/*
 * PushRouter
 *
 * Copyright 2011   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.pushrouter;

import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author zyclonite
 */
public final class Util {

    private final static String WILDCARD = "*";

    private Util() {
    }

    public static String escape(final Object input) {
        if (input instanceof XMLGregorianCalendar) {
            return escape(input.toString());
        }
        if (input instanceof String) {
            return escape((String) input);
        }
        return "";
    }

    public static String escape(final String orig) {
        if (orig == null) {
            return "";
        }
        final StringBuilder buffer = new StringBuilder(orig.length());

        for (int i = 0; i < orig.length(); i++) {
            final char chr = orig.charAt(i);
            switch (chr) {
                case '\b':
                    buffer.append("\\b");
                    break;
                case '\f':
                    buffer.append("\\f");
                    break;
                case '\n':
                    buffer.append("<br />");
                    break;
                case '\r':
                    // ignore
                    break;
                case '\t':
                    buffer.append("\\t");
                    break;
                case '\'':
                    //    buffer.append("\\'");
                    break;
                case '\"':
                    buffer.append("\\\"");
                    break;
                case '\\':
                    buffer.append("\\\\");
                    break;
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                default:
                    buffer.append(chr);
            }
        }
        return buffer.toString();
    }

    public static String forXML(final Object input) {
        if (input instanceof String) {
            return forXML((String) input);
        } else {
            return "";
        }
    }

    public static String forXML(final String orig) {
        if (orig == null) {
            return "";
        }
        final StringBuilder buffer = new StringBuilder(orig.length());

        for (int i = 0; i < orig.length(); i++) {
            final char chr = orig.charAt(i);
            switch (chr) {
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '\'':
                    buffer.append("&#039;");
                    break;
                case '\"':
                    buffer.append("&quot;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                default:
                    buffer.append(chr);
            }
        }
        return buffer.toString();
    }

    public static String stripNonValidXMLCharacters(final String value) {
        final StringBuilder out = new StringBuilder();
        char current;

        for (int i = 0; i < value.length(); i++) {
            current = value.charAt(i);

            if ((current == 0x9)
                    || (current == 0xA)
                    || (current == 0xD)
                    || ((current >= 0x20) && (current <= 0xD7FF))
                    || ((current >= 0xE000) && (current <= 0xFFFD))
                    || ((current >= 0x10000) && (current <= 0x10FFFF))) {
                out.append(current);
            }
        }

        return out.toString();
    }

    public static boolean wildcardMatching(final String value, final String pattern) {
        final byte wildcard = (byte) WILDCARD.charAt(0);
        final byte[] bpattern = pattern.getBytes();
        final byte[] bvalue = value.getBytes();
        if ((bpattern[0] == wildcard) && (bpattern.length == 1)) {
            return true;
        }
        if ((bpattern[bpattern.length - 1] == wildcard) && (bpattern.length > 1)) {
            if (searchBytes(bvalue, 0, bvalue.length, ArrayUtils.remove(bpattern, bpattern.length - 1), processBytes(bpattern)) != -1) {
                return true;
            }
        } else {
            if (value.equals(pattern)) {
                return true;
            }
        }
        return false;
    }

    private static int searchBytes(
            byte[] text,
            int textStart,
            int textEnd,
            byte[] pattern,
            Object processed) {

        int[] b = (int[]) processed;

        int i, j, k, mMinusOne;
        byte last, first;

        i = pattern.length - 1;
        mMinusOne = pattern.length - 2;

        last = pattern[pattern.length - 1];
        first = pattern[0];

        i += textStart;

        while (i < textEnd) {

            if (text[i] == last) {

                if (text[i - (pattern.length - 1)] == first) {

                    k = i - 1;
                    j = mMinusOne;

                    while (k > -1 && j > -1 && text[k] == pattern[j]) {
                        --k;
                        --j;
                    }
                    if (j == -1) {
                        return k + 1;
                    }
                }
            }

            i += b[index(text[i])];
        }

        return -1;

    }

    private static Object processBytes(byte[] pattern) {
        int[] skip = new int[256];

        for (int i = 0; i < skip.length; ++i) {
            skip[i] = pattern.length;
        }

        for (int i = 0; i < pattern.length - 1; ++i) {
            skip[index(pattern[i])] = pattern.length - i - 1;
        }

        return skip;
    }

    private static int index(byte idx) {
        return (idx < 0) ? 256 + idx : idx;
    }
}
