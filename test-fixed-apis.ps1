# PowerShell Script to Test the Two Fixed API Endpoints
# Run this to verify both endpoints are working correctly

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Testing Fixed API Endpoints" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: GET Salary History for Employee 4
Write-Host "Test 1: GET /api/salaries/employee/4" -ForegroundColor Yellow
Write-Host "Purpose: Fetching salary history" -ForegroundColor Gray
Write-Host ""

try {
    $response1 = Invoke-RestMethod -Uri "http://localhost:8080/api/salaries/employee/4" -Method Get -ErrorAction Stop
    Write-Host "✅ SUCCESS!" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor White
    $response1 | ConvertTo-Json -Depth 10
    Write-Host ""
} catch {
    Write-Host "❌ FAILED!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Test 2: DELETE Employee (Deactivate) - Employee 2
Write-Host "Test 2: DELETE /api/employees/2" -ForegroundColor Yellow
Write-Host "Purpose: Deactivating employee" -ForegroundColor Gray
Write-Host ""

try {
    $response2 = Invoke-RestMethod -Uri "http://localhost:8080/api/employees/2" -Method Delete -ErrorAction Stop
    Write-Host "✅ SUCCESS!" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor White
    $response2 | ConvertTo-Json -Depth 10
    Write-Host ""
} catch {
    Write-Host "❌ FAILED!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verify Employee 2 is deactivated
Write-Host "Test 3: Verify Employee 2 is Deactivated" -ForegroundColor Yellow
Write-Host "Purpose: Check active status changed to false" -ForegroundColor Gray
Write-Host ""

try {
    $response3 = Invoke-RestMethod -Uri "http://localhost:8080/api/employees/2" -Method Get -ErrorAction Stop
    Write-Host "Employee Status:" -ForegroundColor White
    Write-Host "Name: $($response3.data.name)" -ForegroundColor White
    Write-Host "Active: $($response3.data.active)" -ForegroundColor $(if ($response3.data.active -eq $false) { "Green" } else { "Red" })
    
    if ($response3.data.active -eq $false) {
        Write-Host "✅ Employee successfully deactivated!" -ForegroundColor Green
    } else {
        Write-Host "⚠️ Employee is still active!" -ForegroundColor Yellow
    }
    Write-Host ""
} catch {
    Write-Host "❌ FAILED!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "All tests completed!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

