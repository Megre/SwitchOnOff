package ctrl.spart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ui.spart.MainApp;

/** 
 * 
 * @author megre
 * @email megre@vip.qq.com
 * @version created on: 2023-10-26 15:56:14
 */
public class Config implements Serializable {

	private static final long serialVersionUID = -2987583206142428470L;
	
	private static final String CONFIG_PATH = "./config.data";
	
	private ConfigItem[] configItems;
	private boolean autoCtrl;
	private String portName;
	
	public Config() {
		configItems = new ConfigItem [MainApp.CTRL_NUM];
		for(int i=0; i<configItems.length; ++i) configItems[i] = new ConfigItem();
	}
	
	public Config(ConfigItem[] configItems, boolean autoCtrl, String comName) {
		this.configItems = configItems;
		this.autoCtrl = autoCtrl;
		this.portName = comName;
	}
	
	public static Config load() {
		File file = new File(CONFIG_PATH);
		if(!file.exists()) return new Config();
		
        FileInputStream fis;
		try {
			fis = new FileInputStream(file);
	        ObjectInputStream ois = new ObjectInputStream(fis);
	        Config config = (Config) ois.readObject();
	        ois.close();
	        fis.close();
	        
	        return config;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return new Config();
	}
	
	public void save() {
		File file = new File(CONFIG_PATH);
		if(!file.exists()) {
		    try {
				file.createNewFile();
			} catch (IOException e) {				
				e.printStackTrace();
				return;
			}
		}
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.flush();        
			fos.close();
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public String getSwitchName(int index) {
		if(configItems == null || configItems.length <= index) return "";
		
		return configItems[index].getSwitchName();
	}
	
	public double getStartTemp(int index) {
		if(configItems == null || configItems.length <= index) return -1;
		
		return configItems[index].getStartTemp();
	}
	
	public double getStopTemp(int index) {
		if(configItems == null || configItems.length <= index) return -1;
		
		return configItems[index].getStopTemp();
	}

	public ConfigItem[] getConfigItems() {
		return configItems;
	}

	public void setConfigItems(ConfigItem[] configItems) {
		this.configItems = configItems;
	}

	public boolean getAutoCtrl() {
		return autoCtrl;
	}

	public void setAutoCtrl(boolean autoCtrl) {
		this.autoCtrl = autoCtrl;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String comName) {
		this.portName = comName;
	}
	
}
