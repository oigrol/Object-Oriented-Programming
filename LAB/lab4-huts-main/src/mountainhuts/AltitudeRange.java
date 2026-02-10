package mountainhuts;

public class AltitudeRange {
    private Integer min;
    private Integer max;

    public AltitudeRange(Integer min, Integer max) {
        if (min == null || min < 0) throw new IllegalArgumentException("Minimo non valido.");
        if (max == null || max < 0) throw new IllegalArgumentException("Massimo non valido.");
        this.min = min;
        this.max = max;
    }

    public boolean findAltitudeRange(int altitude) {
        if (min < altitude && altitude <= max) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return min + "-" + max;
    }
}
