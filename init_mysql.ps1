# MySQL Initialization Script - Requires Admin
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   MySQL Initialization" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check admin privileges
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "ERROR: Admin privileges required!" -ForegroundColor Red
    Write-Host "Please right-click this script and select 'Run as Administrator'" -ForegroundColor Yellow
    Write-Host ""
    pause
    exit
}

Write-Host "Step 1: Checking MySQL installation..." -ForegroundColor Yellow
$mysqlBinPath = "C:\Program Files\MySQL\MySQL Server 8.4\bin"
$mysqlDataPath = "C:\Program Files\MySQL\MySQL Server 8.4\data"

if (-not (Test-Path $mysqlBinPath)) {
    Write-Host "ERROR: MySQL not found at $mysqlBinPath" -ForegroundColor Red
    pause
    exit
}
Write-Host "OK - MySQL found" -ForegroundColor Green

Write-Host ""
Write-Host "Step 2: Checking data directory..." -ForegroundColor Yellow
if (Test-Path $mysqlDataPath) {
    Write-Host "Data directory already exists" -ForegroundColor Green
    $response = Read-Host "Reinitialize? (y/n)"
    if ($response -eq 'y' -or $response -eq 'Y') {
        Write-Host "WARNING: This will delete all data!" -ForegroundColor Red
        $confirm = Read-Host "Confirm? (yes/no)"
        if ($confirm -eq 'yes') {
            Write-Host "Deleting data directory..." -ForegroundColor Yellow
            Remove-Item -Path $mysqlDataPath -Recurse -Force
            Write-Host "Data directory deleted" -ForegroundColor Green
        } else {
            Write-Host "Cancelled" -ForegroundColor Yellow
            pause
            exit
        }
    } else {
        Write-Host "Skipping initialization, trying to start service..." -ForegroundColor Yellow
    }
} else {
    Write-Host ""
    Write-Host "Step 3: Initializing MySQL data directory..." -ForegroundColor Yellow
    Set-Location $mysqlBinPath
    Write-Host "Running: mysqld --initialize-insecure --user=mysql" -ForegroundColor Cyan
    & "$mysqlBinPath\mysqld.exe" --initialize-insecure --user=mysql 2>&1 | ForEach-Object { Write-Host $_ }
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "FAILED - MySQL initialization failed!" -ForegroundColor Red
        pause
        exit
    }
    Write-Host "OK - Data directory initialized" -ForegroundColor Green
}

Write-Host ""
Write-Host "Step 4: Starting MySQL service..." -ForegroundColor Yellow

# Stop existing service if running
$service = Get-Service MySQL -ErrorAction SilentlyContinue
if ($service -and $service.Status -eq 'Running') {
    Write-Host "Stopping MySQL service..." -ForegroundColor Yellow
    Stop-Service MySQL -Force
    Start-Sleep -Seconds 2
}

# Start service
Write-Host "Starting MySQL service..." -ForegroundColor Yellow
Start-Service MySQL
Start-Sleep -Seconds 3

# Check status
$service = Get-Service MySQL -ErrorAction SilentlyContinue
if ($service -and $service.Status -eq 'Running') {
    Write-Host "OK - MySQL service is running!" -ForegroundColor Green
} else {
    Write-Host "FAILED - MySQL service failed to start!" -ForegroundColor Red
    Get-Service MySQL | Format-List
    pause
    exit
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "   MySQL Ready!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Config:" -ForegroundColor Cyan
Write-Host "- Port: 3306" -ForegroundColor White
Write-Host "- Root user: no password" -ForegroundColor White
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Restart your Spring Boot application" -ForegroundColor White
Write-Host "2. Visit: http://localhost:8080" -ForegroundColor White
Write-Host ""

pause
