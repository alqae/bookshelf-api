#!/bin/bash

errorMessage="Something went wrong, terminating script."
#Color codes
red='\033[0;31m'
green='\033[0;32m'
noColor='\033[0m'

#Exit on non-zero status
function exitIfNonZeroStatus {
    if [ $? -ne 0 ]
    then
        echo -e "${red}$errorMessage${noColor}"
        exit 1
    fi
}

exitIfNonZeroStatus

echo "Packaging the application"
mvn -v
mvn clean

exitIfNonZeroStatus

mvn package
echo ""

exitIfNonZeroStatus


#echo "Copying YAMLs to dist dir."
#cp ./src/main/resources/*.yml ./target/

exitIfNonZeroStatus

mkdir -p ./dist
echo "Clean up dist dir."
rm -rf ./dist/*

exitIfNonZeroStatus

echo "Copying JAR to dist dir."
cp ./target/*.jar ./dist/app.jar

exitIfNonZeroStatus

#echo "Adding new contents of dist directory to SVN"
#svn add --force ./dist/

# exitIfNonZeroStatus

#echo "Commting dist directory's content"
#svn ci -m "Any message" ./dist

echo "Maven clean"
mvn clean