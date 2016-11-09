#!/bin/bash

if [ ! -n "$1" ]; then
    HASH=`git log -1 --pretty=format:%h .`
    echo $HASH
else
    if [ -d "$1" ]; then
        cd $1 > /dev/null
        HASH=`git log -1 --pretty=format:%h .`
    else
        if [ -f "$1" ]; then
            HASH=`git log -1 --pretty=format:%h $1`
        fi
    fi


    if [ -d "$1" ]; then
        cd - > /dev/null
    fi

    echo $HASH
fi
