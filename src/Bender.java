import java.util.*;

class Bender {
    private char[][] mapaVisual;
    private RobotBender robot;

    public Bender(String mapa) {
        this.mapaVisual = convertirMapa(mapa);
        int[] posRobot = encontrarRobot();
        this.robot = new RobotBender(posRobot[0], posRobot[1], mapaVisual);
    }

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
        char[] direcciones = {'S', 'E', 'N', 'W'};
        int[][] movimiento = {
                {1, 0},  // S
                {0, 1},  // E
                {-1, 0}, // N
                {0, -1}  // W
        };

        int direccionActual = 0;
        StringBuilder camino = new StringBuilder();

        while (true) {
            int nuevoX = robot.getX() + movimiento[direccionActual][0];
            int nuevoY = robot.getY() + movimiento[direccionActual][1];

            if (esPosicionValida(nuevoX, nuevoY)) {
                robot.mover(nuevoX, nuevoY);
                camino.append(direcciones[direccionActual]);  // Agrega la dirección al camino

                if (mapaVisual[nuevoX][nuevoY] == '$') {  // Si llegó a la meta, termina
                    return camino.toString();
                }
            } else {
                // Si choca con una pared, cambia a la siguiente dirección
                direccionActual = (direccionActual + 1) % 4;
            }
        }
    }

    public String teleport() {
        int[][] Tp = encontrarTeleport();

        return "";
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
