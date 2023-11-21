package ctrl.spart;

import java.io.Serializable;

/** 
 * 
 * @author megre
 * @email megre@vip.qq.com
 * @version created on: 2023-11-21 10:30:29
 */
public class ConfigItem implements Serializable {
	private static final long serialVersionUID = 6090746937693155944L;
	private String switchName;
	private double startTemp, stopTemp;
	
	public ConfigItem() {
		switchName = "Switch";
		startTemp = 75;
		stopTemp = 70;
	}
	
	public ConfigItem(String switchName, double startTemp, double stopTemp) {
		this.switchName = switchName;
		this.startTemp = startTemp;
		this.stopTemp = stopTemp;
	}
	
	public String getSwitchName() {
		return switchName;
	}

	public double getStartTemp() {
		return startTemp;
	}

	public double getStopTemp() {
		return stopTemp;
	}
}
