import java.util.Arrays;
import java.util.function.IntBinaryOperator;

public final class ProgramacionDinamica {
    private static final int INF = Integer.MAX_VALUE;

    private ProgramacionDinamica() {}

    // Mochila 0/1: cada objeto se puede tomar una sola vez.
    public static int mochilaRec(int[] pesos, int[] valores, int capacidad) {
        return mochila(pesos, valores, pesos.length, capacidad, null);
    }

    public static int mochilaMemo(int[] pesos, int[] valores, int capacidad) {
        return mochila(pesos, valores, pesos.length, capacidad,
                       cache(pesos.length + 1, capacidad + 1));
    }

    private static int mochila(int[] pesos, int[] valores, int i, int c, int[][] memo) {
        if (i == 0) return 0;
        if (memo != null && memo[i][c] != -1) return memo[i][c];
        int mejor = mochila(pesos, valores, i - 1, c, memo);
        if (pesos[i - 1] <= c) {
            mejor = Math.max(mejor, valores[i - 1] +
                             mochila(pesos, valores, i - 1, c - pesos[i - 1], memo));
        }
        if (memo != null) memo[i][c] = mejor;
        return mejor;
    }

    public static int mochilaTab(int[] pesos, int[] valores, int capacidad) {
        int[][] tabla = new int[pesos.length + 1][capacidad + 1];
        for (int i = 1; i <= pesos.length; i++) {
            for (int c = 0; c <= capacidad; c++) {
                tabla[i][c] = tabla[i - 1][c];
                if (pesos[i - 1] <= c) {
                    tabla[i][c] = Math.max(tabla[i][c],
                        valores[i - 1] + tabla[i - 1][c - pesos[i - 1]]);
                }
            }
        }
        return tabla[pesos.length][capacidad];
    }

    // Cambio: mínimo de monedas para completar el monto; -1 si es imposible.
    public static int cambioRec(int[] monedas, int monto) {
        return resultadoCambio(cambio(monedas, monto, null));
    }

    public static int cambioMemo(int[] monedas, int monto) {
        int[] memo = new int[monto + 1];
        Arrays.fill(memo, -1);
        return resultadoCambio(cambio(monedas, monto, memo));
    }

    private static int cambio(int[] monedas, int monto, int[] memo) {
        if (monto == 0) return 0;
        if (memo != null && memo[monto] != -1) return memo[monto];
        int mejor = INF;
        for (int moneda : monedas) {
            if (moneda <= monto) {
                int resto = cambio(monedas, monto - moneda, memo);
                if (resto != INF) mejor = Math.min(mejor, 1 + resto);
            }
        }
        if (memo != null) memo[monto] = mejor;
        return mejor;
    }

    public static int cambioTab(int[] monedas, int monto) {
        int[] tabla = new int[monto + 1];
        Arrays.fill(tabla, INF);
        tabla[0] = 0;
        for (int a = 1; a <= monto; a++) {
            for (int moneda : monedas) {
                if (moneda <= a && tabla[a - moneda] != INF) {
                    tabla[a] = Math.min(tabla[a], 1 + tabla[a - moneda]);
                }
            }
        }
        return resultadoCambio(tabla[monto]);
    }

    private static int resultadoCambio(int valor) {
        return valor == INF ? -1 : valor;
    }

    public static int subsecuenciaRec(String a, String b) {
        return subsecuencia(a, b, a.length(), b.length(), null);
    }

    public static int subsecuenciaMemo(String a, String b) {
        return subsecuencia(a, b, a.length(), b.length(), cache(a.length() + 1, b.length() + 1));
    }

    private static int subsecuencia(String a, String b, int i, int j, int[][] memo) {
        if (i == 0 || j == 0) return 0;
        if (memo != null && memo[i][j] != -1) return memo[i][j];
        int mejor;
        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            mejor = 1 + subsecuencia(a, b, i - 1, j - 1, memo);
        } else {
            mejor = Math.max(subsecuencia(a, b, i - 1, j, memo),
                             subsecuencia(a, b, i, j - 1, memo));
        }
        if (memo != null) memo[i][j] = mejor;
        return mejor;
    }

    public static int subsecuenciaTab(String a, String b) {
        return tablaSubsecuencia(a, b)[a.length()][b.length()];
    }

    private static int[][] tablaSubsecuencia(String a, String b) {
        int[][] tabla = new int[a.length() + 1][b.length() + 1];
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                tabla[i][j] = a.charAt(i - 1) == b.charAt(j - 1)
                    ? 1 + tabla[i - 1][j - 1]
                    : Math.max(tabla[i - 1][j], tabla[i][j - 1]);
            }
        }
        return tabla;
    }

    public static String ejemploSubsecuencia(String a, String b) {
        int[][] tabla = tablaSubsecuencia(a, b);
        int i = a.length(), j = b.length();
        StringBuilder inversa = new StringBuilder();
        while (i > 0 && j > 0) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                inversa.append(a.charAt(i - 1));
                i--;
                j--;
            } else if (tabla[i - 1][j] >= tabla[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        return inversa.reverse().toString();
    }

    // El sufijo diagonal se reinicia cuando los caracteres son distintos.
    public static int subcadenaRec(String a, String b) {
        return mejorSubcadena(a, b, (i, j) -> sufijo(a, b, i, j, null));
    }

    public static int subcadenaMemo(String a, String b) {
        int[][] memo = cache(a.length() + 1, b.length() + 1);
        return mejorSubcadena(a, b, (i, j) -> sufijo(a, b, i, j, memo));
    }

    private static int sufijo(String a, String b, int i, int j, int[][] memo) {
        if (i == 0 || j == 0) return 0;
        if (memo != null && memo[i][j] != -1) return memo[i][j];
        int largo = a.charAt(i - 1) == b.charAt(j - 1)
            ? 1 + sufijo(a, b, i - 1, j - 1, memo) : 0;
        if (memo != null) memo[i][j] = largo;
        return largo;
    }

    private static int mejorSubcadena(String a, String b, IntBinaryOperator calcular) {
        int mejor = 0;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                mejor = Math.max(mejor, calcular.applyAsInt(i, j));
            }
        }
        return mejor;
    }

    private record Coincidencia(int fin, int longitud) {}

    private static Coincidencia subcadenaTabla(String a, String b) {
        int[][] tabla = new int[a.length() + 1][b.length() + 1];
        int mejor = 0, fin = 0;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    tabla[i][j] = 1 + tabla[i - 1][j - 1];
                    if (tabla[i][j] > mejor) {
                        mejor = tabla[i][j];
                        fin = i;
                    }
                }
            }
        }
        return new Coincidencia(fin, mejor);
    }

    public static int subcadenaTab(String a, String b) {
        return subcadenaTabla(a, b).longitud();
    }

    public static String ejemploSubcadena(String a, String b) {
        Coincidencia c = subcadenaTabla(a, b);
        return a.substring(c.fin() - c.longitud(), c.fin());
    }

    private static int[][] cache(int filas, int columnas) {
        int[][] memo = new int[filas][columnas];
        for (int[] fila : memo) Arrays.fill(fila, -1);
        return memo;
    }
}

