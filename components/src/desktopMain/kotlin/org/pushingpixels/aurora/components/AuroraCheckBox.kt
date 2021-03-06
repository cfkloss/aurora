/*
 * Copyright (c) 2020 Aurora, Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of the copyright holder nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.pushingpixels.aurora.components

import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.AnimatedFloat
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.TransitionDefinition
import androidx.compose.animation.transition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.unit.dp
import org.pushingpixels.aurora.*
import org.pushingpixels.aurora.utils.*
import java.util.*

private val CheckboxSize = 14.dp

// This will be initialized on first usage using the getSelectedTransitionDefinition
// with duration animation coming from [AmbientAnimationConfig]
private lateinit var SelectedTransitionDefinition: TransitionDefinition<Boolean>

// This will be initialized on first usage using the getRolloverTransitionDefinition
// with duration animation coming from [AmbientAnimationConfig]
private lateinit var RolloverTransitionDefinition: TransitionDefinition<Boolean>

// This will be initialized on first usage using the getPressedTransitionDefinition
// with duration animation coming from [AmbientAnimationConfig]
private lateinit var PressedTransitionDefinition: TransitionDefinition<Boolean>

@Immutable
private class CheckBoxDrawingCache(
    val colorScheme: MutableColorScheme = MutableColorScheme(
        displayName = "Internal mutable",
        isDark = false,
        ultraLight = Color.White,
        extraLight = Color.White,
        light = Color.White,
        mid = Color.White,
        dark = Color.White,
        ultraDark = Color.White,
        foreground = Color.Black
    ),
    val markPath: Path = Path()
)

@Composable
fun AuroraCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    AuroraCheckBox(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        stateTransitionFloat = AnimatedFloat(0.0f, AmbientAnimationClock.current.asDisposableClock()),
        content = content
    )
}

@Composable
private fun AuroraCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    stateTransitionFloat: AnimatedFloat,
    content: @Composable RowScope.() -> Unit
) {
    val drawingCache = remember { CheckBoxDrawingCache() }

    val selectedState = remember { mutableStateOf(checked) }
    val rolloverState = remember { mutableStateOf(false) }
    val interactionState = remember { InteractionState() }
    val isPressed = Interaction.Pressed in interactionState

    val modelStateInfo = remember {
        ModelStateInfo(
            ComponentState.getState(
                isEnabled = enabled,
                isRollover = false,
                isSelected = checked,
                isPressed = isPressed
            )
        )
    }
    val markAlpha = remember { mutableStateOf(if (checked) 1.0f else 0.0f) }

    val decorationAreaType = AuroraSkin.decorationArea.type

    // Transition for the selection state
    if (!::SelectedTransitionDefinition.isInitialized) {
        SelectedTransitionDefinition =
            getSelectedTransitionDefinition(AuroraSkin.animationConfig.short)
    }
    val selectionTransitionState = transition(
        definition = SelectedTransitionDefinition,
        initState = selectedState.value,
        toState = selectedState.value
    )
    // Transition for the rollover state
    if (!::RolloverTransitionDefinition.isInitialized) {
        RolloverTransitionDefinition =
            getRolloverTransitionDefinition(AuroraSkin.animationConfig.regular)
    }
    val rolloverTransitionState = transition(
        definition = RolloverTransitionDefinition,
        initState = rolloverState.value,
        toState = rolloverState.value
    )
    // Transition for the pressed state
    if (!::PressedTransitionDefinition.isInitialized) {
        PressedTransitionDefinition =
            getPressedTransitionDefinition(AuroraSkin.animationConfig.regular)
    }
    val pressedTransitionState = transition(
        definition = PressedTransitionDefinition,
        initState = isPressed,
        toState = isPressed
    )

    // TODO - how to trigger the state transition animation without these transitions
    //  that track the changes in different states?
    selectionTransitionState[SelectionTransitionFraction]
    rolloverTransitionState[RolloverTransitionFraction]
    pressedTransitionState[PressedTransitionFraction]

    val currentState = ComponentState.getState(
        isEnabled = enabled,
        isRollover = rolloverState.value,
        isSelected = selectedState.value,
        isPressed = isPressed
    )

    var duration = AuroraSkin.animationConfig.regular
    if (currentState != modelStateInfo.currModelState) {
        stateTransitionFloat.stop()
        //println("******** Have new state to move to $currentState ********")
        modelStateInfo.dumpState(stateTransitionFloat.value)
        // Need to transition to the new state
        if (modelStateInfo.stateContributionMap.containsKey(currentState)) {
            //println("Already has new state")
            // Going to a state that is already partially active
            val transitionPosition = modelStateInfo.stateContributionMap[currentState]!!.contribution
            duration = (duration * (1.0f - transitionPosition)).toInt()
            stateTransitionFloat.setBounds(transitionPosition, 1.0f)
            stateTransitionFloat.snapTo(transitionPosition)
        } else {
            //println("Does not have new state (curr state ${modelStateInfo.currModelState}) at ${stateTransitionFloat.value}")
            stateTransitionFloat.setBounds(0.0f, 1.0f)
            stateTransitionFloat.snapTo(0.0f)
            //println("\tat ${stateTransitionFloat.value}")
        }

        // Create a new contribution map
        val newContributionMap: MutableMap<ComponentState, StateContributionInfo> = HashMap()
        if (modelStateInfo.stateContributionMap.containsKey(currentState)) {
            // 1. the new state goes from current value to 1.0
            // 2. the rest go from current value to 0.0
            for ((contribState, currRange) in modelStateInfo.stateContributionMap.entries) {
                val newEnd = if (contribState == currentState) 1.0f else 0.0f
                newContributionMap[contribState] = StateContributionInfo(
                    currRange.contribution, newEnd
                )
            }
        } else {
            // 1. all existing states go from current value to 0.0
            // 2. the new state goes from 0.0 to 1.0
            for ((contribState, currRange) in modelStateInfo.stateContributionMap.entries) {
                newContributionMap[contribState] = StateContributionInfo(
                    currRange.contribution, 0.0f
                )
            }
            newContributionMap[currentState] = StateContributionInfo(0.0f, 1.0f)
        }
        modelStateInfo.stateContributionMap = newContributionMap
        modelStateInfo.sync()

        modelStateInfo.currModelState = currentState
        //println("******** After moving to new state *****")
        modelStateInfo.dumpState(stateTransitionFloat.value)

        //println("Animating over $duration from ${stateTransitionFloat.value} to 1.0f")
        stateTransitionFloat.animateTo(
            targetValue = 1.0f,
            anim = FloatTweenSpec(duration = duration),
            onEnd = { endReason, endValue ->
                //println("Ended with reason $endReason at $endValue / ${stateTransitionFloat.value}")
                if (endReason == AnimationEndReason.TargetReached) {
                    modelStateInfo.updateActiveStates(1.0f)
                    modelStateInfo.clear()
                    //println("******** After clear (target reached) ********")
                    modelStateInfo.dumpState(stateTransitionFloat.value)
                    markAlpha.value =
                        if (modelStateInfo.currModelState.isFacetActive(ComponentStateFacet.SELECTION)) 1.0f else 0.0f
                }
            }
        )

        //println()
    }

    if (stateTransitionFloat.isRunning) {
        modelStateInfo.updateActiveStates(stateTransitionFloat.value)
        //println("********* During animation ${stateTransitionFloat.value} to ${stateTransitionFloat.targetValue} *******")
        modelStateInfo.dumpState(stateTransitionFloat.value)
    }

    // The toggleable modifier is set on the checkbox mark, as well as on the
    // content so that the whole thing is clickable to toggle the control.
    Row(
        modifier = modifier
            .pointerMoveFilter(
                onEnter = {
                    rolloverState.value = true
                    false
                },
                onExit = {
                    rolloverState.value = false
                    false
                },
                onMove = {
                    false
                })
            .toggleable(
                value = selectedState.value,
                onValueChange = {
                    selectedState.value = !selectedState.value
                    onCheckedChange.invoke(selectedState.value)
                },
                enabled = enabled,
                interactionState = interactionState,
                indication = null
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Populate the cached color scheme for filling the mark box
        // based on the current model state info
        populateColorScheme(
            drawingCache.colorScheme, modelStateInfo, decorationAreaType,
            ColorSchemeAssociationKind.MARK_BOX
        )
        // And retrieve the mark box colors
        val fillUltraLight = drawingCache.colorScheme.ultraLightColor
        val fillExtraLight = drawingCache.colorScheme.extraLightColor
        val fillLight = drawingCache.colorScheme.lightColor
        val fillMid = drawingCache.colorScheme.midColor
        val fillDark = drawingCache.colorScheme.darkColor
        val fillUltraDark = drawingCache.colorScheme.ultraDarkColor
        val fillIsDark = drawingCache.colorScheme.isDark

        // Populate the cached color scheme for drawing the mark box border
        // based on the current model state info
        populateColorScheme(
            drawingCache.colorScheme, modelStateInfo, decorationAreaType,
            ColorSchemeAssociationKind.BORDER
        )
        // And retrieve the mark box border colors
        val borderUltraLight = drawingCache.colorScheme.ultraLightColor
        val borderExtraLight = drawingCache.colorScheme.extraLightColor
        val borderLight = drawingCache.colorScheme.lightColor
        val borderMid = drawingCache.colorScheme.midColor
        val borderDark = drawingCache.colorScheme.darkColor
        val borderUltraDark = drawingCache.colorScheme.ultraDarkColor
        val borderIsDark = drawingCache.colorScheme.isDark

        // Mark color
        val markColor = getStateAwareColor(
            modelStateInfo,
            decorationAreaType, ColorSchemeAssociationKind.MARK
        ) { it.markColor }

        // Checkmark alpha is the combined strength of all the
        // states that have the selection bit turned on
        markAlpha.value =
            modelStateInfo.stateContributionMap.filter { it.key.isFacetActive(ComponentStateFacet.SELECTION) }
                .map { it.value }
                .sumByDouble { it.contribution.toDouble() }
                .toFloat()

        //println("Mark alpha ${markAlpha.value}")

        // Text color. Note that the text doesn't "participate" in state changes that
        // involve rollover, selection or pressed bits
        val textColor = getTextColor(
            modelStateInfo = modelStateInfo,
            skinColors = AuroraSkin.colors,
            decorationAreaType = decorationAreaType,
            isTextInFilledArea = false
        )
        val alpha = if (currentState.isDisabled)
            AuroraSkin.colors.getAlpha(decorationAreaType, currentState) else 1.0f

        val fillPainter = AuroraSkin.painters.fillPainter
        val borderPainter = AuroraSkin.painters.borderPainter

        Canvas(modifier.wrapContentSize(Alignment.Center).size(CheckboxSize)) {
            val width = this.size.width
            val height = this.size.height

            val outline = getBaseOutline(
                width = this.size.width,
                height = this.size.height,
                radius = 3.0f.dp.toPx(),
                straightSides = null,
                insets = 0.5f
            )

            // Populate the cached color scheme for filling the markbox
            drawingCache.colorScheme.ultraLight = fillUltraLight
            drawingCache.colorScheme.extraLight = fillExtraLight
            drawingCache.colorScheme.light = fillLight
            drawingCache.colorScheme.mid = fillMid
            drawingCache.colorScheme.dark = fillDark
            drawingCache.colorScheme.ultraDark = fillUltraDark
            drawingCache.colorScheme.isDark = fillIsDark
            drawingCache.colorScheme.foreground = textColor
            fillPainter.paintContourBackground(
                this, this.size, outline, drawingCache.colorScheme, alpha
            )

            // Populate the cached color scheme for drawing the markbox border
            drawingCache.colorScheme.ultraLight = borderUltraLight
            drawingCache.colorScheme.extraLight = borderExtraLight
            drawingCache.colorScheme.light = borderLight
            drawingCache.colorScheme.mid = borderMid
            drawingCache.colorScheme.dark = borderDark
            drawingCache.colorScheme.ultraDark = borderUltraDark
            drawingCache.colorScheme.isDark = borderIsDark
            drawingCache.colorScheme.foreground = textColor

            val outlineInner = if (borderPainter.isPaintingInnerOutline) getBaseOutline(
                width = this.size.width,
                height = this.size.height,
                radius = 3.0f.dp.toPx() - 1,
                straightSides = null,
                insets = 2.0f
            ) else null

            borderPainter.paintBorder(
                this, this.size, outline, outlineInner, drawingCache.colorScheme, alpha
            )

            // Draw the checkbox mark with the alpha that corresponds to the current
            // selection and potential transition
            val markStroke = 0.12f * width

            with(drawingCache) {
                markPath.reset()
                markPath.moveTo(0.25f * width, 0.48f * height)
                markPath.lineTo(0.48f * width, 0.73f * height)
                markPath.lineTo(0.76f * width, 0.28f * height)

                // Note that we apply alpha twice - once for the selected / checked
                // state or transition, and the second time based on the enabled state
                drawPath(
                    path = markPath,
                    color = markColor.copy(alpha = markAlpha.value),
                    style = Stroke(
                        width = markStroke,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    ),
                    alpha = alpha
                )
            }
        }
        Providers(AmbientTextColor provides textColor) {
            Row(
                Modifier
                    .defaultMinSizeConstraints(
                        minWidth = 0.dp,
                        minHeight = CheckboxSize
                    )
                    .padding(4.dp, 10.dp, 4.dp, 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}
