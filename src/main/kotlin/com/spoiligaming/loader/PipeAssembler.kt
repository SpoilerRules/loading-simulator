package com.spoiligaming.loader

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle

class PipeAssembler : Application() {
    companion object {
        private const val SOFTWARE_VERSION = "1.0.0"

        private var xOffset = 0.0
        private var yOffset = 0.0
    }

    override fun start(mainStage: Stage) {
        System.setProperty("prism.lcdtext", "false")
        System.setProperty("prism.text", "t2k")

        mainStage.apply mainStageApply@{
            this.scene =
                Scene(
                    BorderPane().apply {
                        center = MainBodyProcessor.initializeBody()
                        top = WindowControlManager.createControlButtons(this@mainStageApply)
                        style = "-fx-background-color: #343434; -fx-background-radius: 26;"
                    },
                    800.0, 600.0,
                ).apply {
                    fill = Color.TRANSPARENT

                    setOnMousePressed { event ->
                        xOffset = event.sceneX
                        yOffset = event.sceneY
                    }
                    setOnMouseDragged { event ->
                        // prevents the user from dragging the window outside the visible area of the screen or behind the taskbar
                        Screen.getPrimary().visualBounds.let { bounds ->
                            mainStage.x =
                                (event.screenX - xOffset).coerceIn(bounds.minX, bounds.maxX - mainStage.width)
                            mainStage.y =
                                (event.screenY - yOffset).coerceIn(bounds.minY, bounds.maxY - mainStage.height)
                        }
                    }
                }
            initStyle(StageStyle.TRANSPARENT)
            title = "Loading Simulator - $SOFTWARE_VERSION"
            isResizable = false
            show()
        }
    }
}