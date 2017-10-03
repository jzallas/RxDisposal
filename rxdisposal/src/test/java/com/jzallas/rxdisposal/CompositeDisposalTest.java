package com.jzallas.rxdisposal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CompositeDisposalTest {

    private CompositeDisposal testDisposal;

    private CompositeDisposable testCompositeDisposable;

    @Mock
    private Disposable mockDisposable;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testCompositeDisposable = new CompositeDisposable();
        testDisposal = new CompositeDisposal(testCompositeDisposable);
    }

    @Test
    public void testRegister() throws Exception {
        assertEquals(0, testCompositeDisposable.size());

        testDisposal.register(mockDisposable);

        assertEquals(1, testCompositeDisposable.size());
    }

    @Test
    public void testDispose() throws Exception {
        testCompositeDisposable.add(mockDisposable);

        testDisposal.dispose();

        assertEquals(0, testCompositeDisposable.size());

        verify(mockDisposable, times(1)).dispose();
    }

    @Test
    public void testIsDisposed() throws Exception {
        assertTrue(testDisposal.isDisposed());

        testCompositeDisposable.add(mockDisposable);
        testDisposal.dispose();
        assertTrue(testDisposal.isDisposed());
    }

    @Test
    public void testIsNotDisposed() throws Exception {
        assertTrue(testDisposal.isDisposed());

        testCompositeDisposable.add(mockDisposable);
        assertFalse(testDisposal.isDisposed());
    }

    @Test
    public void testDisposableAddedDuringDelegate() {
        assertEquals(0, testCompositeDisposable.size());

        testDisposal.delegateDisposable(mockDisposable);

        assertEquals(1, testCompositeDisposable.size());
    }

}