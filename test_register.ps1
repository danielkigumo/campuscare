$body = '{"fullName":"Test User3","email":"test3@test.com","password":"password123","admissionNo":"123458"}'
$response = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/register' -Method POST -Headers @{'Content-Type'='application/json'} -Body $body
$response.StatusCode
$response.Content