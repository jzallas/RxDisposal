package com.jzallas.rxdisposal;

import io.reactivex.disposables.Disposable;

/**
 * A reusable {@link Disposable} collector
 */
public interface Disposal extends Disposable {
    /**
     * Keep track of the provided {@link Disposable} until {@link #dispose()} ()} is called.
     *
     * @param disposable
     */
    void register(Disposable disposable);
}
