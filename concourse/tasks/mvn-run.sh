#!/bin/bash
set -e
Xvfb :99 &
export DISPLAY=:99
sleep 10s
xhost +
icewm &
cd testproject
sleep 10s
mvn clean integration-test