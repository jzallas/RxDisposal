package com.jzallas.rxdisposal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.disposables.Disposable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SubscriptionDisposalTest {

    @Mock
    private Disposal mockDisposal;

    private SubscriptionDisposal testDisposal;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testDisposal = new SubscriptionDisposal(mockDisposal);
    }

    @Test
    public void register() throws Exception {
        Disposable mockDisposable = mock(Disposable.class);
        testDisposal.register(mockDisposable);

        verify(mockDisposal, times(1))
                .register(mockDisposable);
    }

    @Test
    public void dispose() throws Exception {
        testDisposal.dispose();

        verify(mockDisposal, times(1))
                .dispose();
    }

    @Test
    public void isDisposed() throws Exception {
        doReturn(false, true)
                .when(mockDisposal)
                .isDisposed();

        assertFalse(testDisposal.isDisposed());

        assertTrue(testDisposal.isDisposed());
    }

    @Test
    public void delegateDisposable() throws Exception {
        Disposable mockDisposable = mock(Disposable.class);
        testDisposal.delegateDisposable(mockDisposable);

        verify(mockDisposal, times(1))
                .register(mockDisposable);
    }
}