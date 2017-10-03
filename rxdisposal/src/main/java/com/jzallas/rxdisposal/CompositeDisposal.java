package com.jzallas.rxdisposal;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * {@link Disposal} that keeps track multiple {@link Disposable}s.
 * When {@link #dispose()} is called, it will call {@link Disposable#dispose()} on
 * all of the {@link Disposable}s that this {@link Disposal} was watching.
 */
public final class CompositeDisposal extends SubscriptionDecorator implements Disposal {

    private CompositeDisposable compositeDisposable;

    public CompositeDisposal() {
        this(new CompositeDisposable());
    }

    CompositeDisposal(CompositeDisposable disposable) {
        this.compositeDisposable = disposable;
    }

    @Override
    public void register(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    @Override
    public void dispose() {
        compositeDisposable.clear();
    }

    @Override
    public boolean isDisposed() {
        return compositeDisposable.size() < 1;
    }

    @Override
    protected void delegateDisposable(Disposable disposable) {
        register(disposable);
    }
}
