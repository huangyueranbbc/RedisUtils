set modules=RedisUtils

rem 读取版本号
set /P version=<version.txt

set dversion=%date:~0,4%%date:~5,2%%date:~8,2%

echo %version%

@rem 删除版本号文件
if exist src\main\resources\ver_*.txt del /s /q src\main\resources\ver_*.txt

@rem 写入版本号文件
echo version: %version%  datetime: %date% %time% >  src\main\resources\ver_%dversion%.txt

@rem maven 打包
call mvn clean package -Dmaven.test.skip=true

@rem 特殊处理upload版本号文件
copy RedisUtils\src\main\resources\ver_*.txt target\

rem 清空build
rd /s /q build
md build

if exist target\RedisUtils.jar (
md build\RedisUtils
copy target\RedisUtils.jar build\RedisUtils\
copy bin\RedisUtils.sh build\RedisUtils\
copy release-notes.txt build\RedisUtils\
copy readme.md build\RedisUtils\
7z.exe a -tzip build\RedisUtils-v%version%-%svnversion%.zip %baseDir%build\RedisUtils\*
)