package jp.jyn.jbukkitlib.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EmptyDeque<E> implements Deque<E> {
    private final static EmptyDeque instanceIgnore = new EmptyDeque(false);
    private final static EmptyDeque instanceException = new EmptyDeque(true);

    @SuppressWarnings("unchecked")
    public static <E> EmptyDeque<E> getInstanceIgnore() {
        return (EmptyDeque<E>) instanceIgnore;
    }

    @SuppressWarnings("unchecked")
    public static <E> EmptyDeque<E> getInstanceException() {
        return (EmptyDeque<E>) instanceException;
    }

    private final boolean exception;

    private EmptyDeque(boolean exception) {
        this.exception = exception;
    }

    @Override
    public void addFirst(E e) {
        if (exception) {
            throw new IllegalStateException("EmptyDeque");
        }
    }

    @Override
    public void addLast(E e) {
        if (exception) {
            throw new IllegalStateException("EmptyDeque");
        }
    }

    @Override
    public boolean offerFirst(E e) {
        return !exception;
    }

    @Override
    public boolean offerLast(E e) {
        return !exception;
    }

    @Override
    public E removeFirst() {
        throw new NoSuchElementException("EmptyDeque");
    }

    @Override
    public E removeLast() {
        throw new NoSuchElementException("EmptyDeque");
    }

    @Override
    public E pollFirst() {
        return null;
    }

    @Override
    public E pollLast() {
        return null;
    }

    @Override
    public E getFirst() {
        throw new NoSuchElementException("EmptyDeque");
    }

    @Override
    public E getLast() {
        throw new NoSuchElementException("EmptyDeque");
    }

    @Override
    public E peekFirst() {
        return null;
    }

    @Override
    public E peekLast() {
        return null;
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        return false;
    }

    @Override
    public boolean add(E e) {
        this.addLast(e);
        return true;
    }

    @Override
    public boolean offer(E e) {
        return this.offerLast(e);
    }

    @Override
    public E remove() {
        return this.removeFirst();
    }

    @Override
    public E poll() {
        return this.pollFirst();
    }

    @Override
    public E element() {
        return this.getFirst();
    }

    @Override
    public E peek() {
        return this.peekFirst();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (exception) {
            throw new IllegalStateException("EmptyDeque");
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public void push(E e) {
        this.addFirst(e);
    }

    @Override
    public E pop() {
        return this.removeFirst();
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Stream<E> stream() {
        return Stream.empty();
    }

    @Override
    public Stream<E> parallelStream() {
        return Stream.empty();
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) new Object[0];
    }

    @Override
    public Iterator<E> descendingIterator() {
        return Collections.emptyIterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) { }
}
