#!/bin/bash
for i in {1..5}
do
    echo "$i times between omcq(e) Max - omcq(e) RobustMax"
    cd ../bin/ && scala application.Main quoridor 0 0 omcq omcq e e max robustmax 20 /Users/qmha/tmp/quoridor/set5/
done

for i in {1..5}
do
    echo "$i times between omcq(e) RobustMax - omcq(e) Max"
    cd ../bin/ && scala application.Main quoridor 0 0 omcq omcq e e robustmax max 20 /Users/qmha/tmp/quoridor/set5/
done

for i in {1..5}
do
    echo "$i times between uctq(e) Robust - uctq(e) RobustMax"
    cd ../bin/ && scala application.Main quoridor 0 0 uctq uctq e e robust robustmax 20 /Users/qmha/tmp/quoridor/set5/
done

for i in {1..5}
do
    echo "$i times between uctq(e) RobustMax - uctq(e) Robust"
    cd ../bin/ && scala application.Main quoridor 0 0 uctq uctq e e robustmax robust 20 /Users/qmha/tmp/quoridor/set5/
done