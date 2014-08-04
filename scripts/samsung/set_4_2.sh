#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between Best Old 1(omc) - Best Enhanced 2(uctq)"
    scala application.Main quoridor 0 0 omc uctq e e max robust 20 /home/qha/tmp/quoridor/set4/
done

for i in 1 2 3 4 5
do
    echo "$i times between Best Enhanced 2(uctq) - Best Old 1(omc)"
    scala application.Main quoridor 0 0 uctq omc e e robust max 20 /home/qha/tmp/quoridor/set4/
done