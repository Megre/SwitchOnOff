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
	private static byte CMD_CLOSE = (byte) 0xC0;
	
	private StatePresenter fPresenter;	
	
	private SerialPortUtil fSerialPortUtil = SerialPortUtil.instance();
	
	private Queue<Byte> fTaskQueue = new ConcurrentLinkedQueue<Byte>();
	private Thread fTaskThread;
	
	// state
	private byte fData;
	
	public SwitchState() {

	}
	
	public void setPresenter(StatePresenter presenter) {
		fPresenter = presenter;
	}
	
	public ArrayList<String> listSerialPorts() {
		return fSerialPortUtil.listPorts();
	}
	
	public boolean openPort(String portname) {
		fSerialPortUtil.close();
		fSerialPortUtil.open(portname, 4800, 
				SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		fSerialPortUtil.addListener(this);
		startTasker();
		return fSerialPortUtil.isOpen();
	}
	
	public void closePort() {
		pushCmd((byte) 0x80);
    	pushCmd(CMD_CLOSE);
	}
	
	private void disconnect() {
    	fSerialPortUtil.close();
    	updateState((byte) 0); 
	}
	
	public void pushCmd(byte cmd) {
		fTaskQueue.add(cmd);
	}
	
		
	public void startTasker() {
		fTaskThread = new Thread() {
			private byte[] fCmdArray = new byte[1];
			
		    @Override
		    public void run() {
		    	while(true) {
			    	// push cmd
			        Byte topCmd = fTaskQueue.poll();
			        byte cmd = (topCmd==null?0:topCmd);
			        
			        if(cmd == CMD_CLOSE) {			        	
			        	disconnect();
			        	break;
			        }
			        
			        fCmdArray[0] = cmd;			        
		        	if(fSerialPortUtil.send(fCmdArray) > 0) {
		        		if(fCmdArray[0] != 0) updateState(fCmdArray[0]);
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
		
	public byte getData() {
		return fData;
	}
	
	private void updateState(boolean connected){
		if(fPresenter != null) fPresenter.updateUI(connected);
	}
	
	private void updateState(byte data) {
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
