# copy_content {path}
echo "Bash copy terminal..."
cd "$1"
pwd
echo "backing up..."
rm -rf "$1/tmp/bck/_Content"
mkdir -p "$1/tmp/bck/_Content"
mv "$1/Content" "$1/tmp/bck/_Content"
echo "copying..."
cp -rf "$1/tmp/dd-webcontent/" "$1/Content/"
rm -rf "$1/Content/.git"
rm -rf "$1/Content/.svn"
echo "removing backup..."
rm -rf "$1/tmp/bck/_Content"