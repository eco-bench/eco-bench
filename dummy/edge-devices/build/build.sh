#!/bin/bash
echo $PAT | docker login ghcr.io --username phanatic --password-stdin

VERSION="1"
docker build \
    --tag ghcr.io/eco-bench/edge-device:${VERSION} . # run the script from the project root

echo [INFO] Pushin to ghcr.io/eco-bench/edge-device:${VERSION}

docker push ghcr.io/eco-bench/edge-device:${VERSION}
