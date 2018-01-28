#!/bin/sh

SERVER_CLASSPATH=$JVM_CLASSPATH:$SERVER_HOME/lib/*
if [ "x$JVM_CLASSPATH" = "x" ]; then
	SERVER_CLASSPATH=$SERVER_HOME/lib/*
fi

if [ "x$JVM_MIN_MEM" = "x" ]; then
    DR_MIN_MEM=256m
fi
if [ "x$JVM_MAX_MEM" = "x" ]; then
    DR_MAX_MEM=1g
fi
if [ "x$JVM_HEAP_SIZE" != "x" ]; then
    DR_MIN_MEM=$JVM_HEAP_SIZE
    DR_MAX_MEM=$JVM_HEAP_SIZE
fi

# min and max heap sizes should be set to the same value to avoid
# stop-the-world GC pauses during resize, and so that we can lock the
# heap in memory on startup to prevent any of it from being swapped
# out.
JAVA_OPTS="$JAVA_OPTS -Xms${DR_MIN_MEM}"
JAVA_OPTS="$JAVA_OPTS -Xmx${DR_MAX_MEM}"

# new generation
if [ "x$JVM_HEAP_NEWSIZE" != "x" ]; then
    JAVA_OPTS="$JAVA_OPTS -Xmn${DR_HEAP_NEWSIZE}"
fi

# max direct memory
if [ "x$JVM_DIRECT_SIZE" != "x" ]; then
    JAVA_OPTS="$JAVA_OPTS -XX:MaxDirectMemorySize=${DR_DIRECT_SIZE}"
fi

# reduce the per-thread stack size
JAVA_OPTS="$JAVA_OPTS -Xss256k"

# set to headless, just in case
JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true"

# Force the JVM to use IPv4 stack
if [ "x$JVM_USE_IPV4" != "x" ]; then
  JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
fi

JAVA_OPTS="$JAVA_OPTS -XX:+UseParNewGC"
JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC"

JAVA_OPTS="$JAVA_OPTS -XX:CMSInitiatingOccupancyFraction=75"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSInitiatingOccupancyOnly"

# GC logging options
if [ "x$JVM_USE_GC_LOGGING" != "x" ]; then
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCTimeStamps"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintClassHistogram"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintTenuringDistribution"
  JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCApplicationStoppedTime"
  JAVA_OPTS="$JAVA_OPTS -Xloggc:/var/log/qdp/gc.log"
fi

# Causes the JVM to dump its heap on OutOfMemory.
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"
# The path to the heap dump location, note directory must exists and have enough
# space for a full heap dump.
#JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=$SERVER_HOME/logs/heapdump.hprof"