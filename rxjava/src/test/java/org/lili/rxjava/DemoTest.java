package org.lili.rxjava;


import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.AsyncSubject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

class DemoTest {

    @Test
    void test1() {
        Observable.just("Hello")
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("doOnNext:" + s);
                    }
                })
                .doAfterNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("doAfterNext:" + s);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doOnComplete");
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        System.out.println("doOnSubscribe");
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doAfterTerminate");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doFinally");
                    }
                })
                .doOnEach(new Consumer<Notification<String>>() {
                    @Override
                    public void accept(Notification<String> notification) throws Exception {
                        System.out.println("doOnEach:" + (notification.isOnNext() ? "onNext" : notification.isOnComplete() ? "onComplete" : "onError"));
                    }
                })
                .doOnLifecycle(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        System.out.println("doOnLifecycle:" + disposable.isDisposed());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("doOnLifecycle run:");
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("subscribe accept message:" + s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println("subscribe accept:" + throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("subscribe run");
                    }
                });
    }

    @Test
    void test2() {
        Consumer subscriber1 = new Consumer<Long>() {
            @Override
            public void accept(Long o) throws Exception {
                System.out.println("subscriber1:" + o);
            }
        };

        Consumer subscriber2 = new Consumer<Long>() {
            @Override
            public void accept(Long o) throws Exception {
                System.out.println("subscriber2:" + o);
            }
        };

        Observable<Long> observable = Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Long> observableEmitter) throws Exception {
                Observable.interval(10, TimeUnit.MICROSECONDS, Schedulers.computation())
                        .take(Integer.MAX_VALUE)
                        .subscribe(observableEmitter::onNext);
            }
        }).observeOn(Schedulers.newThread());

        observable.subscribe(subscriber1);
        observable.subscribe(subscriber2);

        try {
            Thread.sleep(100000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Test
    void test3() {
        Consumer subscriber1 = new Consumer<Long>() {
            @Override
            public void accept(Long o) throws Exception {
                System.out.println("subscriber1:" + o);
            }
        };

        Consumer subscriber2 = new Consumer<Long>() {
            @Override
            public void accept(Long o) throws Exception {
                System.out.println(" subscriber2:" + o);
            }
        };

        Consumer subscriber3 = new Consumer<Long>() {
            @Override
            public void accept(Long o) throws Exception {
                System.out.println("   subscriber3:" + o);
            }
        };

        ConnectableObservable<Long> observable = Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Long> observableEmitter) throws Exception {
                Observable.interval(10, TimeUnit.MICROSECONDS, Schedulers.computation())
                        .take(Integer.MAX_VALUE)
                        .subscribe(observableEmitter::onNext);
            }
        }).observeOn(Schedulers.newThread()).publish();

        observable.connect();

        observable.subscribe(subscriber1);
        observable.subscribe(subscriber2);

        try {
            Thread.sleep(20L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        observable.subscribe(subscriber3);

        try {
            Thread.sleep(20L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    void testMaybe() {
        Maybe.create(new MaybeOnSubscribe<String>() {
            @Override
            public void subscribe(@NotNull MaybeEmitter<String> maybeEmitter) throws Exception {
                maybeEmitter.onSuccess("testA");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("s=" + s);
            }
        });
    }

    @Test
    void testAsyncSubject() {
        AsyncSubject<String> asyncSubject = AsyncSubject.create();
        asyncSubject.onNext("a1");
        asyncSubject.onNext("a2");
        asyncSubject.onComplete();
        asyncSubject.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("asyncSubject:" + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println(throwable.getMessage());
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("asyncSubject:complete");
            }
        });

        asyncSubject.onNext("s3");
        asyncSubject.onNext("s4");
    }

    @Test
    void testCreate() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Integer> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    for (int i = 0; i < 10; i++) {
                        emitter.onNext(i);
                    }
                    emitter.onComplete();
                }
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("next:" + integer);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println(throwable.getMessage());
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("sequence complete.");
            }
        });
    }

    @Test
    void testScheduler() {
        Observable.just("aaa", "bbb")
                .observeOn(Schedulers.newThread())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NotNull String s) throws Exception {
                        return s.toLowerCase();
                    }
                })
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("accept:" + s + " " + Thread.currentThread().getName());
                    }
                });
    }

    @Test
    void testSubscribeOn() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("hello");
                emitter.onNext("world");
            }
        }).subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println(s);
                    }
                });
    }

    @Test
    void testObserverOn() {
        Observable.just("HELLO", "WORLD")
                .subscribeOn(Schedulers.single())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NotNull String s) throws Exception {
                        s = s.toLowerCase();
                        System.out.println("Thread apply1:" + Thread.currentThread().getName());
                        return s;
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NotNull String s) throws Exception {
                        s = s + "lili";
                        System.out.println("Thread apply2:" + Thread.currentThread().getName());

                        return s;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NotNull String s) throws Exception {
                        s = s + " is test;";
                        System.out.println("Thread apply3:" + Thread.currentThread().getName());
                        return s;
                    }
                })
                .observeOn(Schedulers.newThread())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NotNull String s) throws Exception {

                        System.out.println("Thread apply4:" + Thread.currentThread().getName());
                        return s + "apply4";
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("sub:" + s);
                    }
                });
    }


    @Test
    void test11() {
        Observable.fromArray("one", "two", "three")
                .take(2)
                .subscribe((arg) -> {
                    System.out.println(arg);
                });
    }

    @Test
    void test12() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("");
            }
        });
    }

    class Person {
        private int age;
        private String name;

        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }
    }

    public Observable<Person> getPersonById(String id) {
        return Observable.create(new ObservableOnSubscribe<Person>() {
            @Override
            public void subscribe(@NotNull ObservableEmitter<Person> emitter) throws Exception {
                try {
                    //from network or db
                    Person p = new Person(2, "lili");
                    emitter.onNext(p);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }


    @Test
    void doOnNext() {
        Observable.just(1, 2, 3)
                .doOnNext(integer -> {
                    if (integer.equals(2)) {
                        throw new RuntimeException("I dont like 2");
                    }
                })
                .doOnError(throwable -> {
                    System.err.println("Whoops:" + throwable.getMessage());
                })
                .doOnComplete(() -> {
                    System.out.println("Complete Observable");
                })
                .subscribe(integer -> System.out.println("Got:" + integer));
    }

    @Test
    void testFuzzBuzz() {
        Observable.interval(10, TimeUnit.MICROSECONDS)
                .take(100)
                .map(input -> {
                    if (input % 3 == 0 && input % 5 == 0) return "FizzBizz";
                    else if (input % 3 == 0) return "Fizz";
                    else if (input % 5 == 0) return "Bizz";
                    return Long.toString(input);
                })
                .subscribe(System.out::println, throwable -> {
                });
    }

}