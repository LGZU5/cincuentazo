# 🎴 EXPLICACIÓN COMPLETA DEL PROYECTO CINCUENTAZO

## 📋 ÍNDICE
1. [¿Qué es este proyecto?](#qué-es-este-proyecto)
2. [Estructura del proyecto](#estructura-del-proyecto)
3. [¿Cómo funciona el juego?](#cómo-funciona-el-juego)
4. [Carpetas y archivos explicados](#carpetas-y-archivos-explicados)
5. [Arquitectura y conexiones](#arquitectura-y-conexiones)
6. [Flujo del juego](#flujo-del-juego)

---

## 🎮 ¿QUÉ ES ESTE PROYECTO?

**Cincuentazo** es un **juego de cartas digital** creado en Java. Es como el juego de cartas tradicional "Cincuentazo" pero en la computadora.

### El objetivo del juego:
- Jugar cartas que sumen **hasta 50 puntos** (no más)
- Si no puedes jugar ninguna carta, **pierdes**
- El último jugador que quede **gana**

### Características principales:
- ✅ Puedes jugar contra 1, 2 o 3 computadoras (CPU)
- ✅ Interfaz gráfica con ventanas bonitas
- ✅ Las computadoras juegan solas automáticamente
- ✅ Muestra tus cartas y las de los oponentes (volteadas)
- ✅ Contador que muestra cuántos puntos van en la mesa

---

## 📁 ESTRUCTURA DEL PROYECTO

Imagina el proyecto como una casa con diferentes habitaciones:

```
cincuentazo/                          🏠 LA CASA COMPLETA
│
├── src/                              📦 TODO EL CÓDIGO DEL JUEGO
│   ├── main/                         🎯 CÓDIGO PRINCIPAL
│   │   ├── java/                     ☕ PROGRAMAS EN JAVA
│   │   │   └── com/example/cincuentazo/
│   │   │       ├── HelloApplication.java         🚪 PUERTA DE ENTRADA
│   │   │       ├── Launcher.java                 🚀 ARRANCADOR
│   │   │       ├── controllers/                  🎮 CONTROLES
│   │   │       ├── models/                       🧩 PIEZAS DEL JUEGO
│   │   │       └── views/                        👁️ VENTANAS
│   │   │
│   │   └── resources/                🎨 IMÁGENES Y DISEÑOS
│   │       └── com/example/cincuentazo/
│   │           ├── *.fxml            📄 DISEÑOS DE VENTANAS
│   │           ├── assets/           🖼️ IMÁGENES
│   │           └── styles/           💅 ESTILOS VISUALES
│   │
│   └── test/                         🧪 PRUEBAS DEL CÓDIGO
│       └── java/                     ✅ TESTS AUTOMATICOS
│
├── pom.xml                           📋 LISTA DE HERRAMIENTAS
├── README.md                         📖 DESCRIPCIÓN BREVE
└── mvnw / mvnw.cmd                   🔧 HERRAMIENTAS DE CONSTRUCCIÓN
```

---

## 🎲 ¿CÓMO FUNCIONA EL JUEGO?

### Reglas del Cincuentazo:

#### 1️⃣ **Preparación**
- Se usa una baraja de 52 cartas
- Cada jugador recibe 4 cartas
- Se pone una carta inicial en la mesa

#### 2️⃣ **Valores de las cartas**
```
🎴 Cartas normales:
   2 = +2 puntos
   3 = +3 puntos
   4 = +4 puntos
   5 = +5 puntos
   6 = +6 puntos
   7 = +7 puntos
   8 = +8 puntos
   10 = +10 puntos

🃏 Cartas especiales:
   9 = 0 puntos (no suma nada)
   J (Jota) = -10 puntos (RESTA)
   Q (Reina) = -10 puntos (RESTA)
   K (Rey) = -10 puntos (RESTA)
   A (As) = +10 o +1 (inteligente: elige automáticamente)
```

#### 3️⃣ **Cómo jugar tu turno**
1. Miras tus cartas
2. Eliges una carta que NO haga que la suma pase de 50
3. La carta se juega y se suma a la mesa
4. Robas una nueva carta
5. El turno pasa al siguiente jugador

#### 4️⃣ **Cómo se pierde**
- Si NINGUNA de tus cartas se puede jugar (todas harían que pases de 50)
- Te eliminan del juego automáticamente

#### 5️⃣ **Cómo se gana**
- Eres el ÚLTIMO jugador que queda en el juego

---

## 📂 CARPETAS Y ARCHIVOS EXPLICADOS

### 🎯 ARCHIVOS PRINCIPALES (RAÍZ)

#### `pom.xml` - La lista de compras del proyecto
**¿Qué es?** Un archivo que dice qué herramientas y librerías necesita el proyecto.

**Analogía:** Como una lista de ingredientes en una receta.

**Contenido importante:**
- **JavaFX**: Para crear ventanas y botones bonitos
- **JUnit**: Para hacer pruebas automáticas
- **Maven**: Sistema que construye el proyecto

#### `README.md` - La portada del libro
**¿Qué es?** Un archivo con información básica del proyecto.

**Contiene:**
- Nombre del proyecto: "Cincuentazo"
- Nombres de los autores:
  - Jesus David Tovar Sarasti
  - Maria Jose Agudo Angulo
  - Alejandro Escudero

#### `mvnw` y `mvnw.cmd` - Herramientas de construcción
**¿Qué son?** Programas que construyen y ejecutan el proyecto.
- `mvnw`: Para Mac y Linux
- `mvnw.cmd`: Para Windows

**Analogía:** Como tener tu propia caja de herramientas que no necesita instalación.

#### `.gitignore` - Lista de lo que NO subir
**¿Qué es?** Indica qué archivos NO se deben guardar en GitHub.

**Ejemplo:** Archivos temporales, configuraciones personales, etc.

---

### ☕ CÓDIGO JAVA - src/main/java/

#### 🚪 **HelloApplication.java** - La puerta de entrada
```
┌─────────────────────┐
│  USUARIO EJECUTA    │
│     EL PROGRAMA     │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│ HelloApplication    │ ← Aquí empieza TODO
│   start()           │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│   StartView         │ ← Muestra menú principal
│ (Pantalla inicial)  │
└─────────────────────┘
```

**¿Qué hace?** Es el punto de entrada de la aplicación. Cuando ejecutas el programa, esto es lo primero que se ejecuta.

**Función principal:**
- Crea y muestra la ventana del menú principal

#### 🚀 **Launcher.java** - El arrancador
**¿Qué hace?** Es un truco técnico. Java a veces tiene problemas para arrancar JavaFX directamente, así que este archivo lo hace de manera especial.

**Analogía:** Como un ayudante que enciende el motor del coche.

---

### 🎮 CONTROLLERS - Los cerebros de las ventanas

Los "controllers" son como los **cerebros** de cada ventana. Deciden qué pasa cuando haces clic en un botón.

#### **StartController.java** - Cerebro del menú principal
```
┌───────────────────────┐
│   MENÚ PRINCIPAL      │
│                       │
│  [  GUÍA  ]          │ ← Botón que abre las instrucciones
│  [ JUGAR  ]          │ ← Botón que abre selector de jugadores
│  [ SALIR  ]          │ ← Botón que cierra el programa
│                       │
└───────────────────────┘
         ↓
   StartController
  (Escucha los clics)
```

**Métodos importantes:**
- `onGuia()`: Abre la ventana de instrucciones
- `onPlay()`: Abre la ventana para elegir cuántos jugadores
- `onQuit()`: Cierra la aplicación

#### **SelectPlayersController.java** - Cerebro del selector de jugadores
```
┌────────────────────────┐
│ ¿CUÁNTOS JUGADORES?    │
│                        │
│    [ 2 JUGADORES ]     │ ← Tú + 1 CPU
│    [ 3 JUGADORES ]     │ ← Tú + 2 CPUs
│    [ 4 JUGADORES ]     │ ← Tú + 3 CPUs
│                        │
└────────────────────────┘
           ↓
  SelectPlayersController
   (Inicia el juego con
    el número elegido)
```

**Métodos importantes:**
- `onTwoPlayers()`: Inicia juego con 2 jugadores
- `onThreePlayers()`: Inicia juego con 3 jugadores
- `onFourPlayers()`: Inicia juego con 4 jugadores

#### **GameController.java** - Cerebro del juego (EL MÁS IMPORTANTE)
Este es el **controlador principal** del juego. Coordina TODO lo que pasa durante la partida.

```
┌─────────────────────────────────────┐
│          MESA DE JUEGO              │
│                                     │
│    [CPU1]  🎴🎴🎴🎴                 │
│                                     │
│  [CPU2]   🃏 💯   [CPU3]           │
│   🎴🎴     50      🎴🎴             │
│   🎴🎴            🎴🎴              │
│                                     │
│         [TÚ]                        │
│      🎴  🎴  🎴  🎴                │
│      5♥  J♠  3♦  A♣                │
│     [ JUGAR CARTA ]                 │
│                                     │
└─────────────────────────────────────┘
              ↓
       GameController
    (Controla toda la lógica)
```

**Responsabilidades:**
1. **Mostrar las manos** de todos los jugadores
2. **Detectar cuando haces clic** en una carta
3. **Validar si puedes jugar** esa carta
4. **Actualizar el tablero** después de cada jugada
5. **Controlar el turno** de las computadoras
6. **Detectar ganadores** y perdedores

**Métodos importantes:**
- `setNumberOfPlayers()`: Configura el juego con X jugadores
- `onPlayCard()`: Se ejecuta cuando haces clic en "Jugar Carta"
- `selectCard()`: Marca visualmente la carta que seleccionaste
- `refreshAllHands()`: Actualiza las cartas en pantalla
- `updateBoard()`: Actualiza el contador y la última carta jugada
- `checkWinner()`: Verifica si hay un ganador
- `Fold()`: Abandonar la partida

#### **InstructionsController.java** - Cerebro de las instrucciones
**¿Qué hace?** Controla la ventana que explica cómo jugar.

**Función principal:**
- Mostrar las reglas del juego
- Botón para volver al menú

---

### 🧩 MODELS - Las piezas del juego

Los "models" son las **piezas fundamentales** del juego. Son como los objetos del mundo real.

#### **CardModel.java** - Una carta
```
┌─────────┐
│  5  ♥   │  ← rank = "5", suit = "corazones"
│         │
│    ♥    │
│         │
│   ♥  5  │
└─────────┘
```

**¿Qué representa?** Una carta de la baraja.

**Propiedades:**
- `rank`: El número o letra (A, 2, 3... K)
- `suit`: El palo (picas, corazones, diamantes, tréboles)

**Métodos importantes:**
- `baseValue()`: Devuelve el valor base de la carta
- `valueWhenPlayed(currentSum)`: Calcula cuánto suma cuando se juega (el As es inteligente aquí)
- `isPlayable(currentSum)`: Dice si se puede jugar sin pasar de 50

**Ejemplo de carta inteligente (As):**
```
Si la mesa está en 35:
  As puede sumar +10 → 35+10=45 ✅ OK
  
Si la mesa está en 45:
  As suma solo +1 → 45+1=46 ✅ OK
  (porque +10 pasaría de 50)
```

#### **DeckModel.java** - El mazo de cartas
```
┌─────────────┐
│ ╔═══════╗   │  ← Mazo de 52 cartas
│ ║ ▓▓▓▓▓ ║   │     mezcladas
│ ║ ▓▓▓▓▓ ║   │
│ ║ ▓▓▓▓▓ ║   │
│ ╚═══════╝   │
└─────────────┘
```

**¿Qué representa?** El mazo de donde se roban las cartas.

**Métodos importantes:**
- `DeckModel()`: Constructor que crea las 52 cartas y las mezcla
- `shuffle()`: Mezcla las cartas
- `draw()`: Saca la carta de arriba
- `addToBottom()`: Pone una carta al fondo del mazo
- `size()`: Dice cuántas cartas quedan
- `isEmpty()`: Dice si ya no quedan cartas

#### **HandModel.java** - La mano de un jugador
```
┌─────────────────────────────────┐
│   MANO DEL JUGADOR              │
│                                 │
│   🎴    🎴    🎴    🎴         │
│   2♦    5♠    J♥    A♣         │
│                                 │
└─────────────────────────────────┘
```

**¿Qué representa?** Las cartas que tiene un jugador en su mano.

**Métodos importantes:**
- `add(card)`: Agrega una carta a la mano
- `removeCard(card)`: Quita una carta específica
- `getCards()`: Devuelve todas las cartas
- `size()`: Dice cuántas cartas hay
- `contains(card)`: Pregunta si tiene cierta carta
- `clear()`: Vacía la mano completamente

#### **PlayerModel.java** - Un jugador
```
┌──────────────────┐
│  JUGADOR         │
│                  │
│  Nombre: "Tú"    │
│  Humano: Sí      │
│  Eliminado: No   │
│  Mano: [🎴🎴🎴🎴] │
│                  │
└──────────────────┘
```

**¿Qué representa?** Un jugador (puede ser humano o computadora).

**Propiedades:**
- `name`: Nombre del jugador ("Tú", "CPU1", "CPU2", etc.)
- `human`: ¿Es humano? (true/false)
- `hand`: Su mano de cartas (HandModel)
- `eliminated`: ¿Está eliminado? (true/false)

**Métodos:**
- `getName()`: Devuelve el nombre
- `getHand()`: Devuelve su mano
- `isHuman()`: ¿Es humano?
- `isEliminated()`: ¿Está eliminado?
- `setEliminated()`: Marca como eliminado

#### **GameEngine.java** - El motor del juego (EL MÁS COMPLEJO)
```
┌─────────────────────────────────────┐
│        MOTOR DEL JUEGO              │
│                                     │
│  ⚙️ Controla TODA la lógica        │
│  🎲 Maneja el mazo y descartes     │
│  👥 Gestiona todos los jugadores   │
│  🔄 Controla los turnos            │
│  🏆 Detecta ganadores              │
│  🤖 Decide jugadas de las CPUs     │
│                                     │
└─────────────────────────────────────┘
```

**¿Qué hace?** Es el **cerebro del juego**. Contiene TODAS las reglas y lógica.

**Propiedades importantes:**
- `deck`: El mazo de cartas
- `discard`: Pila de cartas descartadas
- `players`: Lista de jugadores
- `currentPlayerIndex`: Índice del jugador actual
- `lastPlayed`: Última carta jugada
- `tableSum`: Suma actual en la mesa
- `MAX_SUM = 50`: Límite máximo

**Métodos principales:**

1. **`startGame(numPlayers)`** - Inicia una partida nueva
   - Crea los jugadores (1 humano + resto CPUs)
   - Crea y mezcla el mazo
   - Reparte 4 cartas a cada jugador
   - Pone la primera carta en la mesa

2. **`applyResult(card)`** - Aplica una jugada
   - Valida que la carta no pase de 50
   - Valida que la carta esté en la mano del jugador
   - Mueve la carta anterior al descarte
   - Pone la nueva carta en la mesa
   - Actualiza la suma
   - El jugador roba una nueva carta

3. **`isPlayable(card)`** - ¿Se puede jugar esta carta?
   - Verifica si jugar la carta no pasaría de 50

4. **`cpuChooseCard(cpu)`** - La CPU elige una carta
   - **Estrategia:** Elige la carta que deje la suma MÁS BAJA posible
   - **Ejemplo:** Si puede jugar 9 (suma 0) o 2 (suma +2), elige el 9
   - **Defensiva:** Trata de dejar números bajos para protegerse

5. **`eliminateIfStuck(player)`** - Elimina jugador sin jugadas
   - Si no tiene ninguna carta jugable
   - Devuelve sus cartas al fondo del mazo
   - Lo marca como eliminado

6. **`nextTurn()`** - Avanza al siguiente turno
   - Pasa al siguiente jugador que NO esté eliminado

7. **`refillIfNeeded()`** - Rellena el mazo si se acaba
   - Si el mazo está vacío
   - Toma las cartas del descarte
   - Las mezcla
   - Las pone de nuevo en el mazo

8. **`hasWinner()` y `getWinner()`** - Detecta ganador
   - Hay ganador cuando solo queda 1 jugador activo

#### **CpuTurnsThread.java** - El hilo de las computadoras
```
┌─────────────────────────────┐
│   HILO DE LAS CPUs          │
│   (Corre en paralelo)       │
│                             │
│   while (juego activo) {    │
│     1. ¿Hay ganador? → FIN  │
│     2. ¿Turno humano? → ESPERAR │
│     3. Turno CPU:           │
│        - Espera 2-4 seg     │
│        - Elige carta        │
│        - Juega carta        │
│        - Siguiente turno    │
│   }                         │
└─────────────────────────────┘
```

**¿Qué hace?** Es un **proceso separado** que ejecuta automáticamente los turnos de las computadoras.

**Analogía:** Como tener un asistente que mueve las fichas de los oponentes mientras tú piensas.

**Funcionamiento:**
1. Se ejecuta en **bucle infinito**
2. **Espera** cuando es el turno del humano
3. Cuando es turno de CPU:
   - Espera 2-4 segundos (simula "pensar")
   - Elige la mejor carta
   - La juega
   - Avanza el turno
4. Actualiza la interfaz gráfica

**Importante:** Usa `synchronized` para evitar que dos turnos se ejecuten al mismo tiempo (thread-safe).

#### **AlertModel.java** - Ventanas de alerta
```
┌─────────────────────┐
│  ⚠️ ADVERTENCIA     │
│                     │
│  No tienes cartas   │
│  jugables           │
│                     │
│      [ OK ]         │
└─────────────────────┘
```

**¿Qué hace?** Muestra mensajes emergentes al usuario.

**Métodos:**
- `warning(title, content)`: Muestra una advertencia
- `confirm(title, content)`: Pregunta sí/no al usuario

**Ejemplos de uso:**
- "¡Has perdido!"
- "¿Deseas abandonar la partida?"
- "No has seleccionado una carta"

---

### 👁️ VIEWS - Las ventanas

Las "views" crean las **ventanas** del juego.

#### **StartView.java** - Ventana del menú principal
```
┌─────────────────────────────┐
│ 🎴 Cincuentazo               │
├─────────────────────────────┤
│                             │
│       Cincuentazo           │
│                             │
│   [GUÍA] [JUGAR] [SALIR]    │
│                             │
└─────────────────────────────┘
```

**¿Qué hace?** Crea la ventana inicial del juego.

**Características:**
- Carga el archivo `StartMenu.fxml` (diseño)
- Carga los estilos CSS
- Carga el icono de la ventana
- Usa patrón Singleton (solo existe una instancia)

#### **SelectPlayersView.java** - Ventana de selección
**¿Qué hace?** Crea la ventana donde eliges cuántos jugadores.

#### **GameView.java** - Ventana del juego
**¿Qué hace?** Crea la ventana principal donde se juega.

**Características:**
- Carga el archivo `Table.fxml`
- Conecta con el GameController
- Llama a `setNumberOfPlayers()` para iniciar

#### **InstructionsView.java** - Ventana de instrucciones
**¿Qué hace?** Muestra cómo jugar.

---

### 🎨 RESOURCES - Recursos visuales

#### **Archivos FXML** - Diseños de ventanas
Los archivos `.fxml` son como **planos arquitectónicos** para las ventanas.

**Analogía:** Como un plano que dice dónde va cada botón, cada texto, cada imagen.

**Archivos:**
- `StartMenu.fxml`: Diseño del menú principal
- `SelectPlayers.fxml`: Diseño del selector de jugadores
- `Table.fxml`: Diseño de la mesa de juego
- `Instructions.fxml`: Diseño de las instrucciones

**Ejemplo simplificado de Table.fxml:**
```xml
<VBox>                           ← Caja vertical
  <StackPane>                    ← Jugador arriba (CPU)
    <Label text="CPU1"/>         ← Nombre
    <HBox>                       ← Cartas en fila
      [🎴🎴🎴🎴]
    </HBox>
  </StackPane>
  
  <HBox>                         ← Fila del medio
    <StackPane>                  ← CPU izquierda
      [🎴🎴]
    </StackPane>
    
    <StackPane>                  ← Mesa central
      <ImageView id="deckImage"/>        ← Mazo
      <Label id="counterLabel"/>         ← Contador
      <ImageView id="lastPlayedImage"/>  ← Última carta
    </StackPane>
    
    <StackPane>                  ← CPU derecha
      [🎴🎴]
    </StackPane>
  </HBox>
  
  <StackPane>                    ← Jugador abajo (TÚ)
    <Label text="Tú"/>
    <HBox>
      [🎴🎴🎴🎴]
    </HBox>
    <Button text="JUGAR CARTA"/> ← Botón para jugar
  </StackPane>
</VBox>
```

#### **assets/images/** - Todas las imágenes

**Estructura:**
```
assets/images/
├── backgrounds/
│   └── backgroundPrincipal.png    🖼️ Fondo del juego
│
├── cards/                         🎴 52 CARTAS + REVERSO
│   ├── 2C.png   (2 de Corazones)
│   ├── 2D.png   (2 de Diamantes)
│   ├── 2P.png   (2 de Picas)
│   ├── 2T.png   (2 de Tréboles)
│   ├── ... (todas las cartas)
│   ├── KC.png   (Rey de Corazones)
│   ├── AC.png   (As de Corazones)
│   └── back.png (Reverso de la carta)
│
└── icons/
    └── favicon.png                🎴 Icono de la app
```

**Nomenclatura de cartas:**
- **Primer carácter:** Valor de la carta (2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K, A)
- **Segundo carácter:** Palo
  - `C` = Corazones
  - `D` = Diamantes
  - `P` = Picas
  - `T` = Tréboles

**Ejemplo:**
- `5P.png` = 5 de Picas
- `JC.png` = Jota de Corazones
- `AD.png` = As de Diamantes

#### **styles/styles.css** - Estilos visuales
**¿Qué es?** Archivo que define cómo se VEN las cosas (colores, tamaños, fuentes).

**Analogía:** Como el pintor que decora la casa después de construirla.

**Ejemplos de estilos:**
```css
.bg-home {
  /* Fondo del menú principal */
}

.button-play {
  /* Estilo del botón JUGAR */
  /* Color verde, letras grandes */
}

.selected-card {
  /* Borde amarillo cuando seleccionas una carta */
}

.title {
  /* Título "Cincuentazo" grande y llamativo */
}
```

---

### 🧪 TEST - Pruebas automáticas

Los archivos en `src/test/` son **pruebas automáticas** para verificar que el código funciona.

**Analogía:** Como un inspector que revisa que todo funcione correctamente.

#### **CardModelTest.java** - Pruebas de cartas
**Verifica:**
- ✅ Los valores de las cartas son correctos
- ✅ El As cambia entre 1 y 10 correctamente
- ✅ `isPlayable()` funciona bien

#### **HandModelTest.java** - Pruebas de la mano
**Verifica:**
- ✅ Se pueden agregar cartas
- ✅ Se pueden quitar cartas
- ✅ El tamaño de la mano es correcto

#### **PlayerModelTest.java** - Pruebas del jugador
**Verifica:**
- ✅ Se puede crear un jugador
- ✅ El jugador tiene nombre
- ✅ El jugador puede ser eliminado

---

## 🏗️ ARQUITECTURA Y CONEXIONES

### Diagrama general del sistema:

```
┌─────────────────────────────────────────────────────────┐
│                  USUARIO (TÚ)                           │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
         ┌───────────────────────┐
         │   INTERFAZ GRÁFICA    │
         │      (JavaFX)         │
         └───────────┬───────────┘
                     │
        ┌────────────┴────────────┐
        ↓                         ↓
┌───────────────┐         ┌──────────────┐
│     VIEWS     │←────────→│ CONTROLLERS │
│   (Ventanas)  │         │  (Cerebros)  │
└───────────────┘         └──────┬───────┘
                                 │
                                 ↓
                         ┌───────────────┐
                         │    MODELS     │
                         │   (Lógica)    │
                         │               │
                         │  • GameEngine │
                         │  • CardModel  │
                         │  • DeckModel  │
                         │  • PlayerModel│
                         │  • HandModel  │
                         └───────────────┘
```

### Patrón de diseño: MVC (Modelo-Vista-Controlador)

```
┌─────────────────────────────────────────────────────────┐
│                    PATRÓN MVC                           │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  📱 VISTA (View)                                        │
│  ├─ StartView                                          │
│  ├─ GameView                                           │
│  └─ Archivos FXML                                      │
│       ↕️ (actualiza/muestra)                            │
│                                                         │
│  🎮 CONTROLADOR (Controller)                           │
│  ├─ StartController                                    │
│  ├─ GameController        ← COORDINA TODO              │
│  └─ SelectPlayersController                            │
│       ↕️ (usa/modifica)                                 │
│                                                         │
│  🧩 MODELO (Model)                                      │
│  ├─ GameEngine            ← REGLAS DEL JUEGO           │
│  ├─ CardModel                                          │
│  ├─ DeckModel                                          │
│  ├─ PlayerModel                                        │
│  └─ HandModel                                          │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### Flujo de datos en una jugada:

```
1. Usuario hace clic en una carta
         ↓
2. GameController detecta el clic
         ↓
3. GameController llama a GameEngine.applyResult(carta)
         ↓
4. GameEngine valida la jugada
         ↓
5. GameEngine actualiza:
   - Mueve cartas
   - Actualiza suma
   - Avanza turno
         ↓
6. GameController actualiza la vista
         ↓
7. Usuario ve la nueva carta en pantalla
```

---

## 🎯 FLUJO DEL JUEGO (Paso a paso)

### 1️⃣ Arranque de la aplicación

```
Launcher.main()
    ↓
HelloApplication.start()
    ↓
StartView.show()
    ↓
[MENÚ PRINCIPAL]
```

### 2️⃣ Usuario hace clic en "JUGAR"

```
StartController.onPlay()
    ↓
SelectPlayersView.show()
    ↓
[SELECTOR DE JUGADORES]
```

### 3️⃣ Usuario elige número de jugadores (ej: 3)

```
SelectPlayersController.onThreePlayers()
    ↓
GameView.getInstance()
    ↓
GameView.setNumberOfPlayers(3)
    ↓
GameController.setNumberOfPlayers(3)
    ↓
GameEngine.startGame(3)
    ↓
[JUEGO INICIA]
    - Se crean 3 jugadores (Tú + CPU1 + CPU2)
    - Se crea el mazo de 52 cartas
    - Se reparten 4 cartas a cada uno
    - Se pone una carta inicial en la mesa
    - Se inicia CpuTurnsThread
```

### 4️⃣ Durante el juego

```
┌──────────────────────────────────────┐
│  BUCLE PRINCIPAL DEL JUEGO           │
│                                      │
│  mientras (no haya ganador) {        │
│                                      │
│    si (turno humano) {               │
│      → Esperar a que haga clic       │
│      → Validar carta                 │
│      → Aplicar jugada                │
│      → Robar nueva carta             │
│      → Siguiente turno               │
│    }                                 │
│                                      │
│    si (turno CPU) {                  │
│      → CpuTurnsThread actúa:         │
│         - Espera 2-4 segundos        │
│         - Elige mejor carta          │
│         - Juega carta                │
│         - Roba nueva carta           │
│         - Siguiente turno            │
│    }                                 │
│                                      │
│    después de cada jugada {          │
│      → ¿Jugador sin cartas jugables? │
│        → Eliminar jugador            │
│      → ¿Solo queda 1 jugador?        │
│        → ¡FIN DEL JUEGO!             │
│    }                                 │
│  }                                   │
└──────────────────────────────────────┘
```

### 5️⃣ Turno del usuario (detallado)

```
1. Usuario hace clic en una carta
        ↓
2. GameController.selectCard()
   - Marca visualmente la carta (borde amarillo)
        ↓
3. Usuario hace clic en "JUGAR CARTA"
        ↓
4. GameController.onPlayCard()
   - Verifica que sea tu turno
   - Verifica que la carta esté en tu mano
        ↓
5. GameEngine.applyResult(carta)
   - ¿La carta no pasa de 50? ✅
   - Mueve carta anterior al descarte
   - Pone nueva carta en la mesa
   - Suma el valor
   - Quita carta de tu mano
   - Te da una nueva carta
        ↓
6. GameController.refreshAllHands()
   - Actualiza tu mano en pantalla
   - Actualiza el contador (suma)
   - Actualiza la carta visible
        ↓
7. GameEngine.nextTurn()
   - Pasa al siguiente jugador
        ↓
8. Si siguiente es CPU:
   → CpuTurnsThread toma control
```

### 6️⃣ Turno de la CPU (detallado)

```
CpuTurnsThread (bucle constante):
    ↓
1. ¿Es turno de CPU? ✅
    ↓
2. Espera 2-4 segundos (simula pensar)
    ↓
3. GameEngine.cpuChooseCard(cpu)
   - Revisa todas sus cartas
   - Para cada carta:
     • ¿Se puede jugar?
     • ¿Cuánto quedaría la suma?
   - Elige la que deje la suma MÁS BAJA
    ↓
4. GameEngine.applyResult(cartaElegida)
   - Igual que el turno humano
    ↓
5. GameController.refreshAllHands()
   - Actualiza todas las manos
   - Actualiza el tablero
    ↓
6. GameEngine.nextTurn()
   - Siguiente jugador
```

### 7️⃣ Eliminación de jugador

```
Después de cada jugada:
    ↓
GameEngine.eliminateIfStuck(jugador)
    ↓
¿Tiene alguna carta jugable?
    NO ↓
    - Devuelve todas sus cartas al mazo
    - Marca como eliminado
    - Muestra mensaje: "¡Has perdido!"
    ↓
¿Solo queda 1 jugador activo?
    SÍ ↓
    - GameEngine.hasWinner() = true
    - GameEngine.getWinner() = jugador restante
    - Muestra mensaje: "¡[Nombre] ha ganado!"
    - Detiene CpuTurnsThread
    - FIN DEL JUEGO
```

### 8️⃣ Fin del juego

```
hasWinner() = true
    ↓
Muestra alerta con ganador
    ↓
Usuario hace clic en OK
    ↓
[Opción 1] Volver al menú
[Opción 2] Salir del programa
```

---

## 🔗 CONEXIONES ENTRE COMPONENTES

### Cómo se comunican las partes:

```
┌─────────────────────────────────────────────────────────┐
│                  FLUJO DE COMUNICACIÓN                  │
└─────────────────────────────────────────────────────────┘

StartView ──usa──→ StartController
    │
    └──carga──→ StartMenu.fxml

SelectPlayersView ──usa──→ SelectPlayersController
    │                           │
    │                           └──abre──→ GameView
    └──carga──→ SelectPlayers.fxml

GameView ──usa──→ GameController ──usa──→ GameEngine
    │                  │                      │
    │                  │                      ├──usa──→ DeckModel
    │                  │                      ├──usa──→ PlayerModel
    │                  │                      │            │
    │                  │                      │            └──tiene──→ HandModel
    │                  │                      │                           │
    │                  │                      │                           └──contiene──→ CardModel
    │                  │                      │
    │                  ├──usa──→ CpuTurnsThread
    │                  └──usa──→ AlertModel
    │
    └──carga──→ Table.fxml
    └──carga──→ styles.css
    └──carga──→ card images

InstructionsView ──usa──→ InstructionsController
    │
    └──carga──→ Instructions.fxml
```

---

## 📊 RESUMEN VISUAL FINAL

### El proyecto en una imagen mental:

```
CINCUENTAZO = Un juego de cartas digital

┌─────────────────────────────────────────────────┐
│                                                 │
│  🎴 FRONTEND (Lo que ves)                       │
│     • Menú principal                            │
│     • Selector de jugadores                     │
│     • Mesa de juego                             │
│     • Instrucciones                             │
│                                                 │
├─────────────────────────────────────────────────┤
│                                                 │
│  🎮 CONTROLES (Los botones funcionan)           │
│     • Detectan clics                            │
│     • Coordinan acciones                        │
│     • Actualizan ventanas                       │
│                                                 │
├─────────────────────────────────────────────────┤
│                                                 │
│  🧠 LÓGICA (Las reglas del juego)               │
│     • GameEngine = El cerebro                   │
│     • Cartas, Mazos, Manos, Jugadores           │
│     • Validaciones                              │
│     • IA de las computadoras                    │
│                                                 │
├─────────────────────────────────────────────────┤
│                                                 │
│  🎨 RECURSOS (Imágenes y estilos)               │
│     • 52 imágenes de cartas                     │
│     • Fondos                                    │
│     • Iconos                                    │
│     • Colores y fuentes                         │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## ✅ CHECKLIST DE COMPRENSIÓN

Para asegurarte de que entiendes TODO:

### Conceptos generales:
- [ ] Sé qué es Cincuentazo (un juego de cartas hasta 50)
- [ ] Entiendo que está hecho en Java con JavaFX
- [ ] Sé que usa el patrón MVC (Modelo-Vista-Controlador)

### Estructura:
- [ ] Entiendo qué hace cada carpeta principal (src, resources, test)
- [ ] Sé dónde están las imágenes (assets/images/)
- [ ] Sé dónde está el código Java (src/main/java/)
- [ ] Sé dónde están los diseños de ventanas (resources/*.fxml)

### Componentes:
- [ ] Entiendo qué es un Controller (cerebro de una ventana)
- [ ] Entiendo qué es un Model (pieza del juego)
- [ ] Entiendo qué es un View (ventana)
- [ ] Entiendo qué hace GameEngine (el motor del juego)

### Flujo:
- [ ] Sé cómo arranca el programa (Launcher → HelloApplication → StartView)
- [ ] Sé cómo se inicia una partida (selector de jugadores → GameView)
- [ ] Sé cómo funciona un turno (humano: clic en carta, CPU: automático)
- [ ] Sé cómo se detecta un ganador (solo 1 jugador queda)

### Detalles técnicos:
- [ ] Entiendo cómo funciona el As (1 o 10 inteligente)
- [ ] Entiendo la estrategia de las CPUs (dejar suma más baja)
- [ ] Entiendo qué es CpuTurnsThread (hilo paralelo para las CPUs)
- [ ] Entiendo cómo se rellena el mazo (desde el descarte)

---

## 🎓 ANALOGÍAS FINALES PARA RECORDAR

| Componente | Analogía |
|------------|----------|
| **Launcher** | El botón de encendido del coche |
| **HelloApplication** | La llave que arranca el motor |
| **Views** | Las habitaciones de una casa |
| **Controllers** | Los interruptores de luz de cada habitación |
| **Models** | Los muebles y objetos dentro de las habitaciones |
| **GameEngine** | El cerebro que sabe todas las reglas del juego |
| **CpuTurnsThread** | Un asistente robot que juega por los oponentes |
| **CardModel** | Una carta física de la baraja |
| **DeckModel** | La pila de cartas |
| **HandModel** | Las cartas que tienes en la mano |
| **PlayerModel** | Una persona sentada en la mesa |
| **FXML** | El plano arquitectónico de una habitación |
| **CSS** | La pintura y decoración de las paredes |
| **Images** | Los cuadros y fotos en las paredes |

---

## 🎯 CONCLUSIÓN

Este proyecto es un **juego de cartas completo y funcional** que demuestra:

1. ✅ **Organización clara** del código (MVC)
2. ✅ **Interfaz gráfica atractiva** (JavaFX)
3. ✅ **Lógica de juego sólida** (GameEngine)
4. ✅ **Inteligencia artificial** básica (CPU strategy)
5. ✅ **Programación concurrente** (CpuTurnsThread)
6. ✅ **Buenas prácticas** (tests, separación de responsabilidades)

El código está bien estructurado, es fácil de mantener y demuestra comprensión de conceptos avanzados de programación orientada a objetos, interfaces gráficas y manejo de hilos.

---

## 📚 GLOSARIO DE TÉRMINOS

| Término | Significado |
|---------|-------------|
| **JavaFX** | Biblioteca para crear interfaces gráficas en Java |
| **FXML** | Archivo XML que define el diseño de una ventana |
| **Controller** | Clase que controla una ventana (responde a eventos) |
| **Model** | Clase que representa datos o lógica del juego |
| **View** | Clase que representa una ventana |
| **Thread** | Proceso que corre en paralelo |
| **Singleton** | Patrón que garantiza solo 1 instancia de una clase |
| **synchronized** | Palabra clave que evita conflictos entre hilos |
| **Maven** | Herramienta para construir proyectos Java |
| **JUnit** | Biblioteca para hacer tests automáticos |
| **CSS** | Lenguaje para definir estilos visuales |
| **Callback** | Función que se pasa como parámetro para ejecutar después |
| **MVC** | Modelo-Vista-Controlador (patrón de diseño) |

---

**FIN DE LA EXPLICACIÓN COMPLETA** ✨

¿Alguna parte específica que quieras que explique más a fondo? ¡Estoy aquí para ayudarte! 🎴
