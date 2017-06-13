# Running Zeppelin Locally

## Env setup

Important: Twitter's Zeppelin build uses internal libraries for logging and other functionality. In order to build it, setup your Maven to [point to the internal Artifactory first](https://confluence.twitter.biz/display/STREAMCOMPT/Running+Presto+Locally#RunningPrestoLocally-MavenSettings).


## Building & Running

Build with:
```
pmvn clean package -Pbuild-distr -Pscalding -Pscala-2.11
```

Start the daemon locally with:
```
bin/zeppelin-daemon.sh --config conf/ restart
```


## Debugging

Modify your `conf/zeppelin-env.sh` and add the debug agent configuration. Notice Notebooks run on a separate VM, so you'll need to bind a different port to debug them:

```
export ZEPPELIN_JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=6006"

export ZEPPELIN_INTP_JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=7007"
```
