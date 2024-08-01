package org.example.myArrayList;

import java.security.InvalidParameterException;
import java.util.*;

public class CustomArraylist<E> implements Iterable<E> {
    private static final int DEFAULT_CAPACITY = 5;
    private Object[] elements;
    private int size;
    private int modCount;

    public CustomArraylist(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        elements = new Object[capacity];
    }

    public CustomArraylist() {
        this(DEFAULT_CAPACITY);
    }

    private void add(E element, Object[] elements, int s) {
        if (s == elements.length) {
            grow();
        }
        elements[s] = element;
        size = s + 1;
    }

    public boolean add(E element) {
        modCount++;
        grow();
        add(element, elements, size);
        return true;
    }

    public boolean insert(int index, E element) {
        if (index < 0 || index > size) {
            throw new InvalidParameterException("Index must be between 0 and %d".formatted(size));
        }
        modCount++;
        if (index == size) {
            add(element);
        } else {

            System.arraycopy(elements, index, elements, index + 1, size - index);

            elements[index] = element;
            size++;
        }
        return true;
    }

    public boolean remove(int index) {
        if (index < 0 || index >= size) {
            throw new InvalidParameterException("Index must be between 0 and %d ".formatted(size));
        }
        modCount++;
        System.arraycopy(elements, index + 1, elements, index, size - index - 1);
        elements[--size] = null;
        return true;
    }

    public boolean remove(E element) {
        if (isEmpty()) return false;

        int index = indexOf(element);

        if (index == -1) return false;

        remove(index);

        return true;
    }

    public void removeAll(E element) {
        for (Object o : elements) {
            remove(element);
        }
    }

    public boolean contains(E element) {
        return indexOf(element) >= 0;
    }

    public int indexOf(E element) {
        if (isEmpty()) return -1;

        for (int i = 0; i < size; i++) {
            if (Objects.equals(elements[i], element)) {
                return i;
            }
        }
        return -1;
    }

    public void trimToSize() {
        elements = Arrays.copyOf(elements, size);
    }

    public int capacity() {
        return elements.length;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void grow() {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, size * 2 + 2);
        }
    }

    public void clear() {
        Object[] sc = elements;
        for (int to = size, i = size = 0; i < to; i++) {
            sc[i] = null;
        }
    }

    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        if (index < 0 || index > size) {
            throw new InvalidParameterException();
        }
        E oldElement = (E) elements[index];

        elements[index] = element;
        return oldElement;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || index > size) {
            throw new InvalidParameterException();
        }
        return (E) elements[index];
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(" , ", "[", "]");

        for (int i = 0; i < size; i++) {
            sj.add(String.valueOf(elements[i]));
        }
        return sj.toString();
    }
// fail-fast

//    @Override
//    public Iterator<E> iterator() {
//        return new Iterator<E>() {
//            int index = 0;
//            final int modeCountCopy = modCount;
//
//            @Override
//            public boolean hasNext() {
//
//                if (modeCountCopy != modCount) {
//                    throw new ConcurrentModificationException();
//                }
//                return index < size;
//            }
//
//            @Override
//            @SuppressWarnings("unchecked")
//            public E next() {
//                if (modeCountCopy != modCount) {
//                    throw new ConcurrentModificationException();
//                }
//                return (E) elements[index++];
//            }
//        };
//    }
//}

// fail-safe

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int index = 0;
            final Object[] oldElements = Arrays.copyOf(elements, size);

            @Override
            public boolean hasNext() {
                return index < oldElements.length;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                return (E) oldElements[index++];
            }
        };
    }
}
