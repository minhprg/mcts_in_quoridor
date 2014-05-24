#!/bin/bash
for i in {1..10}
do
    echo "Welcome $i times between UCT(300) - UCTQ(300)"
    cd ../bin/ && scala application.Main quoridor 300 300 uct uctq /Users/qmha/tmp/quoridor/uct300-uctq300/
done