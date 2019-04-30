/******************************************************************************
-Oversalmpling de presion 1
-Filtro IIR, coeficiente = 2
-temperature oversampling = 0
******************************************************************************/
//#include <MsTimer2.h>
//#include <stdint.h>
//#include "SPI.h"
#include "SparkFunBME280.h"
#include "Wire.h"
#include <SoftwareSerial.h>

float ambiente = 0;
float p = 0;
float v[5];
unsigned long time;
unsigned long prevmillis=0;
int pulse=54;

BME280 mySensor;
//BME280 mySensor2;
SoftwareSerial btSerial(7, 8); // RX, TX

void setup()
{
	//***Driver settings********************************//
	//commInterface can be I2C_MODE or SPI_MODE
	//specify chipSelectPin using arduino pin names
	//specify I2C address.  Can be 0x77(default) or 0x76
	
	//For I2C, enable the following and disable the SPI section
	mySensor.settings.commInterface = I2C_MODE;
	mySensor.settings.I2CAddress = 0x76;
	
	//For SPI enable the following and dissable the I2C section
	//mySensor.settings.commInterface = SPI_MODE;
	//mySensor.settings.chipSelectPin = 10;


	//***Operation settings*****************************//
	
	//renMode can be:
	//  0, Sleep mode
	//  1 or 2, Forced mode
	//  3, Normal mode
	mySensor.settings.runMode = 3; //Normal mode
	
	//tStandby can be:
	//  0, 0.5ms
	//  1, 62.5ms
	//  2, 125ms
	//  3, 250ms
	//  4, 500ms
	//  5, 1000ms
	//  6, 10ms
	//  7, 20ms
	mySensor.settings.tStandby = 0;
	
	//filter can be off or number of FIR coefficients to use:
	//  0, filter off
	//  1, coefficients = 2
	//  2, coefficients = 4
	//  3, coefficients = 8
	//  4, coefficients = 16
	mySensor.settings.filter = 2;
	
	//tempOverSample can be:
	//  0, skipped
	//  1 through 5, oversampling *1, *2, *4, *8, *16 respectively
	mySensor.settings.tempOverSample = 0;

	//pressOverSample can be:
	//  0, skipped
	//  1 through 5, oversampling *1, *2, *4, *8, *16 respectively
    mySensor.settings.pressOverSample = 4;
	
	//humidOverSample can be:
	//  0, skipped
	//  1 through 5, oversampling *1, *2, *4, *8, *16 respectively
	mySensor.settings.humidOverSample = 1;
	
	Serial.begin(115200);
	//Serial.print("Program Started\n");
	("Starting BME280... result of .begin(): 0x");

  btSerial.begin(9600);
	
	//Calling .begin() causes the settings to be loaded
	delay(10);  //Make sure sensor had enough time to turn on. BME280 requires 2ms to start up.
	(mySensor.begin(), HEX);

	("Displaying ID, reset and ctrl regs\n");
	
	("ID(0xD0): 0x");
	(mySensor.readRegister(BME280_CHIP_ID_REG), HEX);
	("Reset register(0xE0): 0x");
	(mySensor.readRegister(BME280_RST_REG), HEX);
	("ctrl_meas(0xF4): 0x");
	(mySensor.readRegister(BME280_CTRL_MEAS_REG), HEX);
	("ctrl_hum(0xF2): 0x");
	(mySensor.readRegister(BME280_CTRL_HUMIDITY_REG), HEX);


	("Displaying all regs\n");
	uint8_t memCounter = 0x80;
	uint8_t tempReadData;
	for(int rowi = 8; rowi < 16; rowi++ )
	{
		("0x");
		(rowi, HEX);
		("0:");
		for(int coli = 0; coli < 16; coli++ )
		{
			tempReadData = mySensor.readRegister(memCounter);
			((tempReadData >> 4) & 0x0F, HEX);//Print first hex nibble
			(tempReadData & 0x0F, HEX);//Print second hex nibble
			(" ");
			memCounter++;
		}
		("\n");
	}
	

("Displaying concatenated calibration words\n");
("dig_T1, uint16: ");
(mySensor.calibration.dig_T1);
("dig_T2, int16: ");
(mySensor.calibration.dig_T2);
("dig_T3, int16: ");
(mySensor.calibration.dig_T3);
	
("dig_P1, uint16: ");
(mySensor.calibration.dig_P1);
("dig_P2, int16: ");
(mySensor.calibration.dig_P2);
("dig_P3, int16: ");
(mySensor.calibration.dig_P3);
("dig_P4, int16: ");
(mySensor.calibration.dig_P4);
("dig_P5, int16: ");
(mySensor.calibration.dig_P5);
("dig_P6, int16: ");
(mySensor.calibration.dig_P6);
("dig_P7, int16: ");
(mySensor.calibration.dig_P7);
("dig_P8, int16: ");
(mySensor.calibration.dig_P8);
("dig_P9, int16: ");
(mySensor.calibration.dig_P9);

("dig_H1, uint8: ");
(mySensor.calibration.dig_H1);
("dig_H2, int16: ");
(mySensor.calibration.dig_H2);
("dig_H3, uint8: ");
(mySensor.calibration.dig_H3);
("dig_H4, int16: ");
(mySensor.calibration.dig_H4);
("dig_H5, int16: ");
(mySensor.calibration.dig_H5);
("dig_H6, uint8: ");
(mySensor.calibration.dig_H6);

delay (10);
(mySensor.readTempC());
ambiente = mySensor.readFloatPressure();
}

void loop()
{
  if (millis()-prevmillis >= 6){
    presion();
    takepulse();
    if (btSerial.available()){  
      btsend();
    }
	}
}

void presion()
{
      //(mySensor.readTempC());
      prevmillis=millis();
      //p=(mySensor.readFloatPressure()-ambiente);
      p=(mySensor.readFloatPressure()-ambiente)*0.007500617;
}

void takepulse()
{
   pulse = analogRead(A0);
}

void btsend()
{
  btSerial.print(prevmillis);
  btSerial.print(";");
  btSerial.print(p, 3);
  btSerial.print(";");
  btSerial.println(pulse);
  //btSerial.write("Testing...");
  Serial.print(prevmillis);
  Serial.print(";");
  Serial.print(p, 3);
  Serial.print(";");
  Serial.println(pulse);

}
