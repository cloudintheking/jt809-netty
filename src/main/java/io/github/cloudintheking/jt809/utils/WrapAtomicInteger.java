package io.github.cloudintheking.jt809.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 封装AtomicInteger
 */
public class WrapAtomicInteger {

    private AtomicInteger i;

    public WrapAtomicInteger(int i) {
        this.i = new AtomicInteger(i);
    }

    public final int incrementAndGet() {
        int current;
        int next;
        do {
            current = this.i.get();
            next = current >= Integer.MAX_VALUE ? 0 : current + 1;//大于最小值重置为0
        } while (!this.i.compareAndSet(current, next));

        return next;
    }

    public final int decrementAndGet() {
        int current;
        int next;
        do {
            current = this.i.get();
            next = current <= Integer.MIN_VALUE ? 0 : current - 1; //小于最小值重置为0
        } while (!this.i.compareAndSet(current, next));

        return next;
    }

}
