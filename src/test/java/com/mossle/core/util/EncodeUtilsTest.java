package com.mossle.core.util;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class EncodeUtilsTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testHexEncode() {
        Assert.assertEquals("", EncodeUtils.hexEncode(new byte[0]));
        Assert.assertEquals("0001020304",
                EncodeUtils.hexEncode(new byte[]{0, 1, 2, 3, 4}));
    }

    @Test
    public void testHexDecode() {
        Assert.assertArrayEquals(new byte[0], EncodeUtils.hexDecode(""));
        Assert.assertArrayEquals(new byte[]{0, 1, 2, 3, 4},
                EncodeUtils.hexDecode("0001020304"));

        thrown.expect(IllegalStateException.class);
        EncodeUtils.hexDecode("foo");
    }

    @Test
    public void testBase64Encode() throws UnsupportedEncodingException {
        Assert.assertEquals("", EncodeUtils.base64Encode(new byte[0]));
        Assert.assertEquals("AAECAwQ=",
                EncodeUtils.base64Encode(new byte[]{0, 1, 2, 3, 4}));
    }

    @Test
    public void testBase64UrlSafeEncode() {
        Assert.assertEquals("", EncodeUtils.base64UrlSafeEncode(new byte[0]));
        Assert.assertEquals("AAECAwQ",
                EncodeUtils.base64UrlSafeEncode(new byte[]{0, 1, 2, 3, 4}));
    }

    @Test
    public void testBase64Decode() {
        Assert.assertArrayEquals(new byte[0], EncodeUtils.base64Decode(""));
        Assert.assertArrayEquals(new byte[]{-45, 77, 53, -45, 109, 55, -45},
                EncodeUtils.base64Decode("0001020304"));
        Assert.assertArrayEquals(new byte[]{126, -118},
                EncodeUtils.base64Decode("foo"));
    }

    @Test
    public void testUrlEncode() throws UnsupportedEncodingException {
        Assert.assertEquals("", EncodeUtils.urlEncode(""));
        Assert.assertEquals("https%3A%2F%2Fwww.foo.com",
                EncodeUtils.urlEncode("https://www.foo.com"));
    }

    @Test
    public void testUrlDecode() throws UnsupportedEncodingException {
        Assert.assertEquals("", EncodeUtils.urlDecode(""));
        Assert.assertEquals("https://www.foo.com",
                EncodeUtils.urlDecode("https%3A%2F%2Fwww.foo.com"));
    }

    @Test
    public void testHtmlEscape() {
        Assert.assertNull(EncodeUtils.htmlEscape(null));

        Assert.assertEquals("", EncodeUtils.htmlEscape(""));
        Assert.assertEquals("https://www.foo.com",
                EncodeUtils.htmlEscape("https://www.foo.com"));
    }

    @Test
    public void testHtmlUnescape() {
        Assert.assertNull(EncodeUtils.htmlUnescape(null));

        Assert.assertEquals("", EncodeUtils.htmlUnescape(""));
        Assert.assertEquals("https://www.foo.com",
                EncodeUtils.htmlUnescape("https://www.foo.com"));
    }

    @Test
    public void testXmlEscape() {
        Assert.assertNull(EncodeUtils.xmlEscape(null));

        Assert.assertEquals("", EncodeUtils.xmlEscape(""));
    }

    @Test
    public void testXmlUnescape() {
        Assert.assertNull(EncodeUtils.xmlUnescape(null));

        Assert.assertEquals("", EncodeUtils.xmlUnescape(""));
    }

    @Test
    public void testEscapeJS() {
        Assert.assertEquals("1aA", EncodeUtils.escapeJS("1aA"));
        Assert.assertEquals("%5c%0c", EncodeUtils.escapeJS("\\\f"));
        Assert.assertEquals("%uffff", EncodeUtils.escapeJS("\uffff"));
    }

    @Test
    public void testUnescapeJS() {
        Assert.assertEquals("ÿf", EncodeUtils.unescapeJS("%fff"));
        Assert.assertEquals("\uffff", EncodeUtils.unescapeJS("%uffff"));
    }
}
