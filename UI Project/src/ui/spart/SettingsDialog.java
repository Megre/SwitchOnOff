package ui.spart;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ctrl.spart.Config;
import ctrl.spart.ConfigItem;

import javax.swing.SwingConstants;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/** 
 * 
 * @author megre
 * @email megre@vip.qq.com
 * @version created on: 2023-11-21 09:35:57
 */
public class SettingsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3934397794822730957L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtSwitchOne;
	private JTextField txtSwitchTwo;
	private JTextField txtSwitchThree;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SettingsDialog dialog = new SettingsDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SettingsDialog() {
		setTitle("Settings");
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBackground(Color.WHITE);
		
		Config config = MainApp.INSTANCE.getModel().getConfig();
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		setBounds((kit.getScreenSize().width-276)/2, (kit.getScreenSize().height-259)/2, 276, 259);
		getContentPane().setLayout(null);
		getContentPane().setBackground(Color.WHITE);
		contentPanel.setBounds(0, 0, 260, 169);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setBackground(Color.WHITE);
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
				
		JLabel lblSwitchOnc = new JLabel("ON(°C)");
		lblSwitchOnc.setBounds(125, 11, 52, 16);
		contentPanel.add(lblSwitchOnc);
		
		JLabel lblSwitchOffc = new JLabel("OFF(°C)");
		lblSwitchOffc.setBounds(189, 11, 58, 16);
		contentPanel.add(lblSwitchOffc);
		
		txtSwitchOne = new JTextField();
		txtSwitchOne.setText(config.getSwitchName(0));
		txtSwitchOne.setHorizontalAlignment(SwingConstants.CENTER);
		txtSwitchOne.setColumns(10);
		txtSwitchOne.setBounds(17, 40, 91, 28);
		contentPanel.add(txtSwitchOne);
		
		txtSwitchTwo = new JTextField();
		txtSwitchTwo.setText(config.getSwitchName(1));
		txtSwitchTwo.setHorizontalAlignment(SwingConstants.CENTER);
		txtSwitchTwo.setColumns(10);
		txtSwitchTwo.setBounds(17, 89, 91, 28);
		contentPanel.add(txtSwitchTwo);
		
		txtSwitchThree = new JTextField();
		txtSwitchThree.setText(config.getSwitchName(2));
		txtSwitchThree.setHorizontalAlignment(SwingConstants.CENTER);
		txtSwitchThree.setColumns(10);
		txtSwitchThree.setBounds(17, 138, 91, 28);
		contentPanel.add(txtSwitchThree);
		
		JSpinner spinner1Start = new JSpinner();
		spinner1Start.setModel(new SpinnerNumberModel(new Double(config.getStartTemp(0)), null, null, new Double(1)));
		spinner1Start.setBounds(125, 40, 49, 28);
		contentPanel.add(spinner1Start);
		
		JSpinner spinner2Start = new JSpinner();
		spinner2Start.setModel(new SpinnerNumberModel(new Double(config.getStartTemp(1)), null, null, new Double(1)));
		spinner2Start.setBounds(125, 89, 49, 28);
		contentPanel.add(spinner2Start);
		
		JSpinner spinner3Start = new JSpinner();
		spinner3Start.setModel(new SpinnerNumberModel(new Double(config.getStartTemp(2)), null, null, new Double(1)));
		spinner3Start.setBounds(125, 138, 49, 28);
		contentPanel.add(spinner3Start);
		
		JSpinner spinner1Stop = new JSpinner();
		spinner1Stop.setModel(new SpinnerNumberModel(new Double(config.getStopTemp(0)), null, null, new Double(1)));
		spinner1Stop.setBounds(191, 40, 49, 28);
		contentPanel.add(spinner1Stop);
		
		JSpinner spinner2Stop = new JSpinner();
		spinner2Stop.setModel(new SpinnerNumberModel(new Double(config.getStopTemp(1)), null, null, new Double(1)));
		spinner2Stop.setBounds(191, 89, 49, 28);
		contentPanel.add(spinner2Stop);
		
		JSpinner spinner3Stop = new JSpinner();
		spinner3Stop.setModel(new SpinnerNumberModel(new Double(config.getStopTemp(2)), null, null, new Double(1)));
		spinner3Stop.setBounds(191, 138, 49, 28);
		contentPanel.add(spinner3Stop);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 180, 255, 35);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPane.setBackground(Color.WHITE);
			getContentPane().add(buttonPane);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ConfigItem item1 = new ConfigItem(txtSwitchOne.getText(), 
								Double.parseDouble(spinner1Start.getValue().toString()), 
								Double.parseDouble(spinner1Stop.getValue().toString()));
						ConfigItem item2 = new ConfigItem(txtSwitchTwo.getText(), 
								Double.parseDouble(spinner2Start.getValue().toString()), 
								Double.parseDouble(spinner2Stop.getValue().toString()));
						ConfigItem item3 = new ConfigItem(txtSwitchThree.getText(), 
								Double.parseDouble(spinner3Start.getValue().toString()), 
								Double.parseDouble(spinner3Stop.getValue().toString()));	
						
						Config config = MainApp.INSTANCE.getModel().getConfig();
						config.setConfigItems(new ConfigItem[]{item1, item2, item3});
						MainApp.INSTANCE.updateUI(config);
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						SettingsDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	

}
