#!/usr/bin/env bash
# Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
#
# WSO2 Inc. licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file except
# in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.

# WSO2 Display Agent server copy script.
#
# Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
#
# To use, simply run as 'copy_kernel {base_path}'!

echo "=======WSO2 Display Agent - Update Script========"

BASE_FOLDER="$1"
TEMP_FOLDER="$BASE_FOLDER/tmp"
BACKUP_FOLDER="$TEMP_FOLDER/bck"
REPO_FOLDER="$TEMP_FOLDER/$2"

echo "---Switching to $BASE_FOLDER..."
cd "$BASE_FOLDER"

echo "---Backing up..."
rm -rf "$BACKUP_FOLDER"
mkdir -p "$BACKUP_FOLDER"
shopt -s dotglob
mv * "$BACKUP_FOLDER/" > /dev/null 2>&1

echo "---Copying new files..."
echo "$REPO_FOLDER/"
echo "$BASE_FOLDER/"
cp -rf "$REPO_FOLDER/" "$BASE_FOLDER/"

echo "---Removing VCS Information..."
rm -rf "$BASE_FOLDER/.git"
rm -rf "$BASE_FOLDER/.svn"

echo "---Killing wso2server.py"
ps aux | grep 'wso2server.py' | awk '{print $2}' | xargs kill -9

echo "---Killing httpserver.py"
ps aux | grep 'httpserver.py' | awk '{print $2}' | xargs kill -9

echo "---Running server..."
cd "$BASE_FOLDER/"
python wso2server.py &

if [ "$?" -eq "1" ]; then
    echo "Error running Kernel, rolling to prev..."

    echo "---Killing httpserver.py"
    ps aux | grep 'httpserver.py' | awk '{print $2}' | xargs kill -9

    echo "---Killing wso2server.py"
	ps aux | grep 'wso2server.py' | awk '{print $2}' | xargs kill -9

	cd "$1"
	echo "---Replacing Files..."
	rm -rf "$1/*.*"
	mv "$BACKUP_FOLDER/" "$BASE_FOLDER/"

	echo "---Running server..."
	cd "$BASE_FOLDER/"
	python displayagent.py &
else
	echo "$? returned..."
fi