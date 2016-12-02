#!/bin/bash
set -e
Xvfb :99 &
export DISPLAY=:99
echo "Waiting for xvfb"
sleep 10s
#xhost +
icewm &
echo "Waiting for icewm"
cd testproject
sleep 10s
mvn clean integration-test