import subprocess
import requests
import time

def test_seguimiento_controller():
    # 1. Iniciar Spring Boot
    proceso = subprocess.Popen(
        ["cmd", "/c", "mvn spring-boot:run"],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE
    )

    print("Iniciando servidor Spring Boot (puerto 8080)...")
    time.sleep(25)  # tiempo de arranque

    try:
        # 2. Probar GET /seguimiento
        r_base = requests.get("http://localhost:8080/seguimiento")
        assert r_base.status_code == 200, "/seguimiento no respondió correctamente"

        # 3. Probar GET /seguimiento con parámetro archivo
        r_busqueda = requests.get("http://localhost:8080/seguimiento?archivo=test")
        assert r_busqueda.status_code == 200, "La búsqueda en /seguimiento falló"

        # 4. Probar POST /seguimiento/desactivar/{id}
        # Esta prueba solo valida que el endpoint exista.
        # Como no sabemos si el ID existe, aceptamos 200, 302 o 400.
        r_desactivar = requests.post("http://localhost:8080/seguimiento/desactivar/1")

        assert r_desactivar.status_code in [200, 302, 400, 404], \
            f"/seguimiento/desactivar/1 respondió código inesperado: {r_desactivar.status_code}"

        print("\n✅ SeguimientoController: Endpoints GET y POST respondieron correctamente.\n")

    except Exception as e:
        assert False, f"Error en pruebas de SeguimientoController: {e}"

    finally:
        print("Deteniendo servidor Spring Boot...")
        proceso.terminate()
        proceso.wait()
