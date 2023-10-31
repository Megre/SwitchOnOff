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
			fModel.pushCmd((byte) 0xff);
		}
		
		updateUI(fModel.isConnected());
	}
	
	public void updateUI(byte data) {
		fView.updateUI(data);
	}
	
	public void updateUI(boolean connected) {
		fView.updateUI(connected);
	}
	
	public void switchOnOff(int index) {
		int data = fModel.getData();
		int cmd = 0x80;
		
		for(int i=0; i<MainApp.CTRL_NUM; ++i) {
			if(index == i) {
				boolean isOn = isOn(index, data);
				if(!isOn) cmd |= (1<<index);
			}
			else cmd |= (data & (1<<i));
		}
		System.out.println(String.format("state: 0x%X", cmd));
		fModel.pushCmd((byte) cmd);
	}
	
	private boolean isOn(int index, int data) {
		return (data & (1<<index)) == (1<<index);
	}
}
