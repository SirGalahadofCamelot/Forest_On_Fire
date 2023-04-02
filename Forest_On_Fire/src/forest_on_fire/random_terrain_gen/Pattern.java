package forest_on_fire.random_terrain_gen;

public enum Pattern {

    MAIN_BLOB(6,0.52),
    BIG_BLOB(5,0.485),
    FREQ_BLOB(5,0.45),
    RARE_BLOB(5,0.4),
    VERY_RARE_BLOB(5,0.38);

    private int iterations;
    private double density;

    private Pattern(int iterations, double density) {
        this.iterations = iterations;
        this.density = density;
    }

    public int getIterations() {
        return iterations;
    }

    public double getDensity() {
        return density;
    }
}

