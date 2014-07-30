#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between PBBM(a) - PBBM(e)"
    scala application.Main quoridor 0 0 pbbm pbbm a e robustmax robustmax 20 /home/qha/tmp/quoridor/set1/
done

for i in 1 2 3 4 5
do
    echo "$i times between PBBM(e) - PBBM(a)"
    scala application.Main quoridor 0 0 pbbm pbbm e a robustmax robustmax 20 /home/qha/tmp/quoridor/set1/
done