package android.view

/**
 * Pass through to allow internal access to [JavaViewSpy.windowAttachCount] without
 * making that class public.
 */
@Suppress("NOTHING_TO_INLINE")
internal fun windowAttachCount(view: View) = JavaViewSpy.windowAttachCount(view)