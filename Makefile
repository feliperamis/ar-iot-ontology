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

test:
	java -cp ".:code/lib/*:out/SmartCity.jar" jade.Boot -local-host 127.0.0.1 -container dd2:agents.Device

# Aquí pon los agentes

all: compile

