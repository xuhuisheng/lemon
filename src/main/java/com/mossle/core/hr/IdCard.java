package com.mossle.core.hr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdCard {
    private static Logger logger = LoggerFactory.getLogger(IdCard.class);
    private boolean valid;
    private String message;
    private String areaCode;
    private String areaName;
    private Date birthday;
    private String gender;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    /*********************************** 身份证验证开始 ****************************************/
    /**
     * <p>
     * 身份证号码验证
     * </p>
     * <p>
     * 1、号码的结构 公民身份号码是特征组合码，由十七位数字本体码和一位校验码组成。
     * </p>
     * <p>
     * 排列顺序从左至右依次为：六位数字地址码， 八位数字出生日期码，三位数字顺序码和一位数字校验码。
     * </p>
     * <p>
     * 2、地址码(前六位数） 表示编码对象常住户口所在县(市、旗、区)的行政区划代码，按GB/T2260的规定执行。
     * </p>
     * <p>
     * 3、出生日期码（第七位至十四位） 表示编码对象出生的年、月、日，按GB/T7408的规定执行，年、月、日代码之间不用分隔符。
     * </p>
     * <p>
     * 4、顺序码（第十五位至十七位） 表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号， 顺序码的奇数分配给男性，偶数分配给女性。
     * </p>
     * <p>
     * 5、校验码（第十八位数）
     * </p>
     * <p>
     * （1）十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0, ... , 16 ，先对前17位数字的权求和
     * </p>
     * <p>
     * Ai:表示第i位置上的身份证号码数字值 Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
     * </p>
     * <p>
     * （2）计算模 Y = mod(S, 11)
     * </p>
     * <p>
     * （3）通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 10 校验码: 1 0 X 9 8 7 6 5 4 3 2
     * </p>
     */

    /**
     * 功能：身份证的有效验证
     * 
     * @param IDStr
     *            身份证号
     * @return 有效：返回"" 无效：返回String信息
     */
    public static IdCard parse(String text) {
        IdCard idCard = new IdCard();

        String[] valCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
                "3", "2" };
        String[] wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2" };
        String ai = "";

        // ================ 号码的长度 15位或18位 ================
        if ((text.length() != 15) && (text.length() != 18)) {
            idCard.setValid(false);
            idCard.setMessage("身份证号码长度应该为15位或18位。");

            return idCard;
        }

        // =======================(end)========================

        // ================ 数字 除最后以为都为数字 ================
        if (text.length() == 18) {
            ai = text.substring(0, 17);
        } else if (text.length() == 15) {
            ai = text.substring(0, 6) + "19" + text.substring(6, 15);
        }

        if (!isNumeric(ai)) {
            idCard.setValid(false);
            idCard.setMessage("身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。" + text);

            return idCard;
        }

        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = ai.substring(6, 10); // 年份
        String strMonth = ai.substring(10, 12); // 月份
        String strDay = ai.substring(12, 14); // 日

        if (!isDate(strYear + "-" + strMonth + "-" + strDay)) {
            idCard.setValid(false);
            idCard.setMessage("身份证生日无效。" + text);

            return idCard;
        }

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = null;

        try {
            birthday = dateFormat
                    .parse(strYear + "-" + strMonth + "-" + strDay);

            if (((calendar.get(Calendar.YEAR) - Integer.parseInt(strYear, 10)) > 150)
                    || ((calendar.getTime().getTime() - birthday.getTime()) < 0)) {
                idCard.setValid(false);
                idCard.setMessage("身份证生日不在有效范围。" + text);

                return idCard;
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            idCard.setValid(false);
            idCard.setMessage("身份证生日无效。" + text);

            return idCard;
        } catch (java.text.ParseException e) {
            logger.error(e.getMessage(), e);
            idCard.setValid(false);
            idCard.setMessage("身份证生日无效。" + text);

            return idCard;
        }

        if ((Integer.parseInt(strMonth, 10) > 12)
                || (Integer.parseInt(strMonth, 10) == 0)) {
            idCard.setValid(false);
            idCard.setMessage("身份证月份无效" + text);

            return idCard;
        }

        if ((Integer.parseInt(strDay, 10) > 31)
                || (Integer.parseInt(strDay, 10) == 0)) {
            idCard.setValid(false);
            idCard.setMessage("身份证日期无效" + text);

            return idCard;
        }

        // =====================(end)=====================

        // ================ 地区码时候有效 ================
        Map<String, String> areaCodeMap = getAreaMap();

        if (areaCodeMap.get(ai.substring(0, 2)) == null) {
            idCard.setValid(false);
            idCard.setMessage("身份证地区编码错误。" + text);

            return idCard;
        }

        // ==============================================

        // ================ 判断最后一位的值 ================
        int totalmulAiWi = 0;

        for (int i = 0; i < 17; i++) {
            totalmulAiWi = totalmulAiWi
                    + (Integer.parseInt(String.valueOf(ai.charAt(i)), 10) * Integer
                            .parseInt(wi[i], 10));
        }

        int modValue = totalmulAiWi % 11;
        String strVerifyCode = valCodeArr[modValue];
        ai = ai + strVerifyCode;

        if ((text.length() == 18) && (!ai.equals(text))) {
            idCard.setValid(false);
            idCard.setMessage("身份证无效，不是合法的身份证号码" + text);

            return idCard;
        }

        // =====================(end)=====================
        idCard.setValid(true);
        idCard.setAreaCode(ai.substring(0, 2));
        idCard.setAreaName(areaCodeMap.get(ai.substring(0, 2)));
        idCard.setBirthday(birthday);

        int sequence = Integer.parseInt(ai.substring(14, 17), 10);
        idCard.setGender(((sequence & 1) == 1) ? "male" : "female");

        return idCard;
    }

    /**
     * 功能：设置地区编码
     * 
     * @return Hashtable 对象
     */
    private static Map<String, String> getAreaMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("11", "北京");
        map.put("12", "天津");
        map.put("13", "河北");
        map.put("14", "山西");
        map.put("15", "内蒙古");
        map.put("21", "辽宁");
        map.put("22", "吉林");
        map.put("23", "黑龙江");
        map.put("31", "上海");
        map.put("32", "江苏");
        map.put("33", "浙江");
        map.put("34", "安徽");
        map.put("35", "福建");
        map.put("36", "江西");
        map.put("37", "山东");
        map.put("41", "河南");
        map.put("42", "湖北");
        map.put("43", "湖南");
        map.put("44", "广东");
        map.put("45", "广西");
        map.put("46", "海南");
        map.put("50", "重庆");
        map.put("51", "四川");
        map.put("52", "贵州");
        map.put("53", "云南");
        map.put("54", "西藏");
        map.put("61", "陕西");
        map.put("62", "甘肃");
        map.put("63", "青海");
        map.put("64", "宁夏");
        map.put("65", "新疆");
        map.put("71", "台湾");
        map.put("81", "香港");
        map.put("82", "澳门");
        map.put("91", "国外");

        return map;
    }

    /**
     * 功能：判断字符串是否为数字
     * 
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);

        return isNum.matches();
    }

    /**
     * 功能：判断字符串是否为日期格式
     * 
     * @param str
     * @return
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern.compile("^\\d{4}\\-\\d{2}\\-\\d{2}$");
        Matcher m = pattern.matcher(strDate);

        return m.matches();
    }

    /*********************************** 身份证验证结束 ****************************************/
}
