if not exist -d bin\
(md bin) 
(javac -d bin/ src/*.java
java -classpath ./bin Tester
pause)