#!/bin/bash

PAT="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"
DEVICE_TYPE="camera"
USERNAME="SeveHo"


echo $PAT | docker login ghcr.io --username phanatic --password-stdin

VERSION="1"
docker build \
    --tag ghcr.io/eco-bench/edge-${DEVICE_TYPE}:${VERSION} . # run the script from the project root

echo [INFO] Pushin to ghcr.io/eco-bench/edge-${DEVICE_TYPE}:${VERSION}

docker push ghcr.io/eco-bench/edge-${DEVICE_TYPE}:${VERSION}