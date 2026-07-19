$outputFile = Join-Path $PSScriptRoot "AlleJavaBestanden.txt"

if (Test-Path $outputFile) {
    Remove-Item $outputFile -Force
}

$javaFiles = Get-ChildItem -Path $PSScriptRoot -Filter "*.java" -File -Recurse

foreach ($file in $javaFiles) {

    @"

================================================================================
Bestand: $($file.FullName)
================================================================================

"@ | Out-File -FilePath $outputFile -Append -Encoding utf8

    Get-Content -Path $file.FullName -Raw |
        Out-File -FilePath $outputFile -Append -Encoding utf8


    "`r`n" | Out-File -FilePath $outputFile -Append -Encoding utf8
}