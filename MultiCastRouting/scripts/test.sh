#!/bin/bash

#Name       : test.sh
#Arguments  : None
#Details    : To simulate the program.
#Author     : Sanket Chandorkar

cd ..
java -cp bin core.Controller &
echo "Started Controller !!"

for ((  i = 0 ;  i <= 9;  i++  ))
do
  java -cp bin core.Node $i &
  echo "Started Node ID = $i !!"
done

echo "All nodes started !!"
echo "The program will terminate after 150 Secs"
cd scripts