#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between Best Old 1(uct) - Best Old 2(pbbm)"
    scala application.Main quoridor mcts mcts 0 0 uct pbbm e e robust robustmax 20 /home/qha/tmp/quoridor/set4/
done

for i in 1 2 3 4 5
do
    echo "$i times between Best Old 2(pbbmq) - Best Old 1(pbbm)"
    scala application.Main quoridor mcts mcts 0 0 pbbm uct e e robustmax robust 20 /home/qha/tmp/quoridor/set4/
done