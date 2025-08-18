# PowerShell script to restore files from backup and update package declarations
$backupPath = "C:\Users\Devansh\Desktop\MicroserviceTutorials-main\MicroserviceTutorials-main-backup"
$targetPath = "C:\Users\Devansh\Desktop\MicroserviceTutorials-main\MicroserviceTutorials-main"

# Function to create directory if it doesn't exist
function Ensure-DirectoryExists($path) {
    if (-not (Test-Path -Path $path)) {
        New-Item -ItemType Directory -Path $path -Force | Out-Null
    }
}

# Function to copy and update Java files
function Copy-And-Update-JavaFiles($sourceDir, $targetDir) {
    Get-ChildItem -Path $sourceDir -Recurse -Filter "*.java" | ForEach-Object {
        $relativePath = $_.FullName.Substring($backupPath.Length)
        $targetFile = Join-Path -Path $targetPath -ChildPath $relativePath
        
        # Update the path to use devansh instead of telusko
        $targetFile = $targetFile -replace 'telusko', 'devansh'
        
        # Create target directory if it doesn't exist
        $targetFileDir = [System.IO.Path]::GetDirectoryName($targetFile)
        Ensure-DirectoryExists -Path $targetFileDir
        
        # Read the content and update package/import statements
        $content = Get-Content -Path $_.FullName -Raw
        $newContent = $content -replace 'com\.telusko\.', 'com.devansh.'
        
        # Write the updated content to the new location
        $newContent | Set-Content -Path $targetFile -Force
        
        Write-Host "Updated: $($_.FullName) -> $targetFile"
    }
}

# Process each module
$modules = @("question-service", "quiz-service", "api-gateway", "service-registry")

foreach ($module in $modules) {
    $sourceModulePath = Join-Path -Path $backupPath -ChildPath $module
    $targetModulePath = Join-Path -Path $targetPath -ChildPath $module
    
    if (Test-Path -Path $sourceModulePath) {
        Write-Host "Processing module: $module"
        Copy-And-Update-JavaFiles -sourceDir $sourceModulePath -targetDir $targetModulePath
    }
}

Write-Host "Restore and update complete!"
