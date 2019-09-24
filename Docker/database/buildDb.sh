#!/bin/bash

#	Remove any previous DB
#
rm -rfv pathos-db-data

#	Initialise DB with run once SQL
#
docker-compose up  -d

#	Obfuscate names
#
#gunzip -c training.yaml.gz | python mangle.py | gzip > load_dir/training_obfus.yaml.gz

#	Load runs/samples and variants
#
docker-compose run -v $PWD/load_dir/:/pathos-loader-input.d loader

#	Dump entire DB for export to cloud
#
docker-compose exec -T pathos_db mysqldump -upathos -ppathos dblive | gzip > dump.sql.gz
