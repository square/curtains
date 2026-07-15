package curtains.internal

import android.view.View
import com.google.common.truth.Truth.assertThat
import curtains.OnRootViewsChangedListener
import org.junit.Test
import org.mockito.Mockito.mock

/**
 * Unit tests for [RootViewsSpy.buildDelegatingViewList]. Views are Mockito identity tokens; no
 * method is called on them, so no Robolectric/Android runtime is needed. This exercises the list
 * that gets installed into `WindowManagerGlobal.mViews` without reflecting into the framework.
 */
class RootViewsSpyTest {

  private fun view(): View = mock(View::class.java)

  @Test
  fun `notifies own listeners on add and remove`() {
    val events = mutableListOf<Pair<View, Boolean>>()
    val listeners = listOf(OnRootViewsChangedListener { v, added -> events += v to added })
    val list = RootViewsSpy.buildDelegatingViewList(ArrayList(), listeners)

    val v = view()
    list.add(v)
    list.removeAt(list.indexOf(v))

    assertThat(events).containsExactly(v to true, v to false).inOrder()
  }

  @Test
  fun `plain ArrayList previous is not treated as an observer`() {
    // A plain framework ArrayList must be left untouched (no forwarding) - the normal first install.
    val previous = ArrayList<View>()
    val list = RootViewsSpy.buildDelegatingViewList(previous, listeners = emptyList())

    list.add(view())

    assertThat(previous).isEmpty()
  }

  @Test
  fun `seeds from previous without notifying`() {
    val existing = view()
    val previous = ArrayList<View>().apply { add(existing) }
    val events = mutableListOf<Pair<View, Boolean>>()
    val listeners = listOf(OnRootViewsChangedListener { v, added -> events += v to added })

    val list = RootViewsSpy.buildDelegatingViewList(previous, listeners)

    // The pre-existing view is present but must not have generated an "added" callback.
    assertThat(list).containsExactly(existing)
    assertThat(events).isEmpty()
  }

  @Test
  fun `preserves a previously installed observing list`() {
    // Simulate another library's observing mViews list already installed (a custom subclass).
    val priorEvents = mutableListOf<Pair<View, Boolean>>()
    val priorObserver = object : ArrayList<View>() {
      override fun add(element: View): Boolean {
        priorEvents += element to true
        return super.add(element)
      }

      override fun removeAt(index: Int): View {
        val removed = super.removeAt(index)
        priorEvents += removed to false
        return removed
      }
    }

    val ourEvents = mutableListOf<Pair<View, Boolean>>()
    val listeners = listOf(OnRootViewsChangedListener { v, added -> ourEvents += v to added })
    val list = RootViewsSpy.buildDelegatingViewList(priorObserver, listeners)

    val v = view()
    list.add(v)
    list.removeAt(list.indexOf(v))

    // Both the newly installed observer and the previously installed one are notified.
    assertThat(ourEvents).containsExactly(v to true, v to false).inOrder()
    assertThat(priorEvents).containsExactly(v to true, v to false).inOrder()
  }
}
