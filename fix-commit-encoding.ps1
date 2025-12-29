# 修正 Git 提交信息的编码问题
# 使用 UTF-8 编码重新提交

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

# 设置 Git 编码配置
git config --global i18n.commitencoding utf-8
git config --global i18n.logoutputencoding utf-8
git config --global core.quotepath false

Write-Host "Git 编码配置已更新为 UTF-8" -ForegroundColor Green

# 修正最近的合并提交
Write-Host "`n修正最近的提交信息..." -ForegroundColor Yellow
git commit --amend -m "Merge remote-tracking branch 'origin/main' - 解决README.md冲突"

Write-Host "`n提交信息已修正！" -ForegroundColor Green
Write-Host "`n注意：由于修改了已推送的提交，需要使用以下命令强制推送：" -ForegroundColor Yellow
Write-Host "git push --force-with-lease origin main" -ForegroundColor Cyan

