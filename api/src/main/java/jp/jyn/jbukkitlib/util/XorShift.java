package jp.jyn.jbukkitlib.util;

import java.util.Random;

/**
 * XorShift
 */
public class XorShift {
    public static final ThreadLocal<XorShift> THREAD_LOCAL = ThreadLocal.withInitial(() -> new XorShift().skip(50));
    private long x, y, z, w;

    /**
     * Initialize using time etc as seeds.
     */
    public XorShift() {
        setSeed(123456789, System.currentTimeMillis(), System.nanoTime(), Runtime.getRuntime().freeMemory());
    }

    /**
     * Specify the seed and initialize it.
     *
     * @param seed seed
     */
    public XorShift(long seed) {
        setSeed(seed);
    }

    /**
     * <p>Change seed.</p>
     * <p>Note: We recommend that you skip dozens of times after changing species.</p>
     *
     * @param seed new seed
     * @return for method chain
     */
    public XorShift setSeed(long seed) {
        return this.setSeed(seed, seed, seed, seed);
    }

    /**
     * <p>Change seed.</p>
     * <p>Note: We recommend that you skip dozens of times after changing species.</p>
     *
     * @param x new seed x
     * @param y new seed y
     * @param z new seed z
     * @param w new seed w
     * @return for method chain
     */
    public XorShift setSeed(long x, long y, long z, long w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    /**
     * Generate random number.
     *
     * @return random number
     */
    public long next() {
        long t = x ^ (x << 11);
        //noinspection SuspiciousNameCombination
        x = y;
        y = z;
        z = w;
        w = (w ^ (w >> 19)) ^ (t ^ (t >> 8));
        return w;
    }

    /**
     * Advance the random number for the specified number of times.
     *
     * @param count Number of times to advance
     * @return for method chain
     */
    public XorShift skip(int count) {
        for (int i = 0; i < count; i++) {
            next();
        }
        return this;
    }

    /**
     * {@link Random#nextInt()}
     *
     * @return random int
     */
    public int nextInt() {
        return (int) next();
    }

    /**
     * {@link java.util.Random#nextInt(int)}
     *
     * @param bound bound
     * @return for method chain
     */
    public int nextInt(int bound) {
        return (int) Math.floor(nextDouble() * bound);
    }

    /**
     * {@link Random#nextLong()}
     *
     * @return for method chain
     */
    public long nextLong() {
        long l = next();
        if (nextBoolean()) {
            l = ~l + 1L;
        }
        return l;
    }

    /**
     * {@link Random#nextFloat()}
     *
     * @return for method chain
     */
    public float nextFloat() {
        return (float) next() / Long.MAX_VALUE;
    }

    /**
     * {@link Random#nextDouble()}
     *
     * @return for method chain
     */
    public double nextDouble() {
        return (double) next() / Long.MAX_VALUE;
    }

    /**
     * {@link Random#nextBoolean()}
     *
     * @return for method chain
     */
    public boolean nextBoolean() {
        return (next() & 1L) == 0;
    }
}
