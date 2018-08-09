package com.vendormanagement;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Main class for processing Invoice
 * 
 * @author pborsoni
 *
 */
public class InvoiceProcessor {

	private static final Font FONT = new Font("Tahoma", Font.PLAIN, 20);
	private static JFrame frame;
	private static JTextField txtFieldDirPath;

	public static void main(String[] args) {
		
		System.out.println("start program");

		// create panel
		frame = new JFrame();
		frame.setTitle("Process Invoice");
		frame.setBounds(100, 100, 700, 300);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(true);

		addFrameComponents();

		JButton btnProcess = new JButton("Process");
		btnProcess.setName("process");
		btnProcess.setBounds(161, 100, 130, 33);
		btnProcess.setFont(FONT);

		// process files
		btnProcess.addActionListener(new ProcessInvoiceCommand(txtFieldDirPath, frame));
		frame.getContentPane().add(btnProcess);

		frame.setVisible(true);

	}

	private static void addFrameComponents() {
		JLabel lblDirPath = new JLabel("Choose the dir");
		lblDirPath.setFont(FONT);
		lblDirPath.setBounds(50, 21, 400, 35);
		frame.getContentPane().add(lblDirPath);

		txtFieldDirPath = new JTextField();
		txtFieldDirPath.setEditable(false);
		txtFieldDirPath.setFont(FONT);
		txtFieldDirPath.setBounds(50, 60, 460, 30);
		frame.getContentPane().add(txtFieldDirPath);
		txtFieldDirPath.setColumns(10);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setFont(FONT);
		btnBrowse.addActionListener(new FileChooserCommand(frame, txtFieldDirPath));
		btnBrowse.setBounds(520, 59, 120, 33);
		frame.getContentPane().add(btnBrowse);
	}

}
