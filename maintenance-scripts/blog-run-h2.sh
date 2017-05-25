#!/usr/bin/env bash

JAVA_EXEC=`which java`

if [ -z "${BASE_PATH}" ]
then
#        BASE_PATH="/opt/blog"
        BASE_PATH="/Users/yuriy/Documents/blog"
fi

if [ -z "${BIN_PATH}" ]
then
        BIN_PATH="${BASE_PATH}/bin"
fi

if [ -z "${JAR_NAME}" ]
then
        JAR_NAME="blog.jar"
fi

if [ -z "${DB_PATH}" ]
then
        DB_PATH="${BASE_PATH}/db/blog"
fi

JAR_PATH="${BIN_PATH}/${JAR_NAME}"

LOG_FILE="${BASE_PATH}/log/output.log"

if [ -z "${RUN_PROFILE}" ]
then
        RUN_PROFILE="client-integration"
fi

RUN_OPTIONS="${RUN_OPTIONS} -Dspring.datasource.url=jdbc:h2:${DB_PATH}"
RUN_OPTIONS="${RUN_OPTIONS} -Dspring.datasource.driverClassName=org.h2.Driver"
RUN_OPTIONS="${RUN_OPTIONS} -Dspring.jpa.hibernate.ddl-auto=update"
RUN_OPTIONS="${RUN_OPTIONS} -Dspring.jpa.generate-ddl=true"
RUN_OPTIONS="${RUN_OPTIONS} -Dspring.profiles.active=${RUN_PROFILE}"

RUN_OPTIONS="${RUN_OPTIONS} -Dorg.slf4j.simpleLogger.logFile=${LOG_FILE}"
RUN_OPTIONS="${RUN_OPTIONS} -Dlogging.level.root=INFO"
RUN_OPTIONS="${RUN_OPTIONS} -Dlogging.level.org.hibernate.engine.internal.StatisticalLoggingSessionEventListener=ERROR"
RUN_OPTIONS="${RUN_OPTIONS} -Dme.academeg.blog.images.path=${BASE_PATH}images/"
RUN_OPTIONS="${RUN_OPTIONS} -Dme.academeg.blog.avatars.path=${BASE_PATH}avatar/"

${JAVA_EXEC} ${RUN_OPTIONS} -jar ${JAR_PATH}
