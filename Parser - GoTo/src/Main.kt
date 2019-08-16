import StringE.*

sealed class StringE<T> {
    class Str<T>(val value: String, val struct: T) : StringE<T>()
    class Error<T>(val value: String) : StringE<T>()

    override fun toString(): String {
        return when (this) {
            is Str -> value
            is Error -> value
        }
    }
}

abstract class Parser<T> {
    abstract fun parse(string: StringE<T>): StringE<T>
}

class ParseChar<T>() : Parser<T>() {
    override fun parse(input: StringE<T>): StringE<T> {
        return when (input) {
            is Str -> if (input.value.isNotEmpty()) {
                if (!input.value[0].isDigit()) {
                    Str(("" + input.value).substring(1), input.struct)
                } else {
                    Error(input.value + "\nparse failed")
                }
            } else {
                Error(input.value + "\nempty")
            }
            is Error -> Error(input.value)
        }
    }
}

class ParseCustomChar<T>(val char: Char) : Parser<T>() {
    override fun parse(input: StringE<T>): StringE<T> {
        return when (input) {
            is Str -> if (input.value.isNotEmpty()) {
                if (input.value[0] == char) {
                    Str(("" + input.value).substring(1), input.struct)
                } else {
                    Error(input.value + "\nparse failed")
                }
            } else {
                Error(input.value + "\nempty")
            }
            is Error -> Error(input.value)
        }
    }
}

class Compose<T>(val p1: Parser<T>, val p2: Parser<T>) : Parser<T>() {
    override fun parse(string: StringE<T>): StringE<T> {
        return p2.parse(p1.parse(string))
    }
}

class Alternative<T>(val p1: Parser<T>, val p2: Parser<T>) : Parser<T>() {
    override fun parse(string: StringE<T>): StringE<T> {
        val r1 = p1.parse(string)
        return when (r1) {
            is Str -> r1
            is Error -> p2.parse(string)
        }
    }
}

class Repeat<T>(val p: Parser<T>) : Parser<T>() {
    override fun parse(string: StringE<T>): StringE<T> {
        val r = p.parse(string)
        return when (r) {
            is Str -> parse(r)
            is Error -> string
        }
    }
}

class Optional<T>(val p: Parser<T>) : Parser<T>() {
    override fun parse(string: StringE<T>): StringE<T> {
        val r = p.parse(string)

        return when (r) {
            is Str -> r
            is Error -> string
        }
    }
}

class ParseDigit: Parser<Int>() {
    override fun parse(input: StringE<Int>): StringE<Int> {
        return when (input) {
            is Str -> if (input.value.isNotEmpty()) {
                if (input.value[0].isDigit()) {
                    Str(("" + input.value).substring(1), input.struct * 10 + (input.value[0].toInt() - '0'.toInt()))
                } else {
                    Error(input.value + "\nparse failed")
                }
            } else {
                Error(input.value + "\nempty")
            }
            is Error -> Error(input.value)
        }
    }
}

class ParseInt: Parser<Int>() {
    override fun parse(string: StringE<Int>): StringE<Int> {
        val digitParser = ParseDigit()
        return Repeat(digitParser).parse(string)
    }
}

class ParseString: Parser<String>() {
    override fun parse(string: StringE<String>): StringE<String> {
        return Repeat(ParseChar<String>()).parse(string)
    }
}

/*
class ParseFloat: Parser<Float>() {
    override fun parse(string: StringE<Float>): StringE<Float> {
        val intParser = ParseInt()
        val dotParser = ParseCustomChar<Float>('.')

        val parser : Compose<Float> = Compose<Float>(Compose<Float>(intParser, dotParser), intParser)

        return Repeat(parser).parse(string)
    }
}
*/

class ParseXMLLight: Parser<String>() {
    override fun parse(string: StringE<String>): StringE<String> {
        val leftParser = ParseCustomChar<String>('<')
        val rightParser = ParseCustomChar<String>('>')
        val strParser = ParseString()
        val parser = Alternative(Alternative(leftParser, strParser), rightParser)

        return Repeat(parser).parse(string)
    }
}

fun main() {
    val x = ParseXMLLight()

    val xml = x.parse(Str("<>b</>", ""))

    when (xml) {
        is Str -> println(xml)
    }
}