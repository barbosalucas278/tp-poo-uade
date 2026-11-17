# Herramientas de prueba de equipamiento

Estas herramientas usan únicamente rutas relativas al proyecto. No apuntan a una carpeta personal ni escriben directamente en `partidas/`.

## Compilar

Desde la raíz del proyecto:

```powershell
if (Test-Path out\tools) { Remove-Item -Recurse -Force out\tools }
New-Item -ItemType Directory -Force out\tools | Out-Null
$src = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d out\tools $src tests\tools\PrepararPartidaEquipamiento.java tests\tools\VerificarSlot3Equipamiento.java
```

`out\tools` es una carpeta auxiliar de compilación y no forma parte de la entrega.

## Generar y verificar el fixture

Los fixtures conservados son `tests/tools/partida-equipamiento-prueba.txt` (entrada inicial, sin equipamiento) y `tests/tools/partida-equipamiento-verificada.txt` (entrada equipada, con el hacha persistida). El segundo archivo no es un slot personal: es una entrada reproducible para ejecutar directamente el verificador. La primera ejecución no sobrescribe un archivo existente:

```powershell
java -cp out\tools tools.PrepararPartidaEquipamiento
```

Para regenerarlo de manera intencional hay que explicitarlo:

```powershell
java -cp out\tools tools.PrepararPartidaEquipamiento --force
```

El generador crea una partida inicial con espada, hacha y poción; produce también el fixture equipado; y verifica equipar, reemplazar, guardar/cargar, evitar duplicar el bonus y desequipar. Los archivos temporales del round-trip se crean con nombres únicos dentro de `tests/tools` y se eliminan al terminar.

## Cargar en la aplicación

La aplicación carga desde `partidas/slotN.txt`. Para no sobrescribir una partida personal, copiar el fixture sólo si el slot elegido no existe:

```powershell
$slot = 'partidas\slot1.txt'
if (Test-Path $slot) { throw "El slot ya existe; elegí otro sin sobrescribirlo." }
Copy-Item tests\tools\partida-equipamiento-prueba.txt $slot
```

Después se ejecuta `rpg.Main` y se carga ese slot desde la pantalla de guardado.

## Verificar un archivo específico

El verificador ya no carga automáticamente `slot3`, ni depende del nombre de un personaje o de valores de una partida personal. Recibe el archivo a verificar:

```powershell
java -cp out\tools tools.VerificarSlot3Equipamiento tests\tools\partida-equipamiento-verificada.txt
```

Para verificar una partida equipada real, indicar explícitamente su ruta, por ejemplo:

```powershell
java -cp out\tools tools.VerificarSlot3Equipamiento partidas\slot3.txt
```

El archivo indicado debe contener al menos un personaje con un arma equipada. El verificador carga, guarda en un temporal único, vuelve a cargar y comprueba que se conserven ataque, defensa, arma e inventario sin duplicar bonificaciones. Nunca sobrescribe el archivo de entrada.
