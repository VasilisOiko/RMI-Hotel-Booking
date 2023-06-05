FROM ubuntu

RUN apt-get update && \
    apt-get install -yq tzdata
    
ENV TZ="Europe/Athens"


RUN apt-get update && apt-get install -y \
        build-essential  \
        sudo \
        libssl-dev  \
        gdb \
        default-jdk \
    && apt-get -y upgrade \
    && apt-get update \
    && apt-get clean