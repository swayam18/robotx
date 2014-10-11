find . -name "*.java" > sources_list.txt
javac -cp ".:robotx/lib/*" @sources_list.txt
rm sources_list.txt
