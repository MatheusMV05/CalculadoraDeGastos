@echo off
echo Compilando e executando Dashboard Financeiro...
echo.

REM Verificar se o Maven está instalado
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Maven não encontrado no PATH
    echo Por favor, instale o Maven ou adicione-o ao PATH do sistema
    echo Download: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

REM Limpar e compilar
echo [1/3] Limpando build anterior...
call mvn clean

echo.
echo [2/3] Compilando projeto...
call mvn compile

echo.
echo [3/3] Executando aplicação...
call mvn javafx:run

pause

