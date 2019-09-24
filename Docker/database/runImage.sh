#!/bin/bash

if [ "$1" == "up" ];then
	docker-compose -f ../build/docker-compose.yaml up -d
fi

if [ "$1" == "down" ];then
	docker-compose -f ../build/docker-compose.yaml down
fi
