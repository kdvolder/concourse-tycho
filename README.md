concourse-tycho-test example
============================

This is an example using a concourse-ci to build and run junit-plugin tests
using maven-tycho.

To use this sample you will need to have concourse setup somewhere. 

I have access to a concourse at ci.spring.io and have tested this
there and confirm it works.

The trickiest part of this is probably that we need to somehow
provide an environment for the test that allows opening
windows. Most CI environments do not provide this and assume
you are running everything in 'headless' mode, i.e. without
access to a display.

The solution is to use 'xvfb' to provide a 'fake' windowing
environment allowing the tests to run.

Preparation
===========

Make sure you have the following:

 - a concourse-ci installation. I am using ci.spring.io. There is also 
   a way to run concourse in a vagrant vm on your own machine.  
 - A docker-hub login (we will build a docker-image to use as our
   build/test environment. You need a place to push that docker-image to)


Contents:
=========

testproject: An eclipse-plugin-test project defined via maven tycho. 
   This contains one test that we want to run.

concourse: This subdirectory contains all files that define our
   simple concourse pipeline.

Setup the pipeline:
===================

The `concourse\set-pipeline.sh` script will create the pipeline.
Before running the script make sure to inspect it and:
 
1. change some of the details as may be required for your own
    instance of concourse.
2. create the credentials `.sts4-concourse-credentials` file in your 
   home directory. Place information in as shown below:

```
docker_hub_email: kdevolder@pivotal.io
docker_hub_username: kdvolder
docker_hub_password: SECRET
```
    
If that is all setup properly, run the script:

```
cd concourse
./set-pipeline.sh

The pipeline
============

The pipeline consists of two jobs. One job builds the docker image
to be used as the build/test-env for the second job. The two 
jobs run independently.


   