package org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.impl.real.operation;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.device.mgt.iot.agent.kura.firealarm.core.operation.AgentOperationManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AgentOperationManagerImpl implements AgentOperationManager {
	private static final Logger log = LoggerFactory.getLogger(AgentOperationManagerImpl.class);

	private I2CBus _i2cbus;
	private I2CDevice _temperatureSensor;
	private GpioController _gpioController;
	private GpioPinDigitalMultipurpose _lightActuator;

	private float _temperatureRef = Float.MIN_VALUE;
	private float _humidityRef = Float.MIN_VALUE;

	private ScheduledThreadPoolExecutor _scheduledThreadPoolExecutor;
	private ScheduledFuture<?> _handle;

	private Runnable temperatureReader = new Runnable() {
		@Override
		public void run() {
			try {
				float newTemperature = readTemperature();
				if (Math.abs(_temperatureRef - newTemperature) > .1f) {
					_temperatureRef = newTemperature;
				}
			} catch (IOException e) {
				log.error(
						"An error occurred whilst trying to read temperature from GPIO Pins.");
			}
		}
	};


	@Override
	public void changeBulbStatus(boolean status) {
//		String bulbScriptPath = "";
//		String pythonCommand = "python " + bulbScriptPath + " -s " + (status ? "ON" : "OFF");
//		executeCommand(pythonCommand);

		_lightActuator.setState(status);
	}

	@Override
	public double getTemperature() {
//		String temperatureScriptPath = "";
//		String pythonCommand = "python " + temperatureScriptPath;
//		String returnVal = executeCommand(pythonCommand);
//		_temperatureRef = Float.parseFloat(returnVal.split(":")[0].replace("C", ""));
		return _temperatureRef;
	}

	@Override
	public double getHumidity() {
//		String temperatureScriptPath = "";
//		String pythonCommand = "python " + temperatureScriptPath;
//		String returnVal = executeCommand(pythonCommand);
//		_humidityRef = Float.parseFloat(returnVal.split(":")[1].replace("%", ""));
		_humidityRef = _temperatureRef;
		return _humidityRef;
	}


	public void init(){
		try {
			_gpioController = GpioFactory.getInstance();
			_i2cbus = I2CFactory.getInstance(I2CBus.BUS_1);

			_temperatureSensor = _i2cbus.getDevice(0x40);
			_lightActuator = _gpioController.provisionDigitalMultipurposePin(RaspiPin.GPIO_00, "led", PinMode.DIGITAL_OUTPUT);
			_lightActuator.setShutdownOptions(true); // unexport on shutdown

			// monitor temperature changes
			// every change of more than 0.1C will notify SensorChangedListeners
			_scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
			_handle = _scheduledThreadPoolExecutor.scheduleAtFixedRate(temperatureReader, 0, 100, TimeUnit.MILLISECONDS);

		} catch (IOException e) {
			log.error("An error occurred whilst trying to read temperature from GPIO Pins.");
		}
	}


	public void terminate(){
		if (_gpioController != null) {
			log.info("... unexport all GPIOs");
			_gpioController.unexportAll();
			log.info("... shutdown");
			_gpioController.shutdown();
			log.info("... DONE.");
		}

		if (_handle != null) {
			_handle.cancel(true);
		}
	}


	private synchronized float readTemperature() throws IOException {
		float temperature;
		// Set START (D0) and TEMP (D4) in CONFIG (register 0x03) to begin a
		// new conversion, i.e., write CONFIG with 0x11
		_temperatureSensor.write(0x03, (byte) 0x11);

		// Poll RDY (D0) in STATUS (register 0) until it is low (=0)
		int status = -1;
		while ((status & 0x01) != 0) {
			status = _temperatureSensor.read(0x00);
		}

		// Read the upper and lower bytes of the temperature value from
		// DATAh and DATAl (registers 0x01 and 0x02), respectively
		byte[] buffer = new byte[3];
		_temperatureSensor.read(buffer, 0, 3);

		int dataH = buffer[1] & 0xff;
		int dataL = buffer[2] & 0xff;

		// s_logger.info("I2C: [{}, {}]", new Object[] {dataH, dataL} );

		temperature = (dataH * 256 + dataL) >> 2;
		temperature = (temperature / 32f) - 50f;

		// s_logger.info("Temperature: {}", temperature);

		// truncate to 2 decimals
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Float.valueOf(twoDForm.format(temperature));
	}


	private String executeCommand(String command) {
		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			log.info(e.getMessage(), e);
		}

		return output.toString();

	}

}
