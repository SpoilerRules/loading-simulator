package com.spoiligaming.loader

import javafx.animation.FadeTransition
import javafx.animation.ParallelTransition
import javafx.animation.ScaleTransition
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.text.Text
import javafx.util.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

object MainBodyProcessor {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var loadingDuration = (10..20).random().toDouble()
    private var isLabelUpdated = false
    private var totalDecrement = 0.0

    private var previousParticlePosition: Pair<Double, Double>? = null

    fun initializeBody(): HBox = HBox().apply {
        alignment = Pos.CENTER
        children.add(createLoadingPane())
    }

    private fun createLoadingPane() = StackPane().apply {
        val loadingLabel = Label("Loading").apply labelApply@{
            style = "-fx-font-size: 32; -fx-font-family: ${ResourceHandler.comfortaaBold}; -fx-text-fill: ${ColorPalette.TEXT_COLOR};"
            setMaxSize(385.0, 285.0)
            setMinSize(385.0, 285.0)
            alignment = Pos.CENTER
            setOnMousePressed { scaleAndPause(0.85) }
            setOnMouseReleased { scaleAndResume(1.0, this@labelApply) }
            addEventFilter(MouseEvent.MOUSE_DRAGGED) { it.consume() }
        }

        val backgroundRegion = Region().apply {
            style = "-fx-background-color: ${ColorPalette.PRIMARY_COLOR}; -fx-background-radius: 28;"
            setMaxSize(400.0, 300.0)
            setMinSize(400.0, 300.0)
        }

        children.addAll(backgroundRegion, loadingLabel)
        scope.launch { updateLoadingText(loadingLabel) }
    }

    private suspend fun updateLoadingText(label: Label) {
        var dotCount = 0

        while (!isLabelUpdated) {
            delay(1000)
            println("Seconds left: $loadingDuration")
            if (loadingDuration > 0) {
                loadingDuration -= 1
            } else {
                updateLabelToLoaded(label)
                isLabelUpdated = true
                break
            }

            dotCount = (dotCount + 1) % 4
            Platform.runLater { label.text = "Loading${".".repeat(dotCount)}" }
        }
    }

    private fun Label.scaleAndPause(factor: Double) {
        if (isLabelUpdated) return

        animateScale(factor)
        scope.launch { pauseCountdown() }
    }

    private fun Label.scaleAndResume(factor: Double, label: Label) {
        if (isLabelUpdated) return

        val decrement = Random.nextDouble(0.08, 1.0).format(2)
        animateScale(factor)
        resumeCountdown(decrement.toDouble(), label)
        createDecrementParticle(label, decrement.toDouble())
    }

    private suspend fun pauseCountdown() {
        while (loadingDuration > 0) {
            delay(100)
        }
    }

    private fun resumeCountdown(decrement: Double, label: Label) {
        loadingDuration -= decrement
        totalDecrement += decrement
        println(totalDecrement)
        if (loadingDuration <= 0) {
            Platform.runLater {
                updateLabelToLoaded(label)
                isLabelUpdated = true
            }
        }
    }

    private fun Label.animateScale(factor: Double) =
        ScaleTransition(Duration.millis(100.0), this).apply {
            toX = factor
            toY = factor
            play()
        }

    private fun updateLabelToLoaded(label: Label) = Platform.runLater {
        label.text = "Loaded!"
        label.style = "-fx-font-size: 36; -fx-font-family: ${ResourceHandler.comfortaaBold}; -fx-text-fill: ${ColorPalette.TEXT_COLOR};"
        println("Label updated to Loaded!")
    }

    private fun createDecrementParticle(label: Label, decrement: Double) {
        val particle = Text("-${"%.2f".format(decrement)}${if (decrement >= 1.0) "s" else "ms"}").apply {
            style = "-fx-font-size: 15; -fx-font-family: ${ResourceHandler.comfortaaBold}; -fx-fill: ${ColorPalette.ACCENT_COLOR};"
            var newX: Double
            var newY: Double
            do {
                newX = Random.nextDouble(-label.width / 3, label.width / 3)
                newY = Random.nextDouble(-label.height / 2.33, label.height / 2.33)
            } while (previousParticlePosition?.let { (prevX, prevY) -> abs(prevX - newX) < 145 && abs(prevY - newY) < 145 } == true)
            translateX = newX
            translateY = newY
            previousParticlePosition = newX to newY
        }

        Platform.runLater {
            (label.parent as StackPane).children.add(particle)

            val fadeTransition = FadeTransition(Duration.millis(Random.nextDouble(400.0, 800.0)), particle).apply {
                fromValue = 1.0
                toValue = 0.0
            }

            val translateTransition = TranslateTransition(Duration.millis(Random.nextDouble(400.0, 800.0)), particle).apply {
                byY = -50.0
            }

            val parallelTransition = ParallelTransition(particle, fadeTransition, translateTransition).apply {
                setOnFinished { (particle.parent as StackPane).children.remove(particle) }
            }

            parallelTransition.play()
        }
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}
