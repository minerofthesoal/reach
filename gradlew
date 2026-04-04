#!/bin/sh

#
# Gradle wrapper script for Linux/macOS
#

# Determine the Java command to use
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Resolve project directory
APP_HOME=$( cd "${APP_HOME:-./}" > /dev/null && pwd -P ) || exit

# Setup classpath
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Launch Gradle
exec "$JAVACMD" \
    $JAVA_OPTS \
    $GRADLE_OPTS \
    "-Dorg.gradle.appname=$APP_BASE_NAME" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
