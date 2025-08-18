# PowerShell script to update package names and file paths from telusko to devansh
$basePath = "C:\Users\Devansh\Desktop\MicroserviceTutorials-main\MicroserviceTutorials-main"

# Function to create directory if it doesn't exist
function Ensure-DirectoryExists($path) {
    if (-not (Test-Path -Path $path)) {
        New-Item -ItemType Directory -Path $path -Force | Out-Null
    }
}

# Process each Java file in the project
Get-ChildItem -Path $basePath -Recurse -Filter "*.java" | ForEach-Object {
    $file = $_.FullName
    
    # Skip files that are already in the correct location
    if ($file -match "devansh") {
        return
    }
    
    # Read the file content
    $content = Get-Content -Path $file -Raw
    
    # Update package and import statements
    $newContent = $content -replace 'com\.telusko\.', 'com.devansh.'
    
    # Only proceed if changes were made
    if ($content -ne $newContent) {
        # Determine the new file path
        $newPath = $file -replace 'telusko', 'devansh'
        
        # Create the target directory if it doesn't exist
        $targetDir = [System.IO.Path]::GetDirectoryName($newPath)
        Ensure-DirectoryExists -Path $targetDir
        
        # Save the updated content to the new location
        $newContent | Set-Content -Path $newPath -Force
        
        Write-Host "Updated: $file -> $newPath"
    }
}

# Remove old telusko directories after moving all files
Get-ChildItem -Path $basePath -Recurse -Directory -Filter "telusko" | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue

Write-Host "Package update complete!"
