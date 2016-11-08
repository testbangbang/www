cd $1 > /dev/null
VERSION=`git log -1 --pretty=format:%h .`
cd - > /dev/null
echo $VERSION
