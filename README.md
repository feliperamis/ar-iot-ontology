# ar-iot-ontology

Para poder ejecutar el proyecto se puede hacer desde un IDE como intellij y ejecutar el MainContainer.java


Otro formato es usando el Makefile que serviría para probar el comportamiento de cada agente por separado siguiendo el orden de:

- `make`: Compila el proyecto
- `make gui`: Abre la UI de Jade
- `make agents`: Inicializa todos los agentes, pero estos no pueden comunicarse con la ontología debido a que es una instancia única