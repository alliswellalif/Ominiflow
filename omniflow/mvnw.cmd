@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.1"
if exist "%~dp0..\apache-maven-3.9.16\bin\mvn.cmd" (
    call "%~dp0..\apache-maven-3.9.16\bin\mvn.cmd" %*
) else (
    call mvn %*
)
