# copy_kernel {base_path}
echo "Bash copy terminal... $1"
cd "$1"
pwd
echo "backing up..."
rm -rf "$1/tmp/bck/_KernelRunner"
mkdir -p "$1/tmp/bck/"
mv "$1/KernelRunner" "$1/tmp/bck/_KernelRunner"
echo "copying..."
cp -rf "$1/tmp/dd-kernel/" "$1/KernelRunner/"
rm -rf "$1/KernelRunner/.git"
rm -rf "$1/KernelRunner/.svn"
echo "killing wso2server.py"
ps aux | grep 'wso2server.py' | awk '{print $2}' | xargs kill -9
echo "killing httpserver.py"
ps aux | grep 'httpserver.py' | awk '{print $2}' | xargs kill -9
echo "running server..."
cd "$1/KernelRunner/"
python wso2server.py &
if [ "$?" -eq "1" ]; then
    echo "Error running Kernel, rolling to prev..."
    echo "killing httpserver.py"
    ps aux | grep 'httpserver.py' | awk '{print $2}' | xargs kill -9
    echo "killing wso2server.py"
	ps aux | grep 'wso2server.py' | awk '{print $2}' | xargs kill -9
	cd "$1"
	echo "replacing files..."
	rm -rf "$1/KernelRunner"
	mv "$1/tmp/bck/_KernelRunner" "$1/KernelRunner"
	cd "$1/KernelRunner"
	pwd
	echo "starting up..."
	python wso2server.py &
else
	echo "$? returned..."
fi