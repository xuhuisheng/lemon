package com.mossle.core;

import com.mossle.core.util.Md5Utils;

/**
 * 对传入的URL附加key，计算md5，得到一个32位的字符串。 MD5的值确实是16进制的，所以它可以表示16 ^ 32 = 3.4028236692093846346337460743177e+38种组合
 * 把md5分成四段，每段是16 ^ 8 = 4 294 967 296，40多亿，认为这种的碰撞几率也是很小的 然后用a-zA-Z0-9表示6位的62进制，62 ^ 6 = 56 800 235
 * 584，500多亿，可以把上面的1/4映射到里边
 * 
 * 但是此处的代码和http://www.jb51.net/article/29308.htm描述的有差别。 体现在三点 网址描述的是a-z0-5共32个字符。所以总数是 32 ^ 6 = 1 073 741 824，10多亿，比40亿小
 * 所以需要先二进制与0x3FFFFFFF，只处理32位，否则就超出了，不过32位也比32 ^ 6多一，不过几率很小，可以忽略。 然后就是最后计算每次二进制右移5，2 ^ 5 =
 * 32也是按照32位来计算的，如果按照62位计算，应该除以62才对。
 */
public class ShortUrlGenerator {
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        // 长连接： http://tech.sina.com.cn/i/2011-03-23/11285321288.shtml

        // 新浪解析后的短链接为： http://t.cn/h1jGSC
        String sLongUrl = "http://tech.sina.com.cn/i/2011-03-23/11285321288.shtml"; // 3BD768E58042156E54626860E241E999

        String[] aResult = shortUrl(sLongUrl);

        // 打印出结果
        for (int i = 0; i < aResult.length; i++) {
            System.out.println("[" + i + "]:::" + aResult[i]);
        }
    }

    public static String[] shortUrl(String url) {
        // 可以自定义生成 MD5 加密字符传前的混合 KEY
        String key = "wuguowei";

        // 要使用生成 URL 的字符
        String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h",

        "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",

        "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",

        "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",

        "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",

        "U", "V", "W", "X", "Y", "Z" };

        // 对传入网址进行 MD5 加密
        String sMD5EncryptResult = Md5Utils.getMd5(key + url);

        String hex = sMD5EncryptResult;
        System.out.println("hex all : " + hex);

        String[] resUrl = new String[4];

        for (int i = 0; i < 4; i++) {
            // 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
            String sTempSubString = hex.substring(i * 8, (i * 8) + 8);
            System.out.println(i + " hex : " + sTempSubString);

            // 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用 long ，则会越界
            long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
            System.out.println(i + " long : " + lHexLong);
            System.out.println(i + " hex : " + Long.toString(lHexLong, 16));

            StringBuilder buff = new StringBuilder();

            for (int j = 0; j < 6; j++) {
                // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
                long index = 0x0000003D & lHexLong;

                // 把取得的字符相加
                buff.append(chars[(int) index]);

                // 每次循环按位右移 5 位
                lHexLong = lHexLong >> 5;
            }

            // 把字符串存入对应索引的输出数组
            resUrl[i] = buff.toString();
        }

        return resUrl;
    }
}
