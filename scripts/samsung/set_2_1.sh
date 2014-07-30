#!/bin/bash

cd ../../bin/

for i in 1 2 3 4 5
do
    echo "$i times between omcq(a) - omcq(e)"
    scala application.Main quoridor 0 0 omcq omcq a e max max 20 /home/qha/tmp/quoridor/set2/
done

for i in 1 2 3 4 5
do
    echo "$i times between omcq(e) - omcq(a)"
    scala application.Main quoridor 0 0 omcq omcq e a max max 20 /home/qha/tmp/quoridor/set2/
done