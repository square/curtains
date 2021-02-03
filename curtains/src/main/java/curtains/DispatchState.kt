package curtains

enum class DispatchState {
  CONSUMED {
    override val consumed = true
  },
  NOT_CONSUMED {
    override val consumed = false
  };

  abstract val consumed: Boolean

  companion object {
    fun from(consumed: Boolean) = if (consumed) CONSUMED else NOT_CONSUMED
  }
}
