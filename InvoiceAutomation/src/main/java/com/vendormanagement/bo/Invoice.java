package com.vendormanagement.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.vendormanagement.util.Util;

/**
 * BO class that represents an invoice.htm file
 * 
 * @author pborsoni
 *
 */
public class Invoice {

	public static final String NORMAL_HOUR = "NORMAL_HOUR";
	public static final String OVERTIME = "OVERTIME";
	public static final String STANDBY = "STANDBY";

	// column U to X
	private String woi;

	// column H
	private String purchaseOrderNumber;

	// corresponding tab
	private String month;
	private String year;

	// column B
	private String id;

	// column C
	private String resource;

	/*
	 * If HORA NORMAL, check column I .If HORA EXTRA, check column M .If
	 * STANDBY, check column Q
	 */
	private Map<String, String> hoursMap = new HashMap<>();

	private String fileName;
	private String errorReason;

	public String getWoi() {
		return woi;
	}

	public void setWoi(String woi) {
		this.woi = woi;
	}

	public String getPurchaseOrderNumber() {
		return purchaseOrderNumber;
	}

	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {

		String lowerCase = StringUtils.lowerCase(month);

		String monthEdited = StringUtils.capitalize(lowerCase);

		this.month = monthEdited;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public Map<String, String> getHoursMap() {
		return hoursMap;
	}

	public void addHours(String hourType, String hours) {
		if (StringUtils.isNoneBlank(hourType, hours)) {

			hoursMap.put(StringUtils.upperCase(hourType), hours);
		}
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {

		if (Util.canParseInt(year)) {
			this.year = year;
		}
	}

	public boolean isValidInvoice() {

		return StringUtils.isNoneBlank(id, resource, month, year, purchaseOrderNumber, woi) && !Util.isEmpty(hoursMap);
	}

	public String getMissingFields() {

		if (Util.isEmpty(hoursMap)) {
			return "total hours or hour type";
		}

		Map<String, String> strings = new HashMap<>();
		strings.put("id", id);
		strings.put("resource", resource);
		strings.put("month", month);
		strings.put("year", year);
		strings.put("purchase order number", purchaseOrderNumber);
		strings.put("woi", woi);

		List<String> emptyStrings = Util.whichStringIsEmpty(strings);

		if (Util.isEmpty(emptyStrings)) {
			return null;
		} else {
			return emptyStrings.toString();
		}

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

	@Override
	public String toString() {
		return fileName;
	}
}
