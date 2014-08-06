#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between Best Old 1(uct) - Best Enhanced 2(pbbmq)"
    scala application.Main quoridor mcts mcts 0 0 uct pbbmq e e robust robustmax 20 /home/qha/tmp/quoridor/set4/
done

for i in 1 2 3 4 5
do
    echo "$i times between Best Enhanced 2(pbbmq) - Best Old 1(uct)"
    scala application.Main quoridor mcts mcts 0 0 pbbmq uct e e robust robust 20 /home/qha/tmp/quoridor/set4/
done