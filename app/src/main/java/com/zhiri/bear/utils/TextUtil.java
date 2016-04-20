package com.zhiri.bear.utils;

import android.text.Editable;
import android.text.TextUtils;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

	public static boolean isValidate(String content) {
		return content != null && !"".equals(content.trim());
	}

	public static boolean isValidate(CharSequence content) {
		return content != null && !"".equals(content.toString().trim());
	}

	public static boolean isValidate(String[] content) {
		return content != null && content.length > 0;
	}

	public static boolean isValidate(Collection<?> list) {
		return list != null && list.size() > 0;
	}

	public static boolean isEmpty(String str) {
		return TextUtils.isEmpty(str);
	}

	public static boolean isEmpty(Editable editableText) {
		return TextUtils.isEmpty(editableText);
	}

	public static boolean isEmpty(Object obj) {
		return obj != null ? true : false;
	}

	public static boolean isPhone(String phoneNumber){
		String expression = "^(((13[0-9]{1})|(18[0-9]{1})|(17[6-9]{1})|(15[0-9]{1}))+\\d{8})$";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		boolean isValid = false;
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}


}
