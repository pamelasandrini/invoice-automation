package com.vendormanagement;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.vendormanagement.bo.Invoice;

/**
 * Invoice reader class
 * 
 * @author pborsoni
 *
 */
public class InvoiceReader {

	private static final String WOI_TEXT = "Reference document#";
	private static final String PON_TEXT = "Purchase order number";
	private static final String INVOICE_FREE_TEXT = "Invoice item free text";
	private static final Splitter TEXT_SPLITTER = Splitter.on('-').trimResults();
	private static final Splitter HOURS_SPLITTER = Splitter.on('h').trimResults();
	private static final Splitter MONTH_SPLITTER = Splitter.on('/').trimResults();
	private static final int YEAH_SPLIT = 1;
	private static final int MONTH_SPLIT = 0;
	private static final int HOURS_TYPE_SPLIT = 4;
	private static final int HOURS_TEXT_SPLIT = 3;
	private static final int MONTH_TEXT_SPLIT = 2;
	private static final int RESOURCE_SPLIT = 1;
	private static final int ID_SPLIT = 0;

	/**
	 * Extract info from invoice.htm file and create an Invoice object
	 * 
	 * @param invoiceHtmFile
	 * @return
	 * @throws IOException
	 */
	public static Invoice buildInvoice(String invoiceHtmFile) throws IOException {

		CharSource charSource = Files.asCharSource(new File(invoiceHtmFile), Charsets.UTF_8);

		if (charSource.isEmpty()) {
			return null;
		}

		Invoice invoice = new Invoice();
		invoice.setFileName(invoiceHtmFile);

		ImmutableList<String> lines = charSource.readLines();

		processLines(invoice, lines);

		return invoice;
	}

	private static void processLines(Invoice invoice, ImmutableList<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);

			String value = extractValueFromLine(line);

			if (StringUtils.isBlank(value)) {
				continue;
			}

			switch (value) {
			case WOI_TEXT:

				setInvoiceField(extractValueFromLine(lines.get(i + 1)), invoice::setWoi);
				break;
			case PON_TEXT:
				setInvoiceField(extractValueFromLine(lines.get(i + 1)), invoice::setPurchaseOrderNumber);
				break;

			case INVOICE_FREE_TEXT:

				String nextLineValue = extractValueFromLine(lines.get(i + 1));

				setInvoiceFreeFields(invoice, nextLineValue);

			default:
				// do nothing
				break;
			}

		}
	}

	private static void setInvoiceFreeFields(Invoice invoice, String nextLineValue) {
		List<String> lineSplitted = TEXT_SPLITTER.splitToList(nextLineValue);

		String id = lineSplitted.get(ID_SPLIT);
		String resource = lineSplitted.get(RESOURCE_SPLIT);
		String monthText = lineSplitted.get(MONTH_TEXT_SPLIT);
		String hoursText = lineSplitted.get(HOURS_TEXT_SPLIT);
		String hourType = lineSplitted.get(HOURS_TYPE_SPLIT);

		String hours = HOURS_SPLITTER.splitToList(hoursText).get(0);

		List<String> monthSplitted = MONTH_SPLITTER.splitToList(monthText);
		String month = monthSplitted.get(MONTH_SPLIT);
		String year = monthSplitted.get(YEAH_SPLIT);

		// split next line and set values on invoice
		setInvoiceField(id, invoice::setId);
		setInvoiceField(resource, invoice::setResource);
		setInvoiceField(month, invoice::setMonth);
		setInvoiceField(year, invoice::setYear);

		invoice.addHours(hourType, hours);
	}

	private static void setInvoiceField(String value, Consumer<String> action) {

		if (StringUtils.isNotBlank(value)) {
			action.accept(value);
		}
	}

	private static String extractValueFromLine(String line) {
		if (StringUtils.isEmpty(line)) {
			return null;
		}

		Pattern pattern = Pattern.compile(">(.+?)</.*");
		Matcher matcher = pattern.matcher(line);

		String group = null;
		if (matcher.find()) {
			group = matcher.group(1);

		}

		return group;
	}

}
