package ctrl.spart;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class DataListener implements SerialPortDataListener {
	private SerialPort fSerialPort;
	
	public DataListener(SerialPort serialPort) {
		fSerialPort = serialPort;
	}

	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		try {
			if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
				byte[] readBuffer = new byte[1024];
				int numRead = fSerialPort.readBytes(readBuffer, readBuffer.length);
				if (numRead > 0) {
					StringBuilder data = new StringBuilder();
					for (int i = 0; i < numRead; i++) {
						// 加入阻塞队列用于其他线程后续数据处理
						// DataQueue.sensorDataQueue.put(String.format("%02X",
						// readBuffer[i]));
						data.append(String.format("%02X ", readBuffer[i]));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
