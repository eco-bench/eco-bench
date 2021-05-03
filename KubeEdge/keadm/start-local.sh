#!/bin/bash
# build the image if not existing
if [[ "$(docker images -q keadm-client:1.6.1 2> /dev/null)" == "" ]]
then
  sh ./build.sh
fi

echo "path ${PWD}"
# start a container with an interactive session
docker run -it \
  -v ~/.config/gcloud:/root/.config/gcloud \
  -v "$(pwd)"/admin.conf:/root/.kube/config \
  keadm-client:1.6.1 \
  bash