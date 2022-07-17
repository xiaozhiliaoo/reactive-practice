package org.lili.rxjava;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.observables.GroupedObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author lili
 * @date 2021/7/6 22:32
 */
public class OperatorTest {
    @Test
    void map() {

    }

    class User {
        public String userName;
        public List<Address> addresses;
    }

    class Address {
        public String street;
        public String city;

        public Address(String street, String city) {
            this.street = street;
            this.city = city;
        }
    }

    @Test
    void flatMap() {
        User user = new User();
        user.userName = "lili";
        Address a1 = new Address("sss", "aaa");
        Address a2 = new Address("sss2", "aaa2");
        user.addresses = Arrays.asList(a1, a2);
        Observable.just(user).flatMap(new Function<User, ObservableSource<Address>>() {
            @Override
            public ObservableSource<Address> apply(User user) throws Throwable {
                return Observable.fromIterable(user.addresses);
            }
        }).subscribe(new Consumer<Address>() {
            @Override
            public void accept(Address o) throws Throwable {
                System.out.println(o.city + "----" + o.street);
            }
        });
    }

    @Test
    void groupBy() {
        Observable<GroupedObservable<String, Integer>> groupedObservable = Observable.range(1, 8).groupBy(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Throwable {
                return integer % 2 == 0 ? "偶数" : "奇数";
            }
        });

        groupedObservable.subscribe(new Consumer<GroupedObservable<String, Integer>>() {
            @Override
            public void accept(GroupedObservable<String, Integer> z) throws Throwable {
                if (z.getKey().equalsIgnoreCase("奇数")) {
                    z.subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Throwable {
                            System.out.println("奇数组:"+integer);
                        }
                    });
                }
            }
        });
    }


    @Test
    void buffer() {
        Observable.range(1,10).buffer(2).subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> integers) throws Throwable {
                System.out.println(integers);
            }
        });
    }

    @Test
    void window() {
        Observable.range(1,10)
                .window(2)
                .subscribe(new Consumer<Observable<Integer>>() {
                    @Override
                    public void accept(Observable<Integer> integerObservable) throws Throwable {
                        System.out.println("OnNext:");
                        integerObservable.subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Throwable {
                                System.out.println("accept:"+integer);
                            }
                        });
                    }
                });
    }


    @Test
    void debounce() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                if (emitter.isDisposed()) return;
                for (int i = 1; i <= 10; i++) {
                    emitter.onNext(i);
                    Thread.sleep(i * 100);
                }
                emitter.onComplete();
            }
        }).debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        System.out.println("Next:"+ integer);
                    }
                });
    }

    @Test
    void zip() {
        Observable<Integer> odds = Observable.just(1, 3, 5);
        Observable<Integer> evens = Observable.just(2, 4, 6);
        Observable.zip(odds, evens, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Throwable {
                return integer + integer2;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                System.out.println(integer);
            }
        });
    }


    @Test
    void testTransformer() {
        Observable.just(1,2,3)
                .compose(new ObservableTransformer<Integer, String>() {
                    @Override
                    public @NonNull ObservableSource<String> apply(@NonNull Observable<Integer> upstream) {
                        return upstream.map(new Function<Integer, String>() {
                            @Override
                            public String apply(Integer integer) throws Throwable {
                                return String.valueOf(integer);
                            }
                        });
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String r) throws Throwable {
                        System.out.println(r);
                    }
                });
    }


    @Test
    void parallel() {
        Observable.range(1,100)
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Throwable {
                        return Observable.just(integer)
                                .subscribeOn(Schedulers.computation())
                                .map(new Function<Integer, String>() {
                                    @Override
                                    public String apply(Integer integer) throws Throwable {
                                        return integer.toString();
                                    }
                                });
                    }
                }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {
                System.out.println(s);
            }
        });
    }
}
