package io.github.debop.ahocorasick.interval

/**
 * [Intervalable]의 size의 올림차순으로 정렬하도록 합니다.
 */
class IntervalableComparatorBySize: Comparator<Intervalable> {

    override fun compare(o1: Intervalable, o2: Intervalable): Int {
        var comparison = o1.size - o2.size
        if (comparison == 0) {
            comparison = o1.start - o2.start
        }
        return comparison
    }
}

/**
 * [Intervalable]의 size의 내림차순으로 정렬하도록 합니다.
 */
class IntervalableComparatorBySizeReverse: Comparator<Intervalable> {
    override fun compare(o1: Intervalable, o2: Intervalable): Int {
        var comparison = o2.size - o1.size
        if (comparison == 0) {
            comparison = o1.start - o2.start
        }
        return comparison
    }
}

class IntervalableComparatorByPosition: Comparator<Intervalable> {
    override fun compare(o1: Intervalable, o2: Intervalable): Int {
        return o1.start - o2.start
    }
}