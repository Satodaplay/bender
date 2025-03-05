import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class Bender {
    private char[][] mapaVisual;
    private RobotBender robot;
    private boolean inversorActivado = false;
    private char[] direcciones = {'S', 'E', 'N', 'W'};
    private int[][] movimiento = {
            {1, 0},
            {0, 1},
            {-1, 0},
            {0, -1}
    };

    /*
     * Constructor de la classe Bender.
     * Inicialitza el mapa visual a partir d'una cadena de text i crea una instància del robot Bender.
     * - 'mapa' és una cadena de text que representa el mapa.
     * - 'mapaVisual' és una matriu de caràcters que representa el mapa en forma de matriu.
     * - 'posRobot' és un array que conté la posició inicial del robot.
     * - Es crea una instància de RobotBender amb la posició inicial i el mapa visual.
     */
    public Bender(String mapa) {
        this.mapaVisual = convertirMapa(mapa);
        int[] posRobot = encontrarRobot();
        this.robot = new RobotBender(posRobot[0], posRobot[1], mapaVisual);
    }

    /*
     * Converteix el mapa de cadena de text a una matriu bidimensional de caràcters.
     * - 'mapa' és la cadena de text que representa el mapa amb línies separades per salts de línia.
     * Passos:
     * 1. Separar les línies del mapa utilitzant el caràcter de salt de línia.
     * 2. Trobar la longitud de la fila més llarga per determinar el nombre de columnes de la matriu.
     * 3. Crear una matriu de caràcters amb el nombre de files i columnes adequades.
     * 4. Emplenar la matriu amb els caràcters corresponents de cada fila.
     * 5. Omplir amb espais en blanc les posicions buides per a les files més curtes.
     * Retorna la matriu bidimensional que representa el mapa.
     */
    public char[][] convertirMapa(String mapa) {
        String[] filas = mapa.split("\n");
        int filasNum = filas.length;
        int columnasMax = 0;
        for (String fila : filas) {
            if (fila.length() > columnasMax) {
                columnasMax = fila.length();
            }
        }
        char[][] mapaArray = new char[filasNum][columnasMax];
        for (int i = 0; i < filasNum; i++) {
            for (int j = 0; j < filas[i].length(); j++) {
                mapaArray[i][j] = filas[i].charAt(j);
            }
            for (int j = filas[i].length(); j < columnasMax; j++) {
                mapaArray[i][j] = ' ';
            }
        }
        return mapaArray;
    }

    /*
     * Troba la posició inicial del robot al mapa.
     * Cerca el caràcter 'X' dins de la matriu 'mapaVisual' que representa la posició del robot.
     * Retorna un array d'enters amb les coordenades [fila, columna] del robot.
     * Si no es troba el robot, llança una excepció IllegalArgumentException.
     */
    private int[] encontrarRobot() {
        for (int i = 0; i < mapaVisual.length; i++) {
            for (int j = 0; j < mapaVisual[i].length; j++) {
                if (mapaVisual[i][j] == 'X') {
                    return new int[]{i, j};
                }
            }
        }
        throw new IllegalArgumentException("No se encontró el robot en el mapa");
    }

    /*
     * Executa el moviment del robot i retorna el camí recorregut com una cadena de text.
     * Crida al mètode 'movimientoRobot' per calcular el camí.
     */
    public String run() {
        return movimientoRobot();
    }

    /*
     * Mou el robot pel mapa segons les regles especificades i retorna el camí recorregut.
     * Utilitza un bucle mentre per continuar movent-se fins que arribi a una condició de parada.
     * Manté un conjunt d'estats visitats per detectar bucles infinits.
     * Passos:
     * 1. Comprova si l'estat actual ja s'ha visitat; si és així, retorna null per indicar un bucle detectat.
     * 2. Afegeix l'estat actual al conjunt de visitats.
     * 3. Intenta moure el robot en cadascuna de les quatre direccions disponibles.
     * 4. Si la posició és vàlida, mou el robot i actualitza el camí.
     * 5. Comprova si ha arribat al destí ('$'), en aquest cas retorna el camí.
     * 6. Gestiona els elements especials del mapa com 'T' per teletransportar-se i 'I' per activar/inactivar l'inversor.
     * 7. Si no es pot moure en cap direcció, surt del bucle i retorna el camí actual.
     */
    private String movimientoRobot() {
        StringBuilder camino = new StringBuilder();
        Set<String> visitado = new HashSet<>();
        while (true) {
            boolean moved = false;
            String estadoActual = robot.getX() + "," + robot.getY() + "," + inversorActivado + "," + Arrays.toString(direcciones);
            if (visitado.contains(estadoActual)) {
                return null;
            }
            visitado.add(estadoActual);
            for (int i = 0; i < 4; i++) {
                int nuevoX = robot.getX() + movimiento[i][0];
                int nuevoY = robot.getY() + movimiento[i][1];
                if (esPosicionValida(nuevoX, nuevoY)) {
                    robot.mover(nuevoX, nuevoY);
                    camino.append(direcciones[i]);
                    if (mapaVisual[nuevoX][nuevoY] == '$') {
                        return camino.toString();
                    }
                    if (mapaVisual[nuevoX][nuevoY] == 'T') {
                        teleport();
                    }
                    if (mapaVisual[nuevoX][nuevoY] == 'I') {
                        inversorActivado = !inversorActivado;
                        invertirDirecciones();
                        moved = true;
                        break;
                    } else {
                        moverDireccionAlFrente(i);
                    }
                    moved = true;
                    break;
                }
            }
            if (!moved) {
                break;
            }
        }
        return camino.toString();
    }

    /*
     * Inverteix l'ordre de les direccions en funció de l'estat de l'inversor.
     * Si 'inversorActivado' és cert, estableix les direccions a ['N', 'W', 'S', 'E'] i els moviments corresponents.
     * Si és fals, restaura les direccions originals ['S', 'E', 'N', 'W'] i els moviments corresponents.
     */
    private void invertirDirecciones() {
        if (inversorActivado) {
            direcciones = new char[]{'N', 'W', 'S', 'E'};
            movimiento = new int[][]{{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
        } else {
            direcciones = new char[]{'S', 'E', 'N', 'W'};
            movimiento = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        }
    }

    /*
     * Mou la direcció seleccionada a la primera posició de l'array 'direcciones' i 'movimiento'.
     * Això permet donar prioritat a la direcció en la pròxima iteració.
     * - 'index' és la posició de la direcció seleccionada que es vol moure al front.
     */
    private void moverDireccionAlFrente(int index) {
        char selectedDir = direcciones[index];
        int[] selectedMove = movimiento[index];
        for (int i = index; i > 0; i--) {
            direcciones[i] = direcciones[i - 1];
            movimiento[i] = movimiento[i - 1];
        }
        direcciones[0] = selectedDir;
        movimiento[0] = selectedMove;
    }

    /*
     * Teletransporta el robot a un altre punt de teletransport més proper.
     * Passos:
     * 1. Troba tots els punts de teletransport disponibles al mapa.
     * 2. Calcula la distància entre la posició actual i cada punt de teletransport.
     * 3. Selecciona el punt de teletransport més proper que no sigui la posició actual.
     * 4. Si hi ha múltiples punts a la mateixa distància, aplica una prioritat basada en la ubicació.
     * 5. Mou el robot a la posició del punt de teletransport seleccionat.
     * Si no hi ha punts disponibles o no es troba un punt diferent del actual, mostra un missatge adequat.
     */
    public void teleport() {
        int[][] teleports = encontrarTeleport();
        if (teleports.length == 0) {
            System.out.println("No hay puntos de teletransporte disponibles.");
            return;
        }
        int currentX = robot.getX();
        int currentY = robot.getY();
        double distanciaMinima = Double.MAX_VALUE;
        int[] tpMasCercano = null;
        int minPriority = Integer.MAX_VALUE;
        for (int[] tp : teleports) {
            if (tp[0] == currentX && tp[1] == currentY) {
                continue;
            }
            double distancia = calcularDistancia(currentX, currentY, tp[0], tp[1]);
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                tpMasCercano = tp;
                minPriority = getPriority(currentX, currentY, tp[0], tp[1]);
            } else if (distancia == distanciaMinima) {
                int priority = getPriority(currentX, currentY, tp[0], tp[1]);
                if (priority < minPriority) {
                    tpMasCercano = tp;
                    minPriority = priority;
                }
            }
        }
        if (tpMasCercano != null) {
            robot.mover(tpMasCercano[0], tpMasCercano[1]);
            System.out.println("Teletransportado a: (" + tpMasCercano[0] + ", " + tpMasCercano[1] + ")");
        } else {
            System.out.println("No se encontró un teletransporte diferente disponible para moverse.");
        }
    }

    /*
     * Calcula la prioritat entre punts de teletransport en funció de la seva ubicació relativa respecte a la posició actual.
     * Retorna un valor enter que representa la prioritat: menor valor indica major prioritat.
     * Les prioritats s'assignen segons els quadrants:
     * 1 - Nord-est
     * 2 - Sud-est
     * 3 - Sud-oest
     * 4 - Nord-oest
     * 5 - Altres casos
     */
    private int getPriority(int currentX, int currentY, int tpX, int tpY) {
        if (tpX < currentX && tpY > currentY) {
            return 1;
        } else if (tpX > currentX && tpY > currentY) {
            return 2;
        } else if (tpX > currentX && tpY < currentY) {
            return 3;
        } else if (tpX < currentX && tpY < currentY) {
            return 4;
        } else {
            return 5;
        }
    }

    /*
     * Calcula la distància euclidiana entre dos punts (x1, y1) i (x2, y2).
     * Retorna la distància com un valor de doble precisió.
     */
    private double calcularDistancia(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /*
     * Troba totes les posicions dels punts de teletransport ('T') al mapa.
     * Passos:
     * 1. Recorre el mapa per comptar quants punts de teletransport hi ha.
     * 2. Crea una matriu per emmagatzemar les coordenades dels punts de teletransport.
     * 3. Recorre el mapa novament per omplir la matriu amb les coordenades.
     * Retorna una matriu bidimensional amb les coordenades [fila, columna] de cada punt de teletransport.
     */
    private int[][] encontrarTeleport() {
        int count = 0;
        for (int i = 0; i < mapaVisual.length; i++) {
            for (int j = 0; j < mapaVisual[i].length; j++) {
                if (mapaVisual[i][j] == 'T') {
                    count++;
                }
            }
        }
        int[][] teleports = new int[count][2];
        int index = 0;
        for (int i = 0; i < mapaVisual.length; i++) {
            for (int j = 0; j < mapaVisual[i].length; j++) {
                if (mapaVisual[i][j] == 'T') {
                    teleports[index][0] = i;
                    teleports[index][1] = j;
                    index++;
                }
            }
        }
        return teleports;
    }

    /*
     * Comprova si una posició donada (x, y) és vàlida per al moviment.
     * Una posició és vàlida si es troba dins dels límits del mapa i no és una paret ('#').
     * Retorna cert si la posició és vàlida, fals altrament.
     */
    private boolean esPosicionValida(int x, int y) {
        return x >= 0 && x < mapaVisual.length && y >= 0 && y < mapaVisual[0].length && mapaVisual[x][y] != '#';
    }
}

class RobotBender {
    private int x, y;
    private char[][] mapa;

    /*
     * Constructor de la classe RobotBender.
     * Inicialitza la posició del robot i guarda referència al mapa.
     * - 'x' i 'y' són les coordenades inicials del robot.
     * - 'mapa' és la matriu de caràcters que representa el mapa.
     */
    public RobotBender(int x, int y, char[][] mapa) {
        this.x = x;
        this.y = y;
        this.mapa = mapa;
    }

    /*
     * Mètodes d'accés per obtenir les coordenades actuals del robot.
     * - 'getX()' retorna la coordenada x (fila).
     * - 'getY()' retorna la coordenada y (columna).
     */
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /*
     * Mou el robot a una nova posició.
     * Actualitza les coordenades 'x' i 'y' amb els nous valors.
     * - 'nuevoX' i 'nuevoY' són les noves coordenades del robot.
     */
    public void mover(int nuevoX, int nuevoY) {
        this.x = nuevoX;
        this.y = nuevoY;
    }
}