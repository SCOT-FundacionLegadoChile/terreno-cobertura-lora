// LoRa 9x_TX
// -*- mode: C++ -*-
// Example sketch showing how to create a simple messaging client (transmitter)
// with the RH_RF95 class. RH_RF95 class does not provide for addressing or
// reliability, so you should only use RH_RF95 if you do not need the higher
// level messaging abilities.
// It is designed to work with the other example LoRa9x_RX

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

// PARAMETERS
  //POWER
//int PABOOST=1; // IF INAIR9B IS USED PABOOST=1, IF INAIR9 IS USED PABOOST=0.
//int POW=20; // IF PABOOST=1 POW={+5,+20}, IF PABOOST=0 POW={-1,+14}.
  //Config
//int conf=2;

void setup() 
{
  pinMode(RFM95_RST, OUTPUT);
  digitalWrite(RFM95_RST, HIGH);

  while (!Serial);
  Serial.begin(57600);
  delay(100);
  
  toApp.begin(57600);


  Serial.println("Arduino LoRa TX Test!");

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

int16_t packetnum = 0;  // packet counter, we increment per xmission.
String Flag; // Flag = start -> start sending packets, Flag = stop -> stop sending packets.
String Bost; // Bost=1 -> Inair9B, Bost=0 -> Inair9.
String Pot; // Power used.
String Mode; // LoRa Mode
boolean Tx; // if Tx = true, continue sending packets. Tx = false, stop sending packets

void loop()
{
if (toApp.available()>0){
  Serial.println("toApp disponible");
  // Read commands from serial.
Flag = toApp.readStringUntil('+');
Serial.println(Flag);
Bost = toApp.readStringUntil('+');
Serial.println(Bost);
Pot = toApp.readStringUntil('+');
Serial.println(Pot);
Mode = toApp.readStringUntil('+');
Serial.println(Mode);

// Start configuring the module.
if (Flag=="start"){
    Serial.println("Tx -> true");
  Tx=true; // Start sending packets.
  
// PARAMETER CONFIGURATION
  // POWER:
    // driver.setTxPower(10); // use PA_BOOST transmitter pin. +5 to +23 (for modules that use PA_BOOST) <- INAIR9B.
    // driver.setTxPower(10, true); // use PA_RFO pin transmitter pin. -1 to +14 (for modules that use RFO transmitter pin) <- INAIR9.
   if (Bost.toInt()==1){
     rf95.setTxPower(Pot.toInt());
     //Serial.println("PA=1");
     //Serial.println(Pot);
     }
   else{
     rf95.setTxPower(Pot.toInt(),true);
     //Serial.println("PA=0");
     }
  // SPREADING FACTOR (SF), BANDWITH (B), CODE RATE (CR):
    switch(Mode.toInt()){
    case 0:{
    rf95.setModemConfig(RH_RF95::mod0);
    //Serial.println("mod0");
    break;}
    case 1:{
    rf95.setModemConfig(RH_RF95::mod1);
    break;}
    case 2:{
    rf95.setModemConfig(RH_RF95::mod2);
    break;}
    case 3:{
    rf95.setModemConfig(RH_RF95::mod3);
    break;}
    case 4:{
    rf95.setModemConfig(RH_RF95::mod4);
    break;}
    case 5:{
    rf95.setModemConfig(RH_RF95::mod5);
    break;}
    case 6:{
    rf95.setModemConfig(RH_RF95::mod6);
    break;}
    case 7:{
    rf95.setModemConfig(RH_RF95::mod7);
    break;}
    case 8:{
    rf95.setModemConfig(RH_RF95::mod8);
    break;}
    case 9:{
    rf95.setModemConfig(RH_RF95::mod9);
    break;}
    case 10:{
    //Serial.println("mode 10");
    rf95.setModemConfig(RH_RF95::mod10);
    break;}
}}
// if Flag = stop, change Tx to false.
else if(Flag=="stop"){
   Serial.println("Tx -> false");
  Tx=false;
}
} 

if (Tx==true){
   Serial.println("Tx mensajes");
  // Print registers
  //rf95.printRegisters();
  //Serial.println("Sending to rf95_server");
  // Send a message to rf95_server
  
  char radiopacket[20] = "Hello World #      ";
  //Convert integer to string and insert in existing str: itoa(value,str,base).
  itoa(packetnum++, radiopacket+13, 10); 
  
  //Serial.print("Sending "); 
  //Serial.println(radiopacket);
  radiopacket[19] = 0;
  
  //Serial.println("Sending...");
  delay(10);
  rf95.send((uint8_t *)radiopacket, 20);
  Serial.println(radiopacket);
  toApp.println(radiopacket);
  toApp.write(10);
  //Serial.println("Waiting for packet to complete..."); 
  delay(10);
  rf95.waitPacketSent();
  // Now wait for a reply
  uint8_t buf[RH_RF95_MAX_MESSAGE_LEN];
  uint8_t len = sizeof(buf);

  //Serial.println("Waiting for reply...");
  delay(10);
  if (rf95.waitAvailableTimeout(1000))
  { 
    // Should be a reply message for us now   
    if (rf95.recv(buf, &len))
   {
     // Serial.print("Got reply: ");
     //Serial.println((char*)buf);
     // Serial.print("RSSI: ");
     // Serial.println(rf95.lastRssi(), DEC);    
    }
    else
    {
      //Serial.println("Receive failed");
    }
  }
  else
  {
    //Serial.println("No reply, is there a listener around?");
  }
  delay(1000);
  }
}
