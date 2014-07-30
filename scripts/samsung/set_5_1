#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between omcq(e) Max - omcq(e) RobustMax"
    scala application.Main quoridor 0 0 omcq omcq e e max robustmax 20 /home/qha/tmp/quoridor/set5/
done

for i in 1 2 3 4 5
do
    echo "$i times between omcq(e) RobustMax - omcq(e) Max"
    scala application.Main quoridor 0 0 omcq omcq e e robustmax max 20 /home/qha/tmp/quoridor/set5/
done