package com.vendormanagement;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import com.vendormanagement.bo.Invoice;
import com.vendormanagement.util.FileUtil;
import com.vendormanagement.util.Util;

public class ProcessInvoiceCommand implements ActionListener {

	private JTextField txtFieldDirPath;
	private JFrame frame;

	public ProcessInvoiceCommand(JTextField txtFieldDirPath, JFrame frame) {
		this.txtFieldDirPath = txtFieldDirPath;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {

			System.out.println("start processing invoice folder");

			String dirPath = txtFieldDirPath.getText();

			System.out.println("dirPath: " + dirPath);

			if (StringUtils.isBlank(dirPath)) {
				System.out.println("terminate program as dirPath is empty");
				terminateProgram("Execution stopped. Empty directory.", false);
			}

			FileUtil fileUtil = new FileUtil(dirPath);
			List<String> invoiceHtmFiles = fileUtil.getHtmFileList();

			String billingXlsFile = fileUtil.getXlsFile();

			if (Util.isEmpty(invoiceHtmFiles)) {
				System.out.println("terminate program as dirPath has no invoice file");
				terminateProgram("Execution stopped. No invoice file .htm or .html found", false);
			}
			if (StringUtils.isBlank(billingXlsFile)) {
				System.out.println("terminate program as dirPath has no xls file");
				terminateProgram("Execution stopped. No billing file .xls or .xlsx found", false);
			}

			System.out.println("creating log file");
			fileUtil.createLogFile();

			System.out.println("reading invoice files");
			List<Invoice> invoiceList = new ArrayList<>();
			for (String invoiceHtmFile : invoiceHtmFiles) {
				Invoice invoice = InvoiceReader.buildInvoice(invoiceHtmFile);

				if (invoice == null) {
					fileUtil.log(String.format("Could not open file %s", invoiceHtmFile));
					continue;
				}

				if (invoice.isValidInvoice()) {
					invoiceList.add(invoice);
				} else {
					fileUtil.log(String.format("Invoice %s is not valid, missing: %s ", invoiceHtmFile,
							invoice.getMissingFields()));
				}
			}

			InvoiceValidator validator = new InvoiceValidator(billingXlsFile, invoiceList);
			validator.validate();

			List<Invoice> successInvoiceList = validator.getSuccessInvoiceList();

			if (!Util.isEmpty(successInvoiceList)) {

				fileUtil.log("**************************** Invoice files with success ****************************");

				for (Invoice invoiceSuccess : successInvoiceList) {
					fileUtil.log(String.format("Woi added in Invoice %s", invoiceSuccess.getFileName()));
				}
			}

			List<Invoice> failedInvoiceList = validator.getFailedInvoiceList();
			if (!Util.isEmpty(failedInvoiceList)) {

				fileUtil.log("**************************** Invoice files with error ****************************");
				for (Invoice invoiceFailed : failedInvoiceList) {
					fileUtil.log(String.format("Invoice %s was not populated. %s ", invoiceFailed.getFileName(),
							invoiceFailed.getErrorReason()));

				}
			}
		} catch (IOException ex) {
			System.out.println("Exception: " + ex);
			terminateProgram("Erro: " + ex, false);
		} catch (Exception ex) {
			System.out.println("Exception: " + ex);
			terminateProgram("Erro: " + ex + " " + ex.getMessage(), false);
		}

		System.out.println("terminate program successfully");
		terminateProgram("Execution completed successfully! Please check the log.", true);
	}

	private void terminateProgram(String message, boolean success) {
		frame.dispose();

		JLabel jLabel = new JLabel(message);
		jLabel.setFont(new Font("Arial", Font.BOLD, 18));

		String label = success ? "SUCCESS" : "ERROR";

		int errorMessage = success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;

		JOptionPane.showMessageDialog(null, jLabel, label, errorMessage);

		System.exit(0);
	}

}
