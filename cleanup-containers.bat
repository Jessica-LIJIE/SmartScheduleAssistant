@echo off
echo ====================================
echo 清理Docker容器
echo ====================================
echo.

echo 停止所有相关容器...
docker stop ssa-mysql ssa-backend ssa-frontend 2>nul

echo.
echo 删除所有相关容器...
docker rm ssa-mysql ssa-backend ssa-frontend 2>nul

echo.
echo 清理完成！
echo.
pause


