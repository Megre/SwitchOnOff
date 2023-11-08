package ui.spart;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

import javax.swing.JTree;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultTreeModel;

import ctrl.spart.StatePresenter;
import ctrl.spart.SwitchState;
import resource.Images;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JTextField;

/** 
 * 
 * @author megre
 * @email megre@vip.qq.com
 * @version created on: 2023-10-26 15:19:01
 */
public class MainApp {
	public static int CTRL_NUM = 3;
	
	private StatePresenter fPresenter;
	
	private JFrame frame;
	private JTextField txtSwitch;
	private JTextField txtSwitchTwo;
	private JTextField txtSwitchThree;
	
	private SwitchState fSwitchState;
	private JTree portTree;
	private JButton btnConnect;
	private JButton btnCtrl1, btnCtrl2, btnCtrl3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp window = new MainApp();
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
		fSwitchState = new SwitchState();
		fPresenter = new StatePresenter(fSwitchState, this);
		fSwitchState.setPresenter(fPresenter);
		
		initialize();
		

		// list ports
		ArrayList<String> ports = fSwitchState.listSerialPorts();
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
		frame = new JFrame();
		frame.setBackground(Color.WHITE);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 249, 345);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		btnConnect = new JButton("");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) portTree.getLastSelectedPathComponent();
				if(node.getLevel() == 0) return;
                fPresenter.switchPortOpenState(node.toString());
			}
		});

		btnConnect.setIcon(Images.DISCONNECT);
		btnConnect.setBounds(158, 2, 64, 64);
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
		btnCtrl1.setBounds(115, 73, 107, 64);
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
		btnCtrl2.setBounds(115, 148, 107, 64);
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
		btnCtrl3.setBounds(115, 223, 107, 64);
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
		scrollPane1.setBounds(12, 2, 132, 64);
		frame.getContentPane().add(scrollPane1);
		
		txtSwitch = new JTextField();
		txtSwitch.setHorizontalAlignment(SwingConstants.CENTER);
		txtSwitch.setText("Switch One");
		txtSwitch.setBounds(12, 73, 91, 64);
		frame.getContentPane().add(txtSwitch);
		txtSwitch.setColumns(10);
		
		txtSwitchTwo = new JTextField();
		txtSwitchTwo.setHorizontalAlignment(SwingConstants.CENTER);
		txtSwitchTwo.setText("Switch Two");
		txtSwitchTwo.setColumns(10);
		txtSwitchTwo.setBounds(12, 148, 91, 64);
		frame.getContentPane().add(txtSwitchTwo);
		
		txtSwitchThree = new JTextField();
		txtSwitchThree.setHorizontalAlignment(SwingConstants.CENTER);
		txtSwitchThree.setText("Switch Three");
		txtSwitchThree.setColumns(10);
		txtSwitchThree.setBounds(12, 223, 91, 64);
		frame.getContentPane().add(txtSwitchThree);
	}
	
	public void updateUI(byte data) { 
		btnCtrl1.setIcon((data&0x1)==0x1?Images.ON:Images.OFF);
		btnCtrl2.setIcon((data&0x2)==0x2?Images.ON:Images.OFF);
		btnCtrl3.setIcon((data&0x4)==0x4?Images.ON:Images.OFF);
	}
	
	public void updateUI(boolean connected) { 
		invokeLater(new Runnable() {
			
			@Override
			public void run() {
				btnConnect.setIcon(connected?Images.CONNECT:Images.DISCONNECT);				
			}
		});
	}
	
	private void invokeLater(Runnable runnable) {
		EventQueue.invokeLater(runnable);
	}
}
