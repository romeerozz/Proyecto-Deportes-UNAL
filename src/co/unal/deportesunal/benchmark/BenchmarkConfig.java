package co.unal.deportesunal.benchmark;

/**
 * Configuración principal para las pruebas de rendimiento.
 * Define los tamaños de datos, el número de ensayos, la semilla y los factores
 * de consulta/eliminación, así como parámetros de calentamiento de la JVM.
 */
public class BenchmarkConfig {
    public final int[] sizes;
    public final int trials;
    public final long seed;
    public final int getQueriesFactor;
    public final int removeFactor;

    public final int warmupTrials;
    public final int warmupSize;

    /**
     * Crea una configuración con valores por defecto para calentamiento (1 ensayo, 1000 elementos).
     *
     * @param sizes            tamaños de datos a probar
     * @param trials           número de ensayos por tamaño
     * @param seed             semilla base para generación de datos
     * @param getQueriesFactor factor multiplicador para la cantidad de consultas GET
     * @param removeFactor     divisor para la cantidad de eliminaciones REMOVE
     */
    public BenchmarkConfig(int[] sizes, int trials, long seed, int getQueriesFactor, int removeFactor) {
        this(sizes, trials, seed, getQueriesFactor, removeFactor, 1, 1_000);
    }

    /**
     * Crea una configuración completa de benchmark.
     *
     * @param sizes            tamaños de datos a probar
     * @param trials           número de ensayos por tamaño
     * @param seed             semilla base para generación de datos
     * @param getQueriesFactor factor multiplicador para consultas GET
     * @param removeFactor     divisor para la cantidad de eliminaciones REMOVE
     * @param warmupTrials     número de ensayos de calentamiento
     * @param warmupSize       tamaño de datos para el calentamiento
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public BenchmarkConfig(
            int[] sizes,
            int trials,
            long seed,
            int getQueriesFactor,
            int removeFactor,
            int warmupTrials,
            int warmupSize
    ) {
        validateSizes(sizes);

        if (trials <= 0) {
            throw new IllegalArgumentException("Trials must be greater than 0.");
        }

        if (getQueriesFactor <= 0) {
            throw new IllegalArgumentException("Get queries factor must be greater than 0.");
        }

        if (removeFactor <= 0) {
            throw new IllegalArgumentException("Remove factor must be greater than 0.");
        }

        if (warmupTrials < 0) {
            throw new IllegalArgumentException("Warmup trials cannot be negative.");
        }

        if (warmupSize <= 0) {
            throw new IllegalArgumentException("Warmup size must be greater than 0.");
        }

        this.sizes = copyArray(sizes);
        this.trials = trials;
        this.seed = seed;
        this.getQueriesFactor = getQueriesFactor;
        this.removeFactor = removeFactor;
        this.warmupTrials = warmupTrials;
        this.warmupSize = warmupSize;
    }

    /**
     * Calcula la cantidad de consultas GET a ejecutar para un tamaño dado.
     *
     * @param n tamaño de los datos
     * @return cantidad de consultas GET
     */
    public int getQueryCount(int n) {
        return n * getQueriesFactor;
    }

    /**
     * Calcula la cantidad de eliminaciones a ejecutar para un tamaño dado.
     *
     * @param n tamaño de los datos
     * @return cantidad de eliminaciones (mínimo 1)
     */
    public int getRemoveCount(int n) {
        int count = n / removeFactor;
        return Math.max(1, count);
    }

    /**
     * Valida que el arreglo de tamaños no sea nulo, vacío ni contenga valores no positivos.
     *
     * @param sizes arreglo de tamaños a validar
     * @throws IllegalArgumentException si la validación falla
     */
    private void validateSizes(int[] sizes) {
        if (sizes == null || sizes.length == 0) {
            throw new IllegalArgumentException("Sizes cannot be null or empty.");
        }

        for (int size : sizes) {
            if (size <= 0) {
                throw new IllegalArgumentException("All sizes must be greater than 0.");
            }
        }
    }

    /**
     * Crea una copia defensiva del arreglo de tamaños.
     *
     * @param source arreglo original
     * @return nueva copia del arreglo
     */
    private int[] copyArray(int[] source) {
        int[] copy = new int[source.length];

        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i];
        }

        return copy;
    }

    /**
     * Retorna una configuración por defecto para benchmarks completos.
     *
     * @return configuración con tamaños desde 100.000 hasta 100.000.000 y 3 ensayos
     */
    public static BenchmarkConfig defaultConfig() {
        return new BenchmarkConfig(
                new int[]{100_000, 1_000_000, 10_000_000, 100_000_000},
                3,
                42L,
                1,
                10,
                1,
                10_000
        );
    }

    /**
     * Retorna una configuración rápida para pruebas exploratorias.
     *
     * @return configuración con tamaños pequeños (10 a 10.000) y 2 ensayos
     */
    public static BenchmarkConfig quickConfig() {
        return new BenchmarkConfig(
                new int[]{10, 100, 1_000, 10_000},
                2,
                42L,
                1,
                10,
                1,
                1_000
        );
    }
}