@file:Suppress("UNUSED_PARAMETER") // <- REMOVE
package galaxyraiders.core.physics

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Suppress("TooManyFunctions", "MagicNumber")
@JsonIgnoreProperties("unit", "normal", "degree", "magnitude")
data class Vector2D(val dx: Double, val dy: Double) {
  override fun toString(): String {
    return "Vector2D(dx=$dx, dy=$dy)"
  }

  val magnitude: Double
    get() = Math.sqrt((dx * dx) + (dy * dy))

  val radiant: Double
    get() = Math.atan2(dy, dx)

  val degree: Double
    get() = (180 * radiant) / Math.PI

  val unit: Vector2D
    get() = this.div(magnitude)

  val normal: Vector2D
    get() {
      val x: Double = -(this.dy / this.dx)
      val resultV: Vector2D = Vector2D(x / Math.sqrt((x * x) + 1), 1 / Math.sqrt((x * x) + 1))
      if (this.isInFirstQuadrant() || this.isInFourthQuadrant()) return resultV.unaryMinus()
      else return resultV
    }

  fun isInFirstQuadrant(): Boolean {
    return this.dx > 0 && this.dy > 0
  }

  fun isInFourthQuadrant(): Boolean {
    return this.dx > 0 && this.dy < 0
  }

  operator fun times(scalar: Double): Vector2D {
    return Vector2D(dx * scalar, dy * scalar)
  }

  operator fun div(scalar: Double): Vector2D {
    return Vector2D(dx / scalar, dy / scalar)
  }

  operator fun times(v: Vector2D): Double {
    return (dx * v.dx) + (dy * v.dy)
  }

  operator fun plus(v: Vector2D): Vector2D {
    return Vector2D(dx + v.dx, dy + v.dy)
  }

  operator fun plus(p: Point2D): Point2D {
    return Point2D(p.x + dx, p.y + dy)
  }

  operator fun unaryMinus(): Vector2D {
    return Vector2D(dx * -1, dy * -1)
  }

  operator fun minus(v: Vector2D): Vector2D {
    return Vector2D(dx - v.dx, dy - v.dy)
  }

  fun scalarProject(target: Vector2D): Double {
    return this.times(target.unit)
  }

  fun vectorProject(target: Vector2D): Vector2D {
    return this.scalarProject(target).times(target.unit)
  }
}

operator fun Double.times(v: Vector2D): Vector2D {
  return v.times(this)
}
