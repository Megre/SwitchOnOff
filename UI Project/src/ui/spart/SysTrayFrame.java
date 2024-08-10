package ui.spart;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class SysTrayFrame extends JFrame {

	private static final long serialVersionUID = 517067427505580044L;
	private TrayIcon trayIcon;
	private SystemTray tray = SystemTray.getSystemTray();
	private ActionListener listener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			setVisible(!isVisible());
			setExtendedState(Frame.NORMAL);
		}
	};

	protected void initTray(ImageIcon image) {
		makeTrayIcon(image.getImage());
		try {
			tray.add(trayIcon);
		} catch (AWTException ex) {
			ex.printStackTrace();
		}
	}

	protected void updateTray(ImageIcon image) {
		tray.remove(this.trayIcon);
		initTray(image);
	}
	
	private void makeTrayIcon(Image image) {
		trayIcon = new TrayIcon(image.getScaledInstance(16, 16, Image.SCALE_SMOOTH), "SwitchOnOff", null);
		trayIcon.addActionListener(listener);
	}
}
