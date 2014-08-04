#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between Best Enhanced 2(uctq) - Best Old 2(pbbm)"
    scala application.Main quoridor 0 0 uctq pbbm e e robust robustmax 20 /home/qha/tmp/quoridor/set4/
done

for i in 1 2 3 4 5
do
    echo "$i times between Best Old 2(pbbm) - Best Enhanced 2(uctq)"
    scala application.Main quoridor 0 0 pbbm uctq e e robustmax robust 20 /home/qha/tmp/quoridor/set4/
done