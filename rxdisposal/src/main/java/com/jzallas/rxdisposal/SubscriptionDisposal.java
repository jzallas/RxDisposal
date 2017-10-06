package com.jzallas.rxdisposal;

import io.reactivex.disposables.Disposable;

/**
 * {@link Disposal} that can listen to subscriptions and intercept {@link Disposable}s
 */
public class SubscriptionDisposal extends SubscriptionDecorator implements Disposal {

    private Disposal disposal;

    public SubscriptionDisposal(Disposal disposal) {
        this.disposal = disposal;
    }

    @Override
    public void register(Disposable disposable) {
        disposal.register(disposable);
    }

    @Override
    public void dispose() {
        disposal.dispose();
    }

    @Override
    public boolean isDisposed() {
        return disposal.isDisposed();
    }

    @Override
    protected void delegateDisposable(Disposable disposable) {
        register(disposable);
    }
}
