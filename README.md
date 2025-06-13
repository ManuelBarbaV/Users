# 📦 Proyecto Spring Boot - Autenticación con Feign y dummyjson.com

Este proyecto expone dos endpoints principales:

- `/usuarios/login`: Permite autenticarse contra dummyjson.com. Si el token de acceso está expirado, lo renueva automáticamente con el refresh token. También guarda la información del usuario y su hora de login.
- `/usuarios/all`: Recupera una lista de usuarios desde dummyjson.com usando un cliente Feign.

## ▶️ Instrucciones de Ejecución

1. Clona el repositorio y navega al directorio:

git clone https://github.com/tu-usuario/tu-repo.git
cd tu-repo

2. Configura la conexión a base de datos en el archivo `src/main/resources/application.properties`.


3. Asegúrate de tener la tabla `postgres.users` creada en tu base de datos. Puedes usar la siguiente sentencia SQL:

```sql
CREATE TABLE postgres.users (
  id SERIAL PRIMARY KEY,
  username TEXT,
  login_time TIMESTAMP,
  access_token TEXT,
  refresh_token TEXT
);
```
4. Ejecuta la aplicación con:

./mvnw spring-boot:run

(O bien, desde IntelliJ usando el botón verde "Run") Agregando el RUN en el VM el siguiente comando: -Dnet.bytebuddy.experimental=true 

## 🔐 Usuario y Contraseña de Prueba

{
"username": "emilys",
"password": "emilyspass"
}

## 📡 Ejemplo cURL de Login

curl -X POST http://localhost:8080/usuarios/login \
-H "Content-Type: application/json" \
-d '{
"username": "emilys",
"password": "emilyspass"
}'

Respuesta esperada:

{
"id": 1,
"username": "kminchelle",
"accessToken": "eyJhbGciOi...",
"refreshToken": "eyJhbGciOi...",
"loginTime": "2025-06-13T20:41:32.123+00:00"
}

## 📄 Ejemplo cURL para obtener todos los usuarios

curl -X GET http://localhost:8080/usuarios/all

Respuesta esperada (resumen):

{
"users": [
{
"id": 1,
"firstName": "Emily",
"lastName": "Johnson",
"maidenName": "Smith",
"age": 28,
"gender": "female",
"email": "emily.johnson@x.dummyjson.com",
"phone": "+81 965-431-3024",
"username": "emilys",
"password": "emilyspass",
"birthDate": "1996-5-30",
"image": "https://dummyjson.com/icon/emilys/128",
"bloodGroup": "O-",
"height": 193.24,
"weight": 63.16,
"eyeColor": "Green",
"hair": {
"color": "Brown",
"type": "Curly"
},
"ip": "42.48.100.32",
"address": {
"address": "626 Main Street",
"city": "Phoenix",
"state": "Mississippi",
"stateCode": "MS",
"postalCode": "29112",
"coordinates": {
"lat": -77.16213,
"lng": -92.084824
},
"country": "United States"
},
"macAddress": "47:fa:41:18:ec:eb",
"university": "University of Wisconsin--Madison",
"bank": {
"cardExpire": "03/26",
"cardNumber": "9289760655481815",
"cardType": "Elo",
"currency": "CNY",
"iban": "YPUXISOBI7TTHPK2BR3HAIXL"
},
"company": {
"department": "Engineering",
"name": "Dooley, Kozey and Cronin",
"title": "Sales Manager",
"address": {
"address": "263 Tenth Street",
"city": "San Francisco",
"state": "Wisconsin",
"stateCode": "WI",
"postalCode": "37657",
"coordinates": {
"lat": 71.814525,
"lng": -161.150263
},
"country": "United States"
}
},
"ein": "977-175",
"ssn": "900-590-289",
"userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36",
"crypto": {
"coin": "Bitcoin",
"wallet": "0xb9fc2fe63b2a6c003f1c324c3bfa53259162181a",
"network": "Ethereum (ERC20)"
},
"role": "admin"
}
]
}

## ♻️ Flujo de Autenticación y Registro

- Se hace login con las credenciales proporcionadas.
- El sistema intenta autenticarse con el token recibido.
- Si se recibe un error 401 (token expirado), se realiza una solicitud de refresh con el `refreshToken`.
- El token actualizado es guardado en el `TokenManager`.
- Se guarda en base de datos la información del usuario junto con la hora del login (`loginTime`).
