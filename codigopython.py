from time import sleep

import time
import wiringpi
import Adafruit_DHT
import signal
from datetime import datetime


from gpiozero import LED, Button

from threading import Thread

import firebase_admin

from firebase_admin import credentials

from firebase_admin import db
from firebase_admin import firestore
import paho.mqtt.client as mqtt




PAHT_CRED = '/home/pi/Downloads/termohealth-44b6c-firebase-adminsdk-cdi23-7d414702c3.json'

URL_DB = 'https://termohealth-44b6c-default-rtdb.firebaseio.com/'

REF_HOME = 'home'



REF_SENSORES = 'sensores'



REF_TEMPERATURA = 'temperatura'



REF_TOUCH = 'touch'


REF_WATA = 'wata'

class IOT():
    
    def __init__(self):
                
        
                

        cred = credentials.Certificate(PAHT_CRED)# credencial para mandar datos hacia firebase

        firebase_admin.initialize_app(cred, {

            'databaseURL': URL_DB# url para conectar con el proyecto Termohealth creado en firebase

        })
        
        
        

        self.refHome = db.reference(REF_HOME)#Suscripción al broker home

 

        self.estructuraInicialDB() #Creación de canales en firebase
        

        

       
        

       


        self.refSensores = self.refHome.child(REF_SENSORES)# Se crea un topic en el canal Home para los sensores

        self.refTemperatura= self.refSensores.child(REF_TEMPERATURA)#Se crea el campo de temperatura
        
        self.refTouch= self.refSensores.child(REF_TOUCH)#Se crea el campo del sensor touch
        
        self.refWata= self.refSensores.child(REF_WATA)# se crea el campo del medidor de agua
        
    def medidor_agua(self):
        
        if (wiringpi.digitalRead(0) and wiringpi.digitalRead(1) and wiringpi.digitalRead(4) and wiringpi.digitalRead(6)) :
            self.refWata.set(100)
            return("100")
            
        elif(wiringpi.digitalRead(0) and wiringpi.digitalRead(1) and wiringpi.digitalRead(4) and not(wiringpi.digitalRead(6))):
            self.refWata.set(75)
            return("75")
            
        elif(wiringpi.digitalRead(0) and wiringpi.digitalRead(1) and not(wiringpi.digitalRead(4)) and not(wiringpi.digitalRead(6))):
            self.refWata.set(50)
            return("50")
        elif(wiringpi.digitalRead(0) and not(wiringpi.digitalRead(1)) and not(wiringpi.digitalRead(4)) and not(wiringpi.digitalRead(6))):
            self.refWata.set(25)
            return("25")
        elif(not(wiringpi.digitalRead(0) and wiringpi.digitalRead(1) and wiringpi.digitalRead(4) and wiringpi.digitalRead(6))) :
            self.refWata.set(0)
            return("0")
        else:
            return("Medicion erronea")
    def temperatura(self):
        humidity, temperature = Adafruit_DHT.read_retry(11, 4)#definición de lectura sensor de temperatura
        
        
        if humidity is not None and temperature is not None:
            self.refTemperatura.set(int(temperature))#envío de datos de temperatura hacia firebase realtime database 
            return (int(temperature))
        
        else:
            return('Failed to get reading. Try again!')
        
    def touch(self):
        if wiringpi.digitalRead(3)==0:
            self.refTouch.set(1)#envío de datos del sensor touch hacia firebase realtime database
            return(1)
            
        else:
            self.refTouch.set(0)#envío de datos del sensor touch hacia firebase realtime database
            return(0)
    
    
    
    
  
   
            
        

    def estructuraInicialDB(self):

        self.refHome.set({
         
            #definición de la estructura del topic de firebase
            'sensores':{
                #aquí se define de qué tipo de variable va a ser cada campo
                'temperatura':0,
                'touch':0,
                'wata':0

            }

        })

   

  


#setup del código
print ('START !')

iot = IOT()#Inicialización de la clase
#inicialización de contadores
T_touchBef=time.perf_counter()
T_touchAct=time.perf_counter()
T_tempBef=time.perf_counter()
T_tempAct=time.perf_counter()
T_tempEBef=time.perf_counter()
T_tempEAct=time.perf_counter()
T_WaterBef=time.perf_counter()
T_WaterAct=time.perf_counter()
#Inicialización de pines de entrada4
wiringpi.wiringPiSetup()
wiringpi.pinMode(3, 0)
wiringpi.pinMode(4, 0)
wiringpi.pinMode(6, 0)
wiringpi.pinMode(0, 0)
wiringpi.pinMode(1, 0)
client = mqtt.Client()
client.connect("mqtt.thingspeak.com",1883,60)##Se inicia el proceso mqtt con el servidor de thingspeak
channelId = "1349786"         # Put your channel ID here,i.e.. the number from the URL, https://thingspeak.com/channels/285697
apiKey = "6S31WMH29PB2P79N"  # Put the API key here (the Write API Key from the API Keys tab in ThingSpeak)
temp=[]#creación de una lista para los datos de temperatura
puls=[]#creación de una lista para los datos del sensor touch
wata=[]#creación de una lista para los datos del medidor de agua
#inicialización de contadores
CONTD=0
CONTD2=0
CONTD3=0
while True:
    #Toma de datos sensor touch cada 4 segundos
        if(T_touchAct-T_touchBef>=4):
            puls.append('p')#identificador
            puls.append(str(iot.touch()))#dato sensor
            puls.append(str(datetime.now()))#timestamp
            T_touchBef= T_touchAct#reiniciar contador
            CONTD2=CONTD2+1
        else:
            T_touchAct=time.perf_counter()
    #Toma de datos sensor temperatura cada 10 segundos
        if(T_tempAct-T_tempBef>=10):
            temp.append('t')#identificador
            temp.append(str(iot.temperatura()))#dato sensor
            temp.append(str(datetime.now()))#timestamp
            T_tempBef= T_tempAct#reiniciar contador
            CONTD=CONTD+1
        else:
            T_tempAct=time.perf_counter()
    #Toma de datos medidor de agua cada 10 segundos
        if(T_WaterAct-T_WaterBef>=10):
            wata.append('w')#identificador
            wata.append(str(iot.medidor_agua()))#dato sensor
            wata.append(str(datetime.now()))#timestamp
            CONTD3=CONTD3+1
            
            T_WaterBef= T_WaterAct#reiniciar contador
            
        else:
            T_WaterAct=time.perf_counter();#Actualiza el contador
        if((CONTD==2)&(CONTD2==5)&(CONTD3==2)):#se pregunta si se han tomado todos los datos
            client.publish("channels/%s/publish/%s" % (channelId,apiKey), "field1="+str(temp)+"&field2="+str(puls)+"&field3="+str(wata))#Envío de datos a los 3 campos de thingspeak
            #impresión de listas
            print(str(temp))
            print(str(puls))
            print(str(wata))
            #se vacían las listas
            temp=[]
            puls=[]
            wata=[]
            #reinicio de contadores
            T_WaterBef= T_WaterAct
            T_touchBef= T_touchAct
            T_tempBef= T_tempAct
            T_tempEBef= T_tempEAct
            #reinicio de contadores
            CONTD=0
            CONTD2=0
            CONTD3=0
        else:
            T_tempEAct=time.perf_counter()#Actualiza el contador 
        