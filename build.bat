@echo off
set modules=RedisUtils

@rem maven 打包
call mvn clean package -Dmaven.test.skip=true

rem 清空build
rd /s /q build
mkdir build

rem 读取版本号
set /P version=<version.txt

echo version: %version%  >> build\version.txt

copy target\RedisUtils.jar build\
