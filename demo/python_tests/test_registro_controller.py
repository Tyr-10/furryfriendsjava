import subprocess
import requests
import time

def test_registro_controller():
    # Paso 1: Iniciar el servidor Spring Boot
    proceso = subprocess.Popen(
        ["cmd", "/c", "mvn spring-boot:run"],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE
    )

    print("🚀 Iniciando servidor Spring Boot en puerto 8080...")
    time.sleep(25)  # Esperar a que arranque

    try:
        # Paso 2: Probar endpoint principal /registro
        r_principal = requests.get("http://localhost:8080/registro")
        assert r_principal.status_code == 200, "El endpoint /registro no respondió correctamente"

        # Paso 3: Probar formulario adoptante
        r_adoptante_get = requests.get("http://localhost:8080/registro/adoptante")
        assert r_adoptante_get.status_code == 200, "El endpoint /registro/adoptante no respondió correctamente"

        # Paso 4: Probar formulario natural
        r_natural_get = requests.get("http://localhost:8080/registro/natural")
        assert r_natural_get.status_code == 200, "El endpoint /registro/natural no respondió correctamente"

        # Paso 5: Probar formulario refugio
        r_refugio_get = requests.get("http://localhost:8080/registro/refugio")
        assert r_refugio_get.status_code == 200, "El endpoint /registro/refugio no respondió correctamente"

        print("\n✅ Todos los endpoints GET de RegistroController respondieron correctamente.\n")

    except Exception as e:
        assert False, f"Error en las pruebas del RegistroController: {e}"

    finally:
        print("Deteniendo servidor Spring Boot...")
        proceso.terminate()
        proceso.wait()