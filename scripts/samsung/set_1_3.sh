#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between UCT(a) - UCT(e)"
    scala application.Main quoridor 0 0 uct uct a e robust robust 20 /home/qha/tmp/quoridor/set1/
done

for i in 1 2 3 4 5
do
    echo "$i times between UCT(a) - UCT(e)"
    scala application.Main quoridor 0 0 uct uct e a robust robust 20 /home/qha/tmp/quoridor/set1/
done