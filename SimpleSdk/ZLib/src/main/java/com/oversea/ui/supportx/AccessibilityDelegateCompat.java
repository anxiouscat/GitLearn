/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oversea.ui.supportx;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;


/**
 * Helper for accessing {@link AccessibilityDelegate}.
 * <p>
 * <strong>Note:</strong> On platform versions prior to
 * {@link Build.VERSION_CODES#M API 23}, delegate methods on
 * views in the {@code android.widget.*} package are called <i>before</i>
 * host methods. This prevents certain properties such as class name from
 * being modified by overriding
 * {@link AccessibilityDelegateCompat#onInitializeAccessibilityNodeInfo(View, AccessibilityNodeInfoCompat)},
 * as any changes will be overwritten by the host class.
 * <p>
 * Starting in {@link Build.VERSION_CODES#M API 23}, delegate
 * methods are called <i>after</i> host methods, which all properties to be
 * modified without being overwritten by the host class.
 */
public class AccessibilityDelegateCompat {

    private static final class AccessibilityDelegateAdapter extends AccessibilityDelegate {
        private final AccessibilityDelegateCompat mCompat;

        AccessibilityDelegateAdapter(AccessibilityDelegateCompat compat) {
            mCompat = compat;
        }

        @Override
        public boolean dispatchPopulateAccessibilityEvent(View host,
                AccessibilityEvent event) {
            return mCompat.dispatchPopulateAccessibilityEvent(host, event);
        }

        @Override
        public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            mCompat.onInitializeAccessibilityEvent(host, event);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(
                View host, AccessibilityNodeInfo info) {
            mCompat.onInitializeAccessibilityNodeInfo(host,
                    AccessibilityNodeInfoCompat.wrap(info));
        }

        @Override
        public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            mCompat.onPopulateAccessibilityEvent(host, event);
        }

        @Override
        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child,
                AccessibilityEvent event) {
            return mCompat.onRequestSendAccessibilityEvent(host, child, event);
        }

        @Override
        public void sendAccessibilityEvent(View host, int eventType) {
            mCompat.sendAccessibilityEvent(host, eventType);
        }

        @Override
        public void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
            mCompat.sendAccessibilityEventUnchecked(host, event);
        }

        @SuppressLint("NewApi")
        @Override
        public AccessibilityNodeProvider getAccessibilityNodeProvider(View host) {
            AccessibilityNodeProviderCompat provider =
                    mCompat.getAccessibilityNodeProvider(host);
            return (provider != null)
                    ? (AccessibilityNodeProvider) provider.getProvider() : null;
        }

        @Override
        public boolean performAccessibilityAction(View host, int action, Bundle args) {
            return mCompat.performAccessibilityAction(host, action, args);
        }
    }

    private static final AccessibilityDelegate DEFAULT_DELEGATE = new AccessibilityDelegate();

    private final AccessibilityDelegate mBridge;

    /**
     * Creates a new instance.
     */
    public AccessibilityDelegateCompat() {
        mBridge = new AccessibilityDelegateAdapter(this);
    }

    /**
     * @return The wrapped bridge implementation.
     */
    AccessibilityDelegate getBridge() {
        return mBridge;
    }

    /**
     * Sends an accessibility event of the given type. If accessibility is not
     * enabled this method has no effect.
     * <p>
     * The default implementation behaves as {@link View#sendAccessibilityEvent(int)
     * View#sendAccessibilityEvent(int)} for the case of no accessibility delegate
     * been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param eventType The type of the event to send.
     *
     * @see View#sendAccessibilityEvent(int) View#sendAccessibilityEvent(int)
     */
    public void sendAccessibilityEvent(View host, int eventType) {
        DEFAULT_DELEGATE.sendAccessibilityEvent(host, eventType);
    }

    /**
     * Sends an accessibility event. This method behaves exactly as
     * {@link #sendAccessibilityEvent(View, int)} but takes as an argument an
     * empty {@link AccessibilityEvent} and does not perform a check whether
     * accessibility is enabled.
     * <p>
     * The default implementation behaves as
     * {@link View#sendAccessibilityEventUnchecked(AccessibilityEvent)
     * View#sendAccessibilityEventUnchecked(AccessibilityEvent)} for
     * the case of no accessibility delegate been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param event The event to send.
     *
     * @see View#sendAccessibilityEventUnchecked(AccessibilityEvent)
     *      View#sendAccessibilityEventUnchecked(AccessibilityEvent)
     */
    public void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
        DEFAULT_DELEGATE.sendAccessibilityEventUnchecked(host, event);
    }

    /**
     * Dispatches an {@link AccessibilityEvent} to the host {@link View} first and then
     * to its children for adding their text content to the event.
     * <p>
     * The default implementation behaves as
     * {@link View#dispatchPopulateAccessibilityEvent(AccessibilityEvent)
     * View#dispatchPopulateAccessibilityEvent(AccessibilityEvent)} for
     * the case of no accessibility delegate been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param event The event.
     * @return True if the event population was completed.
     *
     * @see View#dispatchPopulateAccessibilityEvent(AccessibilityEvent)
     *      View#dispatchPopulateAccessibilityEvent(AccessibilityEvent)
     */
    public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        return DEFAULT_DELEGATE.dispatchPopulateAccessibilityEvent(host, event);
    }

    /**
     * Gives a chance to the host View to populate the accessibility event with its
     * text content.
     * <p>
     * The default implementation behaves as
     * {@link ViewCompat#onPopulateAccessibilityEvent(View, AccessibilityEvent)
     * ViewCompat#onPopulateAccessibilityEvent(AccessibilityEvent)} for
     * the case of no accessibility delegate been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param event The accessibility event which to populate.
     *
     * @see ViewCompat#onPopulateAccessibilityEvent(View ,AccessibilityEvent)
     *      ViewCompat#onPopulateAccessibilityEvent(View, AccessibilityEvent)
     */
    public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        DEFAULT_DELEGATE.onPopulateAccessibilityEvent(host, event);
    }

    /**
     * Initializes an {@link AccessibilityEvent} with information about the
     * the host View which is the event source.
     * <p>
     * The default implementation behaves as
     * {@link ViewCompat#onInitializeAccessibilityEvent(View v, AccessibilityEvent event)
     * ViewCompat#onInitalizeAccessibilityEvent(View v, AccessibilityEvent event)} for
     * the case of no accessibility delegate been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param event The event to initialize.
     *
     * @see ViewCompat#onInitializeAccessibilityEvent(View, AccessibilityEvent)
     *      ViewCompat#onInitializeAccessibilityEvent(View, AccessibilityEvent)
     */
    public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        DEFAULT_DELEGATE.onInitializeAccessibilityEvent(host, event);
    }

    /**
     * Initializes an {@link AccessibilityNodeInfoCompat} with information about the host view.
     * <p>
     * The default implementation behaves as
     * {@link ViewCompat#onInitializeAccessibilityNodeInfo(View, AccessibilityNodeInfoCompat)
     * ViewCompat#onInitializeAccessibilityNodeInfo(View, AccessibilityNodeInfoCompat)} for
     * the case of no accessibility delegate been set.
     * </p>
     *
     * @param host The View hosting the delegate.
     * @param info The instance to initialize.
     *
     * @see ViewCompat#onInitializeAccessibilityNodeInfo(View, AccessibilityNodeInfoCompat)
     *      ViewCompat#onInitializeAccessibilityNodeInfo(View, AccessibilityNodeInfoCompat)
     */
    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
        DEFAULT_DELEGATE.onInitializeAccessibilityNodeInfo(
                host, info.unwrap());
    }

    public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child,
            AccessibilityEvent event) {
        return DEFAULT_DELEGATE.onRequestSendAccessibilityEvent(host, child, event);
    }

    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View host) {
        if (Build.VERSION.SDK_INT >= 16) {
            Object provider = DEFAULT_DELEGATE.getAccessibilityNodeProvider(host);
            if (provider != null) {
                return new AccessibilityNodeProviderCompat(provider);
            }
        }
        return null;
    }

    /**
     * Performs the specified accessibility action on the view. For
     * possible accessibility actions look at {@link AccessibilityNodeInfoCompat}.
     * <p>
     * The default implementation behaves as
     * {@link View#performAccessibilityAction(int, Bundle)
     *  View#performAccessibilityAction(int, Bundle)} for the case of
     *  no accessibility delegate been set.
     * </p>
     *
     * @param action The action to perform.
     * @return Whether the action was performed.
     *
     * @see View#performAccessibilityAction(int, Bundle)
     *      View#performAccessibilityAction(int, Bundle)
     */
    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        if (Build.VERSION.SDK_INT >= 16) {
            return DEFAULT_DELEGATE.performAccessibilityAction(host, action, args);
        }
        return false;
    }
}
