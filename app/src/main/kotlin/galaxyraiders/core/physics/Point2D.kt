@file:Suppress("UNUSED_PARAMETER") // <- REMOVE
package galaxyraiders.core.physics

data class Point2D(val x: Double, val y: Double) {
  operator fun plus(p: Point2D): Point2D {
    return Point2D(x + p.x, y + p.y)
  }

  operator fun plus(v: Vector2D): Point2D {
    return Point2D(v.dx + x, v.dy + y)
  }

  override fun toString(): String {
    return "Point2D(x=$x, y=$y)"
  }

  fun toVector(): Vector2D {
    return Vector2D(x, y)
  }

  fun impactVector(p: Point2D): Vector2D {
    return Vector2D(p.x - x, p.y - y)
  }

  fun impactDirection(p: Point2D): Vector2D {
    return Vector2D(p.x - x, p.y - y).unit
  }

  fun contactVector(p: Point2D): Vector2D {
    return impactVector(p).normal
  }

  fun contactDirection(p: Point2D): Vector2D {
    return contactVector(p).unit
  }

  fun distance(p: Point2D): Double {
    return Math.sqrt(((x - p.x) * (x - p.x)) + ((y - p.y) * (y - p.y)))
  }
}
