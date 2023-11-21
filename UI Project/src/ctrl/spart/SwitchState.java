package ctrl.spart;

import java.util.ArrayList;
import java.util.Queue;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentLinkedQueue;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/** 
 * 
 * @author megre
 * @email megre@vip.qq.com
 * @version created on: 2023-10-27 16:15:20
 */
public class SwitchState implements SerialPortEventListener {
	private static int CMD_CLOSE = 0xC0;
	
	private StatePresenter fPresenter;	
	
	private SerialPortUtil fSerialPortUtil = SerialPortUtil.instance();
	
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
		fSerialPortUtil.open(portname, 4800, 
				SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
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
	
	/**
	 * @see gnu.io.SerialPortEventListener#serialEvent(gnu.io.SerialPortEvent)
	 */
	@Override
	public void serialEvent(SerialPortEvent arg0) {
		int eventType = arg0.getEventType();
        if (eventType == SerialPortEvent.DATA_AVAILABLE) {            
            
	        // read response
            byte[] bytes = fSerialPortUtil.read();
            if(bytes != null) {
            	updateState(bytes[0]);
            }
            updateState(bytes != null);
        }
        else if(eventType == SerialPortEvent.BI) {
        	System.out.println("SerialPortEvent.BI");
        }
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

	/**
	 * @param args
	 * @throws TooManyListenersException 
	 */
	public static void main(String args[]) throws InterruptedException, TooManyListenersException {
		SwitchState switchControl = new SwitchState();
        ArrayList<String> port = switchControl.listSerialPorts();
        System.out.println("发现全部串口：" + port);
        if(switchControl.openPort("COM8")) {
        	switchControl.startTasker();
        }    

    }

}
