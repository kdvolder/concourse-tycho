#!/bin/bash
fly -t tools set-pipeline --load-vars-from ${HOME}/.sts4-concourse-credentials.yml -p test -c pipeline.yml
