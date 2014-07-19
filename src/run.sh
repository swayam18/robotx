find . -name "*.java" > sources_list.txt
javac -cp ".:robotx/lib/*" @sources_list.txt
java -cp ".:robotx/lib/*" robotx/boat/BoatClient
rm sources_list.txt
