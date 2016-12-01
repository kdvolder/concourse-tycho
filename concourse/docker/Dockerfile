FROM ubuntu:16.04

ADD npmrc /root/.npmrc

RUN apt-get update && apt-get install -y \
  build-essential \
  git \
  openjdk-8-jdk \
  maven \
  curl \
  icewm icewm-themes

CMD /bin/bash  
