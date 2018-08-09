package com.vendormanagement;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;

import com.vendormanagement.bo.Invoice;

/**
 * Validates invoice.html on xls file and set WOI.
 * 
 * @author pborsoni
 *
 */
public class InvoiceValidator {

	private static final String NORMAL_HOUR = "NORMAL_HOUR";
	private static final String OVERTIME = "OVERTIME";
	private static final String STANDBY = "HORA STANDBY";
	private static final int H_COLUMN = 7;
	private static final int B_COLUMN = 1;
	private static final int C_COLUMN = 2;
	private static final int I_COLUMN = 8;
	private static final int M_COLUMN = 12;
	private static final int Q_COLUMN = 16;
	private static final int U_WOI_COLUMN = 20;
	private static final int V_WOI_COLUMN = 21;
	private static final int W_WOI_COLUMN = 22;
	private static final int X_WOI_COLUMN = 23;

	private String billingXlsFile;
	private List<Invoice> invoiceList;
	private List<Invoice> invoiceSuccessList = new ArrayList<>();
	private List<Invoice> invoiceFailedList = new ArrayList<>();
	private Workbook workbook;

	public InvoiceValidator(String billingXlsFile, List<Invoice> invoiceList) {
		this.billingXlsFile = billingXlsFile;
		this.invoiceList = invoiceList;
	}

	public void validate() throws IOException {

		try (FileInputStream fileInputStream = new FileInputStream(billingXlsFile);) {

			workbook = WorkbookFactory.create(fileInputStream);

			for (Invoice invoice : invoiceList) {

				validate(invoice);
			}
		} catch (IOException | InvalidFormatException e) {

			throw new IOException("An error has occurred. Could not open billig spreadsheet: "
					+ billingXlsFile + " \n " + e.getMessage());
		} finally {

			// Open FileOutputStream to write updates
			FileOutputStream output_file = new FileOutputStream(billingXlsFile);
			// write changes
			workbook.write(output_file);
			// close the stream
			output_file.close();
		}

	}

	private void validate(Invoice invoice) {

		String sheetName = invoice.getMonth() + invoice.getYear();
		Sheet sheet = workbook.getSheet(sheetName);

		if (sheet == null) {
			invoice.setErrorReason(String.format("Did not find tab %s on spreadsheet", sheetName));
			invoiceFailedList.add(invoice);
			return;
		}

		Iterator<Row> rowIterator = sheet.rowIterator();
		rowIterator.next();
		Row row = null;
		boolean foundRow = false;

		while (rowIterator.hasNext()) {
			row = (Row) rowIterator.next();

			if (row == null || row.getCell(H_COLUMN) == null) {
				break;
			}

			String hColumn = row.getCell(H_COLUMN).getStringCellValue();

			if (StringUtils.equalsIgnoreCase(hColumn, invoice.getPurchaseOrderNumber())) {
				foundRow = true;
				break;
			}
		}

		if (row == null || !foundRow) {
			invoice.setErrorReason(String.format(" Did not find purchase order number %s on column H",
					invoice.getPurchaseOrderNumber()));
			invoiceFailedList.add(invoice);
			return;
		}

		if (!StringUtils.equalsIgnoreCase(row.getCell(B_COLUMN).getStringCellValue(), invoice.getId())) {
			invoice.setErrorReason(String.format("Did not find id %s on column B", invoice.getId()));
			invoiceFailedList.add(invoice);
			return;
		}

		if (!StringUtils.equalsIgnoreCase(row.getCell(C_COLUMN).getStringCellValue(), invoice.getResource())) {
			invoice.setErrorReason(String.format("Did not find resource %s on column C", invoice.getResource()));
			invoiceFailedList.add(invoice);
			return;
		}

		for (Entry<String, String> hoursEntry : invoice.getHoursMap().entrySet()) {

			if (NORMAL_HOUR.equals(hoursEntry.getKey())) {

				String textValue = NumberToTextConverter.toText(row.getCell(I_COLUMN).getNumericCellValue());

				if (!StringUtils.equalsIgnoreCase(textValue, hoursEntry.getValue())) {
					invoice.setErrorReason(String.format("Did not find %s hours on column I", hoursEntry.getValue()));
					invoiceFailedList.add(invoice);
					return;
				}
			}

			if (OVERTIME.equals(hoursEntry.getKey())) {

				String textValue = NumberToTextConverter.toText(row.getCell(M_COLUMN).getNumericCellValue());

				if (!StringUtils.equalsIgnoreCase(textValue, hoursEntry.getValue())) {
					invoice.setErrorReason(String.format("Did not find %s hours on column M", hoursEntry.getValue()));
					invoiceFailedList.add(invoice);
					return;
				}
			}

			if (STANDBY.equals(hoursEntry.getKey())) {

				String textValue = NumberToTextConverter.toText(row.getCell(Q_COLUMN).getNumericCellValue());

				if (!StringUtils.equalsIgnoreCase(textValue, hoursEntry.getValue())) {
					invoice.setErrorReason(String.format("Did not find %s hours on column Q", hoursEntry.getValue()));
					invoiceFailedList.add(invoice);
					return;
				}
			}
		}

		String woi = invoice.getWoi();
		if (setWoi(row, woi)) {
			invoiceSuccessList.add(invoice);

		} else {
			invoice.setErrorReason(String.format("Did not populate WOI %s cause columns from U to X are already filled", woi));
			invoiceFailedList.add(invoice);
		}

	}

	private boolean setWoi(Row row, String woi) {

		if (StringUtils.isBlank(row.getCell(U_WOI_COLUMN).getStringCellValue())) {
			row.getCell(U_WOI_COLUMN).setCellValue(woi);
			return true;
		}
		if (StringUtils.isBlank(row.getCell(V_WOI_COLUMN).getStringCellValue())) {
			row.getCell(V_WOI_COLUMN).setCellValue(woi);
			return true;
		}
		if (StringUtils.isBlank(row.getCell(W_WOI_COLUMN).getStringCellValue())) {
			row.getCell(W_WOI_COLUMN).setCellValue(woi);
			return true;
		}
		if (StringUtils.isBlank(row.getCell(X_WOI_COLUMN).getStringCellValue())) {
			row.getCell(X_WOI_COLUMN).setCellValue(woi);
			return true;
		}

		return false;

	}

	public List<Invoice> getSuccessInvoiceList() {
		return invoiceSuccessList;
	}

	public List<Invoice> getFailedInvoiceList() {
		return invoiceFailedList;
	}
}
