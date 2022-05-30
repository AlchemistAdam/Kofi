/*
 * Copyright (c) 2021, Adam Martinu. All rights reserved. Altering or
 * removing copyright notices or this file header is not allowed.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package dk.martinu.kofi;

import org.jetbrains.annotations.*;

import java.io.IOException;
import java.io.PrintStream;

import dk.martinu.kofi.codecs.KofiCodec;

/**
 * Contains static utility methods for strings and characters based on the KoFi
 * Text Syntax.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class KofiUtil {

    /**
     * An array of precomputed strings for escaping characters in the range
     * [0x00;0x1F] as a six-character escape sequence, or a two-character
     * escape sequence if the character is one of the following:
     * <ul>
     *    <li>{@code \0 U+0000} NULL</li>
     *    <li>{@code \b U+0008} BACKSPACE</li>
     *    <li>{@code \t U+0009} HORIZONTAL TAB</li>
     *    <li>{@code \n U+000A} LINE FEED</li>
     *    <li>{@code \f U+000C} FORM FEED</li>
     *    <li>{@code \r U+000D} CARRIAGE RETURN</li>
     * </ul>
     */
    private static final String[] ESCAPED_CHARS_00_1F = {
            "\\0", "\\u0001", "\\u0002",
            "\\u0003", "\\u0004", "\\u0005",
            "\\u0006", "\\u0007", "\\b",
            "\\t", "\\n", "\\u000B",
            "\\f", "\\r", "\\u000E",
            "\\u000F", "\\u0010", "\\u0011",
            "\\u0012", "\\u0013", "\\u0014",
            "\\u0015", "\\u0016", "\\u0017",
            "\\u0018", "\\u0019", "\\u001A",
            "\\u001B", "\\u001C", "\\u001D",
            "\\u001E", "\\u001F"
    };

    // DOC update javadoc to better describe what method actually does
    /**
     * Returns {@code true} if the specified subarray of {@code chars} is equal
     * to {@code comp}, ingoring case. Otherwise {@code false} is returned. The
     * {@code comp} array must only contain uppercase Latin letters (A-Z).
     * <p>
     * <b>NOTE:</b> this is a specialized method that only works for Latin
     * letters, and is not a replacement of
     * {@link String#equalsIgnoreCase(String)}.
     *
     * @param chars the characters to compare for equality
     * @param start the starting index, inclusive
     * @param end   the ending index, exclusive
     * @param comp  the charaters to compare to
     * @return {@code true} if equal ignoring case, otherwise {@code false}
     * @throws NullPointerException     if {@code chars} is {@code null}
     * @throws IllegalArgumentException if <code>start &lt; 0</code>,
     *                                  <code>end &lt; start</code> or
     *                                  <code>end &gt; chars.length</code>
     */
    @Contract(pure = true)
    public static boolean equalsIgnoreCase(final char[] chars, final int start, final int end, final char[] comp) {
        if (end - start < comp.length)
            return false;
        for (int i = 0; i < comp.length; i++)
            if (chars[start + i] != comp[i] && chars[start + i] != (comp[i] | 0x20))
                return false;
        return true;
    }

    /**
     * Returns an escaped version of {@code string}. Characters in the range
     * [0x00;0x1F] are escaped as a six-character escape sequence, or a
     * two-character escape sequence if the character is one of the following:
     * <ul>
     *    <li>{@code \0 U+0000} Null</li>
     *    <li>{@code \b U+0008} Backspace</li>
     *    <li>{@code \t U+0009} Horizontal Tabulation</li>
     *    <li>{@code \n U+000A} Line Feed</li>
     *    <li>{@code \f U+000C} Form Feed</li>
     *    <li>{@code \r U+000D} Carriage Return</li>
     * </ul>
     * {@code \ U+005C} Reverse Solidus characters are also escaped as a
     * two-character escape sequence. If no characters were escaped then
     * {@code string} is returned.
     *
     * @param string the string to escape
     * @return an escaped version of {@code string}
     * @see #escape(String, char...)
     */
    @Contract(pure = true)
    @NotNull
    public static String escape(@NotNull final String string) {
        final char[] chars = string.toCharArray();
        final StringBuilder sb = new StringBuilder(chars.length);
        for (char c : chars)
            if (c < 0x20)
                sb.append(ESCAPED_CHARS_00_1F[c]);
            else if (c == '\\')
                sb.append("\\\\");
            else
                sb.append(c);
        return chars.length == sb.length() ? string : sb.toString();
    }

    /**
     * Returns an escaped version of {@code string}. Characters in the range
     * [0x00;0x1F] are escaped as a six-character escape sequence, or a
     * two-character escape sequence if the character is one of the following:
     * <ul>
     *    <li>{@code \0 U+0000} Null</li>
     *    <li>{@code \b U+0008} Backspace</li>
     *    <li>{@code \t U+0009} Horizontal Tabulation</li>
     *    <li>{@code \n U+000A} Line Feed</li>
     *    <li>{@code \f U+000C} Form Feed</li>
     *    <li>{@code \r U+000D} Carriage Return</li>
     * </ul>
     * {@code \ U+005C} Reverse Solidus and {@code other} characters are also
     * escaped as two-character escape sequences. If no characters were escaped
     * then {@code string} is returned.
     *
     * @param string the string to escape
     * @param other  other characters to escape
     * @return an escaped version of {@code string}
     * @see #escape(String)
     */
    @Contract(pure = true)
    @NotNull
    public static String escape(@NotNull final String string, final char... other) {
        final char[] chars = string.toCharArray();
        final StringBuilder sb = new StringBuilder(chars.length);
        outer:
        for (char c : chars) {
            if (c < 0x20) {
                sb.append(ESCAPED_CHARS_00_1F[c]);
                continue;
            }
            else if (c == '\\') {
                sb.append("\\\\");
                continue;
            }
            else
                for (char co : other)
                    if (c == co) {
                        sb.append('\\').append(c);
                        continue outer;
                    }
            sb.append(c);
        }
        return string.length() == sb.length() ? string : sb.toString();
    }

    /**
     * Returns an escape sequence for the specified control character as a
     * six-character escape sequence, or a two-character  escape sequence if
     * {@code c} is one of the following:
     * <ul>
     *    <li>{@code \0 U+0000} NULL</li>
     *    <li>{@code \b U+0008} BACKSPACE</li>
     *    <li>{@code \t U+0009} HORIZONTAL TAB</li>
     *    <li>{@code \n U+000A} LINE FEED</li>
     *    <li>{@code \f U+000C} FORM FEED</li>
     *    <li>{@code \r U+000D} CARRIAGE RETURN</li>
     * </ul>
     *
     * @param c the control character to escape
     * @return an escape sequence of {@code c}
     * @throws ArrayIndexOutOfBoundsException if {@code c} is not a character
     *                                        in the range 0-1F, inclusive.
     */
    @Contract(pure = true)
    @NotNull
    public static String escape_00_1F(@Range(from = 0, to = 0x1F) final char c) {
        return ESCAPED_CHARS_00_1F[c];
    }

    /**
     * Returns the lowest index of {@code c} in {@code chars} in the specified
     * range, or {@code -1} if the character was not found.
     *
     * @param c     the character to search for
     * @param chars the array of characters
     * @param start the starting index, inclusive
     * @param end   the ending index, exclusive
     * @return the lowest index of {@code c}, or {@code -1}
     */
    // DOC escapable characters
    // TODO does not work correctly for reverse solidus \
    @Contract(pure = true)
    @Range(from = -1, to = Integer.MAX_VALUE)
    public static int indexOf(final char c, final char[] chars, final int start, final int end) {
        for (int i = start; i < end; i++)
            if (chars[i] == c)
                // TODO logic could be refactored into separate method - check for uses in other places (entry key fx)
                if (i == 0 || chars[i - 1] != '\\')
                    return i;
                else {
                    int count = 1;
                    for (int k = i - 2; k > 0 && chars[k--] == '\\'; count++) ;
                    if ((count & 1) == 0)
                        return i;
                }
        return -1;
    }

    /**
     * Returns {@code true} if the specified character {@code c} is a decimal
     * digit, otherwise {@code false} is returned.
     * <p>
     * A decimal digit is any character between {@code '0'} and {@code '9'}
     * (inclusive).
     *
     * @param c the character to be tested
     * @return {@code true} if {@code c} is a decimal digit, otherwise
     * {@code false}
     */
    @Contract(pure = true)
    public static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Returns {@code true} if the specified character {@code c} is a
     * hexadecimal digit, otherwise {@code false} is returned.
     * <p>
     * A hexadecimal digit is any character between {@code '0'} and
     * {@code '9'}, or {@code 'A'} and {@code 'F'}, or {@code 'a'} and
     * {@code 'f'} (inclusive).
     *
     * @param c the character to be tested
     * @return {@code true} if {@code c} is a hexadecimal digit, otherwise
     * {@code false}
     */
    @Contract(pure = true)
    public static boolean isHexDigit(final char c) {
        if (isDigit(c))
            return true;
        else if (c >= 'A' && c <= 'F')
            return true;
        else
            return c >= 'a' && c <= 'f';
    }

    /**
     * Returns {@code true} if the specified character is whitespace, otherwise
     * {@code false} is returned. The following characters are whitespace:
     * <ul>
     *    <li>{@code ' ' U+0020} Space</li>
     *    <li>{@code \r U+000A} Carriage Return</li>
     *    <li>{@code \t U+0009} Horizontal Tabulation</li>
     * </ul>
     *
     * @param c the character to be tested
     * @return {@code true} if {@code c} is whitespace, otherwise {@code false}
     */
    @Contract(pure = true)
    public static boolean isWhitespace(final char c) {
        if (c > ' ')
            return false;
        else
            return c == ' ' || c == '\r' || c == '\t';
    }

    /**
     * Debug method that prints {@code doc} to the specified
     * {@code PrintStream} using an instance of {@link KofiCodec}.
     *
     * @param doc         the document to print
     * @param printStream the stream to print to
     */
    @Contract(pure = true)
    public static void printDocument(@NotNull final Document doc, @NotNull final PrintStream printStream) {
        try {
            printStream.print(KofiCodec.provider().writeString(doc));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a subarray of the specified char array with all leading and
     * trailing whitespace characters removed. If no characters were removed,
     * then {@code chars} is returned.
     *
     * @param chars the array to trim
     * @return a subarray of {@code chars} with no leading and trailing
     * whitespace, or {@code chars} if no characters were removed
     * @see #isWhitespace(char)
     */
    @Contract(value = "null -> fail", pure = true)
    public static char[] trim(final char[] chars) {
//        int start = 0, end = chars.length;
//        // trim leading whitespace
//        for (; start < end; start++) {
//            if (!isWhitespace(chars[start]))
//                break;
//        }
//        // trim trailing whitespace
//        for (; end > start; end--) {
//            if (!isWhitespace(chars[end - 1]))
//                break;
//        }
//        // no characters are whitespace
//        if (start == 0 && end == chars.length) {
//            return chars;
//        }
//        // all characters are whitespace
//        else if (start == end) {
//            return new char[0];
//        }
//        // some characters are whitespace
//        else {
//            final char[] sub = new char[end - start];
//            System.arraycopy(chars, start, sub, 0, sub.length);
//            return sub;
//        }
        return trim(chars, 0, chars.length);
    }

    // DOC
    @Contract(value = "null, _, _ -> fail", pure = true)
    public static char[] trim(final char[] chars, final int offset, final int length) {
        int start = offset, end = length;
        // trim leading whitespace
        for (; start < end; start++) {
            if (!isWhitespace(chars[start]))
                break;
        }
        // trim trailing whitespace
        for (; end > start; end--) {
            if (!isWhitespace(chars[end - 1]))
                break;
        }
        // no characters are whitespace
        if (start == offset && end == length) {
            // check if subarray and array are equal
            if (offset == 0 && length == chars.length)
                return chars;
            else {
                final char[] sub = new char[length - offset];
                System.arraycopy(chars, offset, sub, 0, sub.length);
                return sub;
            }
        }
        // all characters are whitespace or subarry is empty
        if (start == end) {
            return new char[0];
        }
        // some or no characters are whitespace
        else {
            final char[] sub = new char[end - start];
            System.arraycopy(chars, start, sub, 0, sub.length);
            return sub;
        }
    }

    /**
     * Returns an unescaped substring of {@code string} in the specified range.
     * If the unescaped substring is equal to {@code string} then
     * {@code string} is returned.
     *
     * @param string the string to unescape
     * @param start  the starting index, inclusive
     * @param end    the ending index, exclusive
     * @return an unescaped substring
     * @see #escape(String)
     */
    @Contract(pure = true)
    @NotNull
    public static String unescape(@NotNull final String string, final int start, final int end) {
        final char[] chars0 = string.toCharArray();
        final char[] chars1 = unescape(chars0, start, end);
        if (chars0 != chars1)
            return new String(chars1);
        else
            return string;
    }

    // DOC
    @Contract(value = "null -> fail", pure = true)
    public static char[] unescape(final char[] chars) {
        return unescape(chars, 0, chars.length);
    }

    /**
     * Returns an unescaped subarray of {@code chars} in the specified range.
     * If the unescaped subarray is equal to {@code chars} then {@code chars}
     * is returned.
     *
     * @param chars the characters to unescape
     * @param start the starting index, inclusive
     * @param end   the ending index, exclusive
     * @return an unescaped subarray
     * @see #escape(String)
     */
    @Contract(value = "null, _, _ -> fail", pure = true)
    public static char[] unescape(final char[] chars, final int start, final int end) {
        final CharBuffer cb = new CharBuffer(end - start);
        for (int i = start; i < end; ) {
            if (chars[i] == '\\') {
                // remaining characters
                final int rem = end - i;
                // no more chars
                if (rem == 1) {
                    break;
                }
                // six-character escape sequence
                else if (chars[i + 1] == 'u' || chars[i + 1] == 'U') {
                    if (rem >= 6 && isHexDigit(chars[i + 2]) && isHexDigit(chars[i + 3])
                            && isHexDigit(chars[i + 4]) && isHexDigit(chars[i + 5])) {
                        // get int value of 4-digit hex and cast it to char
                        cb.append((char) Integer.valueOf(String.copyValueOf(
                                chars, i + 2, 4), 16).intValue());
                        i += 6;
                    }
                    else {
                        cb.append(chars[i + 1]);
                        i += 2;
                    }
                }
                // two-character escape sequence
                else {
                    final char c = chars[i + 1];
                    if (c == '0')
                        cb.append('\0');
                    else if (c == 'b')
                        cb.append('\b');
                    else if (c == 't')
                        cb.append('\t');
                    else if (c == 'n')
                        cb.append('\n');
                    else if (c == 'f')
                        cb.append('\f');
                    else if (c == 'r')
                        cb.append('\r');
                    else
                        cb.append(c);
                    i += 2;
                }
            }
            else
                cb.append(chars[i++]);
        }
        if (cb.length() != chars.length)
            return cb.toCharArray();
        else
            return chars;
    }

    // DOC
    private static class CharBuffer {

        final char[] chars;
        int cursor = 0;

        CharBuffer(final int size) {
            chars = new char[size];
        }

        void append(final char c) {
            chars[cursor++] = c;
        }

        int length() {
            return cursor;
        }

        char[] toCharArray() {
            if (cursor != chars.length) {
                final char[] rv = new char[cursor];
                System.arraycopy(chars, 0, rv, 0, cursor);
                return rv;
            }
            else
                return chars;
        }
    }
}
