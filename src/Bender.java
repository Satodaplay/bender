import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class Bender {
    private char[][] mapaVisual;
    private RobotBender robot;
    private boolean inversorActivado = false;
    private char[] direcciones = {'S', 'E', 'N', 'W'};
    private int[][] movimiento = {
            {1, 0},  // S
            {0, 1},  // E
            {-1, 0}, // N
            {0, -1}  // W
    };

    public Bender(String mapa) {
        this.mapaVisual = convertirMapa(mapa);
        int[] posRobot = encontrarRobot();
        this.robot = new RobotBender(posRobot[0], posRobot[1], mapaVisual);
    }

    public char[][] convertirMapa(String mapa) {
        String[] filas = mapa.split("\n");
        int filasNum = filas.length;
        int columnasMax = 0;

        // Encontrar la longitud de la fila más larga
        for (String fila : filas) {
            if (fila.length() > columnasMax) {
                columnasMax = fila.length();
            }
        }

        // Crear el mapa visual con espacios como relleno
        char[][] mapaArray = new char[filasNum][columnasMax];
        for (int i = 0; i < filasNum; i++) {
            for (int j = 0; j < filas[i].length(); j++) {
                mapaArray[i][j] = filas[i].charAt(j);
            }
            // Relleno de espacios para filas más cortas
            for (int j = filas[i].length(); j < columnasMax; j++) {
                mapaArray[i][j] = ' ';
            }
        }
        return mapaArray;
    }

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

    public String run() {
        return movimientoRobot();
    }

    private String movimientoRobot() {
        StringBuilder camino = new StringBuilder();
        Set<String> visitado = new HashSet<>();
        while (true) {
            boolean moved = false;
            // Include the complete 'direcciones' array in the state
            String estadoActual = robot.getX() + "," + robot.getY() + "," + inversorActivado + "," + Arrays.toString(direcciones);
            if (visitado.contains(estadoActual)) {
                return null;  // Loop detected
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
                    }
                    moverDireccionAlFrente(i);
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

    private void invertirDirecciones() {
        if (inversorActivado) {
            direcciones = new char[]{'N', 'W', 'S', 'E'};
            movimiento = new int[][]{{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
        } else {
            direcciones = new char[]{'S', 'E', 'N', 'W'};
            movimiento = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        }
    }

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
                continue; // Omitir la posición actual
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

    private int getPriority(int currentX, int currentY, int tpX, int tpY) {
        if (tpX < currentX && tpY > currentY) {
            return 1; // NE
        } else if (tpX > currentX && tpY > currentY) {
            return 2; // SE
        } else if (tpX > currentX && tpY < currentY) {
            return 3; // SW
        } else if (tpX < currentX && tpY < currentY) {
            return 4; // NW
        } else {
            return 5; // Otros casos
        }
    }

    private double calcularDistancia(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

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

    private boolean esPosicionValida(int x, int y) {
        return x >= 0 && x < mapaVisual.length && y >= 0 && y < mapaVisual[0].length && mapaVisual[x][y] != '#';
    }
}

class RobotBender {
    private int x, y;
    private char[][] mapa;

    public RobotBender(int x, int y, char[][] mapa) {
        this.x = x;
        this.y = y;
        this.mapa = mapa;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void mover(int nuevoX, int nuevoY) {
        this.x = nuevoX;
        this.y = nuevoY;
    }
}