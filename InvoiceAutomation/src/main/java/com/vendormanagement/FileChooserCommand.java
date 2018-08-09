package com.vendormanagement;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

public class FileChooserCommand implements ActionListener {

	private Component parent = null;
	private JTextField inputText = null;

	public FileChooserCommand(Component parent, JTextField inputText) {
		this.parent = parent;
		this.inputText = inputText;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser("C:\\");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fc.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFolder = fc.getSelectedFile();
			String path = selectedFolder.getAbsolutePath();
			path = path.replaceAll("\\\\", "/");
			inputText.setText(path);
		}
	}

}
