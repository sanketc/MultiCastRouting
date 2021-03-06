#!/bin/bash

#Name       : run_scenario3.sh
#Arguments  : None
#Details    : To simulate the program.
#Author     : Sanket Chandorkar

sh cleanup.sh

cd ..

java -cp bin core.Controller &
java -cp bin core.Node 4 &
java -cp bin core.Node 5 &
java -cp bin core.Node 9 receiver 0 &
java -cp bin core.Node 0 sender "this is node 0 multicast message" &
java -cp bin core.Node 3 &
pid3=$!
cd scripts
sleep 1
echo "  Program running in background"
echo "  The program will terminate after 150 Secs"
echo ""
echo "  Waiting for 40 sec"
sleep 39
kill -9 $pid3
echo "  Killing Node 3"