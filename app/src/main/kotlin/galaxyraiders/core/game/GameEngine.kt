package galaxyraiders.core.game

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import galaxyraiders.Config
import galaxyraiders.ports.RandomGenerator
import galaxyraiders.ports.ui.Controller
import galaxyraiders.ports.ui.Controller.PlayerCommand
import galaxyraiders.ports.ui.Visualizer
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.measureTimeMillis

const val MILLISECONDS_PER_SECOND: Int = 1000
const val MAX_LEADERBOARD_SIZE: Int = 3

object GameEngineConfig {
  private val config = Config(prefix = "GR__CORE__GAME__GAME_ENGINE__")

  val frameRate = config.get<Int>("FRAME_RATE")
  val spaceFieldWidth = config.get<Int>("SPACEFIELD_WIDTH")
  val spaceFieldHeight = config.get<Int>("SPACEFIELD_HEIGHT")
  val asteroidProbability = config.get<Double>("ASTEROID_PROBABILITY")
  val coefficientRestitution = config.get<Double>("COEFFICIENT_RESTITUTION")

  val msPerFrame: Int = MILLISECONDS_PER_SECOND / this.frameRate
}

@Suppress("TooManyFunctions")
open class GameEngine(
  val generator: RandomGenerator,
  val controller: Controller,
  val visualizer: Visualizer,
) {
  val field = SpaceField(
    width = GameEngineConfig.spaceFieldWidth,
    height = GameEngineConfig.spaceFieldHeight,
    generator = generator
  )

  var playing = true

  protected var destroyedAsteroids: Int = 0
  protected var scoredPoints: Int = 0

  var leaderboardObj: ArrayList<MatchInfo> = ArrayList<MatchInfo>()
  var scoreboardObj: ArrayList<MatchInfo> = ArrayList<MatchInfo>()
  val mapper = jacksonObjectMapper()

  var startTime: String = ""
  val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  init {
    val (scoreboardFile, leaderboardFile) = getScoreAndLeaderboardFile()

    getInitialSaveData(leaderboardFile, scoreboardFile)
  }

  public fun saveData() {
    putNewScoreboardInfo()
    putNewLeaderboardInfo()
    writeSaveData()
  }

  public fun setStartTime() {
    val time = LocalDateTime.now()
    startTime = time.format(timeFormatter)
  }

	open fun getScoreAndLeaderboardFile(): Pair<File, File> {
		val leaderboardFilename = "/src/main/kotlin/galaxyraiders/core/score/Leaderboard.json"
		val scoreboardFilename = "/src/main/kotlin/galaxyraiders/core/score/Scoreboard.json"
		val leaderboardFile = File("/home/gradle/galaxy-raiders/app/", leaderboardFilename)
		val scoreboardFile = File("/home/gradle/galaxy-raiders/app/", scoreboardFilename)

    return Pair(scoreboardFile, leaderboardFile)
  }

  fun putNewScoreboardInfo() {
    val newEntry = MatchInfo(startTime, scoredPoints, destroyedAsteroids)
    scoreboardObj.add(newEntry)
  }

  fun putNewLeaderboardInfo() {
    val newEntry = MatchInfo(startTime, scoredPoints, destroyedAsteroids)
    leaderboardObj.add(newEntry)
    leaderboardObj = ArrayList(leaderboardObj.sorted())

    if (leaderboardObj.size > MAX_LEADERBOARD_SIZE) leaderboardObj =
      ArrayList(leaderboardObj.take(MAX_LEADERBOARD_SIZE))
  }

  fun writeSaveData() {
    val scoreboardString = mapper.writeValueAsString(scoreboardObj)
    val leaderboardString = mapper.writeValueAsString(leaderboardObj)

    val (scoreboardFile, leaderboardFile) = getScoreAndLeaderboardFile()
    scoreboardFile.writeText(scoreboardString)
    leaderboardFile.writeText(leaderboardString)
  }

	fun getInitialSaveData(leaderboardFile: File, scoreboardFile: File) {
		if (!leaderboardFile.exists()) leaderboardFile.createNewFile()
		else {
			val leaderboardText = leaderboardFile.readText(Charsets.UTF_8)
			if (leaderboardText != "")
				leaderboardObj = mapper.readValue<ArrayList<MatchInfo>>(leaderboardText)
		}

		if (!scoreboardFile.exists()) scoreboardFile.createNewFile()
		else {
			val scoreboardText = scoreboardFile.readText(Charsets.UTF_8)
			if (scoreboardText != "")
				scoreboardObj = mapper.readValue<ArrayList<MatchInfo>>(scoreboardText)
		}
	}

  fun execute() {
    while (true) {
      val duration = measureTimeMillis { this.tick() }

      Thread.sleep(
        maxOf(0, GameEngineConfig.msPerFrame - duration)
      )
    }
  }

  fun execute(maxIterations: Int) {
    repeat(maxIterations) {
      this.tick()
    }
  }

  fun tick() {
    this.processPlayerInput()
    this.updateSpaceObjects()
    this.renderSpaceField()
  }

  fun processPlayerInput() {
    this.controller.nextPlayerCommand()?.also {
      when (it) {
        PlayerCommand.MOVE_SHIP_UP ->
          this.field.ship.boostUp()
        PlayerCommand.MOVE_SHIP_DOWN ->
          this.field.ship.boostDown()
        PlayerCommand.MOVE_SHIP_LEFT ->
          this.field.ship.boostLeft()
        PlayerCommand.MOVE_SHIP_RIGHT ->
          this.field.ship.boostRight()
        PlayerCommand.LAUNCH_MISSILE ->
          this.field.generateMissile()
        PlayerCommand.PAUSE_GAME ->
          this.playing = !this.playing
      }
    }
  }

  fun updateSpaceObjects() {
    if (!this.playing) return
    this.handleExplosions()
    this.handleCollisions()
    this.moveSpaceObjects()
    this.trimSpaceObjects()
    this.generateAsteroids()
  }

  fun handleExplosions() {
    this.field.tickExplosions()

    handleMissileCollisions()
  }

  fun handleMissileCollisions() {
    this.field.missiles.forEach { m ->
      this.field.asteroids.forEach { a ->
        if (m.impacts(a)) {
          m.markForDestruction()
          a.markForDestruction()
          this.field.generateExplosion(a.center, a.radius)
          scoredPoints += a.calculatePoints()
          destroyedAsteroids++
        }
      }
    }
  }

  fun handleCollisions() {
    this.field.spaceObjects.forEachPair {
        (first, second) ->
      if (first.impacts(second)) {
        first.collideWith(second, GameEngineConfig.coefficientRestitution)
      }
    }
  }

  fun moveSpaceObjects() {
    this.field.moveShip()
    this.field.moveAsteroids()
    this.field.moveMissiles()
  }

  fun trimSpaceObjects() {
    this.field.trimAsteroids()
    this.field.trimMissiles()
    this.field.trimExplosions()
  }

  fun generateAsteroids() {
    val probability = generator.generateProbability()

    if (probability <= GameEngineConfig.asteroidProbability) {
      this.field.generateAsteroid()
    }
  }

  fun renderSpaceField() {
    this.visualizer.renderSpaceField(this.field)
  }
}

fun <T> List<T>.forEachPair(action: (Pair<T, T>) -> Unit) {
  for (i in 0 until this.size) {
    for (j in i + 1 until this.size) {
      action(Pair(this[i], this[j]))
    }
  }
}
