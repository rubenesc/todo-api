#!/bin/bash

clear;
echo "Running Integration Tests... ==============================="
# Run Maven build on embedded Jetty Server 
mvn verify
#mvn -Djetty.port=9999 verify

