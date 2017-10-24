// Arduino9x_RX
// -*- mode: C++ -*-
// Example sketch showing how to create a simple messaging client (receiver)
// with the RH_RF95 class. RH_RF95 class does not provide for addressing or
// reliability, so you should only use RH_RF95 if you do not need the higher
// level messaging abilities.
// It is designed to work with the other example Arduino9x_TX

#include <SPI.h>
#include <RH_RF95.h>

#define RFM95_CS 10
#define RFM95_RST 9
#define RFM95_INT 2

// Change to 434.0 or other frequency, must match RX's freq!
#define RF95_FREQ 915.0

// Singleton instance of the radio driver
RH_RF95 rf95(RFM95_CS, RFM95_INT);

// Blinky on receipt
#define LED 13

// PARAMETERS
  //POWER
int PABOOST=0; // IF INAIR9B IS USED PABOOST=1, IF INAIR9 IS USED PABOOST=0.
int POW=14; // IF PABOOST=1 POW={+5,+20}, IF PABOOST=0 POW={-1,+14}.
  //Config
int conf=10;


void setup() 
{
  pinMode(LED, OUTPUT);     
  pinMode(RFM95_RST, OUTPUT);
  digitalWrite(RFM95_RST, HIGH);

  while (!Serial);
  Serial.begin(9600);
  delay(100);

  Serial.println("Arduino LoRa RX Test!");
  
  // manual reset
  digitalWrite(RFM95_RST, LOW);
  delay(10);
  digitalWrite(RFM95_RST, HIGH);
  delay(10);

  while (!rf95.init()) {
    Serial.println("LoRa radio init failed");
    while (1);
  }
  Serial.println("LoRa radio init OK!");

  // Defaults after init are 434.0MHz, modulation GFSK_Rb250Fd250, +13dbM
  if (!rf95.setFrequency(RF95_FREQ)) {
    Serial.println("setFrequency failed");
    while (1);
  }
  Serial.print("Set Freq to: "); Serial.println(RF95_FREQ);

}

void loop()
{
// PARAMETER CONFIGURATION
  // POWER:
    // driver.setTxPower(10); // use PA_BOOST transmitter pin. +5 to +23 (for modules that use PA_BOOST) <- INAIR9B.
    // driver.setTxPower(10, true); // use PA_RFO pin transmitter pin. -1 to +14 (for modules that use RFO transmitter pin) <- INAIR9.
   if (PABOOST==1){
     rf95.setTxPower(POW);
     //Serial.println("PA=1");
     }
   else{
     rf95.setTxPower(POW,true);
     //Serial.println("PA=0");
     }
  // SPREADING FACTOR (SF), BANDWITH (B), CODE RATE (CR):
    switch(conf){
    case 0:
    rf95.setModemConfig(RH_RF95::mod0);
    case 1:
    rf95.setModemConfig(RH_RF95::mod1);
    case 2:
    rf95.setModemConfig(RH_RF95::mod2);
    case 3:
    rf95.setModemConfig(RH_RF95::mod3);
    case 4:
    rf95.setModemConfig(RH_RF95::mod4);
    case 5:
    rf95.setModemConfig(RH_RF95::mod5);
    case 6:
    rf95.setModemConfig(RH_RF95::mod6);
    case 7:
    rf95.setModemConfig(RH_RF95::mod7);
    case 8:
    rf95.setModemConfig(RH_RF95::mod8);
    case 9:
    rf95.setModemConfig(RH_RF95::mod9);
    case 10:
    rf95.setModemConfig(RH_RF95::mod10);
}
    //rf95.printRegisters();
  if (rf95.available())
  {
    // Should be a message for us now   
    uint8_t buf[RH_RF95_MAX_MESSAGE_LEN];
    uint8_t len = sizeof(buf);
    
    if (rf95.recv(buf, &len))
    {
      digitalWrite(LED, HIGH);
      RH_RF95::printBuffer("Received: ", buf, len);
      Serial.print("Got: ");
      Serial.println((char*)buf);
       Serial.print("RSSI: ");
      Serial.println(rf95.lastRssi(), DEC);
      
      // Send a reply
      uint8_t data[] = "And hello back to you";
      rf95.send(data, sizeof(data));
      rf95.waitPacketSent();
      Serial.println("Sent a reply");
      digitalWrite(LED, LOW);
    }
    else
    {
      Serial.println("Receive failed");
    }
  }
}
