package com.example.closetscore.coroutines

import android.util.Log
import com.example.closetscore.data.Item
import com.example.closetscore.db.BrandType
import com.example.closetscore.db.ItemStatus
import com.example.closetscore.db.MaterialType
import java.time.LocalDate

data class GraphPoint(
    val date: LocalDate,
    val price: Double,
    val label: String
)

data class WearBucket(
    val label: String,
    val count: Int
)

fun calculateThriftAvg(items: List<Item>): Double {
    if (items.isEmpty()) { return 0.0 }
    var amount = 0
    items.forEach { item ->
        if (item.isSecondHand) {
            amount++
        }
    }
    return amount / items.size.toDouble() * 100
}

fun calculatePrice(items: List<Item>): List<GraphPoint> {
    val sortedItems = items.filter { it.status == ItemStatus.ACTIVE }.sortedBy { it.dateAcquired }
    val groupedByMonth = sortedItems.groupBy {
        it.dateAcquired.withDayOfMonth(1)
    }
    val points = mutableListOf<GraphPoint>()
    val months = groupedByMonth.keys.sorted()
    var currentTotalValue = 0.0
    for (monthDate in months) {
        val itemsInMonth = groupedByMonth[monthDate] ?: emptyList()
        val monthlySum = itemsInMonth.sumOf { it.price }

        currentTotalValue += monthlySum

        points.add(
            GraphPoint(
                date = monthDate,
                price = currentTotalValue,
                label = monthDate.month.name.take(3)
            )
        )
    }
    return points
}

fun calculateWearFrequency(items: List<Item>): List<WearBucket> {
    val activeItems = items.filter { it.status == ItemStatus.ACTIVE }

    return listOf(
        WearBucket("0", activeItems.count { it.wearCount == 0 }),
        WearBucket("1-5", activeItems.count { it.wearCount in 1..5 }),
        WearBucket("6-15", activeItems.count { it.wearCount in 6..15 }),
        WearBucket("16-30", activeItems.count { it.wearCount in 16..30 }),
        WearBucket("30+", activeItems.count { it.wearCount > 30 })
    )
}

fun calculateLogic(items: List<Item>): Int {
    var score = 0
    if (items.isEmpty()) {
        return 0
    }
    items.forEach { item ->
        score += calculateValue(item)
    }
    val average = score / items.size
    return average.coerceIn(0, 100)
}

fun calculateValue(item: Item): Int {
    var score = 50
    if (item.isSecondHand) {
        score += 20
    }

    when (item.brandType) {
        BrandType.FAST_FASHION -> score -= 20
        BrandType.ECO_SUSTAINABLE -> score += 15
        BrandType.STANDARD -> score += 0
    }

    when (item.material) {
        MaterialType.NATURAL -> score += 10
        MaterialType.SYNTHETIC -> score -= 10
        MaterialType.MIXED -> score -= 5
    }

    score += item.wearCount / 3

    when (item.status) {
        ItemStatus.TRASHED -> {
            if (item.wearCount > 50) {
                score += 0
            } else if (item.wearCount > 30) {
                score -= 10
            } else {
                score -= 50
            }
        }
        ItemStatus.SOLD, ItemStatus.DONATED -> score += 10
        ItemStatus.ACTIVE -> score += 0
        ItemStatus.LOST -> score -= 1
    }
    return score
}