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

import static android.os.Build.VERSION.SDK_INT;

import android.graphics.Rect;
import android.view.WindowInsets;

/**
 * Describes a set of insets for window content.
 *
 * <p>WindowInsetsCompats are immutable and may be expanded to include more inset types in the
 * future. To adjust insets, use one of the supplied clone methods to obtain a new
 * WindowInsetsCompat instance with the adjusted properties.</p>
 */
public class WindowInsetsCompat {
    private final Object mInsets;

    private WindowInsetsCompat(Object insets) {
        mInsets = insets;
    }

    /**
     * Constructs a new WindowInsetsCompat, copying all values from a source WindowInsetsCompat.
     *
     * @param src source from which values are copied
     */
    public WindowInsetsCompat(WindowInsetsCompat src) {
        if (SDK_INT >= 20) {
            mInsets = src == null ? null : new WindowInsets((WindowInsets) src.mInsets);
        } else {
            mInsets = null;
        }
    }

    /**
     * Returns the left system window inset in pixels.
     *
     * <p>The system window inset represents the area of a full-screen window that is
     * partially or fully obscured by the status bar, navigation bar, IME or other system windows.
     * </p>
     *
     * @return The left system window inset
     */
    public int getSystemWindowInsetLeft() {
        if (SDK_INT >= 20) {
            return ((WindowInsets) mInsets).getSystemWindowInsetLeft();
        } else {
            return 0;
        }
    }

    /**
     * Returns the top system window inset in pixels.
     *
     * <p>The system window inset represents the area of a full-screen window that is
     * partially or fully obscured by the status bar, navigation bar, IME or other system windows.
     * </p>
     *
     * @return The top system window inset
     */
    public int getSystemWindowInsetTop() {
        if (SDK_INT >= 20) {
            return ((WindowInsets) mInsets).getSystemWindowInsetTop();
        } else {
            return 0;
        }
    }

    /**
     * Returns the right system window inset in pixels.
     *
     * <p>The system window inset represents the area of a full-screen window that is
     * partially or fully obscured by the status bar, navigation bar, IME or other system windows.
     * </p>
     *
     * @return The right system window inset
     */
    public int getSystemWindowInsetRight() {
        if (SDK_INT >= 20) {
            return ((WindowInsets) mInsets).getSystemWindowInsetRight();
        } else {
            return 0;
        }
    }

    /**
     * Returns the bottom system window inset in pixels.
     *
     * <p>The system window inset represents the area of a full-screen window that is
     * partially or fully obscured by the status bar, navigation bar, IME or other system windows.
     * </p>
     *
     * @return The bottom system window inset
     */
    public int getSystemWindowInsetBottom() {
        if (SDK_INT >= 20) {
            return ((WindowInsets) mInsets).getSystemWindowInsetBottom();
        } else {
            return 0;
        }
    }

    /**
     * Returns true if this WindowInsets has nonzero system window insets.
     *
     * <p>The system window inset represents the area of a full-screen window that is
     * partially or fully obscured by the status bar, navigation bar, IME or other system windows.
     * </p>
     *
     * @return true if any of the system window inset values are nonzero
     */
    public boolean hasSystemWindowInsets() {
        if (SDK_INT >= 20) {
            return ((WindowInsets) mInsets).hasSystemWindowInsets();
        } else {
            return false;
        }
    }

    /**
     * Returns true if this WindowInsets has any nonzero insets.
     *
     * @return true if any inset values are nonzero
     */
    public boolean hasInsets() {
        if (SDK_INT >= 20) {
            return ((WindowInsets) mInsets).hasInsets();
        } else {
            return false;
        }
    }

    /**
     * Check if these insets have been fully consumed.
     *
     * <p>Insets are considered "consumed" if the applicable <code>consume*</code> methods
     * have been called such that all insets have been set to zero. This affects propagation of
     * insets through the view hierarchy; insets that have not been fully consumed will continue
     * to propagate down to child views.</p>
     *
     * <p>The result of this method is equivalent to the return value of
     *
     * @return true if the insets have been fully consumed.
     */
    public boolean isConsumed() {
        if (SDK_INT >= 21) {
            return ((WindowInsets) mInsets).isConsumed();
        } else {
            return false;
        }
    }

    /**
     * Returns true if the associated window has a round shape.
     *
     * <p>A round window's left, top, right and bottom edges reach all the way to the
     * associated edges of the window but the corners may not be visible. Views responding
     * to round insets should take care to not lay out critical elements within the corners
     * where they may not be accessible.</p>
     *
     * @return True if the window is round
     */
    public boolean isRound() {
        if (SDK_INT >= 20) {
            return ((WindowInsets) mInsets).isRound();
        } else {
            return false;
        }
    }

    /**
     * Returns a copy of this WindowInsets with the system window insets fully consumed.
     *
     * @return A modified copy of this WindowInsets
     */
    public WindowInsetsCompat consumeSystemWindowInsets() {
        if (SDK_INT >= 20) {
            return new WindowInsetsCompat(((WindowInsets) mInsets).consumeSystemWindowInsets());
        } else {
            return null;
        }
    }

    /**
     * Returns a copy of this WindowInsets with selected system window insets replaced
     * with new values.
     *
     * @param left New left inset in pixels
     * @param top New top inset in pixels
     * @param right New right inset in pixels
     * @param bottom New bottom inset in pixels
     * @return A modified copy of this WindowInsets
     */
    public WindowInsetsCompat replaceSystemWindowInsets(int left, int top, int right, int bottom) {
        if (SDK_INT >= 20) {
            return new WindowInsetsCompat(
                    ((WindowInsets) mInsets).replaceSystemWindowInsets(left, top, right, bottom));
        } else {
            return null;
        }
    }

    /**
     * Returns a copy of this WindowInsets with selected system window insets replaced
     * with new values.
     *
     * @param systemWindowInsets New system window insets. Each field is the inset in pixels
     *                           for that edge
     * @return A modified copy of this WindowInsets
     */
    public WindowInsetsCompat replaceSystemWindowInsets(Rect systemWindowInsets) {
        if (SDK_INT >= 21) {
            return new WindowInsetsCompat(
                    ((WindowInsets) mInsets).replaceSystemWindowInsets(systemWindowInsets));
        } else {
            return null;
        }
    }

    /**
     * Returns the top stable inset in pixels.
     *
     * <p>The stable inset represents the area of a full-screen window that <b>may</b> be
     * partially or fully obscured by the system UI elements.  This value does not change
     * based on the visibility state of those elements; for example, if the status bar is
     * normally shown, but temporarily hidden, the stable inset will still provide the inset
     * associated with the status bar being shown.</p>
     *
     * @return The top stable inset
     */
    public int getStableInsetTop() {
        if (SDK_INT >= 21) {
            return ((WindowInsets) mInsets).getStableInsetTop();
        } else {
            return 0;
        }
    }

    /**
     * Returns the left stable inset in pixels.
     *
     * <p>The stable inset represents the area of a full-screen window that <b>may</b> be
     * partially or fully obscured by the system UI elements.  This value does not change
     * based on the visibility state of those elements; for example, if the status bar is
     * normally shown, but temporarily hidden, the stable inset will still provide the inset
     * associated with the status bar being shown.</p>
     *
     * @return The left stable inset
     */
    public int getStableInsetLeft() {
        if (SDK_INT >= 21) {
            return ((WindowInsets) mInsets).getStableInsetLeft();
        } else {
            return 0;
        }
    }

    /**
     * Returns the right stable inset in pixels.
     *
     * <p>The stable inset represents the area of a full-screen window that <b>may</b> be
     * partially or fully obscured by the system UI elements.  This value does not change
     * based on the visibility state of those elements; for example, if the status bar is
     * normally shown, but temporarily hidden, the stable inset will still provide the inset
     * associated with the status bar being shown.</p>
     *
     * @return The right stable inset
     */
    public int getStableInsetRight() {
        if (SDK_INT >= 21) {
            return ((WindowInsets) mInsets).getStableInsetRight();
        } else {
            return 0;
        }
    }


    /**
     * Returns the bottom stable inset in pixels.
     *
     * <p>The stable inset represents the area of a full-screen window that <b>may</b> be
     * partially or fully obscured by the system UI elements.  This value does not change
     * based on the visibility state of those elements; for example, if the status bar is
     * normally shown, but temporarily hidden, the stable inset will still provide the inset
     * associated with the status bar being shown.</p>
     *
     * @return The bottom stable inset
     */
    public int getStableInsetBottom() {
        if (SDK_INT >= 21) {
            return ((WindowInsets) mInsets).getStableInsetBottom();
        } else {
            return 0;
        }
    }

    /**
     * Returns true if this WindowInsets has nonzero stable insets.
     *
     * <p>The stable inset represents the area of a full-screen window that <b>may</b> be
     * partially or fully obscured by the system UI elements.  This value does not change
     * based on the visibility state of those elements; for example, if the status bar is
     * normally shown, but temporarily hidden, the stable inset will still provide the inset
     * associated with the status bar being shown.</p>
     *
     * @return true if any of the stable inset values are nonzero
     */
    public boolean hasStableInsets() {
        if (SDK_INT >= 21) {
            return ((WindowInsets) mInsets).hasStableInsets();
        } else {
            return false;
        }
    }

    /**
     * Returns a copy of this WindowInsets with the stable insets fully consumed.
     *
     * @return A modified copy of this WindowInsetsCompat
     */
    public WindowInsetsCompat consumeStableInsets() {
        if (SDK_INT >= 21) {
            return new WindowInsetsCompat(((WindowInsets) mInsets).consumeStableInsets());
        } else {
            return null;
        }
    }

    /*@Nullable
    public DisplayCutoutCompat getDisplayCutout() {
        if (SDK_INT >= 28) {
            return DisplayCutoutCompat.wrap(((WindowInsets) mInsets).getDisplayCutout());
        } else {` `
            return null;
        }
    }*/

    /**
     * Returns a copy of this WindowInsets with the cutout fully consumed.
     *
     * @return A modified copy of this WindowInsets
     */
    public WindowInsetsCompat consumeDisplayCutout() {
        if (SDK_INT >= 28) {
            return new WindowInsetsCompat(((WindowInsets) mInsets).consumeDisplayCutout());
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WindowInsetsCompat other = (WindowInsetsCompat) o;
        return mInsets == null ? other.mInsets == null : mInsets.equals(other.mInsets);
    }

    @Override
    public int hashCode() {
        return mInsets == null ? 0 : mInsets.hashCode();
    }

    static WindowInsetsCompat wrap(Object insets) {
        return insets == null ? null : new WindowInsetsCompat(insets);
    }

    static Object unwrap(WindowInsetsCompat insets) {
        return insets == null ? null : insets.mInsets;
    }
}
