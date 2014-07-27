#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between omc(e) - omcq(e)"
    scala application.Main quoridor 0 0 omc omcq e e max max 20 /home/qha/tmp/quoridor/set3/
done

for i in 1 2 3 4 5
do
    echo "$i times between omcq(e) - omc(e)"
    scala application.Main quoridor 0 0 omcq omc e e max max 20 /home/qha/tmp/quoridor/set3/
done

for i in 1 2 3 4 5
do
    echo "$i times between uct(e) - uctq(e)"
    scala application.Main quoridor 0 0 uct uctq e e robust robust 20 /home/qha/tmp/quoridor/set3/
done

for i in 1 2 3 4 5
do
    echo "$i times between uctq(e) - uct(e)"
    scala application.Main quoridor 0 0 uctq uct e e robust robust 20 /home/qha/tmp/quoridor/set3/
done

for i in 1 2 3 4 5
do
    echo "$i times between pbbm(e) - pbbmq(e)"
    scala application.Main quoridor 0 0 pbbm pbbmq e e robustmax robustmax 20 /home/qha/tmp/quoridor/set3/
done

for i in 1 2 3 4 5
do
    echo "$i times between pbbmq(e) - pbbm(e)"
    scala application.Main quoridor 0 0 pbbmq pbbm e e robustmax robustmax 20 /home/qha/tmp/quoridor/set3/
done