package com.example.catsanddogs

fun main() {
    val data = listOf(2,7,11,15)
    println(twoSum(data, 22))
}

fun twoSum(data: List<Int>, target: Int): List<Int> {
    val list = mutableListOf<Int>()

    for (index in 0..data.lastIndex) {
        for (index1 in (index + 1)..data.lastIndex) {
            if (data[index] + data[index1] == target) {
                list.add(index)
                list.add(index1)
            }
        }
    }

    return list
}


