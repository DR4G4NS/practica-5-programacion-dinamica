import java.util.Arrays;
import java.util.Locale;
import java.util.function.IntSupplier;

public final class Mediciones {
    private static volatile long control;
    private static final long OBJETIVO_NS = 15_000_000;
    private static final int MUESTRAS = 5;

    public static void main(String[] args) {
        Locale.setDefault(Locale.ROOT);
        System.out.println("problema;entrada;optimo;rec_us;memo_us;tab_us");
        for (int n : new int[]{12, 18, 23}) {
            int[] pesos = new int[n], valores = new int[n];
            for (int i = 0; i < n; i++) {
                pesos[i] = 1 + i % 4;
                valores[i] = 2 + i % 7;
            }
            int c = n + n / 2;
            fila("Mochila", "n=" + n + ", C=" + c,
                 () -> ProgramacionDinamica.mochilaRec(pesos, valores, c),
                 () -> ProgramacionDinamica.mochilaMemo(pesos, valores, c),
                 () -> ProgramacionDinamica.mochilaTab(pesos, valores, c));
        }
        for (int monto : new int[]{12, 19, 25}) {
            int[] monedas = {1, 3, 4};
            fila("Cambio", "A=" + monto,
                 () -> ProgramacionDinamica.cambioRec(monedas, monto),
                 () -> ProgramacionDinamica.cambioMemo(monedas, monto),
                 () -> ProgramacionDinamica.cambioTab(monedas, monto));
        }
        for (int n : new int[]{6, 10, 13}) {
            String a = "A".repeat(n), b = "B".repeat(n);
            fila("Subsecuencia", "m=n=" + n,
                 () -> ProgramacionDinamica.subsecuenciaRec(a, b),
                 () -> ProgramacionDinamica.subsecuenciaMemo(a, b),
                 () -> ProgramacionDinamica.subsecuenciaTab(a, b));
        }
        for (int n : new int[]{32, 64, 128}) {
            String a = "A".repeat(n), b = a;
            fila("Subcadena", "m=n=" + n,
                 () -> ProgramacionDinamica.subcadenaRec(a, b),
                 () -> ProgramacionDinamica.subcadenaMemo(a, b),
                 () -> ProgramacionDinamica.subcadenaTab(a, b));
        }
    }

    private static void fila(String problema, String entrada, IntSupplier rec,
                             IntSupplier memo, IntSupplier tab) {
        int esperado = rec.getAsInt();
        if (memo.getAsInt() != esperado || tab.getAsInt() != esperado)
            throw new AssertionError("Resultados distintos en " + problema + " " + entrada);
        System.out.printf("%s;%s;%d;%.3f;%.3f;%.3f%n", problema, entrada, esperado,
                          tiempoUs(rec), tiempoUs(memo), tiempoUs(tab));
    }

    private static double tiempoUs(IntSupplier f) {
        int rep = 1;
        // Ajustar el lote también calienta la JVM antes de tomar las muestras.
        while (lote(f, rep) < OBJETIVO_NS && rep < (1 << 20)) rep *= 2;
        lote(f, rep);
        double[] medidas = new double[MUESTRAS];
        for (int i = 0; i < medidas.length; i++) medidas[i] = lote(f, rep) / (1000.0 * rep);
        Arrays.sort(medidas);
        return medidas[medidas.length / 2];
    }

    private static long lote(IntSupplier f, int rep) {
        long inicio = System.nanoTime(), suma = 0;
        for (int i = 0; i < rep; i++) suma += f.getAsInt();
        control = suma;
        return System.nanoTime() - inicio;
    }
}

