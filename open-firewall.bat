@echo off
title 开放端口 8080 防火墙规则
echo ========================================
echo  正在添加防火墙规则 - 端口 8080
echo ========================================
echo.

netsh advfirewall firewall add rule name="MiniProgram 8080" dir=in action=allow protocol=TCP localport=8080

echo.
echo 防火墙规则添加完成！
echo 现在手机端应该可以连接 http://172.21.37.167:8080
echo.
pause
