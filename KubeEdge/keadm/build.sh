#!/bin/bash
VERSION="1.6.1"
docker build \
    --build-arg VERSION=${VERSION} \
    --tag keadm-client:${VERSION} .
