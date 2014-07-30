#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between omc(e) - omcq(e)"
    scala application.Main quoridor 0 0 omc omcq e e max max 20 /home/qha/tmp/quoridor/set3/
done

for i in 1 2 3 4 5
do
    echo "$i times between omcq(e) - omc(e)"
    scala application.Main quoridor 0 0 omcq omc e e max max 20 /home/qha/tmp/quoridor/set3/
done