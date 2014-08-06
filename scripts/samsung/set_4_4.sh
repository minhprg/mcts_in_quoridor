#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between Best Old 2(pbbm) - Best Enhanced 2(pbbmq)"
    scala application.Main quoridor mcts mcts 0 0 pbbm pbbmq e e robustmax robustmax 20 /home/qha/tmp/quoridor/set4/
done

for i in 1 2 3 4 5
do
    echo "$i times between Best Enhanced 2(pbbmq) - Best Old 2(pbbm)"
    scala application.Main quoridor mcts mcts 0 0 pbbmq pbbm e e robustmax robustmax 20 /home/qha/tmp/quoridor/set4/
done