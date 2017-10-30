/* Simple test of the functionality of the photo resistor

Connect the photoresistor one leg to pin 0, and pin to +5V
Connect a resistor (around 10k is a good value, higher
values gives higher readings) from pin 0 to GND. (see appendix of arduino notebook page 37 for schematics).

----------------------------------------------------

           PhotoR     10K
 +5    o---/\/\/--.--/\/\/---o GND
                  |
 Pin 0 o-----------

----------------------------------------------------
*/
// Sensor luz.
int led_l1= 13;
int led_l2= 12;
int led_l3= 11;
int led_l4= 10;
int led_l5= 9;
int ul1=900;
int ul2=600;
int ul3=300;
int ul4=150;
int ul5=50;
int sensorPin = A0; 
int luz=0;

// Sensor luz.
int led_t1= 14;
int led_t2= 15;
int led_t3= 16;
int led_t4= 17;
int led_t5= 18;
int utmm=740;
int utm=720;
int utl=680;
int utll=640;
int sensorPint = A1; 
int temp=0;


// Sensor sonido.
int led_s1= 22;
int led_s2= 23;
int led_s3= 24;
int led_s4= 25;
int led_s5= 26;
int usmm=;
int usm=;
int usl=;
int usll=;
int sensorPins = A2; 
int sound=0;


// Sensor distancia.
long tiempo;
int disparador = 7;   // triger
int entrada = 8;      // echo
int led_d1= 6;
int led_d2= 5;
int led_d3= 4;
int led_d4= 3;
int led_d5= 2;
int ud1=150;
int ud2=45;
int ud3=15;
int ud4=5;
int ud5=0;
float distancia;





void setup()
{
    Serial.begin(9600);  //Begin serial communcation
  pinMode(led_l1,OUTPUT);
  pinMode(led_l2,OUTPUT);
  pinMode(led_l3,OUTPUT);
  pinMode(led_l4,OUTPUT);
  pinMode(led_l5,OUTPUT);  

  pinMode(led_t1,OUTPUT);
  pinMode(led_t2,OUTPUT);
  pinMode(led_t3,OUTPUT);
  pinMode(led_t4,OUTPUT);
  pinMode(led_t5,OUTPUT); 
  
  pinMode(disparador, OUTPUT);
  pinMode(entrada, INPUT);
  pinMode(led_d1,OUTPUT);
  pinMode(led_d2,OUTPUT);
  pinMode(led_d3,OUTPUT);
  pinMode(led_d4,OUTPUT);
  pinMode(led_d5,OUTPUT);  
  
}

void loop()
{
  //LUZ
luz = analogRead(sensorPin);
//Serial.println(luz);

 if (luz>ul1){
    Serial.println("1");
    digitalWrite(led_l1,HIGH);
    digitalWrite(led_l2,HIGH);
    digitalWrite(led_l3,HIGH);
    digitalWrite(led_l4,HIGH);
    digitalWrite(led_l5,HIGH);
} else if (luz<ul1  && luz>=ul2) {
    Serial.println("2");
    digitalWrite(led_l1,HIGH);
    digitalWrite(led_l2,HIGH);
    digitalWrite(led_l3,HIGH);
    digitalWrite(led_l4,HIGH);
    digitalWrite(led_l5,LOW);
} else if (luz<ul2 && luz>=ul3) {
    Serial.println("3");
    digitalWrite(led_l1,HIGH);
    digitalWrite(led_l2,HIGH);
    digitalWrite(led_l3,HIGH);
    digitalWrite(led_l4,LOW);
    digitalWrite(led_l5,LOW);
} else if (luz<ul3 && luz>=ul4) {
    Serial.println("4");
    digitalWrite(led_l1,HIGH);
    digitalWrite(led_l2,HIGH);
    digitalWrite(led_l3,LOW);
    digitalWrite(led_l4,LOW);
    digitalWrite(led_l5,LOW);

} else if (luz<ul4 && luz>=ul5) {
    Serial.println("5");
    digitalWrite(led_l1,HIGH);
    digitalWrite(led_l2,LOW);
    digitalWrite(led_l3,LOW);
    digitalWrite(led_l4,LOW);
    digitalWrite(led_l5,LOW);
} else if (luz<ul5){
    digitalWrite(led_l1,LOW);
    digitalWrite(led_l2,LOW);
    digitalWrite(led_l3,LOW);
    digitalWrite(led_l4,LOW);
    digitalWrite(led_l5,LOW);
  }

// TEMP
  temp= analogRead(sensorPint);
Serial.print(temp);
Serial.println("AUDIO");

 if (temp>utmm){
    Serial.println("1");
    digitalWrite(led_t1,HIGH);
    digitalWrite(led_t2,HIGH);
    digitalWrite(led_t3,HIGH);
    digitalWrite(led_t4,LOW);
    digitalWrite(led_t5,LOW);
} else if (temp<utmm  && temp>=utm) {
    Serial.println("2");
    digitalWrite(led_t1,LOW);
    digitalWrite(led_t2,HIGH);
    digitalWrite(led_t3,HIGH);
    digitalWrite(led_t4,LOW);
    digitalWrite(led_t5,LOW);
} else if (temp<utm && luz>=utl) {
    Serial.println("3");
    digitalWrite(led_t1,LOW);
    digitalWrite(led_t2,LOW);
    digitalWrite(led_t3,HIGH);
    digitalWrite(led_t4,LOW);
    digitalWrite(led_t5,LOW);
} else if (temp<utl && luz>=utll) {
    Serial.println("4");
    digitalWrite(led_t1,LOW);
    digitalWrite(led_t2,LOW);
    digitalWrite(led_t3,HIGH);
    digitalWrite(led_t4,HIGH);
    digitalWrite(led_t5,LOW);
} else if (temp<utll){
    digitalWrite(led_t1,LOW);
    digitalWrite(led_t2,LOW);
    digitalWrite(led_t3,HIGH);
    digitalWrite(led_t4,HIGH);
    digitalWrite(led_t5,HIGH);
  }

// PROXIMIDAD

  // lanzamos un pequeño pulso para activar el sensor
  digitalWrite(disparador, HIGH);
  delayMicroseconds(10);
  digitalWrite(disparador, LOW);
  
  // medimos el pulso de respuesta
  tiempo = (pulseIn(entrada, HIGH)/2); // dividido por 2 por que es el 
                                       // tiempo que el sonido tarda
                                       // en ir y en volver
  // ahora calcularemos la distancia en cm
  // sabiendo que el espacio es igual a la velocidad por el tiempo
  // y que la velocidad del sonido es de 343m/s y que el tiempo lo 
  // tenemos en millonesimas de segundo
  distancia = float(tiempo * 0.0343);

  if (distancia>ud1){
    Serial.println("1");
    digitalWrite(led_d1,HIGH);
    digitalWrite(led_d2,LOW);
    digitalWrite(led_d3,LOW);
    digitalWrite(led_d4,LOW);
    digitalWrite(led_d5,LOW);
} else if (distancia<ud1  && distancia>=ud2) {
    Serial.println("2");
    digitalWrite(led_d1,HIGH);
    digitalWrite(led_d2,HIGH);
    digitalWrite(led_d3,LOW);
    digitalWrite(led_d4,LOW);
    digitalWrite(led_d5,LOW);
} else if (distancia<ud2 && distancia>=ud3) {
    Serial.println("3");
    digitalWrite(led_d1,HIGH);
    digitalWrite(led_d2,HIGH);
    digitalWrite(led_d3,HIGH);
    digitalWrite(led_d4,LOW);
    digitalWrite(led_d5,LOW);
} else if (distancia<ud3 && distancia>=ud4) {
    Serial.println("4");
    digitalWrite(led_d1,HIGH);
    digitalWrite(led_d2,HIGH);
    digitalWrite(led_d3,HIGH);
    digitalWrite(led_d4,HIGH);
    digitalWrite(led_d5,LOW);
} else if (distancia<ud4) {                     
    Serial.println("5");
    digitalWrite(led_d1,HIGH);
    digitalWrite(led_d2,HIGH);
    digitalWrite(led_d3,HIGH);
    digitalWrite(led_d4,HIGH);
    digitalWrite(led_d5,HIGH);
}
  
  // y lo mostramos por el puerto serie una vez por segundo
  //Serial.println(distancia);

   delay(100); //short delay for faster response to light.
}
