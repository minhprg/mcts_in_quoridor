#!/bin/bash
for i in {1..5}
do
    echo "Welcome $i times between UCT(100) - UCTQ(100"
    scala application.Main quoridor 100 100 uct uctq /Users/qmha/tmp/quoridor/
done