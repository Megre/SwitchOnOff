package ctrl.spart;

import ui.spart.MainApp;

/** 
 * 
 * @author megre
 * @email megre@vip.qq.com
 * @version created on: 2023-10-31 10:08:47
 */
public class StatePresenter {
	private SwitchState fModel;
	private MainApp fView;

	public StatePresenter(SwitchState model, MainApp view) {
		fModel = model;
		fView = view;
		
		model.setPresenter(this);
	}
	
	public void switchPortOpenState(String portname) {
		// close
		if(fModel.isConnected()) {
			fModel.closePort();
			updateUI(false);
			return;
		}
		
		// open
		if(fModel.openPort(portname)) {
			fModel.pushCmd(0xff);
		}
		
		updateUI(fModel.isConnected());
	}
	
	public void updateUI(int data) {
		fView.updateUI(data);
	}
	
	public void updateUI(boolean connected) {
		fView.updateUI(connected);
	}
	
	public void updateUI(double cpuTemp) {
		fView.updateUI(cpuTemp);
	}
	
	public void updateUI(Config config) {
		fView.updateUI(config);
	}
	
	public void switchOnOff(int index) {
		int data = fModel.getData();
		int cmd = buildCmd(index, !isOn(index, data), data);
		
		System.out.println(String.format("state: 0x%X", cmd));
		fModel.pushCmd(cmd);
	}
	
	private int buildCmd(int index, boolean switchOn, int data) {
		int cmd = 0x80 | data;
		
		if(switchOn) {
			cmd = (cmd | (1 << index));
		}
		else {
			cmd = cmd - (cmd & (1 << index));
		}

		return cmd;
	}
	
	
	public void autoCtrl(double cpuTemp) {
		
		int cmd = fModel.getData();
		for(int i=0; i<MainApp.CTRL_NUM; ++i) {
			double startTemp = fModel.getConfig().getStartTemp(i), 
					stopTemp = fModel.getConfig().getStopTemp(i);
			
			if(cpuTemp >= startTemp) {			
				cmd = buildCmd(i, true, cmd);
			}
			else if(cpuTemp <= stopTemp) {
				cmd = buildCmd(i, false, cmd);			
			}
		}
		
		fModel.pushCmd(cmd); 
	}
	
	private boolean isOn(int index, int data) {
		return (data & (1<<index)) == (1<<index);
	}
}
