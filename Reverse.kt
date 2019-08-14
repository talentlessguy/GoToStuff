class Reverse {
  public static fun main() {
    var list = mutableListOf(1, 2, 3)
    var list2: MutableList<Int> = mutableListOf()
    
    for (i in 0..2) {
       if (i == list.size - 1) {
           list2.add(list[0])
       } else {
           list2.add(list[list.size - i - 1])
       }
    }
    println(list2)
  }
}