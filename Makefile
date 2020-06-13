#!/bin/sh

compile:
	javac -cp lib/jade.jar -d out/ -sourcepath code/ code/src/agents/*.java
	javac -cp lib/jade.jar -d out/ -sourcepath code/ code/src/domain/*.java

gui:
	java -cp lib/jade.jar:out/ jade.Boot -gui -local-host 127.0.0.1

termometro:
	java -cp lib/jade.jar:out/ jade.Boot -local-host 127.0.0.1 -container Termometro:code.agents.Termometro\("Que","pasa","tio"\) -local-host 127.0.0.1

all: compile gui

