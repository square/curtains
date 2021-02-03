package curtains

enum class AttachState {
  ATTACHED {
    override val attached = true
  },
  DETACHED {
    override val attached = false
  };

  abstract val attached: Boolean

  companion object {
    fun from(attached: Boolean) = if (attached) ATTACHED else DETACHED
  }
}