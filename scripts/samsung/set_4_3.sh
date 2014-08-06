#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between Best Old 2(pbbm) - Best Enhanced 1(omcq)"
    scala application.Main quoridor mcts mcts 0 0 pbbm omcq e e robustmax max 20 /home/qha/tmp/quoridor/set4/
done

for i in 1 2 3 4 5
do
    echo "$i times between Best Enhanced 1(omcq) - Best Old 2(pbbm)"
    scala application.Main quoridor mcts mcts 0 0 omcq pbbm e e max robustmax 20 /home/qha/tmp/quoridor/set4/
done