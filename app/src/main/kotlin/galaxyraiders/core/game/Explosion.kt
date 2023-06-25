package galaxyraiders.core.game

import galaxyraiders.core.physics.Point2D
import galaxyraiders.core.physics.Vector2D

const val EXPLOSION_MAX_TICK_DURATION: Int = 10

class Explosion(initialPosition: Point2D, radius: Double) :
  SpaceObject("Explosion", 'E', initialPosition, Vector2D(0.0, 0.0), radius, 0.0) {
  var currentTickDuration: Int = 1
  var canBeDestroyed: Boolean = false

  fun increaseTickDuration() {
    currentTickDuration++

    if (currentTickDuration >= EXPLOSION_MAX_TICK_DURATION)
      canBeDestroyed = true
  }

  fun completedLifeSpan(): Boolean {
    return canBeDestroyed
  }

  fun getMaxTickDuration(): Int {
    return EXPLOSION_MAX_TICK_DURATION
  }
}
