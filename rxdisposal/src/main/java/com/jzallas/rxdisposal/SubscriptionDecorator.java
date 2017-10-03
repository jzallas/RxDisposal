package com.jzallas.rxdisposal;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.observers.LambdaObserver;

/**
 * Transforms various rx subscriptions into {@link Observer}s
 * while giving the subclass an opportunity to intercept
 * the {@link Disposable} generated from the subscription
 */
abstract class SubscriptionDecorator {

    /**
     * @see Observable#subscribe()
     */
    public <T> Observer<? super T> wrap() {
        return wrap(Functions.emptyConsumer(), Functions.ON_ERROR_MISSING, Functions.EMPTY_ACTION, Functions.emptyConsumer());
    }

    /**
     * @see Observable#subscribe(Consumer)
     */
    public <T> Observer<? super T> wrap(Consumer<? super T> onNext) {
        return wrap(onNext, Functions.ON_ERROR_MISSING, Functions.EMPTY_ACTION, Functions.emptyConsumer());
    }

    /**
     * @see Observable#subscribe(Consumer, Consumer)
     */
    public <T> Observer<? super T> wrap(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return wrap(onNext, onError, Functions.EMPTY_ACTION, Functions.emptyConsumer());
    }

    /**
     * @see Observable#subscribe(Consumer, Consumer)
     */
    public <T> Observer<? super T> wrap(Consumer<? super T> onNext, Consumer<? super Throwable> onError,
                                        Action onComplete) {
        return wrap(onNext, onError, onComplete, Functions.emptyConsumer());
    }

    /**
     * @see Observable#subscribe(Consumer, Consumer, Action, Consumer)
     */
    public <T> Observer<? super T> wrap(Consumer<? super T> onNext, Consumer<? super Throwable> onError,
                                        Action onComplete, Consumer<? super Disposable> onSubscribe) {
        LambdaObserver<? super T> disposableObserver = new LambdaObserver<>(
                onNext,
                onError,
                onComplete,
                onSubscribe
        );

        return handleObserver(disposableObserver);
    }

    /**
     * @see Observable#subscribe(Observer)
     */
    public <T> Observer<? super T> wrap(final Observer<? super T> observer) {
        LambdaObserver<? super T> disposableObserver = new LambdaObserver<>(
                new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        observer.onNext(t);
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        observer.onError(throwable);
                    }
                },
                new Action() {
                    @Override
                    public void run() throws Exception {
                        observer.onComplete();
                    }
                },
                new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        observer.onSubscribe(disposable);
                    }
                }
        );

        return handleObserver(disposableObserver);
    }

    /*VisibleForTesting*/
    <T> Observer<? super T> handleObserver(LambdaObserver<? super T> disposableObserver) {
        delegateDisposable(disposableObserver);
        return disposableObserver;
    }


    /**
     * Handle the {@link Disposable} that will be attached to event whenever it gets subscribed.
     * Override this if you want to capture the {@link Disposable}
     *
     * @param disposable
     */
    protected void delegateDisposable(Disposable disposable) {
        // do nothing by default
    }
}
