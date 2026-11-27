import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Random;

public final class Pruebas {
    private static final Random AZAR = new Random(5);
    private static int casos;

    public static void main(String[] args) {
        mochila(new int[]{2, 3, 4}, new int[]{4, 5, 7}, 5, 9);
        mochila(new int[]{6}, new int[]{20}, 5, 0);
        mochila(new int[]{1, 2}, new int[]{6, 7}, 0, 0);
        mochila(new int[]{}, new int[]{}, 10, 0);

        cambio(new int[]{1, 3, 4}, 6, 2);
        cambio(new int[]{4, 6}, 5, -1);
        cambio(new int[]{2, 5}, 0, 0);
        cambio(new int[]{}, 3, -1);

        cadenas("ABCBDAB", "BDCABA", 4, 2);
        cadenas("ABCD", "AXBYCZD", 4, 1);
        cadenas("ABCDE", "XBCDY", 3, 3);
        cadenas("AAAA", "AA", 2, 2);
        cadenas("ABC", "XYZ", 0, 0);
        cadenas("", "ABC", 0, 0);

        for (int n = 0; n < 200; n++) {
            int[] pesos = vector(AZAR.nextInt(9), 1, 6);
            int[] valores = vector(pesos.length, 0, 12);
            int c = AZAR.nextInt(15);
            mochila(pesos, valores, c, mochilaPorSubconjuntos(pesos, valores, c));

            int[] monedas = vector(AZAR.nextInt(5), 1, 7);
            int monto = AZAR.nextInt(11);
            cambio(monedas, monto, cambioPorNiveles(monedas, monto));

            String a = texto(), b = texto();
            cadenas(a, b, subsecuenciaPorSubconjuntos(a, b), subcadenaPorIntervalos(a, b));
        }

        System.out.println(casos + " casos correctos");
        String a = "ABCD", b = "AXBYCZD";
        System.out.println("Subsecuencia de ejemplo: " + ProgramacionDinamica.ejemploSubsecuencia(a, b));
        System.out.println("Subcadena de ejemplo: " + ProgramacionDinamica.ejemploSubcadena(a, b));
    }

    private static void mochila(int[] p, int[] v, int c, int esperado) {
        String caso = "mochila " + Arrays.toString(p) + " " + Arrays.toString(v) + " c=" + c;
        igual(caso + " rec", esperado, ProgramacionDinamica.mochilaRec(p, v, c));
        igual(caso + " memo", esperado, ProgramacionDinamica.mochilaMemo(p, v, c));
        igual(caso + " tab", esperado, ProgramacionDinamica.mochilaTab(p, v, c));
        casos++;
    }

    private static void cambio(int[] m, int a, int esperado) {
        String caso = "cambio " + Arrays.toString(m) + " monto=" + a;
        igual(caso + " rec", esperado, ProgramacionDinamica.cambioRec(m, a));
        igual(caso + " memo", esperado, ProgramacionDinamica.cambioMemo(m, a));
        igual(caso + " tab", esperado, ProgramacionDinamica.cambioTab(m, a));
        casos++;
    }

    private static void cadenas(String a, String b, int sec, int sub) {
        String caso = "'" + a + "' / '" + b + "'";
        igual(caso + " subsecuencia rec", sec, ProgramacionDinamica.subsecuenciaRec(a, b));
        igual(caso + " subsecuencia memo", sec, ProgramacionDinamica.subsecuenciaMemo(a, b));
        igual(caso + " subsecuencia tab", sec, ProgramacionDinamica.subsecuenciaTab(a, b));
        String s = ProgramacionDinamica.ejemploSubsecuencia(a, b);
        if (s.length() != sec || !esSubsecuencia(s, a) || !esSubsecuencia(s, b)) {
            throw new AssertionError("subsecuencia reconstruida: " + caso + " -> " + s);
        }
        casos++;

        igual(caso + " subcadena rec", sub, ProgramacionDinamica.subcadenaRec(a, b));
        igual(caso + " subcadena memo", sub, ProgramacionDinamica.subcadenaMemo(a, b));
        igual(caso + " subcadena tab", sub, ProgramacionDinamica.subcadenaTab(a, b));
        String t = ProgramacionDinamica.ejemploSubcadena(a, b);
        if (t.length() != sub || !a.contains(t) || !b.contains(t)) {
            throw new AssertionError("subcadena reconstruida: " + caso + " -> " + t);
        }
        casos++;
    }

    private static void igual(String caso, int esperado, int obtenido) {
        if (obtenido != esperado) {
            throw new AssertionError(caso + ": esperado " + esperado + ", obtenido " + obtenido);
        }
    }

    private static int mochilaPorSubconjuntos(int[] p, int[] v, int c) {
        int mejor = 0;
        for (int mascara = 0; mascara < (1 << p.length); mascara++) {
            int peso = 0, valor = 0;
            for (int i = 0; i < p.length; i++) {
                if ((mascara & (1 << i)) != 0) {
                    peso += p[i];
                    valor += v[i];
                }
            }
            if (peso <= c) mejor = Math.max(mejor, valor);
        }
        return mejor;
    }

    private static int cambioPorNiveles(int[] m, int a) {
        int[] distancia = new int[a + 1];
        Arrays.fill(distancia, -1);
        distancia[0] = 0;
        ArrayDeque<Integer> cola = new ArrayDeque<>();
        cola.add(0);
        while (!cola.isEmpty()) {
            int actual = cola.remove();
            if (actual == a) return distancia[actual];
            for (int moneda : m) {
                int p = actual + moneda;
                if (p <= a && distancia[p] == -1) {
                    distancia[p] = distancia[actual] + 1;
                    cola.add(p);
                }
            }
        }
        return -1;
    }

    private static boolean esSubsecuencia(String s, String texto) {
        int i = 0;
        for (int j = 0; j < texto.length() && i < s.length(); j++) {
            if (s.charAt(i) == texto.charAt(j)) i++;
        }
        return i == s.length();
    }

    private static int subsecuenciaPorSubconjuntos(String a, String b) {
        int mejor = 0;
        for (int mascara = 0; mascara < (1 << a.length()); mascara++) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < a.length(); i++) {
                if ((mascara & (1 << i)) != 0) s.append(a.charAt(i));
            }
            if (s.length() > mejor && esSubsecuencia(s.toString(), b)) mejor = s.length();
        }
        return mejor;
    }

    private static int subcadenaPorIntervalos(String a, String b) {
        int mejor = 0;
        for (int i = 0; i < a.length(); i++) {
            for (int j = i + 1; j <= a.length(); j++) {
                if (j - i > mejor && b.contains(a.substring(i, j))) mejor = j - i;
            }
        }
        return mejor;
    }

    private static String texto() {
        StringBuilder s = new StringBuilder();
        int n = AZAR.nextInt(8);
        for (int i = 0; i < n; i++) s.append((char) ('A' + AZAR.nextInt(3)));
        return s.toString();
    }

    private static int[] vector(int n, int min, int max) {
        int[] valores = new int[n];
        for (int i = 0; i < n; i++) valores[i] = min + AZAR.nextInt(max - min + 1);
        return valores;
    }
}

