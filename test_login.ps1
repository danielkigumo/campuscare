$body = '{"email":"student@campus.example","password":"password123"}'
$response = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/login' -Method POST -Headers @{'Content-Type'='application/json'} -Body $body
$response.StatusCode
$response.Content