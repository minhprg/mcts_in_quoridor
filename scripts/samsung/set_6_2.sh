#!/bin/bash

cd ../../bin/

for i in 1 2
do
    echo "$i times between uctq(e) Max - Minimax"
    scala application.Main quoridor mcts minimax 0 0 uctq minimax e e robust robust 20 /home/qha/tmp/quoridor/set6/
done

for i in 1 2
do
    echo "$i times between Minimax - uctq(e) Max"
    scala application.Main quoridor minimax mcts 0 0 minimax uctq e e robust robust 20 /home/qha/tmp/quoridor/set6/
done