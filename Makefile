#!/bin/sh

compile:
	mkdir -p out/
	mkdir -p build/classes
	javac -cp ".:code/lib/*" -d build/classes code/src/**/*.java
	jar cfm out/SmartCity.jar MANIFEST.MF -C build/classes .

compileold:
	javac -cp ".:code/lib/*" -d out/ -sourcepath code/src/ code/src/agents/*.java

gui:
	java -cp ".:code/lib/*:out/SmartCity.jar" jade.Boot -gui -local-host 127.0.0.1

agents:
	java -cp ".:code/lib/*:out/SmartCity.jar" jade.Boot -local-host 127.0.0.1 -container Environment:agents.Environment
	java -cp ".:code/lib/*:out/SmartCity.jar" jade.Boot -local-host 127.0.0.1 -container Camera:agents.Camara_p3
	java -cp ".:code/lib/*:out/SmartCity.jar" jade.Boot -local-host 127.0.0.1 -container SensorPaso:agents.SensorPasos_p3
	java -cp ".:code/lib/*:out/SmartCity.jar" jade.Boot -local-host 127.0.0.1 -container SensorRuido:agents.SensorRuido_p3
	java -cp ".:code/lib/*:out/SmartCity.jar" jade.Boot -local-host 127.0.0.1 -container SensorTemperatura:agents.Termometro_p3

# Aquí pon los agentes

all: compile

