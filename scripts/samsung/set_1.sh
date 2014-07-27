#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between OMC(a) - OMC(e)"
    scala application.Main quoridor 0 0 omc omc a e max max 20 /home/qha/tmp/quoridor/set1/
done

for i in 1 2 3 4 5
do
    echo "$i times between OMC(e) - OMC(a)"
    scala application.Main quoridor 0 0 omc omc e a max max 20 /home/qha/tmp/quoridor/set1/
done

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

for i in 1 2 3 4 5
do
    echo "$i times between UCT(a) - UCT(e)"
    scala application.Main quoridor 0 0 uct uct a e robust robust 20 /home/qha/tmp/quoridor/set1/
done

for i in 1 2 3 4 5
do
    echo "$i times between UCT(a) - UCT(e)"
    scala application.Main quoridor 0 0 uct uct e a robust robust 20 /home/qha/tmp/quoridor/set1/
done