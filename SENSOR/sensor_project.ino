/*
   Harjoitus 6. AskSensors-pilvi.
   Kurssi: TIES536 Sulautettu Internet
   Author @ Teemu Käpylä
   https://www.teemukapyla.fi
*/

// sisällytetään relevantit kirjastot
#include <WiFi.h>
#include <WiFiMulti.h>
#include <HTTPClient.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>
#include <ArduinoJson.h>
#include <SPI.h>

Adafruit_BME280 bme;

#define LIGHTPIN 34 // valosensori pinnissä 34
#define REDPIN 16 // punainen ledi pinnissä 16
#define GREENPIN 4 // vihreä ledi pinnissä 4
#define BLUEPIN 17  // sininen ledi pinnissä 17
#define MOTIONPIN 26 // PIR-liikesensori pinnissä 26

WiFiMulti WiFiMulti;
HTTPClient ask;
WiFiClient client;

// WiFi, API ja intervallikonfiguraatio ja vähän muutakin
const char* ssid     = "xxx"; //Wifi SSID
const char* password = "xxx"; //Wifi Password
const char* firstSensorApi = "xxx";      // API KEY IN
const char* secondSensorApi = "xxx";      // API KEY IN
const unsigned int writeInterval = 1800000;   // write interval (in ms)
const int NIGHTLIGHT = 60000; // yövalo pysyy käynnissä (ms)
bool firstRound = true;

// ASKSENSORS API host config
const char* host = "api.asksensors.com";  // API host
const char* weatherHost = "api.openweathermap.org"; // API host
const int httpPort = 80;      // port

// liikkeenhavainnointiin liittyvät liput
bool motionDetected = false;
bool isMotionSetOff = true;
bool isLightSetOff = true;
bool lightOn = false;

// liikkeenhavainnointiin liittyvät aikamuuttujat
unsigned long timeLastUpdated;
unsigned long lastMotionDetected;
unsigned long lightOnSince;

void setup() {

  bme.begin(0x76);
  pinMode(MOTIONPIN, INPUT);
  pinMode(REDPIN, OUTPUT);
  pinMode(GREENPIN, OUTPUT);
  pinMode(BLUEPIN, OUTPUT);

  // avataan sarjaportti
  Serial.begin(115200);

  Serial.println("*****************************************************");
  Serial.println("********** Program Start : Connect ESP32 to AskSensors.");
  Serial.println("Wait for WiFi... ");

  // connecting to the WiFi network
  WiFiMulti.addAP(ssid, password);
  while (WiFiMulti.run() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }

  // yhdistetty
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  // keskeytystä kutsutaan vain kun PIR-sensorin tilassa on tapahtunut muutos (CHANGE)
  attachInterrupt(digitalPinToInterrupt(MOTIONPIN), tunnistus, CHANGE);
}

// tätä kutsutaan liikkeentunnistussensorin keskeytyskäsittelyssä
// globaaliin muuttujaan tallennetaan tulos
void tunnistus() {
  byte tila = digitalRead(MOTIONPIN);
  if (tila == 1) {
    motionDetected = true;
    lightOn = true;
  }
}


void loop() {

  // Laitetaan yövalo päälle kun ollaan havaittu liikettä
  if (lightOn && isLightSetOff) {
    digitalWrite(REDPIN, 1);
    digitalWrite(GREENPIN, 1);
    digitalWrite(BLUEPIN, 1); 
    Serial.println("Valot päällä!");
    lightOnSince = millis();   
    lightOn = false;
    isLightSetOff = false;
  }

  // ja valo pois päältä kun liikkeestä on kulunut tarpeeksi aikaa
  if (!isLightSetOff && millis() - lightOnSince >= NIGHTLIGHT) {
    digitalWrite(REDPIN, 0);
    digitalWrite(GREENPIN, 0);
    digitalWrite(BLUEPIN, 0); 
    Serial.println("Valot pois päältä!");
    lightOn = false;
    isLightSetOff = true;
  }


  // Päivitetään PIR-sensoria kuvaavaa arvoa tarvittaessa, jos viime kerrasta on kymmenen sekuntia
  if (motionDetected && millis() - lastMotionDetected >= 10000) {
    Serial.println("Liikettä havaittu!");

    if (client.connect(host, httpPort)) {

      String motionUrl = "http://api.asksensors.com/write/";
      motionUrl += secondSensorApi;
      motionUrl += "?module2=";
      motionUrl += 1;
      motionUrl += "&module3=";
      motionUrl += 1;

      ask.begin(motionUrl);

      // katsotaan mikä koodi saadaan HTTP-pyynnöstä
      int httpCode = ask.GET();

      if (httpCode > 0) {

        String payload = ask.getString();
        Serial.println(httpCode);
        Serial.println(payload);
      } else {
        Serial.println("Error on HTTP request");
      }
  
      ask.end();
      client.stop();
      motionDetected = false;
      isMotionSetOff = false;
      lastMotionDetected = millis();
    }
  }

  // palautetaan myös pilven PIR-tieto OFF-asentoon, jos viimeisimmästä havaitusta
  // liikkeestä on mennyt 30s, eikä se ole vielä OFF-asennossa
  if (!isMotionSetOff && millis() - lastMotionDetected >= 30000) {
     if (client.connect(host, httpPort)) {

      String motionUrl = "http://api.asksensors.com/write/";
      motionUrl += secondSensorApi;
      motionUrl += "?module2=";
      motionUrl += 0;
      motionUrl += "&module3=";
      motionUrl += 0;

      ask.begin(motionUrl);

      // katsotaan mikä koodi saadaan HTTP-pyynnöstä
      int httpCode = ask.GET();

      if (httpCode > 0) {

        String payload = ask.getString();
        Serial.println(httpCode);
        Serial.println(payload);
      } else {
        Serial.println("Error on HTTP request");
      }
  
      ask.end();
      client.stop();
      isMotionSetOff = true;
    }
  }

  // ajetaan sensoripäivitykset, jos viimeisimmästä päivityksestä on kulunut 30 min tai on ensimmäinen kierros menossa
  if (millis() - timeLastUpdated > writeInterval || firstRound == true) {

    // TCP yhteyden luonti
    if (!client.connect(host, httpPort)) {
      Serial.println("connection failed");
      return;
    } else {

      // haetaan Open Weather Map APIsta lämpötilatieto
      float celsiusOpenApi = weather();

      // tarkastetaan kyselyn yhteydessä kaikkien muiden paitsi liikesensorin tilanne
      float temperature = bme.readTemperature();
      int humidity = bme.readHumidity();
      int pressure = bme.readPressure() / 100;
      int lux = analogRead(LIGHTPIN);

      // luodaan url ensimmäisen sensorin päivitystä varten
      String url = "http://api.asksensors.com/write/";
      url += firstSensorApi;
      url += "?module1=";
      url += temperature;
      url += "&module2=";
      url += temperature;
      url += "&module3=";
      url += humidity;
      url += "&module4=";
      url += pressure;

      // url toisen sensorin päivitystä varten
      String secondUrl = "http://api.asksensors.com/write/";
      secondUrl += secondSensorApi;
      secondUrl += "?module1=";
      secondUrl += lux;
      secondUrl += "&module4=";
      secondUrl += celsiusOpenApi;
      secondUrl += "&module2=";
      secondUrl += 0;
      secondUrl += "&module3=";
      secondUrl += 0;

      // pyyntö ensimmäin sensorin apille
      Serial.println(url);
      // lähetetään API-pyyntö
      ask.begin(url);

      // katsotaan mikä koodi saadaan HTTP-pyynnöstä
      int httpCode = ask.GET();

      if (httpCode > 0) {

        String payload = ask.getString();
        Serial.println(httpCode);
        Serial.println(payload);
      } else {
        Serial.println("Error on HTTP request");
      }

      ask.end(); //End

      // pyyntö toisen sensorin apille
      Serial.println(secondUrl);
      // lähetetään API-pyyntö
      ask.begin(secondUrl);

      // katsotaan mikä koodi saadaan HTTP-pyynnöstä
      httpCode = ask.GET();

      if (httpCode > 0) {

        String payload = ask.getString();
        Serial.println(httpCode);
        Serial.println(payload);
      } else {
        Serial.println("Error on HTTP request");
      }

      ask.end(); // lähetyksen loppu
      client.stop();  // pysäytetään yhteys

    }
    timeLastUpdated = millis();
    firstRound = false;
  }
}

// aliohjelma säätietoAPIn käyttöön
float weather() {

  if (!client.connect(weatherHost, 80)) {
    Serial.println("2. connection failed");
    return 0;
  } else {

    // kovakoodattu API-pyyntö Open Weather Mappiin.
    client.println(F("GET /data/2.5/weather?q=Jyväskylä&appid=3ad47f513f5e580203d5790e72d26e16 HTTP/1.0"));
    client.println(F("Host: api.openweathermap.org"));
    client.println(F("Connection: close"));

    if (client.println() == 0) {
      Serial.println(F("Failed to send request"));
      return 0;
    }

    // Check HTTP status
    char status[32] = {0};
    client.readBytesUntil('\r', status, sizeof(status));
    if (strcmp(status, "HTTP/1.1 200 OK") != 0) {
      Serial.print(F("Unexpected response: "));
      Serial.println(status);
      return 0;
    }

    // Skip HTTP headers
    char endOfHeaders[] = "\r\n\r\n";
    if (!client.find(endOfHeaders)) {
      Serial.println(F("Invalid response"));
      return 0;
    }

    // Allocate the JSON document
    // Use arduinojson.org/v6/assistant to compute the capacity.
    const size_t capacity = JSON_ARRAY_SIZE(1) + JSON_OBJECT_SIZE(1) + 2 * JSON_OBJECT_SIZE(2) + JSON_OBJECT_SIZE(4) + JSON_OBJECT_SIZE(5) + JSON_OBJECT_SIZE(6) + JSON_OBJECT_SIZE(13) + 270;
    DynamicJsonDocument doc(capacity);

    // Parse JSON object
    DeserializationError error = deserializeJson(doc, client);
    if (error) {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.c_str());
      return 0;
    }

    // kaivetaan haluttu lämpötilaarvo ja muunnetaan celsius-skaalalle
    JsonObject main = doc["main"];
    float main_temp = main["temp"];
    float celsius = main_temp - 273.15;

    client.stop();
    // palautetaan lämpötila-arvo pääluuppiin
    return celsius;
  }
}
