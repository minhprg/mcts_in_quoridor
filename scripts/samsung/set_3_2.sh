#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between uct(e) - uctq(e)"
    scala application.Main quoridor 0 0 uct uctq e e robust robust 20 /home/qha/tmp/quoridor/set3/
done

for i in 1 2 3 4 5
do
    echo "$i times between uctq(e) - uct(e)"
    scala application.Main quoridor 0 0 uctq uct e e robust robust 20 /home/qha/tmp/quoridor/set3/
done