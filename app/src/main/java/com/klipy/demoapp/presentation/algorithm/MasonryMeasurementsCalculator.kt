package com.klipy.demoapp.presentation.algorithm

import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.domain.models.isAD
import com.klipy.demoapp.presentation.features.conversation.model.MediaItemUIModel
import kotlin.math.abs
import kotlin.math.min

typealias MediaItemRow = List<MediaItemUIModel>

object MasonryMeasurementsCalculator {
    private const val ITEM_MIN_HEIGHT = 50
    private const val ITEM_MAX_HEIGHT = 180
    private const val MAX_ITEMS_PER_ROW = 3

    private var calculatedItems = mutableListOf<MediaItem>()
    private var calculatedResults = mutableListOf<MediaItemRow>()

    /**
     * Splits a list of items into rows based on a maximum number of items per row.
     * The actual items per row may be adjusted by `precalculateSingleRow()`.
     *
     * @param {Array} items - The full list of items to arrange in rows.
     * @return {Array} rows - A list of rows, where each row is an array of items.
     */
    fun createRows(
        items: List<MediaItem>,
        containerWidth: Int,
        gap: Int
    ): List<MediaItemRow> {
        val isNewList = isNewList(calculatedItems, items)
        if (isNewList) {
            // If list is completely new, we calculate everything from scratch
            calculatedResults = calculateRows(items, containerWidth, gap).toMutableList()
        } else if (items.size != calculatedItems.size) {
            // If items were added to the list, we take last row of previous list + new list
            // This is needed because last row of previous list may have changed after adding new items
            val lastCalculatedRow = calculatedResults.last()
            calculatedResults.remove(lastCalculatedRow)
            val itemsToCalculate = items.subList(
                calculatedResults.flatten().size,
                items.size
            )
            val newRows = calculateRows(itemsToCalculate, containerWidth, gap)
            calculatedResults = (calculatedResults + newRows).toMutableList()
        }
        calculatedItems = items.toMutableList()
        return calculatedResults
    }

    private fun calculateRows(
        items: List<MediaItem>,
        containerWidth: Int,
        gap: Int
    ): List<MediaItemRow> {
        val rows = mutableListOf<MediaItemRow>()
        var currentIndex = 0

        while (currentIndex < items.size) {
            val possibleItemsInRow = items.subList(
                currentIndex,
                min(currentIndex + MAX_ITEMS_PER_ROW, items.size)
            )

            val adjustedRow = precalculateSingleRow(possibleItemsInRow, containerWidth, gap)

            rows.add(adjustedRow)
            currentIndex += adjustedRow.size
        }

        return rows
    }

    private fun precalculateSingleRow(
        items: List<MediaItem>,
        containerWidth: Int,
        gap: Int
    ): MediaItemRow {
        var possibleItemsInRow = items
        var minimumChange = Int.MAX_VALUE
        var currentRow = mutableListOf<MediaItemUIModel>()
        var itemsHeightInRow = 0

        var currentMinHeight = ITEM_MIN_HEIGHT
        var currentMaxHeight = ITEM_MAX_HEIGHT

        val adIndex = possibleItemsInRow.indexOfFirst { it.isAD() }
        val adsInRow = possibleItemsInRow.filter { it.isAD() }
        if (adIndex > 1) {
            possibleItemsInRow = possibleItemsInRow.subList(0, 2)
        } else if (adIndex >= 0) {
            currentMinHeight = possibleItemsInRow[adIndex].lowQualityMetaData!!.height
            currentMaxHeight = possibleItemsInRow[adIndex].lowQualityMetaData!!.height
        }

        for (height in currentMinHeight..currentMaxHeight) {
            val itemsInRow = mutableListOf<MediaItemUIModel>()
            for (element in possibleItemsInRow) {
                val item = element.copy()
                itemsInRow.add(MediaItemUIModel(item, 0, 0))
                val mediaHeight = item.lowQualityMetaData!!.height
                val mediaWidth = item.lowQualityMetaData.width

                val newWidth = if (item.isAD()) {
                    item.lowQualityMetaData.width
                } else {
                    Math.round((mediaWidth.toFloat() * height) / mediaHeight)
                }
                itemsInRow[itemsInRow.size - 1] =
                    itemsInRow.last().copy(measuredWidth = newWidth)
                val totalWidth =
                    itemsInRow.sumOf { it.measuredWidth } + (itemsInRow.size - 1) * gap
                val change = containerWidth - totalWidth

                if (abs(change) < abs(minimumChange) || (currentRow.size == 1 && itemsInRow.size != 1)) {
                    minimumChange = change
                    currentRow = itemsInRow.toMutableList()
                    itemsHeightInRow = height
                }
            }
        }

        currentRow.forEachIndexed { index, item ->
            val adNumber = if (adsInRow.isNotEmpty()) 1 else 0
            val addition = if (item.mediaItem.isAD()) {
                0
            } else {
                minimumChange / (currentRow.size - adNumber)
            }
            currentRow[index] = item.copy(
                measuredWidth = item.measuredWidth + addition,
                measuredHeight = itemsHeightInRow
            )
        }

        return currentRow
    }

    private fun isNewList(existingItems: List<MediaItem>, newItems: List<MediaItem>): Boolean {
        return existingItems.isEmpty() || newItems.size < existingItems.size
                || existingItems.map { it.id } != newItems.map { it.id }.take(existingItems.size)
    }

}