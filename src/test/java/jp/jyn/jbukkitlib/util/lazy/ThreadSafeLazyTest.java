package jp.jyn.jbukkitlib.util.lazy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreadSafeLazyTest {
    @Disabled
    @Test
    public void test() {
        final int COUNT = 100000;
        Supplier<Double> s = Math::random;
        ExecutorService e = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Double>> f = new ArrayList<>(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < COUNT; i++) {
            Lazy<Double> l = ThreadSafeLazy.of(s);
            Callable<Double> c = l::get;
            for (int j = 0; j < Runtime.getRuntime().availableProcessors(); j++) {
                f.add(e.submit(c));
            }

            Double value, prev = null;
            for (Future<Double> fs : f) {
                try {
                    value = fs.get();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                if (prev != null) {
                    assertEquals(prev, value);
                }
                prev = value;
            }
            f.clear();
        }
    }

    @Disabled
    @Test
    public void typeTest() {
        assertEquals(String.class, ThreadSafeLazy.of(() -> "").get().getClass());
        assertEquals(Double.class, ThreadSafeLazy.of(Math::random).get().getClass());
        assertEquals(ArrayList.class, ThreadSafeLazy.of(ArrayList::new).get().getClass());
    }
}
