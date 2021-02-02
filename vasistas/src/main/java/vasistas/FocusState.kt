package vasistas

enum class FocusState {
  FOCUSED {
    override val focused = true
  },
  NOT_FOCUSED {
    override val focused = false
  };

  abstract val focused: Boolean

  companion object {
    fun from(hasFocus: Boolean) = if (hasFocus) FOCUSED else NOT_FOCUSED
  }
}