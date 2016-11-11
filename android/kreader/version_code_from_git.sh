#!/bin/bash

if [ ! -n "$1" ]; then
    VERSION=`git rev-list --count --all .`
    echo $VERSION
else
    if [ -d "$1" ]; then
        cd $1 > /dev/null
        VERSION=`git rev-list --count --all .`
    else
        if [ -f "$1" ]; then
            VERSION=`git rev-list --count --all $1`
        fi
    fi


    if [ -d "$1" ]; then
        cd - > /dev/null
    fi

    echo $VERSION
fi
