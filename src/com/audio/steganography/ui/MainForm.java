
package com.audio.steganography.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import com.audio.steganography.core.SteganoInformation;
import com.audio.steganography.core.Steganograph;
import com.audio.steganography.ui.widgets.ImageButton;
import com.audio.steganography.ui.widgets.WidgetFactory;
import com.audio.steganography.utils.SimpleFileFilter;

public class MainForm extends JFrame implements ActionListener {

	private static final long	serialVersionUID		= 5775503582879812597L;
	private JLabel				lblMsg;
	private JLabel				lblSource;
	private JLabel				lblTarget;
	private JFileChooser		fileChooserSource;
	private JFileChooser		fileChooserTarget;
	private JScrollPane			scroller;

	private JButton				btnSelectSource;
	private JButton				btnSelectTarget;

	private ImageButton			btnStegno;
	private ImageButton			btnDeStegno;
	private ImageButton			btnClear;
	private ImageButton			btnExitApp;
	private JCheckBox			chkCompress;

	private JTextField			txtSourceFile;
	private JTextField			txtTargetFile;
	private JTextArea			txtMessageArea;
	private JScrollPane			scrollPane;

	// Status flags
	private boolean				isSourceOpen;
	private boolean				isTargetOpen;
	private int					defaultCompressionLevel	= 6;
	String						strFilename;

	public static void main(String[] args) {
		setTheme();
		new MainForm();
	}

	public MainForm() {

		isSourceOpen = false;
		isTargetOpen = false;

		lblMsg = new JLabel("Message");
		lblSource = new JLabel("Source : ");
		lblTarget = new JLabel("Target : ");

		btnSelectSource = new JButton("Browse");
		btnSelectTarget = new JButton("Browse");

		chkCompress = new JCheckBox("Compress", false);

		txtMessageArea = new JTextArea(1, 1);
		txtMessageArea.setEditable(true);
		txtMessageArea.setBounds(25, 240, 385, 180);
		txtMessageArea.setAutoscrolls(true);

		scrollPane = new JScrollPane(txtMessageArea);
		scrollPane.setBounds(25, 240, 385, 180);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL));
		scrollPane.setVerticalScrollBar(new JScrollBar(JScrollBar.VERTICAL));

		txtSourceFile = new JTextField();
		txtTargetFile = new JTextField();
		txtTargetFile.setEditable(false);
		txtSourceFile.setEditable(false);

		fileChooserSource = new JFileChooser();
		fileChooserTarget = new JFileChooser();

		fileChooserSource.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooserTarget.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// This should come from WaveAudioSteganoStream
		String[] audios = new String[] { "au", "aiff", "wav" };
		FileFilter fileFilter = new SimpleFileFilter(audios, "Sound (*.aiff, *.au, *.wav)");

		fileChooserSource.addChoosableFileFilter(fileFilter);
		fileChooserTarget.addChoosableFileFilter(fileFilter);

		// Exit Button is placed at the top right
		btnExitApp = WidgetFactory.createImageButton("btn_close_normal.png", "btn_close_hover.png", 380, 5, 50, 50);

		// Other interface buttons
		btnStegno = WidgetFactory.createImageButton("btn_steg_normal.png", "btn_steg_hover.png", 25, 90, 73, 32);
		btnClear = WidgetFactory.createImageButton("btn_clear_normal.png", "btn_clear_hover.png", 100, 90, 85, 32);
		btnDeStegno = WidgetFactory.createImageButton("btn_desteg_normal.png", "btn_desteg_hover.png", 180, 90, 75, 32);

		// set bounds
		lblSource.setBounds(25, 140, 80, 20);
		lblTarget.setBounds(25, 170, 80, 20);
		btnSelectSource.setBounds(300, 140, 100, 20);
		btnSelectTarget.setBounds(300, 170, 100, 20);

		txtSourceFile.setBounds(80, 140, 200, 20);
		txtTargetFile.setBounds(80, 170, 200, 20);

		lblMsg.setBounds(25, 220, 400, 20);
		;
		chkCompress.setBounds(300, 200, 100, 20);

		// add the elements to the frame
		getContentPane().add(btnSelectSource);
		getContentPane().add(btnSelectTarget);

		getContentPane().add(btnStegno);
		getContentPane().add(btnDeStegno);
		getContentPane().add(btnClear);
		getContentPane().add(btnExitApp);

		getContentPane().add(lblMsg);
		getContentPane().add(lblSource);
		getContentPane().add(lblTarget);
		getContentPane().add(scrollPane);
		getContentPane().add(chkCompress);
		getContentPane().add(txtSourceFile);
		getContentPane().add(txtTargetFile);

		// add background image for the form
		add(WidgetFactory.createImageBackground("main_background.png"));

		// add action listners for buttons
		btnSelectSource.addActionListener(this);
		btnSelectTarget.addActionListener(this);

		btnStegno.addActionListener(this);
		btnDeStegno.addActionListener(this);
		btnClear.addActionListener(this);
		btnExitApp.addActionListener(this);

		// finally set stage params
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Remove the default bounding box supplied by java
		setUndecorated(true);
		getContentPane().setLayout(null);
		setTitle("Steganography Application");
		setSize(435, 450);
		setLocation(300, 150);
		setVisible(true);
		setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// select source
		if (ae.getSource() == btnSelectSource)
		{
			getInputFile();
		}
		// select target
		else if (ae.getSource() == btnSelectTarget)
		{
			getOutputFile();
		}
		// clear flds
		else if (ae.getSource() == btnClear)
		{
			clearFields();
		}
		else if (ae.getSource() == btnExitApp)
		{
			this.dispose();
		}
		else if (ae.getSource() == btnStegno)
		{
			encryptFile();

		}
		else if (ae.getSource() == btnDeStegno)
		{
			decryptFile();
		}
	}

	private void getInputFile() {
		try
		{
			// show dialogue box for choosing new file.
			// strEncrKey=JOptionPane

			int r = fileChooserSource.showOpenDialog(this);
			File fEncFile = fileChooserSource.getSelectedFile();

			if (r == JFileChooser.CANCEL_OPTION)
			{
			}
			else
			{
				strFilename = fEncFile.getName();
				if (!(strFilename.endsWith(".wav")))
				{
					JOptionPane.showMessageDialog(null, "Please Select Only Wave files (*.wav)", "Wrong File type", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					// ok
					isSourceOpen = true;
					String selectedFilePath = fileChooserSource.getSelectedFile().getAbsolutePath();
					txtSourceFile.setText(selectedFilePath);
					System.out.println("Selected File : " + selectedFilePath);
				}
			}
		}
		catch (Exception e)
		{
			System.err.println("Exception : " + e.getMessage());
		}
	}

	private void getOutputFile() {
		try
		{
			// show dialogue box for choosing new file.

			String outputFile = strFilename.substring(0, strFilename.lastIndexOf(".wav")) + "_encrypted.wav";
			fileChooserTarget.setSelectedFile(new File(outputFile));
			int r = fileChooserTarget.showOpenDialog(this);
			File fEncFile = fileChooserTarget.getSelectedFile();
			String strFoldername;
			if (r == JFileChooser.CANCEL_OPTION)
			{
				// do nothing

			}
			else
			{
				strFoldername = fEncFile.getPath();
				if (strFoldername.length() < 1)
					JOptionPane.showMessageDialog(this, "Please Select an output location", "No Folder chsosen", JOptionPane.ERROR_MESSAGE);
				else
				{
					// ok
					// txtFilepath.setText(strFilename);
					fileChooserTarget.setSelectedFile(fEncFile);
					isTargetOpen = true;
					String selectedFilePath = fileChooserTarget.getSelectedFile().getAbsolutePath();
					if (!selectedFilePath.endsWith(".wav"))
						selectedFilePath += ".wav";
					txtTargetFile.setText(selectedFilePath);
					System.out.println("User has selected an output file : " + selectedFilePath);
				}
			}
		}
		catch (Exception e)
		{
			System.err.println("Exception : " + e.getMessage());
		}
	}

	private void clearFields() {
		txtMessageArea.setText("");
		txtSourceFile.setText("");
		txtTargetFile.setText("");
		isSourceOpen = false;
		isTargetOpen = false;
	}

	private void encryptFile() {
		if (isSourceOpen == true && isTargetOpen == true)
		{

			// get key and encrypt file
			String targetFilepath, targetFilename, targetFile;
			String encrKey;
			// show key dialog and steg
			encrKey = JOptionPane.showInputDialog("Enter Encryption key : ");
			if (encrKey.length() < 8)
			{
				JOptionPane.showMessageDialog(null, "Password length should be atleast 8 characters", "Invalid Input", JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (txtMessageArea.getText().isEmpty())
			{
				JOptionPane.showMessageDialog(null, "Message Cannot Be Empty", "Invalid Input", JOptionPane.WARNING_MESSAGE);
				return;
			}
			// set correct compression level
			int compressionLevel = (chkCompress.isSelected()) ? defaultCompressionLevel : 0;

			// get output file path / name
			targetFilepath = txtTargetFile.getText();
			targetFilename = targetFilepath.substring(targetFilepath.lastIndexOf('\\') + 1);
			if (targetFilename.length() > 0)
			{
				if (!targetFilename.endsWith(".wav"))
				{
					targetFilename += ".wav";
				}

				targetFile = targetFilepath.substring(0, targetFilepath.lastIndexOf('\\')) + "\\";
				targetFile += targetFilename;

				Steganograph encrypt = new Steganograph();
				boolean isDone = encrypt.embedMessage(new File(txtSourceFile.getText()), new File(txtTargetFile.getText()), txtMessageArea.getText(), compressionLevel, encrKey);
				if (isDone)
				{
					JOptionPane.showMessageDialog(this, "Output file \n\"" + targetFile + " \"\ncreated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
					System.out.println("Output file " + targetFile + " created successfully.");
					clearFields();
				}
			}
		}
		else
		{
			// no file chosen
			String sMissingInfo;
			if (isSourceOpen == true && isTargetOpen == false)
				sMissingInfo = " Target File";
			else if (isSourceOpen == false && isTargetOpen == true)
				sMissingInfo = " Source File";
			else
				sMissingInfo = "Target & Source Files";

			JOptionPane.showMessageDialog(this, "Please Select the" + sMissingInfo + " (*.wav)", "No File Choosen", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void decryptFile() {
		if (isSourceOpen)
		{
			SteganoInformation steg = new SteganoInformation(new File(txtSourceFile.getText()));
			String decrKey = JOptionPane.showInputDialog("Enter Decryption key : ");
			String message;

			if (decrKey.length() < 8)
			{
				// validate for minimum key length
				JOptionPane.showMessageDialog(null, "Password length should be atleast 8 characters", "Invalid Input", JOptionPane.WARNING_MESSAGE);
				return;
			}
			// start key operation
			Steganograph decrObj = new Steganograph();
			message = decrObj.retrieveMessage(steg, new String(decrKey));

			if (message != null && !message.equals("#FAILED#"))
			{
				txtMessageArea.setText(message);
				System.out.println("Successfully extracted encrypted data.");
			}
			else
			{
				message = Steganograph.getMessage();
				if (message != null && message.equals("Incorrent Password"))
					JOptionPane.showMessageDialog(null, "Unfortunately it seems that the password is incorrect.", "Invalid password!", JOptionPane.WARNING_MESSAGE);
				else
					JOptionPane.showMessageDialog(null, "Error!\n" + Steganograph.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			}

		}
		else
		{
			JOptionPane.showMessageDialog(this, "Please Select a Wave file (*.wav)", "No File Choosen", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void setTheme() {
		try
		{
			javax.swing.UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");

		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{

			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (Exception err)
			{
				System.err.println("Error In set Theme " + err);
			}
		}
	}
}