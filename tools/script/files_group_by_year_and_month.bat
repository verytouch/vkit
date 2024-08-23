@echo off
setlocal enabledelayedexpansion

:: 获取脚本所在目录
set "scriptDir=%~dp0"
set "scriptName=%~nx0"

:: 设置源目录和目标目录
set "sourceDir=%scriptDir%"
set "targetBaseDir=%scriptDir%groupByYearAndMonth\"


:: 创建目标目录
if not exist "%targetBaseDir%" mkdir "%targetBaseDir%"

:: 输出源目录和目标目录信息
echo Source directory: %sourceDir%
echo Target base directory: %targetBaseDir%


:: 统计源目录下的文件总数
for /f "tokens=* delims=" %%f in ('dir /a-d /b /s "%sourceDir%"') do (
    set /a fileCount+=1
)
set /a fileCount-=1
echo Total files to process: !fileCount!


:: 获取源目录下所有的文件
for /r "%sourceDir%" %%f in (*) do (
    set "file=%%f"
    set "fileYearMonth=%%~tf"
    set "fileYear=!fileYearMonth:~0,4!"
    set "fileMonth=!fileYearMonth:~5,2!"
	
	if not "%%~nxf" == "%scriptName%" (
		:: 构建目标文件夹路径
		set "targetDir=%targetBaseDir%!fileYear!\!fileMonth!"
		if not exist "!targetDir!" mkdir "!targetDir!"

		:: 移动文件到目标文件夹
		echo Copy file: %%f to !targetDir!
		copy "%%f" "!targetDir!"
	)
    
)

:: 输出完成信息
echo Script completed successfully.

endlocal
pause
