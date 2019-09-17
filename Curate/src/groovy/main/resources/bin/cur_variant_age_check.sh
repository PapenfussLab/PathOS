#!/bin/bash

### cur_variant_age_check.sh
#
#   Options:
#       Make some options and tell people about 'em here
#
#   Requirements:
#       mysql/mariadb
#
#   Options:
#     -u mysql username
#     -p mysql password
#     -d mysql database, (dblive by default)
#     -m Months that curVariants stay fresh for (24 by default)
#     -i Initialise - copies last_modified to last_authorised
#
#   23-October-2017 by DKGM (David Ma) for PATHOS-2413
#   Updated 8-Dec-2017 by DKGM to use last reported date for initialisation.

echo "Bash version ${BASH_VERSION}"
echo "Welcome to the PathOS CurVariant aging script"

#
#   Get options
#
while getopts u:p:m:i opt                    # Add additional options here
do case "$opt" in
    u)      USERNAME="$OPTARG";;
    p)      PASSWORD="$OPTARG";;
    d)      DATABASE="$OPTARG";;
    m)      MONTHS="$OPTARG";;
    i)      INIT="true";;
    esac
done
shift `expr $OPTIND - 1`

MONTHS=${MONTHS:-24}
echo "This script will deauthorise anything older than $MONTHS months."

#if [ "${USERNAME:+x}" != 'x' ]
#then
#    echo "Please enter your mysql username"
#    read USERNAME
#fi

#if [ "${PASSWORD:+x}" != 'x' ]
#then
#    echo "Please enter your mysql password"
#    read $PASSWORD
#fi

DATABASE=${DATABASE:-dblive}
echo "Databse is $DATABASE"

if [ "$PASSWORD" != '' ]
then
    PASSWORDPIECE="--password=$PASSWORD"
fi

if [ "$USERNAME" != '' ]
then
    USERNAMEPIECE="--user=$USERNAME"
fi

BASE_COMMAND="mysql -vv $USERNAMEPIECE $PASSWORDPIECE -N $DATABASE -e "

# One off script to set last_authorised date
if [ "$INIT" != '' ]
then
    echo "Running once-off script to set last reported date as the last_authorised date"

    # Everything starts as unauthorised
    $BASE_COMMAND "UPDATE cur_variant set authorised_flag = 0;"

    # Look up the most recent Report for each CurVariant, and enter that date into it's last_authorised field
    $BASE_COMMAND "UPDATE cur_variant, (select cur_variant.id, v.date_created from cur_variant, (select * from (select hgvsg, max(date_created) as date_created from (select seq_sample_id, max(date_created) as date_created from seq_sample_report group by seq_sample_id) as ss, seq_variant as sv where sv.reportable = 1 and sv.seq_sample_id = ss.seq_sample_id) as x group by hgvsg) as v where cur_variant.hgvsg = v.hgvsg) as last_reported SET cur_variant.last_authorised = last_reported.date_created where cur_variant.id = last_reported.id and cur_variant.last_authorised is null;"

    # If there is no usage, use the last updated field
    $BASE_COMMAND "UPDATE cur_variant SET last_authorised = last_updated where authorised_id is not null and last_authorised is null;"

    # Set things that have been authorised in the last 24 months as authorised
    $BASE_COMMAND "UPDATE cur_variant SET authorised_flag = 1 WHERE TIMESTAMPDIFF(month, last_authorised, now()) < $MONTHS;"

    # If there is no Authorisor, set it as unauthorised
    $BASE_COMMAND "UPDATE cur_variant SET authorised_flag = 0, last_authorised = null WHERE authorised_id is null;"

    # If the date of last authorisation is before the creation date, set it as unauthorised
    $BASE_COMMAND "UPDATE cur_variant SET authorised_flag = 0, last_authorised = null, authorised_id = null WHERE date_created > last_authorised;"
fi

# If the last_authorised date is older than $MONTHS, set it as unauthorised
$BASE_COMMAND "UPDATE cur_variant SET authorised_flag = 0 WHERE TIMESTAMPDIFF(month, last_authorised, now()) > $MONTHS;"









