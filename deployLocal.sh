#!/bin/bash

cd src/main/frontend
npm install
npm run build
cd ../../..
mvn clean install

java -jar target/lms-1.0.jar
