package galaxyraiders.core.game

import galaxyraiders.core.physics.Point2D
import galaxyraiders.core.physics.Vector2D

class Explosion(initialPosition: Point2D, radius: Double) :
  SpaceObject("Explosion", 'E', initialPosition, Vector2D(0.0, 0.0), radius, 0.0) {
  val explosionMaxTickDuration: Int = 10
  var currentTickDuration: Int = 1
  var canBeDestroyed: Boolean = false

  fun increaseTickDuration() {
    currentTickDuration++

    if (currentTickDuration >= explosionMaxTickDuration)
      canBeDestroyed = true
  }

  fun completedLifeSpan(): Boolean {
    return canBeDestroyed
  }

  fun getMaxTickDuration(): Int {
    return explosionMaxTickDuration
  }
}
