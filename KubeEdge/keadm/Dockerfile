FROM ubuntu:18.04
SHELL ["/bin/bash", "-c"]

RUN apt-get update \
    && apt-get install -y apt-transport-https curl wget gnupg2 sudo \
    && apt-get clean

# install kubectl

WORKDIR /eco

RUN curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add - \
    && echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | tee -a /etc/apt/sources.list.d/kubernetes.list \
    && apt-get update \
    && apt-get install -y kubectl

# RUN mkdir config
# ENV KUBECONFIG=/eco/config/admin.conf

# install keadm https://github.com/kubeedge/kubeedge/releases
ARG VERSION=1.6.1
RUN wget https://github.com/kubeedge/kubeedge/releases/download/v${VERSION}/keadm-v${VERSION}-linux-arm64.tar.gz \
    && tar -xf keadm-v${VERSION}-linux-arm64.tar.gz \
    && rm keadm-v${VERSION}-linux-arm64.tar.gz \
    && cd keadm-v${VERSION}-linux-arm64/keadm \
    && chmod +x keadm \
    && mv keadm /usr/local/bin \
    && echo keadm version

# Install gcloud skd https://cloud.google.com/sdk/docs/quickstart-debian-ubuntu
RUN echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] http://packages.cloud.google.com/apt cloud-sdk main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list \
    && curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key --keyring /usr/share/keyrings/cloud.google.gpg  add - \
    && apt-get update -y \
    && apt-get install google-cloud-sdk -y

ENTRYPOINT []