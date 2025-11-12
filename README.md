# Cincuentazo — Estado, faltantes y mejoras recomendadas

Estado actual (resumen)
-----------------------

Implementado (o parcialmente)
- Estructura MVC: `models/`, `controllers/`, `views/` y FXML + controladores.
- Modelos básicos: `CardModel`, `DeckModel`, `HandModel`, `PlayerModel`, "GameEngine".
- Vistas: `StartView`, `SelectPlayersView`, `GameView`, FXML `Table.fxml` y estilos `styles.css`.
- Lógica de reparto (DeckModel.draw(), initDeckAndDeal) — se reparten 4 cartas por jugador.
- Renderizado en interfaz de cartas con `ImageView` (carga con resource path).
- Lógica de mostrar/ocultar paneles según nº de jugadores.
- Cache simple de imágenes (en el controlador).
- Interacción básica: selección de jugadores desde la pantalla de inicio, apertura de la vista de juego.
- Manejo visual para que paneles ocultos no reserven espacio (setManaged/setVisible).
- Ajustes responsive básicos (listeners para viewport y ajuste de ImageView).

NUEVO (11/11):
- Motor de reglas central (GameEngine): no hay una clase única que centralice turnos, estado de la mesa (pile / discard stack), reglas completas de juego, lógica CPU, reabastecer mazo desde descartes, eliminación de jugadores, etc.
- Lógica de CPU: no existe una estrategia/cronómetro consolidada (2-4s).
- Reposición de mazo desde descartes cuando se agote: no se ve implementado.
- Manejo de cartas eliminadas (devolver al final del mazo).

Falta o está incompleto / mejorable
----------------------------------
- Manejo de hilos: no hay hilos separados para la IA/animaciones (requerido por entregables). (Se estan usando Transitions y no threads)
- Excepciones propias y control de errores robusto (ej. recursos faltantes).
- Tests unitarios (3 clases de pruebas requeridas).
- Documentación JavaDoc en las clases principales. (Parcialmente hecho en GameEngine y GameController)
- Accesibilidad, keyboard navigation y drag & drop.
- Problemas Ganador/Perdedor

Acciones pendientes
------------------------------------------

HU-1: Inicio del juego
- Estado: Implementado (SelectPlayersView + StartView).
- Mejora: validar visualmente la transición, bloquear selección múltiple mientras se crea la vista, mostrar carga si GameView tarda.

HU-2: Preparación del juego
- Estado: Parcial. Reparto de cartas implementado en `initDeckAndDeal` y `DeckModel`.
- Faltante: carta inicial en mesa (poner primera carta y calcular suma inicial) y mostrarla en `tableTopCard` con valor inicial de la suma.

HU-3: Jugar una carta
- Estado: Parcial. Hay UI para seleccionar cartas (ImageView userData) pero integración con motor de reglas incompleta.
- Faltante: aplicar `valueWhenPlayed` y validar `isPlayable` contra suma actual; mover carta de mano a la mesa y actualizar `tableSumLabel` y `tableTopCard`.

HU-4: Tomar una carta del mazo
- Estado: Parcial. `DeckModel.draw()` existe y las cartas se usan en reparto.
- Faltante: lógica de robar **durante** el juego (tras jugar una carta), sincronización con UI y reabastecimiento cuando mazo vacío.

HU-5: Eliminación de un jugador
- Estado: Faltante. No hay manejo global de “eliminado” y reingreso de cartas al mazo central.
- Acción: Implementar `PlayerModel.setEliminated(true)` y lógica para mover las cartas al final del mazo.

HU-6: Fin del juego
- Estado: Parcial. Hay `checkWinner()` en versiones anteriores; consolidar en el GameEngine es necesario.
- Acción: detectar sólo 1 jugador vivo y mostrar diálogo de ganador / reinicio.

Entregables (lista y estado)
----------------------------
- Interfaz GUI: presente; mejorar UX/animaciones y accesibilidad.
- Interfaces / adaptadores / internas: parcialmente (modelo simple). Requiere diseño y extracción de interfaces (GameEngine, PlayerStrategy).
- Eventos mouse/keyboard: mouse básico presente; falta keyboard.
- MVC: presente, mejorar separación de responsabilidades.
- Javadoc: faltante en la mayoría de clases.
- Layouts: implementados en FXML; mejorar responsividad.
- Estructura de datos: Deck/Hand/Player existenten.
- GitHub: repo ya está.
- Excepciones: faltantes (crear excepciones propias).
- Patrones: algunos (Singleton en Views), faltan Strategy (CPU), Observer (UI update), Factory (mazo).
- Hilos: faltan (IA / animaciones).
- Tests unitarios: no encontrados (faltar crear al menos 3).
