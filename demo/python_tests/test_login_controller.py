import subprocess
import requests
import time

def test_login_controller():
    # Paso 1: Iniciar servidor Spring Boot
    proceso = subprocess.Popen(
        ["cmd", "/c", "mvn spring-boot:run"],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE
    )

    print("🚀 Iniciando servidor Spring Boot (puerto 8080)...")
    time.sleep(25)  # Dar tiempo para que arranque correctamente

    try:
        # Paso 2: Probar endpoint /login
        r_login = requests.get("http://localhost:8080/login")
        assert r_login.status_code == 200, "❌ El endpoint /login no respondió correctamente"

        # Paso 3: Probar endpoint /bienvenido
        r_bienvenido = requests.get("http://localhost:8080/bienvenido")
        assert r_bienvenido.status_code == 200, "❌ El endpoint /bienvenido no respondió correctamente"

        print("\n✅ LoginController: Todos los endpoints respondieron correctamente.\n")

    except Exception as e:
        assert False, f"❌ Error en pruebas de LoginController: {e}"

    finally:
        print("🛑 Deteniendo servidor Spring Boot...")
        proceso.terminate()
        proceso.wait()