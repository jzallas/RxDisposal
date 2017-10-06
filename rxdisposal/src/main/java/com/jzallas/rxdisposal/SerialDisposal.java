package com.jzallas.rxdisposal;

import io.reactivex.disposables.Disposable;

/**
 * Reusable {@link Disposal} that keeps track of the last {@link Disposable} it has seen.
 * If another {@link Disposable} is registered, the last {@link Disposable}
 * will have {@link Disposable#dispose()} called before being replaced by the new {@link Disposable}.
 */
public final class SerialDisposal implements Disposal {

    // TODO: 10/6/2017 Consider using SerialDisposable
    private Disposable disposable;

    @Override
    public void register(Disposable disposable) {
        synchronized (this) {
            if (this.disposable != null && !this.disposable.isDisposed()) {
                this.disposable.dispose();
            }
            this.disposable = disposable;
        }
    }

    @Override
    public void dispose() {
        if (isDisposed()) {
            return;
        }
        synchronized (this) {
            if (isDisposed()) {
                // by the time we got the lock, this was disposed of
                return;
            }
            disposable.dispose();
            disposable = null; // we don't need this reference anymore
        }
    }

    @Override
    public boolean isDisposed() {
        return disposable == null || disposable.isDisposed();
    }

}
