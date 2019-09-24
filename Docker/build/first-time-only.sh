#!/bin/bash

# Exit if an error occurs
#
set -e

if test -f "/run-once/done"
then
    # Ok, it worked, so we can exit.
    exit 0
fi

for f in $(ls /run-once.d | sort)
do
    g="/run-once.d/${f}"
    echo "executing ${g}"
    if [ ${g: -3} == ".sh" ]
    then
        bash $g
    elif [ ${g: -4} == ".sql" ]
    then
        cat $g | mysql -h${MYSQL_HOST} -u${MYSQL_USER} -p${MYSQL_PASSWORD} -D${MYSQL_DATABASE}
    elif [ ${g: -7} == ".sql.gz" ]
    then
        zcat $g | mysql -h${MYSQL_HOST} -u${MYSQL_USER} -p${MYSQL_PASSWORD} -D${MYSQL_DATABASE}
    fi
done

touch "/run-once/done"
