package com.vendormanagement.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

public class Util {

	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();

	}

	public static boolean isEmpty(List<?> list) {
		return list == null || list.isEmpty();

	}

	public static List<String> whichStringIsEmpty(Map<String, String> strings) {
		List<String> emptyStrings = new ArrayList<>();

		if (!isEmpty(strings)) {
			for (Entry<String, String> string : strings.entrySet()) {

				if (StringUtils.isBlank(string.getValue())) {
					emptyStrings.add(string.getKey());
				}
			}
		}

		return emptyStrings;
	}

	public static boolean canParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String getToday() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

}
