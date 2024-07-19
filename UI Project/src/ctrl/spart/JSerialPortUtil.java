package ctrl.spart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;

public class JSerialPortUtil {
	
	private static JSerialPortUtil fSerialPortUtil = null;
    
    private SerialPort fSerialPort;
    private String fPortName;

    static {
        if (fSerialPortUtil == null) {
            fSerialPortUtil = new JSerialPortUtil();
        }
    }

    private JSerialPortUtil() {
    }

    public static JSerialPortUtil instance() {
        if (fSerialPortUtil == null) {
            fSerialPortUtil = new JSerialPortUtil();
        }
        return fSerialPortUtil;
    }

    public ArrayList<String> listPorts() {
    	ArrayList<String> portNameList = new ArrayList<>();
    	SerialPort[] ports = SerialPort.getCommPorts();

        for (SerialPort p: ports) {
            portNameList.add(p.getSystemPortName());
        }

        return portNameList;
    }
    

    public SerialPort open(String portName, int baudrate, int databits, int stopbatis, int parity) {
    	fPortName = portName;
    	
        fSerialPort = SerialPort.getCommPort(portName); 
        fSerialPort.setComPortParameters(baudrate, databits, stopbatis, parity);
        fSerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 0);

        if (fSerialPort.openPort()) {
            System.out.println("Port " + portName + " opened successfully.");
            return fSerialPort;
        } 
        
        System.out.println("Unable to open " + portName);
        return null;
    }

    public void close() {
        fPortName = null;
        
        if (fSerialPort != null) { 
            fSerialPort.closePort();
            fSerialPort = null;
        }
    }
    
    public boolean isOpen() {
    	return fSerialPort != null && fSerialPort.isOpen();
    }
    
    public String getPortName() {
    	if(fSerialPort == null) return null;
    	
    	return fPortName;
    }

    public int send(byte[] order) {
    	if(!isOpen()) return 0;
    	
        OutputStream out = null;
        try {
            out = fSerialPort.getOutputStream();
            out.write(order);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }           
        }
        
        return order.length;
    }

    public byte[] read() {
        InputStream in=null;
        byte[] bytes=null;
        try {
            in=fSerialPort.getInputStream();
            int bufflenth=in.available();
            while (bufflenth!=0){
                bytes=new byte[bufflenth];
                in.read(bytes);
                bufflenth=in.available();
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  bytes;
    }

    public void addListener(SerialPortDataListener listener){
    	if(fSerialPort == null) return;
    	
        try {
            fSerialPort.addDataListener(listener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void removeListener(SerialPortDataListener listener){
        fSerialPort.removeDataListener();
    }
    
    public static void main(String[] args) {
    	ArrayList<String> ports = fSerialPortUtil.listPorts();
    	System.out.println(ports);  
    }
}
