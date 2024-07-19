package ctrl.spart;

import java.util.ArrayList;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;

/** 
 * 
 * @author megre
 * @email megre@vip.qq.com
 * @version created on: 2023-10-27 16:15:20
 */
public class SwitchState implements SerialPortDataListener {
	private static int CMD_CLOSE = 0xC0;
	
	private StatePresenter fPresenter;	
	
	private JSerialPortUtil fSerialPortUtil = JSerialPortUtil.instance();
	
	private Queue<Integer> fTaskQueue = new ConcurrentLinkedQueue<Integer>();
	private Thread fTaskThread;
	
	private CPUTemp fTempDetecter = new CPUTemp();
	
	private Config fConfig;
	
	// state
	private int fData;
	
	public SwitchState() {
		fConfig = Config.load();
		if(fConfig.getPortName() != null) {
			openPort(fConfig.getPortName());
		}
	}
	
	public void setPresenter(StatePresenter presenter) {		
		fPresenter = presenter;
		
		if(fPresenter != null) {
			fPresenter.updateUI(fConfig);
		}
	}
	
	public ArrayList<String> listSerialPorts() {
		return fSerialPortUtil.listPorts();
	}
	
	public boolean openPort(String portname) {
		fSerialPortUtil.close();
		fSerialPortUtil.open(portname, 9600, 
				8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
		fSerialPortUtil.addListener(this);
		return fSerialPortUtil.isOpen();
	}
	
	public void closePort() {
		pushCmd(0x80);
    	pushCmd(CMD_CLOSE);
	}
	
	private void disconnect() {
    	fSerialPortUtil.close();
    	updateState((byte) 0); 
    	updateState(false);
	}
	
	public void pushCmd(int cmd) {
		fTaskQueue.add(cmd);
	}
	
		
	public void startTasker() {
		fTaskThread = new Thread() {
			private byte[] fCmdArray = new byte[1];
			
		    @Override
		    public void run() {
		    	while(true) {			    				        
		    		if(fSerialPortUtil.isOpen()) {		
		    			// pop cmd
				        Integer topCmd = fTaskQueue.poll();
				        int cmd = (topCmd==null?0:topCmd);
				        
				        if(cmd == CMD_CLOSE) {			        	
				        	disconnect();
				        }
				        
				        fCmdArray[0] = (byte) cmd;			        
			        	if(fSerialPortUtil.send(fCmdArray) > 0) {
			        		if(fCmdArray[0] != 0) updateState(cmd);
			        	}		    
		    		}    		
		        	
		        	// CPU temprature
		        	double temp = fTempDetecter.detect();
		        	fPresenter.updateUI(temp);
		        	if(isAutoCtrl()) {
		        		fPresenter.autoCtrl(temp);
		        	}			        
			        
			        // wait
			        try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		    	}
		    }
		};
		fTaskThread.start();		
	}
	
	
	public boolean isConnected() {
		return fSerialPortUtil.isOpen();
	}
		
	public int getData() {
		return fData;
	}
	
	public boolean isAutoCtrl() {
		return fConfig.getAutoCtrl();
	}
	
	public void setAutoCtrl(boolean autoCtrl) {
		fConfig.setAutoCtrl(autoCtrl);
	}
	
	private String getPortName() {
		return fSerialPortUtil.getPortName();
	}
	
	public Config getConfig() {
		return fConfig;
	}
	
	public void saveConfig() {
		fConfig.setAutoCtrl(isAutoCtrl());
		fConfig.setPortName(getPortName());
		fConfig.save();
	}
	
	private void updateState(boolean connected){
		if(fPresenter != null) fPresenter.updateUI(connected);
	}
	
	private void updateState(int data) {
		fData = data;
		if(fPresenter != null) fPresenter.updateUI(data);
	}



	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
	}
	

	@Override
	public void serialEvent(SerialPortEvent event) {
		try {
			if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
		        // read response
	            byte[] bytes = fSerialPortUtil.read();
	            if(bytes != null) {
	            	updateState(bytes[0]);
	            }
	            updateState(bytes != null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String args[])  {
		SwitchState switchControl = new SwitchState();
        ArrayList<String> port = switchControl.listSerialPorts();
        System.out.println(port);
        if(switchControl.openPort("COM3")) {
        	switchControl.startTasker();
        }    

    }

}
