package org.lili.rxjava;

import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static io.reactivex.rxjava3.core.Observable.timer;

public class RxmarblesTest {
    @Test
    void testFrom() {
        Observable.fromArray(10, 20, 30).delay(x -> timer(x, TimeUnit.MICROSECONDS));
    }

    @Test
    void testInterval() {
        Observable.interval(10, TimeUnit.MICROSECONDS);
    }
}
