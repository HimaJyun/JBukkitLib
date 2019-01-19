package jp.jyn.jbukkitlib.util.pool;

public interface ObjectPool<E> {
    E get();

    void put(E obj);
}
