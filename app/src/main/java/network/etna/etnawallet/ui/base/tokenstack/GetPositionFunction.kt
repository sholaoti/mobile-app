package network.etna.etnawallet.ui.base.tokenstack

/***
 * get new Y position by index and Original Y
 * returns y position and should animate or not
 */
typealias GetPositionFunction = (Int, Float) -> Pair<Float, Boolean>

sealed class GetPositionFunctionVariant(val function: GetPositionFunction) {
    object OriginalAnimate : GetPositionFunctionVariant(
        { _, originalY ->
            Pair(originalY, true)
        }
    )

    object OriginalNoAnimate : GetPositionFunctionVariant(
        { _, originalY ->
            Pair(originalY, false)
        }
    )

    class Collapsing(val scrollY: Int) : GetPositionFunctionVariant(
        { _, originalY ->
            if (originalY < scrollY) {
                Pair(scrollY.toFloat(), false)
            } else {
                Pair(originalY, false)
            }
        }
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Collapsing

            if (scrollY != other.scrollY) return false

            return true
        }

        override fun hashCode(): Int {
            return scrollY
        }
    }

    class Bouncing(val capturedVerticalNestedPreScrollChangeY: Float) : GetPositionFunctionVariant(
        { index, originalY ->
            var changeY = capturedVerticalNestedPreScrollChangeY / 5 * index

            if (changeY < 0) {
                changeY /= 5
            }

            val newY = originalY + changeY

            Pair(newY, false)
        }
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Bouncing

            if (capturedVerticalNestedPreScrollChangeY != other.capturedVerticalNestedPreScrollChangeY) return false

            return true
        }

        override fun hashCode(): Int {
            return capturedVerticalNestedPreScrollChangeY.hashCode()
        }
    }
}