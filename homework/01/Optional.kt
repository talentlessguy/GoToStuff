class Optional<T>(val p: Parser<T>) : Parser<T>() {
  override fun parse(string: StringE<T>): StringE<T> {
      val r = p.parse(string)

      return when (r) {
          is Str -> r
          is Error -> string
      }
  }
}