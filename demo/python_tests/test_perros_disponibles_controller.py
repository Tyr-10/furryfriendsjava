import subprocess
import requests
import time

def test_perros_disponibles_controller():
    # 1. Iniciar servidor Spring Boot
    proceso = subprocess.Popen(
        ["cmd", "/c", "mvn spring-boot:run"],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE
    )

    print("Iniciando servidor Spring Boot (puerto 8080)...")
    time.sleep(25)  # esperar que arranque

    try:
        # 2. Probar GET /perrosdisponibles
        r_base = requests.get("http://localhost:8080/perrosdisponibles")
        assert r_base.status_code == 200, "/perrosdisponibles no respondió correctamente"

        # 3. Probar GET /perrosdisponibles con filtros (pueden no retornar nada, pero deben responder)
        params = {
            "edad": 3,
            "color": "negro",
            "tamanio": "mediano",
            "descripcion": "jugueton"
        }
        r_filtros = requests.get("http://localhost:8080/perrosdisponibles", params=params)
        assert r_filtros.status_code == 200, "/perrosdisponibles con filtros no respondió correctamente"

        # 4. Probar GET /imagen/{id}
        # Como no sabemos si existe un perro o imagen con ID=1,
        # aceptamos 200 (si existe) o 404 (si no existe).
        r_imagen = requests.get("http://localhost:8080/imagen/1")

        assert r_imagen.status_code in [200, 404], \
            f"/imagen/1 devolvió un código inesperado: {r_imagen.status_code}"

        print("\n✅ PerrosDisponiblesController: Endpoints GET respondieron correctamente.\n")

    except Exception as e:
        assert False, f"Error en pruebas de PerrosDisponiblesController: {e}"

    finally:
        print("Deteniendo servidor Spring Boot...")
        proceso.terminate()
        proceso.wait()
