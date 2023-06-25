package galaxyraiders.helpers

import com.fasterxml.jackson.module.kotlin.readValue
import galaxyraiders.core.game.GameEngine
import galaxyraiders.core.game.MatchInfo
import galaxyraiders.ports.RandomGenerator
import galaxyraiders.ports.ui.Controller
import galaxyraiders.ports.ui.Visualizer
import java.io.File

class GameEngineScoreMan(
  val gen: RandomGenerator,
  val con: Controller,
  val vis: Visualizer
) : GameEngine(gen, con, vis) {

  public fun setFinalScore(score: Int) {
    this.scoredPoints = score
  }

  public fun setAsteroidsDestroyed(destroyedAsteroids: Int) {
    this.destroyedAsteroids = destroyedAsteroids
  }

  public fun triggerGameEngineBeginGame() {
    this.setStartTime()
    val (scoreboardFile, leaderboardFile) = getScoreAndLeaderboardFile()

    getInitialSaveData(leaderboardFile, scoreboardFile)
  }

  public fun triggerGameEngineEndGame() {
    this.saveData()
  }

  public fun deleteTestBoardFiles() {
    val (scoreboardFile, leaderboardFile) = getScoreAndLeaderboardFile()

    if (scoreboardFile.exists()) scoreboardFile.delete()
    if (leaderboardFile.exists()) leaderboardFile.delete()
  }

  public fun getScoreAndLeaderboardObjs(): Pair<ArrayList<MatchInfo>, ArrayList<MatchInfo>> {
    val (scoreboardFile, leaderboardFile) = getScoreAndLeaderboardFile()

    var leaderboardObj: ArrayList<MatchInfo> = ArrayList<MatchInfo>()
    var scoreboardObj: ArrayList<MatchInfo> = ArrayList<MatchInfo>()

    val leaderboardText = leaderboardFile.readText(Charsets.UTF_8)
    if (leaderboardText != "")
      leaderboardObj = mapper.readValue<ArrayList<MatchInfo>>(leaderboardText)

    val scoreboardText = scoreboardFile.readText(Charsets.UTF_8)
    if (scoreboardText != "")
      scoreboardObj = mapper.readValue<ArrayList<MatchInfo>>(scoreboardText)

    return Pair(scoreboardObj, leaderboardObj)
  }

  public fun getScoreAndLeaderboardTexts(): Pair<String, String> {
    val (scoreboardFile, leaderboardFile) = getScoreAndLeaderboardFile()

    val leaderboardText = leaderboardFile.readText(Charsets.UTF_8)
    val scoreboardText = scoreboardFile.readText(Charsets.UTF_8)

    return Pair(scoreboardText, leaderboardText)
  }

  override fun getScoreAndLeaderboardFile(): Pair<File, File> {
    val leaderboardFilename = "/src/test/kotlin/galaxyraiders/core/score/Leaderboard.json"
    val scoreboardFilename = "/src/test/kotlin/galaxyraiders/core/score/Scoreboard.json"
    val leaderboardFile = File(System.getProperty("user.dir"), leaderboardFilename)
    val scoreboardFile = File(System.getProperty("user.dir"), scoreboardFilename)

    return Pair(scoreboardFile, leaderboardFile)
  }
}
