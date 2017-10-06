package com.jzallas.rxdisposal.lifecycleaware;

import android.arch.lifecycle.Lifecycle;

import com.jzallas.rxdisposal.Disposal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SubscriptionAutoDisposalTest {
    @Mock
    private Disposal mockDisposal;

    private SubscriptionAutoDisposal testDisposal;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testDisposal = new SubscriptionAutoDisposal(mockDisposal);
    }

    @Test
    public void onLifecycleEvent() throws Exception {
        testDisposal.onLifecycleEvent(Lifecycle.Event.ON_ANY);

        verify(mockDisposal, times(1))
                .dispose();
    }

}