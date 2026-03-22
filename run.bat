@echo off
title Lab3 Factory GUI

echo Компиляция...
javac Lab3FactorySolution.java

if %errorlevel% neq 0 (
echo.
echo ОШИБКА КОМПИЛЯЦИИ!
pause
exit
)

echo Запуск приложения...
start javaw Lab3FactorySolution

exit
