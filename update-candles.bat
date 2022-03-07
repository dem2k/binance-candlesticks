@echo off
set CLASSPATH=%~dp0target\classes;%~dp0config
for %%i in ("%~dp0target\dependency\*.jar") do call :addcp %%i
java dem2k.Main %*
goto ende
:addcp
set CLASSPATH=%1;%CLASSPATH%
:ende
