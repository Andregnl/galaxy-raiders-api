package galaxyraiders.core.game

import java.time.LocalDateTime

data class MatchInfo(var startTime: String,
										 var finalScore: Int,
										 var destroyedAsteroids: Int): Comparable<MatchInfo> {

	override fun compareTo(other: MatchInfo): Int {
		if (this.finalScore > other.finalScore) return 1
		else if (this.finalScore < other.finalScore) return -1
		else return 0
	}
}
