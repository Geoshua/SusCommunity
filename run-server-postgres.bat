@echo off
set DATABASE_URL=jdbc:postgresql://localhost:5432/suscommunity
set DATABASE_USER=suscommunity_user
set DATABASE_PASSWORD=dev_password_2024
gradlew.bat :server:run
