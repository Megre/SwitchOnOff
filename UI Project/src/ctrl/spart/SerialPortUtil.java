package ctrl.spart;
/**
 * @Description:
 * @Author: megre
 * @Email: renhao.x@seu.edu.cn
 * @Date: 2023/3/30 15:21
 */


import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

public class SerialPortUtil {

    private static SerialPortUtil fSerialPortUtil = null;
    
    private SerialPort fSerialPort;

    static {
        if (fSerialPortUtil == null) {
            fSerialPortUtil = new SerialPortUtil();
        }
    }

    private SerialPortUtil() {
    }

    public static SerialPortUtil instance() {
        if (fSerialPortUtil == null) {
            fSerialPortUtil = new SerialPortUtil();
        }
        return fSerialPortUtil;
    }

    public ArrayList<String> listPorts() {
        @SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();

        ArrayList<String> portNameList = new ArrayList<>();

        while (portList.hasMoreElements()) {
            String portName = portList.nextElement().getName();
            portNameList.add(portName);
        }

        return portNameList;
    }


    public SerialPort open(String portName, int baudrate, int databits, int stopbatis, int parity) {

        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            CommPort commPort = portIdentifier.open(portName, 2000);
            if (commPort instanceof SerialPort) {
                fSerialPort = (SerialPort) commPort;
                try {
                    fSerialPort.setSerialPortParams(baudrate, databits, stopbatis, SerialPort.PARITY_NONE);
                } catch (UnsupportedCommOperationException e) {
                    e.printStackTrace();
                }

                System.out.println("Open " + portName + " sucessfully");
                return fSerialPort;
            } else {
                System.err.println("Not serial port");
            }
        } catch (NoSuchPortException e) {
            System.err.println("Cann't find port");
            e.printStackTrace();
        } catch (PortInUseException e) {
            System.err.println("Port is in use");
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        if (fSerialPort != null) { 
            fSerialPort.close();
            fSerialPort = null;
        }
    }
    
    public boolean isOpen() {
    	return fSerialPort != null;
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

    public void addListener(SerialPortEventListener listener){
        try {
            fSerialPort.addEventListener(listener);
            fSerialPort.notifyOnDataAvailable(true);
            fSerialPort.notifyOnBreakInterrupt(true);

        }catch (TooManyListenersException e){
            System.err.println("Too Many Listeners");
            e.printStackTrace();
        }
    }

    public void removeListener(SerialPortEventListener listener){
        fSerialPort.removeEventListener();
    }
    
    
}


