// Arduino9x_RX
// -*- mode: C++ -*-
// Example sketch showing how to create a simple messaging client (receiver)
// with the RH_RF95 class. RH_RF95 class does not provide for addressing or
// reliability, so you should only use RH_RF95 if you do not need the higher
// level messaging abilities.
// It is designed to work with the other example Arduino9x_TX

#include <SPI.h>
#include <RH_RF95.h>
#include <SoftwareSerial.h>

SoftwareSerial toApp(6,7); //Rx, Tx.

#define RFM95_CS 10
#define RFM95_RST 9
#define RFM95_INT 2

// Change to 434.0 or other frequency, must match RX's freq!
#define RF95_FREQ 915.0

// Singleton instance of the radio driver
RH_RF95 rf95(RFM95_CS, RFM95_INT);

// Blinky on receipt
#define LED 13

String Flag; // Flag = start -> start sending packets, Flag = stop -> stop sending packets.
String Bost; // Bost=1 -> Inair9B, Bost=0 -> Inair9.
String Pot; // Power used.
String Mode; // LoRa Mode

void setup() 
{
  pinMode(LED, OUTPUT);     
  pinMode(RFM95_RST, OUTPUT);
  digitalWrite(RFM95_RST, HIGH);

  while (!Serial);
  Serial.begin(115200);
  delay(100);
  toApp.begin(115200);

  Serial.println("Arduino LoRa RX Tgest!");
  
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

void loop() {
  if (toApp.available()>0){
  // Serial.println("Adentro");
  // Read commands from serial.
  Flag = toApp.readStringUntil('+');
  Serial.println(Flag);
  if (Flag=="start") {
    Bost = toApp.readStringUntil('+');
    Serial.println(Bost);
    Pot = toApp.readStringUntil('+');
    Serial.println(Pot);
    Mode = toApp.readStringUntil('+');
    Serial.println(Mode);
    // SPREADING FACTOR (SF), BANDWITH (B), CODE RATE (CR):
    switch(Mode.toInt()) {
       case 0:{ rf95.setModemConfig(RH_RF95::mod0); break;}
      case 1:{ rf95.setModemConfig(RH_RF95::mod1); break;}
      case 2:{ rf95.setModemConfig(RH_RF95::mod2); break;}
      case 3:{ rf95.setModemConfig(RH_RF95::mod3); break;}
      case 4:{ rf95.setModemConfig(RH_RF95::mod4); break;}
      case 5:{ rf95.setModemConfig(RH_RF95::mod5); break;}
      case 6:{ rf95.setModemConfig(RH_RF95::mod6); break;}
      case 7:{ rf95.setModemConfig(RH_RF95::mod7); break;}
      case 8:{ rf95.setModemConfig(RH_RF95::mod8); break;}
      case 9:{ rf95.setModemConfig(RH_RF95::mod9); break;}
      case 10:{ rf95.setModemConfig(RH_RF95::mod10); break;}
        }
     }
  }
  //rf95.printRegisters();
  if (rf95.available()) {
    // Should be a message for us now   
    uint8_t buf[RH_RF95_MAX_MESSAGE_LEN];
    uint8_t len = sizeof(buf);
    
    if (rf95.recv(buf, &len)) {
      digitalWrite(LED, HIGH);
      RH_RF95::printBuffer("Received: ", buf, len);
      //Serial.print("Got: ");
      Serial.print((char*)buf);
      toApp.print((char*)buf);
      
      //Serial.print("RSSI: ");
      Serial.print(":");
      toApp.print(":");
      Serial.println(rf95.lastRssi(), DEC);
      toApp.print(rf95.lastRssi(), DEC);
      delay(10);
      toApp.write(10);
      // Send a reply
      uint8_t data[] = "And hello back to you";
      rf95.send(data, sizeof(data));
      rf95.waitPacketSent();
      Serial.println("Sent a reply");
      digitalWrite(LED, LOW);
    } else {
      Serial.println("Receive failed");
    }
  }
}


