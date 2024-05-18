package com.spoiligaming.loader

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.stage.Stage

object WindowControlManager {
    fun createControlButtons(stage: Stage): HBox =
        HBox().apply {
            alignment = Pos.TOP_RIGHT

            children.addAll(
                createButton("_", stage::isIconified, "#525252"),
                createButton("x", Platform::exit, "#7E1515", roundedTopRight = true)
            )
        }

    private fun createButton(
        text: String,
        action: () -> Unit,
        hoverColor: String,
        roundedTopRight: Boolean = false
    ): Button {
        val baseStyle = """
            -fx-background-color: transparent;
            -fx-text-fill: #B4B4B4;
            -fx-font-family: ${ResourceHandler.comfortaaRegular};
            -fx-font-size: ${if (text == "_") 20 else 16};
            -fx-padding: ${if (text == "_") 0 else 5} 15 5 15;
            -fx-background-radius: ${if (roundedTopRight) "12 26 12 12" else "12"};
        """.trimIndent()

        val hoverStyle = baseStyle.replace("transparent", hoverColor)

        return Button(text).apply {
            setMaxSize(60.0, 40.0)
            setMinSize(60.0, 40.0)
            style = baseStyle
            setOnAction { action() }

            setOnMouseEntered { style = hoverStyle }
            setOnMouseExited { style = baseStyle }
        }
    }
}
