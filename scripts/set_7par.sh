#!/bin/bash

# test paralelism

cd ../bin/

for i in 1 2 3 4 5
do
    echo "$i times between omc(e) Max - omcq(e) Max"
    scala application.Main quoridorPar mcts mcts 0 0 omc omcq e e max max 20 /Users/qmha/tmp/quoridor/set7par/
done

for i in 1 2 3 4 5
do
    echo "$i times between omcq(e) Max - omc(e) Max"
    scala application.Main quoridorPar mcts mcts 0 0 omcq omc e e max max 20 /Users/qmha/tmp/quoridor/set7par/
done