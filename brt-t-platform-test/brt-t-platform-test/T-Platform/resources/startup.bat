@echo off
title T-Platform
echo T-Platform Startup...

for /f "tokens=5" %%p in (' netstat -ano ^| findstr 8082 ') do taskkill /f /PID %%p

timeout 3

cmd /c start T-Platform -Xmx512m -jar T-Launcher-1.0.0.jar

exit