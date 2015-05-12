# copy_content {path}
echo "Bash copy terminal..."
cd "$1"
pwd
echo "backing up..."
rm -rf "$1/tmp/bck/_Content"
mkdir -p "$1/tmp/bck/"
mv "$1/Content" "$1/tmp/bck/_Content"
echo "copying..."
cp -rf "$1/tmp/dd-webcontent/" "$1/Content/"
rm -rf "$1/Content/.git"
rm -rf "$1/Content/.svn"
ps aux | grep 'wso2server.py' | awk '{print $2}' | xargs kill -9
echo "killing httpserver.py"
ps aux | grep 'httpserver.py' | awk '{print $2}' | xargs kill -9
echo "removing backup..."
rm -rf "$1/tmp/bck/_Content"
cd "$1/KernelRunner/"
python wso2server.py &