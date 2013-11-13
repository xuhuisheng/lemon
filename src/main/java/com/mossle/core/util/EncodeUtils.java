package com.mossle.core.util;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * encode utils.
 * 
 * @author Lingo
 */
public class EncodeUtils {
    /** default url encoding. */
    private static final String DEFAULT_URL_ENCODING = "UTF-8";

    /** unprintable char code. */
    private static final int UNPRINTABLE_CHAR_CODE = 16;

    /** ansi char code. */
    private static final int ANSI_CHAR_CODE = 256;

    /** hex. */
    private static final int HEX = 16;

    /** unicode length. */
    private static final int UNICODE_LENGTH = "\\u0000".length();

    /** ansi length. */
    private static final int ANSI_LENGTH = "%FF".length();

    /** protected constructor. */
    protected EncodeUtils() {
    }

    /**
     * Hex编码.
     * 
     * @param input
     *            byte[]
     * @return String
     */
    public static String hexEncode(byte[] input) {
        return Hex.encodeHexString(input);
    }

    /**
     * Hex解码.
     * 
     * @param input
     *            String
     * @return byte[]
     */
    public static byte[] hexDecode(String input) {
        try {
            return Hex.decodeHex(input.toCharArray());
        } catch (DecoderException e) {
            throw new IllegalStateException("Hex Decoder exception", e);
        }
    }

    /**
     * Base64编码.
     * 
     * @param input
     *            byte[]
     * @return String
     */
    public static String base64Encode(byte[] input)
            throws UnsupportedEncodingException {
        return new String(Base64.encodeBase64(input), "UTF-8");
    }

    /**
     * Base64编码, URL安全(将Base64中的URL非法字符如+,/=转为其他字符, 见RFC3548).
     * 
     * @param input
     *            byte[]
     * @return String
     */
    public static String base64UrlSafeEncode(byte[] input) {
        return Base64.encodeBase64URLSafeString(input);
    }

    /**
     * Base64解码.
     * 
     * @param input
     *            String
     * @return byte[]
     */
    public static byte[] base64Decode(String input) {
        return Base64.decodeBase64(input);
    }

    /**
     * URL 编码, Encode默认为UTF-8.
     * 
     * @param input
     *            String
     * @return String
     */
    public static String urlEncode(String input)
            throws UnsupportedEncodingException {
        return URLEncoder.encode(input, DEFAULT_URL_ENCODING);
    }

    /**
     * URL 解码, Encode默认为UTF-8.
     * 
     * @param input
     *            String
     * @return String
     */
    public static String urlDecode(String input)
            throws UnsupportedEncodingException {
        return URLDecoder.decode(input, DEFAULT_URL_ENCODING);
    }

    /**
     * Html 转码.
     * 
     * @param html
     *            String
     * @return String
     */
    public static String htmlEscape(String html) {
        return StringUtils.escapeHtml(html);
    }

    /**
     * Html 解码.
     * 
     * @param htmlEscaped
     *            String
     * @return String
     */
    public static String htmlUnescape(String htmlEscaped) {
        return StringUtils.unescapeHtml(htmlEscaped);
    }

    /**
     * Xml 转码.
     * 
     * @param xml
     *            String
     * @return String
     */
    public static String xmlEscape(String xml) {
        return StringUtils.escapeXml(xml);
    }

    /**
     * Xml 解码.
     * 
     * @param xmlEscaped
     *            String
     * @return String
     */
    public static String xmlUnescape(String xmlEscaped) {
        return StringUtils.unescapeXml(xmlEscaped);
    }

    /**
     * 对应js的escape.
     * 
     * @param src
     *            String
     * @return String
     */
    public static String escapeJS(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * UNICODE_LENGTH);

        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);

            if (Character.isDigit(j) || Character.isLowerCase(j)
                    || Character.isUpperCase(j)) {
                tmp.append(j);
            } else if (j < ANSI_CHAR_CODE) {
                tmp.append("%");

                if (j < UNPRINTABLE_CHAR_CODE) {
                    tmp.append("0");
                }

                tmp.append(Integer.toString(j, HEX));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, HEX));
            }
        }

        return tmp.toString();
    }

    /**
     * 对应js的unescape.
     * 
     * @param src
     *            String
     * @return String
     */
    public static String unescapeJS(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());

        int lastPos = 0;
        int pos = 0;
        char ch;

        while (lastPos < src.length()) {
            pos = src.indexOf('%', lastPos);

            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(
                            src.substring(pos + 2, pos + UNICODE_LENGTH), HEX);
                    tmp.append(ch);
                    lastPos = pos + UNICODE_LENGTH;
                } else {
                    ch = (char) Integer.parseInt(
                            src.substring(pos + 1, pos + ANSI_LENGTH), HEX);
                    tmp.append(ch);
                    lastPos = pos + ANSI_LENGTH;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }

        return tmp.toString();
    }
}
