@echo off
echo Compiling Portfolio Backend...
echo.

if not exist out mkdir out
javac -cp . src/*.java -d out

if %errorlevel% neq 0 (
    echo Error compiling Java files
    pause
    exit /b 1
)

echo.
echo ✅ Backend compiled successfully!
echo 🌐 Starting server on http://localhost:8080
echo.
echo 📝 INSTRUCTIONS:
echo 1. Keep this window open
echo 2. Open frontend/index.html in your browser
echo 3. Test the contact form
echo.
echo ⏹️  Press Ctrl+C to stop the server
echo.

java -cp out ContactServer
pause