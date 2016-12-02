#!/bin/bash
set -e

cd testproject
xvfb-run mvn clean integration-test

# Xvfb :99 &
# export DISPLAY=:99
# echo "Waiting for xvfb"
# sleep 10s
# #xhost +
# #icewm &
# cd testproject
# echo "Starting maven build"
# mvn clean integration-test