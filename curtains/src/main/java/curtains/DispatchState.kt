package curtains

/**
 * Returned by [TouchEventInterceptor.intercept] to indicate whether the motion
 * event has been consumed.
 *
 * You may return [Consumed] from [TouchEventInterceptor.intercept] instead of
 * calling the provided dispatch lambda.
 *
 * [NotConsumed] is exposed as a type so that you can check the returned result,
 * however its constructor is not exposed as you shouldn't be creating an
 * instance directly.
 */
sealed class DispatchState {

  /**
   * The event was consumed during dispatch.
   */
  object Consumed : DispatchState()

  /**
   * The event was not consumed after being dispatched all the way.
   */
  class NotConsumed internal constructor() : DispatchState()

  internal companion object {
    private val NotConsumedInternalOnly = NotConsumed()
    internal fun from(consumed: Boolean) = if (consumed) Consumed else NotConsumedInternalOnly
  }
}
