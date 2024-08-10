package ui.spart;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

import javax.swing.JTree;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultTreeModel;

import ctrl.spart.Config;
import ctrl.spart.ConfigItem;
import ctrl.spart.StatePresenter;
import ctrl.spart.SwitchState;
import resource.Images;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.SystemTray;
import java.awt.Toolkit;

/** 
 * 
 * @author megre
 * @email megre@vip.qq.com
 * @version created on: 2023-10-26 15:19:01
 */
public class MainApp {
	public static int CTRL_NUM = 3;
	public static MainApp INSTANCE;
	
	private StatePresenter fPresenter;
	
	private SysTrayFrame frame;
	private JTextField txtSwitchOne;
	private JTextField txtSwitchTwo;
	private JTextField txtSwitchThree;
	
	private SwitchState fModel;
	private JTree portTree;
	private JButton btnConnect;
	private JButton btnCtrl1, btnCtrl2, btnCtrl3;
	private JButton btnAutoCtrl;
	private JLabel lblNewLabel_2, lblCpuTemp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp window = new MainApp();
					MainApp.INSTANCE = window;
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainApp() {
		initialize();		
		
		fModel = new SwitchState();
		fPresenter = new StatePresenter(fModel, this);		

		fModel.startTasker();

		// list ports
		ArrayList<String> ports = fModel.listSerialPorts();
		if(portTree != null) {
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) portTree.getModel().getRoot();
			for(String port: ports) {
				root.add(new PortTreeNode(port) {

					/**
					 * 
					 */
					private static final long serialVersionUID = 2855817716718535807L;});
			}
			portTree.expandPath(portTree.getPathForRow(0));
			
			portTree.addMouseListener(new PortTreeDoubleClickAdapter(portTree, fPresenter));
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new SysTrayFrame();
		frame.setBackground(Color.WHITE);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setIconImage(Images.DISCONNECT.getImage());
		frame.initTray(Images.DISCONNECT);
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		frame.setBounds((kit.getScreenSize().width-250)/2, (kit.getScreenSize().height-433)/2, 250, 433);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	getModel().closePort();
            	getModel().saveConfig();
                frame.dispose();
            }
            public void windowIconified(WindowEvent e){
                if(SystemTray.isSupported()){
                    frame.setVisible(false);
                }
            }
        });
		
		btnConnect = new JButton("");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) portTree.getLastSelectedPathComponent();
				if(node.getLevel() == 0) return;
                fPresenter.switchPortOpenState(node.toString());
			}
		});

		btnConnect.setIcon(Images.DISCONNECT);
		btnConnect.setBounds(156, 91, 64, 64);
		btnConnect.setBorderPainted(false);
		btnConnect.setFocusPainted(false);
		btnConnect.setContentAreaFilled(false);
		btnConnect.setOpaque(false);
		frame.getContentPane().add(btnConnect);
		
		btnCtrl1 = new JButton("");
		btnCtrl1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fPresenter.switchOnOff(0);
			}
		});
		btnCtrl1.setIcon(Images.OFF);
		btnCtrl1.setBounds(115, 166, 107, 64);
		btnCtrl1.setContentAreaFilled(false);
		btnCtrl1.setBorder(null);
		frame.getContentPane().add(btnCtrl1);
		
		btnCtrl2 = new JButton("");
		btnCtrl2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fPresenter.switchOnOff(1);
			}
		});
		btnCtrl2.setIcon(Images.OFF);
		btnCtrl2.setBounds(115, 241, 107, 64);
		btnCtrl2.setContentAreaFilled(false);
		btnCtrl2.setBorder(null);
		frame.getContentPane().add(btnCtrl2);
		
		btnCtrl3 = new JButton("");
		btnCtrl3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fPresenter.switchOnOff(2);
			}
		});
		btnCtrl3.setIcon(Images.OFF);
		btnCtrl3.setBounds(115, 316, 107, 64);
		btnCtrl3.setContentAreaFilled(false);
		btnCtrl3.setBorder(null);
		frame.getContentPane().add(btnCtrl3);
		
		portTree = new JTree();
		portTree.setBorder(null);
		portTree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("COM") {
				private static final long serialVersionUID = -7013958069439208726L;

				{
				}
			}
		));	
		JScrollPane scrollPane1 = new JScrollPane(portTree);
		scrollPane1.setBounds(12, 91, 132, 64);
		frame.getContentPane().add(scrollPane1);
		
		txtSwitchOne = new JTextField();
		txtSwitchOne.setHorizontalAlignment(SwingConstants.CENTER);
		txtSwitchOne.setText("Switch One");
		txtSwitchOne.setBounds(12, 166, 91, 64);
		frame.getContentPane().add(txtSwitchOne);
		txtSwitchOne.setColumns(10);
		
		txtSwitchTwo = new JTextField();
		txtSwitchTwo.setHorizontalAlignment(SwingConstants.CENTER);
		txtSwitchTwo.setText("Switch Two");
		txtSwitchTwo.setColumns(10);
		txtSwitchTwo.setBounds(12, 241, 91, 64);
		frame.getContentPane().add(txtSwitchTwo);
		
		txtSwitchThree = new JTextField();
		txtSwitchThree.setHorizontalAlignment(SwingConstants.CENTER);
		txtSwitchThree.setText("Switch Three");
		txtSwitchThree.setColumns(10);
		txtSwitchThree.setBounds(12, 316, 91, 64);
		frame.getContentPane().add(txtSwitchThree);
		
		lblCpuTemp = new JLabel("50");
		lblCpuTemp.setFont(new Font("SimSun", Font.PLAIN, 60));
		lblCpuTemp.setBounds(34, 11, 69, 69);
		frame.getContentPane().add(lblCpuTemp);
		
		JLabel lblNewLabel_1 = new JLabel("°C");
		lblNewLabel_1.setBounds(99, 19, 21, 16);
		frame.getContentPane().add(lblNewLabel_1);
		
		btnAutoCtrl = new JButton("");
		btnAutoCtrl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean isAutoCtrl = fModel.isAutoCtrl();
				btnAutoCtrl.setIcon(isAutoCtrl?Images.OFF_SMALL:Images.ON_SMALL);
				fModel.setAutoCtrl(!isAutoCtrl);
			}
		});
		btnAutoCtrl.setContentAreaFilled(false);
		btnAutoCtrl.setIcon(Images.OFF_SMALL);
		btnAutoCtrl.setBorder(null);
		btnAutoCtrl.setBounds(135, 43, 54, 32);
		frame.getContentPane().add(btnAutoCtrl);
		
		lblNewLabel_2 = new JLabel("Auto");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(135, 11, 54, 32);
		frame.getContentPane().add(lblNewLabel_2);
		
		JButton btnSettings = new JButton("");
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingsDialog dlgSettings = new SettingsDialog();
				dlgSettings.setVisible(true);
			}
		});
		btnSettings.setOpaque(false);
		btnSettings.setFocusPainted(false);
		btnSettings.setContentAreaFilled(false);
		btnSettings.setBorderPainted(false);
		btnSettings.setBounds(202, 0, 32, 32);
		btnSettings.setIcon(Images.SETTINGS);
		frame.getContentPane().add(btnSettings);
	}
	
	public void updateUI(int data) { 
		btnCtrl1.setIcon((data&0x1)==0x1?Images.ON:Images.OFF);
		btnCtrl2.setIcon((data&0x2)==0x2?Images.ON:Images.OFF);
		btnCtrl3.setIcon((data&0x4)==0x4?Images.ON:Images.OFF);
	}
	
	public void updateUI(boolean connected) { 
		invokeLater(new Runnable() {
			
			@Override
			public void run() {
				final ImageIcon newIcon = connected?Images.CONNECT:Images.DISCONNECT;
				if(newIcon == btnConnect.getIcon()) return;
				
				btnConnect.setIcon(newIcon);		
				frame.updateTray(newIcon);
				frame.setIconImage(newIcon.getImage());
			}
		});
	}
	
	public void updateUI(Double cpuTemp) { 
		invokeLater(new Runnable() {
			
			@Override
			public void run() {
				lblCpuTemp.setText(String.format("%d", Math.round(cpuTemp)));		
				
				final int threshold = 79;
				if(cpuTemp >= threshold) { // red
					int r = (int) Math.min(200+5*(cpuTemp-threshold), threshold);
					lblCpuTemp.setForeground(new Color(r, 0, 0));					
				}
				else { // green
					int r = (int) Math.min(5*(threshold-cpuTemp), threshold);
					int g = (int) Math.min(175+5*(threshold-cpuTemp), 255);
					lblCpuTemp.setForeground(new Color(r, g, 0));		
				}
			}
		});
	}
	
	public void updateUI(Config config) {
		ConfigItem[] switchItems = config.getConfigItems();
		txtSwitchOne.setText(switchItems[0].getSwitchName());
		txtSwitchTwo.setText(switchItems[1].getSwitchName());
		txtSwitchThree.setText(switchItems[2].getSwitchName());
		
		btnAutoCtrl.setIcon(config.getAutoCtrl()?Images.ON_SMALL:Images.OFF_SMALL);
	}
	
	public SwitchState getModel() {
		return fModel;
	}
	
	private void invokeLater(Runnable runnable) {
		EventQueue.invokeLater(runnable);
	}
}
