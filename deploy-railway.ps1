# ============================================
# Railway Deployment Script
# Run in PowerShell: .\deploy-railway.ps1
# ============================================
$ErrorActionPreference = "Stop"
Write-Host "=== Railway Deployment for ShopOnline ===" -ForegroundColor Cyan

# 1. Check Railway CLI
if (-not (Get-Command railway -ErrorAction SilentlyContinue)) {
    Write-Host "Installing Railway CLI..." -ForegroundColor Yellow
    npm install -g @railway/cli
}

# 2. Login
Write-Host "Logging into Railway (browser will open)..." -ForegroundColor Green
railway login
Write-Host "Login complete." -ForegroundColor Green

# 3. Init project
Write-Host "Initializing project..." -ForegroundColor Green
railway init --name shoponline

# 4. Add MySQL
Write-Host "Adding MySQL database..." -ForegroundColor Green
railway add --plugin mysql

# 5. Set environment variables
Write-Host "Configuring environment variables..." -ForegroundColor Green
railway variables set `
  SPRING_PROFILES_ACTIVE=prod `
  SERVER_PORT=8080 `
  AI_TONGYI_API_KEY=sk-94b1cdb1e6ff45bd912c588678b89609

# 6. Deploy
Write-Host "Deploying to Railway..." -ForegroundColor Green
railway up

Write-Host "=== Deployment complete! ===" -ForegroundColor Cyan
Write-Host "Check your Railway dashboard for the public URL." -ForegroundColor Cyan
Write-Host "https://railway.app/dashboard" -ForegroundColor Yellow
