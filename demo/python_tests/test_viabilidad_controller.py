import subprocess
import requests
import time

def test_viabilidad_controller():
    # 1. Iniciar servidor Spring Boot
    proceso = subprocess.Popen(
        ["cmd", "/c", "mvn spring-boot:run"],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE
    )

    print("Iniciando servidor Spring Boot (puerto 8080)...")
    time.sleep(25)  # darle tiempo a arrancar

    try:
        # 2. Probar GET /viabilidad
        r_base = requests.get("http://localhost:8080/viabilidad")
        assert r_base.status_code == 200, "/viabilidad no respondió correctamente"

        # 3. Probar GET /viabilidad?archivo=test
        r_busqueda = requests.get("http://localhost:8080/viabilidad?archivo=test")
        assert r_busqueda.status_code == 200, "La búsqueda en /viabilidad falló"

        # 4. Probar POST /viabilidad/desactivar/{id}
        # Aceptamos múltiples códigos válidos ya que el ID puede no existir.
        r_desactivar = requests.post("http://localhost:8080/viabilidad/desactivar/1")

        assert r_desactivar.status_code in [200, 302, 400, 404], \
            f"/viabilidad/desactivar/1 devolvió un código inesperado: {r_desactivar.status_code}"

        print("\n✅ ViabilidadController: Endpoints GET y POST respondieron correctamente.\n")

    except Exception as e:
        assert False, f"Error en pruebas de ViabilidadController: {e}"

    finally:
        print("Deteniendo servidor Spring Boot...")
        proceso.terminate()
        proceso.wait()
