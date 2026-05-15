#requires -RunAsAdministrator

Write-Host "MySQL Initialization - Admin Mode" -ForegroundColor Cyan
Write-Host ""

$mysqlBinPath = "C:\Program Files\MySQL\MySQL Server 8.4\bin"
$mysqlDataPath = "C:\Program Files\MySQL\MySQL Server 8.4\data"

# Check if data directory exists
if (Test-Path $mysqlDataPath) {
    Write-Host "Data directory exists. Skipping initialization." -ForegroundColor Yellow
} else {
    Write-Host "Initializing MySQL data directory..." -ForegroundColor Cyan
    Set-Location $mysqlBinPath
    & "$mysqlBinPath\mysqld.exe" --initialize-insecure --console
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Data directory initialized successfully." -ForegroundColor Green
    } else {
        Write-Host "Initialization failed. Check error messages above." -ForegroundColor Red
        pause
        exit 1
    }
}

# Start MySQL service
Write-Host "Starting MySQL service..." -ForegroundColor Cyan
Start-Service MySQL
Start-Sleep -Seconds 3

$service = Get-Service MySQL
if ($service.Status -eq 'Running') {
    Write-Host "MySQL service started successfully!" -ForegroundColor Green
} else {
    Write-Host "Failed to start MySQL service." -ForegroundColor Red
}

pause
