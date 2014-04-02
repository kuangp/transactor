export CLASSPATH=./classes:$CLASSPATH 
DIST=./classes
VERSION=1.1.5

echo "SALSA Build Script v0.2.2"
echo "Please make sure the current directory is in your CLASSPATH"
echo ""

if [ -d $DIST ]; then
		rm -rf $DIST
fi
echo "Making dir: "$DIST
mkdir $DIST
echo ""

echo "Compiling SALSA Compiler Code"
javac -Xlint:none -d $DIST `find salsac/ | grep "java$"`
echo ""

echo "Compiling Language Package"
echo "java source..."
javac -Xlint:none -d $DIST `find salsa/ | grep "java$"`
echo ""

echo "Compling WWC Package"
echo "java source..."
javac -Xlint:none -d $DIST `find wwc/ | grep "java$"`
echo ""

echo "Compiling GC Package"
echo "java source..."
javac -Xlint:none -d $DIST `find gc/ | grep "java$"`
echo ""

echo "Compiling tests"
echo "salsa source..."
java -Dsilent salsac.SalsaCompiler `find gctest/ | grep "salsa$"`
echo "java source..."
javac -Xlint:none -d $DIST `find gctest/ | grep "java$"`
echo "salsa source..."
java -Dsilent salsac.SalsaCompiler `find tests/ | grep "salsa$"`
echo "java source..."
javac -Xlint:none -d $DIST `find tests/ | grep "java$"`
echo ""

echo "Compiling Examples"
echo "salsa source..."
java -Dsilent salsac.SalsaCompiler `find examples/ | grep "salsa$"`
echo "java source..."
javac -Xlint:none -d $DIST `find examples/ | grep "java$"`
echo ""

echo "Generating jar file..."
cd $DIST
jar cf ../salsa$VERSION.jar `find wwc` `find salsa` `find salsac` `find gc`
cd ..

echo ""
echo "Finished!"

