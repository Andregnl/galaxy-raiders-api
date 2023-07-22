package galaxyraiders.adapters.web

import galaxyraiders.core.game.MatchInfo
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.Context

class LeaderboardRouter : Router {
  data class Leaderboard(
    val matches: List<MatchInfo>
  )

  var l: Leaderboard? = null
    private set

  override val path = "/leaderboard"

  override val endpoints = EndpointGroup {
    get("/", ::postMatchInfo)
  }

  private fun postMatchInfo(ctx: Context) {
    ctx.json(this.getMatchInfo() ?: "{}")
  }

  fun getMatchInfo(): Leaderboard {
    val arrList: ArrayList<MatchInfo> = ArrayList<MatchInfo>()
    val matchInfo = MatchInfo("", 10, 10)
    arrList.add(matchInfo)
    val l = Leaderboard(arrList.toList())
    return l
  }
}
