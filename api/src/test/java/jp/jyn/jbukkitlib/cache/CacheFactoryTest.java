package jp.jyn.jbukkitlib.cache;

import com.google.common.cache.CacheBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.DoubleFunction;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class CacheFactoryTest {

    @Ignore
    @Test
    public void benchmarkConcurrentLRU() {
        // Warning: This test may not be correct.
        IntFunction<Map<Integer, Integer>> dangerHashMap = i -> new HashMap<>((i * 4) / 3);
        IntFunction<Map<Integer, Integer>> concurrentHashMap = i -> new ConcurrentHashMap<>((i * 4) / 3);
        DoubleFunction<IntFunction<Map<Integer, Integer>>> dangerLRU = max -> i -> new LRUMap<>((int) (i * max));
        DoubleFunction<IntFunction<Map<Integer, Integer>>> synchronizedLRU = max -> i -> Collections.synchronizedMap(
            new LRUMap<>((int) (i * max))
        );
        DoubleFunction<IntFunction<Map<Integer, Integer>>> guava = max -> i -> CacheBuilder.newBuilder()
            .maximumSize((int) (i * max))
            .initialCapacity((int) (i * max))
            .<Integer, Integer>build()
            .asMap();

        IntFunction<IntConsumer> exec = thread -> ratio -> {
            System.out.printf("=== Thread %d, Write %d%% ===%n", thread, ratio);
            System.out.printf("Time: %dns%n", benchmark(thread, ratio, dangerHashMap));
            System.out.printf("Time: %dns%n", benchmark(thread, ratio, concurrentHashMap));
            System.out.printf("Time: %dns%n", benchmark(thread, ratio, dangerLRU.apply(0.5d)));
            System.out.printf("Time: %dns%n", benchmark(thread, ratio, synchronizedLRU.apply(0.5d)));
            System.out.printf("Time: %dns%n", benchmark(thread, ratio, guava.apply(0.5d)));
            System.out.println();
        };

        System.out.println("=== warm up ===");
        exec.apply(2).accept(50);
        System.out.println("=== warm up ===");

        for (int thread : new int[]{1, 2, 4, 8, 16}) {
            for (int ratio : new int[]{10, 30, 50, 80, 100}) {
                exec.apply(thread).accept(ratio);
            }
        }
    }

    private long benchmark(int concurrency, int ratio, IntFunction<Map<Integer, Integer>> mapGenerator) {
        final int LOOP = 50000;
        final ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        final Map<Integer, Integer> map = mapGenerator.apply(LOOP);

        final List<Runner> runners = new ArrayList<>(concurrency);
        final List<Future<?>> futures = new ArrayList<>(concurrency);
        for (int i = 0; i < concurrency; i++) {
            runners.add(new Runner(LOOP / concurrency, ratio, map));
        }

        // start
        System.out.printf("%s%n", map.getClass().toString());

        System.gc();
        long start, end;
        start = System.nanoTime();
        for (Runner runner : runners) {
            futures.add(executor.submit(runner));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        end = System.nanoTime();
        executor.shutdown();

        System.out.printf("Size: %d%n", map.size());
        return end - start;
    }

    private static class Runner implements Runnable {
        private final Random random = new Random();

        private final Map<Integer, Integer> map;
        private final int loop;
        private final int ratio;

        public Runner(int loop, int ratio, Map<Integer, Integer> map) {
            this.loop = loop;
            this.ratio = ratio;
            this.map = map;
        }

        @Override
        public void run() {
            Integer last = 0;
            for (int i = 0; i < loop; i++) {
                boolean write = random.nextInt(ratio + 1) < ratio;
                if (write) {
                    last = i;
                    map.put(last, random.nextInt());
                } else {
                    map.get(last);
                }
            }
        }
    }
}
