package ctrl.spart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** 
 * 
 * @author megre
 * @email megre@vip.qq.com
 * @version created on: 2023-11-20 11:38:26
 */
public class CPUTemp {
	private final String COMMAND = "wmic /namespace:\\\\root\\WMI PATH MSAcpi_ThermalZoneTemperature get CurrentTemperature";

	public double detect() {
        try {
            Process process = Runtime.getRuntime().exec(COMMAND);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches("[0-9]+ *")) {
                    int temperature = Integer.parseInt(line.trim());
                    double celsius = (temperature - 2732) / 10.0;
//                    System.out.println("CPU 温度: " + celsius + " ℃");
                    
                    return celsius;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return -1;
	}
	
}
