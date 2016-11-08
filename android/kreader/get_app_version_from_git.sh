cd $1 > /dev/null
VERSION=`git rev-list --count --all .`
cd - > /dev/null
echo $VERSION
