import subprocess
import requests
import time

def test_recuperacion_controller():
    # 1. Iniciar servidor Spring Boot
    proceso = subprocess.Popen(
        ["cmd", "/c", "mvn spring-boot:run"],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE
    )

    print("Iniciando servidor Spring Boot (puerto 8080)...")
    time.sleep(25)

    try:
        # 2. GET /recuperar (vista)
        r_get = requests.get("http://localhost:8080/recuperar")
        assert r_get.status_code == 200, "/recuperar no respondió correctamente"

        # --- PLUS EXTRA ---
        # Validar que la vista tenga un formulario válido
        html = r_get.text.lower()
        assert "<form" in html, "La vista /recuperar NO contiene un formulario"
        assert "correo" in html, "El formulario /recuperar NO contiene el campo 'correo'"
        # -------------------

        # 3. POST /recuperar/enviar con correo inexistente
        r_post_fake = requests.post(
            "http://localhost:8080/recuperar/enviar",
            data={"correo": "correo_inexistente@test.com"}
        )

        # Puede devolver 200 (vista recargar) o 302 si haces redirect
        assert r_post_fake.status_code in [200, 302], \
            f"POST recuperar/enviar con correo inexistente devolvió código inesperado: {r_post_fake.status_code}"

        # 4. POST /recuperar/enviar con correo existente
        # El usuario puede o no existir, así que aceptamos 200 o 302 como válidos.
        r_post_real = requests.post(
            "http://localhost:8080/recuperar/enviar",
            data={"correo": "admin@gmail.com"}  # usa un correo que sí exista en tu BD
        )

        assert r_post_real.status_code in [200, 302], \
            f"POST recuperar/enviar con correo existente retornó código inesperado: {r_post_real.status_code}"

        print("\n✅ RecuperacionController: pruebas ejecutadas correctamente.\n")

    except Exception as e:
        assert False, f"Error en pruebas de RecuperacionController: {e}"

    finally:
        print("Deteniendo servidor Spring Boot...")
        proceso.terminate()
        proceso.wait()
