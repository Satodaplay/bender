import java.util.*;

class Bender {
    private char[][] mapaVisual;
    private RobotBender robot;

    // Constructor: inicializa el mapa y el robot
    public Bender(String mapa) {
        this.mapaVisual = convertirMapa(mapa);
        int[] posRobot = encontrarRobot();
        this.robot = new RobotBender(posRobot[0], posRobot[1], mapaVisual);
    }

    // Convierte el mapa de String a una matriz de caracteres
    public char[][] convertirMapa(String mapa) {
        String[] filas = mapa.split("\n");
        int filasNum = filas.length;
        int columnasNum = filas[0].length();

        char[][] mapaArray = new char[filasNum][columnasNum];

        for (int i = 0; i < filasNum; i++) {
            for (int j = 0; j < filas[i].length(); j++) {
                mapaArray[i][j] = filas[i].charAt(j);
            }
        }
        return mapaArray;
    }

    // Encuentra la posición inicial del robot
    private int[] encontrarRobot() {
        for (int i = 0; i < mapaVisual.length; i++) {
            for (int j = 0; j < mapaVisual[i].length; j++) {
                if (mapaVisual[i][j] == 'X') {
                    return new int[]{i, j}; // Devolver la posición del robot
                }
            }
        }
        throw new IllegalArgumentException("No se encontró el robot en el mapa");
    }

    // Método que inicia el movimiento del robot y devuelve la secuencia de movimientos
    public String run() {
        return bfs(); // Usamos BFS para obtener la secuencia más corta de movimientos
    }

    private String bfs() {
        // Direcciones de movimiento: N, S, E, W
        char[] direcciones = {'N', 'S', 'E', 'W'};
        int[][] movimiento = {
                {-1, 0}, // N
                {1, 0},  // S
                {0, 1},  // E
                {0, -1}  // W
        };

        // Cola para BFS: contiene la posición y el camino hasta esa posición
        Queue<Posicion> cola = new LinkedList<>();
        Set<String> visitados = new HashSet<>();

        // Posición inicial del robot
        cola.add(new Posicion(robot.getX(), robot.getY(), ""));
        visitados.add(robot.getX() + "," + robot.getY());

        while (!cola.isEmpty()) {
            Posicion actual = cola.poll();

            // Si llega al destino, devuelve la secuencia de movimientos
            if (mapaVisual[actual.x][actual.y] == '$') {
                return actual.camino;
            }

            // Probar todas las direcciones posibles
            for (int i = 0; i < 4; i++) {
                int nuevoX = actual.x + movimiento[i][0];
                int nuevoY = actual.y + movimiento[i][1];

                // Verificar si la nueva posición es válida
                if (esPosicionValida(nuevoX, nuevoY) && !visitados.contains(nuevoX + "," + nuevoY)) {
                    visitados.add(nuevoX + "," + nuevoY);
                    cola.add(new Posicion(nuevoX, nuevoY, actual.camino + direcciones[i]));
                }
            }
        }

        return ""; // Si no se encuentra un camino
    }

    // Verifica si la posición es válida
    private boolean esPosicionValida(int x, int y) {
        return x >= 0 && x < mapaVisual.length && y >= 0 && y < mapaVisual[0].length && mapaVisual[x][y] != '#';
    }

    // Clase interna para manejar la posición y el camino hasta esa posición
    private class Posicion {
        int x, y;
        String camino;

        Posicion(int x, int y, String camino) {
            this.x = x;
            this.y = y;
            this.camino = camino;
        }
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

    // Obtiene la posición actual del robot
    public int[] getPosicion() {
        return new int[]{x, y};
    }
}
