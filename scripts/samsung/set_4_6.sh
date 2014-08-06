#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between Best Enhanced 1(omcq) - Best Enhanced 2(pbbmq)"
    scala application.Main quoridor mcts mcts 0 0 omcq pbbmq e e max robustmax 20 /home/qha/tmp/quoridor/set4/
done

for i in 1 2 3 4 5
do
    echo "$i times between Best Enhanced 2(pbbmq) - Best Enhanced 1(omcq)"
    scala application.Main quoridor mcts mcts 0 0 pbbmq omcq e e robustmax max 20 /home/qha/tmp/quoridor/set4/
done