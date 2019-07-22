package com.hanwin.product.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @title RegexUtil
 * @description 校验工具类
 * @author peng_yqun
 * @version 
 * @create_date 2014-11-6
 * @copyright (c) CVIC SE
 *
 */
public class RegexUtil {
	// 每位加权因子
	private static int[] power = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

	/**
	 * 邮箱校验
	 *
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 是否为邮箱
	 *
	 * @param str
	 * @return
	 */
	public static boolean isEmail2(String str) {
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	/**
	 * @return
	 * @title compareSex
	 * @description 根据 身份证获取 性别 (15位最后一位 基数: 男 ；偶数: 女) (18位倒数第二位 基数: 男 ；偶数: 女)
	 * @author lu_qwen
	 * @create_date 2014-11-7
	 */
	public static int compareSex(String idCard) {
		int num = 3;
		if (idCard.length() == 15) {
			num = Integer.parseInt(idCard.substring(idCard.length() - 1));
			if ((num % 2) == 0) {
				return 0;
			} else {
				return 1;
			}
		} else if (idCard.length() == 18) {
			num = Integer.parseInt(idCard.substring(idCard.length() - 2,
					idCard.length() - 1));
			if ((num % 2) == 0) {
				return 0;
			} else {
				return 1;
			}
		}
		return 1;
	}

	/**
	 * 身高校验
	 *
	 * @param height
	 * @return
	 */
	public static boolean isHeight(String height) {
		Pattern p = Pattern.compile("[0-9]{2,3}");
		Matcher m = p.matcher(height);
		return m.matches();
	}


	/**
	 * 手机号码校验
	 *
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(16[0-9])|(19[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	/**
	 * 密码校验
	 *
	 * @param password
	 * @return
	 */
	public static boolean isValidatedPassword(String password) {
		Pattern p = Pattern.compile("[0-9a-zA-Z]{6,16}");
		Matcher m = p.matcher(password);
		return m.matches();
	}

	/**
	 * @param address
	 * @return
	 * @title isAddress
	 * @description 家庭住址校验
	 * @author qi_ying
	 * @create_date 2014-8-12
	 */
	public static boolean isAddress(String address) {
		if (Pattern.compile("[\u4e00-\u9fa5]{0,50}").matcher(address).matches())
			return true;
		else
			return false;
	}

	/**
	 * @param username
	 * @return
	 * @title isUsername
	 * @description 姓名校验
	 * @author qi_ying
	 * @create_date 2014-8-12
	 */
	public static boolean isUsername(String username) {
		if (Pattern.compile("[\u4e00-\u9fa5]{2,7}").matcher(username).matches())
			return true;
		else
			return false;
	}

	/**
	 * 密码强度校验
	 *
	 * @param password
	 * @return
	 */
	public static int passwordStrengh(String password) {
		int num = 0;
		if (Pattern.compile("[0-9]{6,16}").matcher(password).matches()) {
			num = 1;
		} else if (Pattern.compile("[a-z]{6,16}").matcher(password).matches()) {
			num = 1;
		} else if (Pattern.compile("[A-Z]{6,16}").matcher(password).matches()) {
			num = 1;
		} else if (Pattern.compile("[a-z0-9]{6,16}").matcher(password)
				.matches()) {
			num = 2;
		} else if (Pattern.compile("[A-Z0-9]{6,16}").matcher(password)
				.matches()) {
			num = 2;
		} else if (Pattern.compile("[A-Za-z]{6,16}").matcher(password)
				.matches()) {
			num = 2;
		} else if (Pattern.compile("[A-Za-z0-9]{6,16}").matcher(password)
				.matches()) {
			num = 3;
		}
		return num;
	}

	/**
	 * 身份证号码校验
	 *
	 * @param idcard
	 * @return
	 */
	public static boolean isValidatedAllIdcard(String idcard) {
		if (idcard.length() == 15) {
			idcard = convertIdcarBy15bit(idcard);
		}

		return vId(idcard);
	}

	public static String convertIdcarBy15bit(String idcard) {
		String idcard17 = null;
		// 非15位身份证
		if (idcard.length() != 15) {
			return null;
		}
		if (isDigital(idcard)) {
			// 获取出生年月日
			String birthday = idcard.substring(6, 12);
			Date birthdate = null;
			try {
				birthdate = new SimpleDateFormat("yyMMdd").parse(birthday);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar cday = Calendar.getInstance();
			cday.setTime(birthdate);
			String year = String.valueOf(cday.get(Calendar.YEAR));

			idcard17 = idcard.substring(0, 6) + year + idcard.substring(8);

			char c[] = idcard17.toCharArray();
			String checkCode = "";

			if (null != c) {
				int bit[] = new int[idcard17.length()];

				// 将字符数组转为整型数组
				bit = converCharToInt(c);
				int sum17 = 0;
				sum17 = getPowerSum(bit);

				// 获取和值与11取模得到余数进行校验码
				checkCode = getCheckCodeBySum(sum17);
				// 获取不到校验位
				if (null == checkCode) {
					return null;
				}

				// 将前17位与第18位校验码拼接
				idcard17 += checkCode;
			}
		} else { // 身份证包含数字
			return null;
		}
		return idcard17;
	}

	/**
	 * 15位和18位身份证号码的基本数字和位数验校
	 *
	 * @param idcard
	 * @return
	 */
	public boolean isIdcard(String idcard) {
		return idcard == null || "".equals(idcard) ? false : Pattern.matches(
				"(^//d{15}$)|(//d{17}(?://d|x|X)$)", idcard);
	}

	public static String getCheckCodeBySum(int sum17) {
		String checkCode = null;
		switch (sum17 % 11) {
			case 10:
				checkCode = "2";
				break;
			case 9:
				checkCode = "3";
				break;
			case 8:
				checkCode = "4";
				break;
			case 7:
				checkCode = "5";
				break;
			case 6:
				checkCode = "6";
				break;
			case 5:
				checkCode = "7";
				break;
			case 4:
				checkCode = "8";
				break;
			case 3:
				checkCode = "9";
				break;
			case 2:
				checkCode = "x";
				break;
			case 1:
				checkCode = "0";
				break;
			case 0:
				checkCode = "1";
				break;
		}
		return checkCode;
	}

	public static int[] converCharToInt(char[] c) throws NumberFormatException {
		int[] a = new int[c.length];
		int k = 0;
		for (char temp : c) {
			a[k++] = Integer.parseInt(String.valueOf(temp));
		}
		return a;
	}

	public static boolean isDigital(String str) {
		return str == null || "".equals(str) ? false : str.matches("^[0-9]*$");
	}

	public static int getPowerSum(int[] bit) {

		int sum = 0;

		if (power.length != bit.length) {
			return sum;
		}

		for (int i = 0; i < bit.length; i++) {
			for (int j = 0; j < power.length; j++) {
				if (i == j) {
					sum = sum + bit[i] * power[j];
				}
			}
		}
		return sum;
	}

	public static boolean isValidate18Idcard(String idcard) {
		// 非18位为假
		if (idcard.length() != 18) {
			return false;
		}
		// 获取前17位
		String idcard17 = idcard.substring(0, 17);
		// 获取第18位
		String idcard18Code = idcard.substring(17, 18);
		char c[] = null;
		String checkCode = "";
		// 是否都为数字
		if (isDigital(idcard17)) {
			c = idcard17.toCharArray();
		} else {
			return false;
		}

		if (null != c) {
			int bit[] = new int[idcard17.length()];

			bit = converCharToInt(c);

			int sum17 = 0;

			sum17 = getPowerSum(bit);

			// 将和值与11取模得到余数进行校验码判断
			checkCode = getCheckCodeBySum(sum17);
			if (null == checkCode) {
				return false;
			}
			// 将身份证的第18位与算出来的校码进行匹配，不相等就为假
			if (!idcard18Code.equalsIgnoreCase(checkCode)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isShengao(String height) {
		int lenght = height.length();
		if (lenght == 2) {
			if (Integer.parseInt(height.substring(0, 1)) < 6) {
				return false;
			} else {
				return true;
			}
		} else if (lenght == 3) {
			if (Integer.parseInt(height.substring(0, 1)) >= 3 || Integer.parseInt(height.substring(0, 1)) < 1) {
				return false;

			} else if (Integer.parseInt(height.substring(0, 1)) > 1 && Integer.parseInt(height.substring(1, 2)) > 4) {
				return false;
			} else {
				return true;
			}
		}

		return true;

	}


	//根据身份证号码获得出生日期
	public static String getbirthday(String idcard) {
		int length = idcard.length();
		String idcardbirth = "";
		if (length != 0 && length > 0) {
			if (length == 15) {
				idcardbirth = idcard.substring(6, 12);
				idcardbirth = "19" + idcardbirth.substring(0, 2) + "-" + idcardbirth.substring(2, 4) + "-" + idcardbirth.substring(4);
				return idcardbirth;
			} else {
				idcardbirth = idcard.substring(6, 14);
				idcardbirth = idcardbirth.substring(0, 4) + "-" + idcardbirth.substring(4, 6) + "-" + idcardbirth.substring(6);
			}
			return idcardbirth;
		} else {
			return null;
		}

	}


	public static boolean isChinaOrEnglishName(String name) {
		String regx = "(([\u4E00-\u9FA5]{2,5})|([a-zA-Z]{3,10}))";
		if (Pattern.compile(regx).matcher(name).matches())
			return true;
		else
			return false;
	}


	public static boolean isChinaOrEnglishName1(String name) {
		String regx = "(([\u4E00-\u9FA5]{2,10})|([a-zA-Z]{3,20}))";
		if (Pattern.compile(regx).matcher(name).matches())
			return true;
		else
			return false;
	}

	/**
	 * 判断字符中是否包含表情符
	 * @param content
	 * @return
	 */
	public static boolean hasEmoji(String content){
		Pattern pattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]");
		Matcher matcher = pattern.matcher(content);
		if(matcher .find()){
			return true;
		}
		return false;
	}

	//中文名字
	public static boolean isChinaName(String name) {
		String regx = "[\u4E00-\u9FA5]{2,5}";
		if (Pattern.compile(regx).matcher(name).matches())
			return true;
		else
			return false;
	}

	//英文名字
	public static boolean isEnglishName(String name) {
		String regx = "[a-zA-Z]{3,12}";
		if (Pattern.compile(regx).matcher(name).matches())
			return true;
		else
			return false;
	}
	//过滤特殊字符

	public static boolean stingFilter(String ss) {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		if (Pattern.compile(regEx).matcher(ss).matches())
			return true;
		else
			return false;

	}


	public static boolean isNickname(String ss) {
		String regEx = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,5}$";
		if (Pattern.compile(regEx).matcher(ss).matches())
			return true;
		else
			return false;

	}

	/**
	 * 隐藏手机号
	 *
	 * @param str
	 * @return
	 */
	public static String toparsephonenum(String str) {
		return str.substring(0, str.length() - (str.substring(3)).length())
				+ "****" + str.substring(7);
	}

	/**
	 * @param num
	 * @return
	 * @title 四舍五入
	 * @description TODO
	 * @author peng_yqun
	 * @create_date 2014-12-17
	 */
	public static double toparsenum(double num) {
		return new java.math.BigDecimal(num).setScale(1, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * @return
	 * @title isExitsSdcard
	 * @description 检测Sdcard是否存在
	 * @author lu_qwen
	 * @create_date 2014-12-19
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * @param expressnumber
	 * @return
	 * @title isExpressNumber
	 * @description 快递单号
	 * @author zhou_xi
	 * @create_date 2015-1-15
	 */
	public static boolean isExpressNumber(String expressnumber) {
		Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
		Matcher m = p.matcher(expressnumber);
		return m.matches();
	}

	/**
	 * @param textstr
	 * @return
	 * @title deleteLuanma
	 * @description 去除乱码
	 * @author zhou_xi
	 * @create_date 2015-1-15
	 */
	public static String deleteLuanma(String textstr) {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？ ]|[\u4E00-\u9FA5]|[a-zA-Z0-9]";
		String newstr = "";
		for (int i = 0; i < textstr.length(); i++) {
			if (Pattern.compile(regEx).matcher(textstr.substring(i, i + 1)).matches()) {
				newstr = newstr + textstr.substring(i, i + 1);
			}

		}
		return newstr;

	}

	/**
	 * @param textstr
	 * @return
	 * @title getstrlength
	 * @description 判断字符串字符长度
	 * @author zhou_xi
	 * @create_date 2015-1-19
	 */

	public static int getstrlength(String textstr) {
		byte[] newstr = textstr.getBytes();
		return newstr.length;
	}

	/**
	 * @return
	 * @title getDate
	 * @description 获取系统当前时间，并处理
	 * @author zhou_xi
	 * @create_date 2015-2-6
	 */

	private static String getDate() {
		Calendar c = Calendar.getInstance();
		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		if (day.length() == 1) {
			day = "0" + day;

		}
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));
		String secon = String.valueOf(c.get(Calendar.SECOND));
		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
				+ mins + ":" + secon);

		return sbBuffer.toString();
	}

	public static String getVerison(Context context) throws PackageManager.NameNotFoundException {
		PackageManager manager = context.getPackageManager();
		PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
		return info.versionName;
	}

	//当前日期一个月后的日期
	public String nextMonth() {
		Date date = new Date();
		int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));//取到年份值
		int month = Integer.parseInt(new SimpleDateFormat("MM").format(date)) + 1;//取到鱼粉值
		int day = Integer.parseInt(new SimpleDateFormat("dd").format(date));//取到天值
		if (month == 0) {
			year -= 1;
			month = 12;
		} else if (day > 28) {
			if (month == 2) {
				if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
					day = 29;
				} else day = 28;
			} else if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 31) {
				day = 30;
			}
		}
		String y = year + "";
		String m = "";
		String d = "";
		if (month < 10) m = "0" + month;
		else m = month + "";
		if (day < 10) d = "0" + day;
		else d = day + "";
		System.out.println(y + "-" + m + "-" + d);
		return y + "-" + m + "-" + d;
	}

	//两个日期之间的所有日期
	public static List<Date> findDates(Date dBegin, Date dEnd) {
		List lDate = new ArrayList();
		lDate.add(dBegin);
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);
		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())) {
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			lDate.add(calBegin.getTime());
		}
		return lDate;
	}

	public static boolean isNull(String str) {
		if (null == str || "".equals(str.replace(" ", "")) || "null".equals(str)) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isRunningApp(Context context) {
		boolean isAppRunning = false;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
		for (ActivityManager.RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals("com.weili.beegoingwl") && info.baseActivity.getPackageName().equals("com.weili.beegoingwl")) {
				isAppRunning = true;
				// find it, break
				break;
			}
		}
		return isAppRunning;
	}


	//MD5加密
	public static String getMD51(String val) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(val.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10) hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	public static String getMD5(String str) {
		try {
			// 生成一个MD5加密计算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(str.getBytes());
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			System.out.println("MD5加密出现错误");
		}
		return null;
	}



	/**
	 * 加密文件 2015年7月1日 上午12:06:37
	 */
	public static String getMd5ByFile(File file) {
		String value = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}
	/*
     * 将时间戳转换为时间
     */
	public static String stampToDate(long time){
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date date = new Date(time);
		res = simpleDateFormat.format(date);
		return res;
	}


	/*
     * 将时间戳转换为时间
     */
	public static String stampToDate1(long time){
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date(time);
		res = simpleDateFormat.format(date);
		return res;
	}





	public static long getMinute(long start){
		long current = System.currentTimeMillis();
		long result = current-start;
		return result;
	}


	public static boolean uninstallSoftware(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
			if (packageInfo != null) {
				return true;
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}


	public static boolean vIDNumByRegex(String idNum) {

		String curYear = "" + Calendar.getInstance().get(Calendar.YEAR);
		int y3 = Integer.valueOf(curYear.substring(2, 3));
		int y4 = Integer.valueOf(curYear.substring(3, 4));
		// 43 0103 1973 0 9 30 051 9
		return idNum.matches("^(1[1-5]|2[1-3]|3[1-7]|4[1-6]|5[0-4]|6[1-5]|71|8[1-2])\\d{4}(19\\d{2}|20([0-" + (y3 - 1) + "][0-9]|" + y3 + "[0-" + y4
				+ "]))(((0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])))\\d{3}([0-9]|x|X)$");
		// 44 1825 1988 0 7 1 3 003 4
	}

	private static int ID_LENGTH = 17;
	public static boolean vIDNumByCode(String idNum) {

		// 系数列表
		int[] ratioArr = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

		// 校验码列表
		char[] checkCodeList = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

		// 获取身份证号字符数组
		char[] cIds = idNum.toCharArray();

		// 获取最后一位（身份证校验码）
		char oCode = cIds[ID_LENGTH];
		int[] iIds = new int[ID_LENGTH];
		int idSum = 0;// 身份证号第1-17位与系数之积的和
		int residue = 0;// 余数(用加出来和除以11，看余数是多少？)

		for (int i = 0; i < ID_LENGTH; i++) {
			iIds[i] = cIds[i] - '0';
			idSum += iIds[i] * ratioArr[i];
		}

		residue = idSum % 11;// 取得余数

		return Character.toUpperCase(oCode) == checkCodeList[residue];
	}

	public static boolean vId(String idNum) {
		return vIDNumByCode(idNum) && vIDNumByRegex(idNum);
	}

	public static int getMinute(String string){
		int minutestr= 0;

		String[] strings = string.split(":");
		if(2==strings.length){
			minutestr = Integer.parseInt(strings[0]);
		}else if(3==strings.length){
			minutestr = Integer.parseInt(strings[0])*60+ Integer.parseInt(strings[1]);

		}

		return minutestr;
	}


	/**
	 * 车辆名字
	 *
	 * @param bikename
	 * @return
	 */
	public static boolean isBikeName(String bikename) {
		boolean ist = false;

		if("C".equals(bikename.substring(0,1)) && Integer.parseInt(bikename.replace("C",""))>0 && Integer.parseInt(bikename.replace("C",""))<1000){
			ist =true;
		}
		return ist;
	}



	public static int compareVersion(String version1, String version2) {
		if (version1.equals(version2)) {
			return 0;
		}

		String[] version1Array = version1.split("\\.");
		String[] version2Array = version2.split("\\.");

		int index = 0;
		int minLen = Math.min(version1Array.length, version2Array.length);
		int diff = 0;

		while (index < minLen && (diff = Integer.parseInt(version1Array[index]) - Integer.parseInt(version2Array[index])) == 0) {
			index ++;
		}

		if (diff == 0) {
			for (int i = index; i < version1Array.length; i ++) {
				if (Integer.parseInt(version1Array[i]) > 0) {
					return 1;
				}
			}

			for (int i = index; i < version2Array.length; i ++) {
				if (Integer.parseInt(version2Array[i]) > 0) {
					return -1;
				}
			}

			return 0;
		} else {
			return diff > 0 ? 1 : -1;
		}
	}

	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	public static String nickParse(String nickname){
		if(null !=nickname && !"".equals(nickname)){
			return nickname.substring(0,1)+"****"+nickname.substring(nickname.length()-1);

		}else {
			return nickname;
		}


	}

}
