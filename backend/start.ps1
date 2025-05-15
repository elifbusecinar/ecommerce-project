# .env dosyasını oku ve environment variable olarak ayarla
Get-Content .env | ForEach-Object {
    if ($_ -match '^(.*?)=(.*)$') {
        [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
    }
}
# Spring Boot'u başlat
mvn spring-boot:run