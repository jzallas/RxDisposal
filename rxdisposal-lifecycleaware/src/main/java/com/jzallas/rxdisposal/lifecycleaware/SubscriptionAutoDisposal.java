package com.jzallas.rxdisposal.lifecycleaware;

import android.arch.lifecycle.Lifecycle;

import com.jzallas.lifecycleaware.LifecycleAware;
import com.jzallas.lifecycleaware.LifecycleAwareObserver;
import com.jzallas.rxdisposal.Disposal;
import com.jzallas.rxdisposal.SubscriptionDisposal;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link SubscriptionDisposal} that can perform auto-dispose
 * when linked with {@link LifecycleAware}
 */
public class SubscriptionAutoDisposal extends SubscriptionDisposal
        implements LifecycleAwareObserver {

    private static boolean DEBUG = false;
    private static Logger LOG = Logger.getLogger(SubscriptionAutoDisposal.class.getName());

    /**
     * Enable debug logging
     *
     * @param debug <em>true</em> to enable logging, <em>false</em> to disable
     */
    public static void setDebug(boolean debug) {
        SubscriptionAutoDisposal.DEBUG = debug;
    }

    public SubscriptionAutoDisposal(Disposal disposal) {
        super(disposal);
    }

    @Override
    public void onLifecycleEvent(Lifecycle.Event event) {
        if (DEBUG) {
            LOG.log(Level.INFO, String.format("dispose() triggered during %s", event.name()));
        }
        dispose();
    }
}
