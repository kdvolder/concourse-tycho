#!/bin/bash
set -e
xvfb :99 &
export DISPLAY=:99
xhost +
icewm &
cd testproject
mvn clean integration-test