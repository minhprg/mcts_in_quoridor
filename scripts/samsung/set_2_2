#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between pbbmq(a) - pbbmq(e)"
    scala application.Main quoridor 0 0 pbbmq pbbmq a e robustmax robustmax 20 /home/qha/tmp/quoridor/set2/
done

for i in 1 2 3 4 5
do
    echo "$i times between pbbmq(e) - pbbmq(a)"
    scala application.Main quoridor 0 0 pbbmq pbbmq e a robustmax robustmax 20 /home/qha/tmp/quoridor/set2/
done