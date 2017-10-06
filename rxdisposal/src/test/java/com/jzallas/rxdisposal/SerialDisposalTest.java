package com.jzallas.rxdisposal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.disposables.Disposable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SerialDisposalTest {
    private SerialDisposal testDisposal;

    @Mock
    private Disposable mockDisposable;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testDisposal = new SerialDisposal();
    }

    @Test
    public void testRegister() throws Exception {
        assertTrue(testDisposal.isDisposed());

        doReturn(false)
                .when(mockDisposable)
                .isDisposed();

        testDisposal.register(mockDisposable);

        assertFalse(testDisposal.isDisposed());
    }

    @Test
    public void testDispose() throws Exception {
        testDisposal.register(mockDisposable);

        assertFalse(testDisposal.isDisposed());

        testDisposal.dispose();

        assertTrue(testDisposal.isDisposed());

        verify(mockDisposable, times(1)).dispose();
    }

    @Test
    public void testIsDisposedNull() throws Exception {
        assertTrue(testDisposal.isDisposed());
    }

    @Test
    public void testIsDisposedNotNull() throws Exception {
        doReturn(true)
                .when(mockDisposable)
                .isDisposed();

        testDisposal.register(mockDisposable);

        assertTrue(testDisposal.isDisposed());

        verify(mockDisposable, times(1)).isDisposed();
    }

    @Test
    public void testIsNotDisposedNotNull() throws Exception {
        doReturn(false)
                .when(mockDisposable)
                .isDisposed();

        testDisposal.register(mockDisposable);

        assertFalse(testDisposal.isDisposed());

        verify(mockDisposable, times(1)).isDisposed();
    }
}