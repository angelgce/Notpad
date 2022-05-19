@ECHO OFF
set CP="C:\Users\abricot\Documents\Notpad\models"
set LIB="C:\Users\abricot\Documents\Notpad\libraries"
set INI="C:\Users\abricot\Documents\Notpad\



start cmd /k "javac -cp %lib%\ini4j-0.5.4.jar;%lib%\commons-cli-1.4.jar  %CP%\*.java"
start cmd /k "java -cp %CP%;%LIB%/ini4j-0.5.4.jar;%LIB%/commons-cli-1.4.jar Engine -c %INI%/config.ini"

