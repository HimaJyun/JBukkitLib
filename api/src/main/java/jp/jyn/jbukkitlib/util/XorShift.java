package jp.jyn.jbukkitlib.util;

public class XorShift {
    public static final ThreadLocal<XorShift> THREAD_LOCAL = ThreadLocal.withInitial(() -> new XorShift().skip(50));
    private long x, y, z, w;

    public XorShift() {
        setSeed(123456789, System.currentTimeMillis(), System.nanoTime(), Runtime.getRuntime().freeMemory());
    }

    public XorShift(long seed) {
        setSeed(seed);
    }

    public XorShift setSeed(long seed) {
        return this.setSeed(seed, seed, seed, seed);
    }

    public XorShift setSeed(long x, long y, long z, long w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public long next() {
        long t = x ^ (x << 11);
        //noinspection SuspiciousNameCombination
        x = y;
        y = z;
        z = w;
        w = (w ^ (w >> 19)) ^ (t ^ (t >> 8));
        return w;
    }

    public XorShift skip(int count) {
        for (int i = 0; i < count; i++) {
            next();
        }
        return this;
    }

    public int nextInt() {
        return (int) next();
    }

    public int nextInt(int bound) {
        return (int) Math.floor(nextDouble() * bound);
    }

    public long nextLong() {
        long l = next();
        if (nextBoolean()) {
            l = ~l + 1L;
        }
        return l;
    }

    public float nextFloat() {
        return (float) next() / Long.MAX_VALUE;
    }

    public double nextDouble() {
        return (double) next() / Long.MAX_VALUE;
    }

    public boolean nextBoolean() {
        return (next() & 1L) == 0;
    }
}
