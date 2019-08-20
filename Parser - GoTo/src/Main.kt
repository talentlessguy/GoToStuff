import StringE.*
import java.util.function.Predicate

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

class IdParser<T> : Parser<T>() {
    override fun parse(string: StringE<T>): StringE<T> = string
}

class ParseChar<T> : Parser<T>() {
    override fun parse(input: StringE<T>): StringE<T> {
        return when (input) {
            is Str -> return if (input.value.isNotEmpty()) {
                return if (!input.value[0].isDigit()) {
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

class ParseSkipChar<T>(val pred: Predicate<Char>) : Parser<T>() {
    override fun parse(string: StringE<T>): StringE<T> {
        return when (string) {
            is Str -> if (string.value.isNotEmpty()) {
                if (pred.test(string.value[0])) {
                    Str(("" + string.value).substring(1), string.struct)
                } else {
                    Error(string.value + "\nparse failed")
                }
            } else {
                Error(string.value + "\nempty")
            }
            is Error -> Error(string.value)
        }
    }
}


class ParseAddChar(val pred: Predicate<Char>) : Parser<String>() {
    override fun parse(string: StringE<String>): StringE<String> {
        return when (string) {
            is Str -> if (string.value.isNotEmpty()) {
                val c = string.value[0]
                if (pred.test(c)) {
                    Str(("" + string.value).substring(1), string.struct + c)
                } else {
                    Error(string.value + "\nparse failed")
                }
            } else {
                Error(string.value + "\nempty")
            }
            is Error -> Error(string.value)
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
        val r = p1.parse(string)
        return when (r) {
            is Str -> r
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

class XML (val name: String, val children: List<XML>)

class XMLOutput (val name: String, val children: String) {
    override fun toString(): String {
        var items = mutableListOf<String>()
        items.add("$name -> XML")
        items.add(children)
        return items.joinToString(separator = ",\n")
    }
}

fun substringAfter(str: String, delimiter: Char): String {
    return if (str.contains(delimiter)) {
        str.substringAfterLast(delimiter)
    } else ""
}

fun structurize(xml: XML, id: Int) : List<XMLOutput> {
    val children = xml.children
    // var id = substringAfter(xml.name, '#').toInt()
    if (children.isEmpty()) {
        return listOf(XMLOutput("${xml.name}#${id}", "XML"))
    } else {
        var out = mutableListOf<XMLOutput>()
        var childrenString = mutableListOf<String>()

        for (child in children) {
            var childrenOut: List<XMLOutput> = structurize(child, id + 1)
            for (c in childrenOut) {
                out.add(c)
                childrenString.add(c.name)
            }
        }
        var joined = childrenString.joinToString(separator = ", ")
        var current = XMLOutput("${xml.name}#$id", "($joined) -> XML")

        out.add(current)

        return out
    }
}

class Combiner<T>(
    val combiner: (Parser<T>, Parser<T>) -> Parser<T>,
    val neutral: Parser<T>,
    vararg val parsers: Parser<T>
) : Parser<T>() {
    override fun parse(string: StringE<T>): StringE<T> {
        return parsers.toList().foldRight(neutral, combiner).parse(string)
    }
}

class OpenTagParser : Parser<String>() {
    override fun parse(string: StringE<String>): StringE<String> {
        return Combiner(
            (::Compose), IdParser(),
            ParseSkipChar(Predicate { x -> x == '<' }),
            Repeat(ParseAddChar(Predicate { x -> x != '<' && x != '>' && x != '/' })),
            ParseSkipChar(Predicate { x -> x == '>' })
        ).parse(string)
    }
}

class CloseTagParser : Parser<String>() {
    override fun parse(string: StringE<String>): StringE<String> {
        return Combiner(
            (::Compose), IdParser(),
            ParseSkipChar(Predicate { x -> x == '<' }),
            ParseSkipChar(Predicate { x -> x == '/' }),
            Repeat(ParseAddChar(Predicate { x -> x != '<' && x != '>' && x != '/' })),
            ParseSkipChar(Predicate { x -> x == '>' })
        ).parse(string)
    }
}

class ParseXML : Parser<String>() {
    override fun parse(string: StringE<String>): StringE<String> {
        return Repeat(Compose(OpenTagParser(), CloseTagParser())).parse(string)
    }
}

fun main() {
    // Input: xml("aa", listOf(xml("bb", listOf())))
    /*
        Output:
        ("bb#1","XML")
        ("aa#1", "(bb#1) -> XML")
    */

    val p = ParseXML()

    val res = p.parse(Str("<aa></aa><c></c>", ""))

    when (res) {
        is Str -> println(res.struct)
    }
}