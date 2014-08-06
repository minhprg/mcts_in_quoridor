#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between Best Old 1(uct) - Best Enhanced 1(omcq)"
    scala application.Main quoridor mcts mcts 0 0 uct omcq e e robust max 20 /home/qha/tmp/quoridor/set4/
done

for i in 1 2 3 4 5
do
    echo "$i times between Best Enhanced 1(omcq) - Best Old 1(uct)"
    scala application.Main quoridor mcts mcts 0 0 omcq uct e e max robust 20 /home/qha/tmp/quoridor/set4/
done