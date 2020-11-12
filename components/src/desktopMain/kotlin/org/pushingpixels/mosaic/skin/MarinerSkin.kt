/*
 * Copyright (c) 2020 Mosaic, Kirill Grouchnikov. All Rights Reserved.
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
package org.pushingpixels.mosaic.skin

import androidx.compose.ui.graphics.Color
import org.pushingpixels.mosaic.*
import org.pushingpixels.mosaic.colorscheme.BaseColorScheme
import org.pushingpixels.mosaic.colorscheme.MosaicColorSchemeBundle
import org.pushingpixels.mosaic.colorscheme.MosaicSkinColors
import org.pushingpixels.mosaic.utils.getColorSchemes

fun marinerSkinColors(): MosaicSkinColors {
    val result = MosaicSkinColors()
    val schemes = getColorSchemes(
        MosaicSkin::class.java.getResourceAsStream(
            "/org/pushingpixels/mosaic/skins/mariner.colorschemes"
        )
    )

    val activeScheme = schemes["Mariner Active"]
    val enabledScheme = schemes["Mariner Enabled"]
    val disabledScheme = schemes["Mariner Disabled"]

    val disabledSelectedScheme = schemes["Mariner Disabled Selected"]

    val defaultSchemeBundle = MosaicColorSchemeBundle(
        activeScheme, enabledScheme, disabledScheme
    )

    defaultSchemeBundle.registerAlpha(0.8f, ComponentState.DISABLED_SELECTED)
    defaultSchemeBundle.registerAlpha(0.8f, ComponentState.DISABLED_UNSELECTED)
    defaultSchemeBundle.registerColorScheme(disabledSelectedScheme, ColorSchemeAssociationKind.FILL,
        ComponentState.DISABLED_SELECTED)
    defaultSchemeBundle.registerColorScheme(disabledScheme, ColorSchemeAssociationKind.FILL,
        ComponentState.DISABLED_UNSELECTED)

    // borders
    val activeBorderScheme = schemes["Mariner Active Border"]
    val enabledBorderScheme = schemes["Mariner Enabled Border"]
    val disabledSelectedBorderScheme = schemes["Mariner Disabled Selected Border"]
    defaultSchemeBundle.registerColorScheme(
        activeBorderScheme,
        ColorSchemeAssociationKind.BORDER, *ComponentState.activeStates
    )
    defaultSchemeBundle.registerColorScheme(
        disabledSelectedBorderScheme,
        ColorSchemeAssociationKind.BORDER, ComponentState.DISABLED_SELECTED
    )
    defaultSchemeBundle.registerColorScheme(
        enabledBorderScheme,
        ColorSchemeAssociationKind.BORDER, ComponentState.ENABLED
    )

    // marks

    // marks
    val activeMarkScheme = schemes["Mariner Active Mark"]
    val enabledMarkScheme = schemes["Mariner Enabled Mark"]
    defaultSchemeBundle.registerColorScheme(
        activeMarkScheme, ColorSchemeAssociationKind.MARK,
        *ComponentState.activeStates
    )
    defaultSchemeBundle.registerColorScheme(
        enabledMarkScheme, ColorSchemeAssociationKind.MARK,
        ComponentState.ENABLED
    )

    val uneditable =
        ComponentState("uneditable", arrayOf(ComponentStateFacet.ENABLE),
            arrayOf(ComponentStateFacet.EDITABLE))
    val uneditableControls = schemes["Mariner Uneditable"]
    defaultSchemeBundle.registerColorScheme(
        uneditableControls, ColorSchemeAssociationKind.FILL,
        uneditable
    )

    result.registerDecorationAreaSchemeBundle(defaultSchemeBundle, DecorationAreaType.NONE)

    // header color scheme bundle

    // header color scheme bundle
    val headerColorScheme = schemes["Mariner Header"]
    val headerBorderColorScheme = schemes["Mariner Header Border"]
    val headerSchemeBundle = MosaicColorSchemeBundle(
        headerColorScheme, headerColorScheme, headerColorScheme
    )
    headerSchemeBundle.registerAlpha(0.4f, ComponentState.DISABLED_SELECTED, ComponentState.DISABLED_UNSELECTED)
    headerSchemeBundle.registerColorScheme(
        headerColorScheme,ColorSchemeAssociationKind.FILL,
        ComponentState.DISABLED_SELECTED, ComponentState.DISABLED_UNSELECTED
    )
    // TODO - this is different from the original
    headerSchemeBundle.registerColorScheme(
        activeScheme,ColorSchemeAssociationKind.FILL,
        ComponentState.ROLLOVER_UNSELECTED, ComponentState.ROLLOVER_SELECTED
    )
    headerSchemeBundle.registerColorScheme(headerColorScheme, ColorSchemeAssociationKind.MARK)
    headerSchemeBundle.registerColorScheme(
        headerBorderColorScheme,
        ColorSchemeAssociationKind.BORDER
    )
    result.registerDecorationAreaSchemeBundle(
        headerSchemeBundle, headerColorScheme,
        DecorationAreaType.PRIMARY_TITLE_PANE, DecorationAreaType.SECONDARY_TITLE_PANE,
        DecorationAreaType.HEADER
    )

    // footer color scheme bundle

    // footer color scheme bundle
    val enabledFooterScheme = schemes["Mariner Footer Enabled"]
    val disabledFooterScheme = schemes["Mariner Footer Disabled"]

    val footerSchemeBundle = MosaicColorSchemeBundle(
        activeScheme,
        enabledFooterScheme, disabledFooterScheme
    )

    footerSchemeBundle.registerAlpha(0.5f, ComponentState.DISABLED_SELECTED)
    footerSchemeBundle.registerAlpha(0.8f, ComponentState.DISABLED_UNSELECTED)
    footerSchemeBundle.registerColorScheme(activeScheme, ColorSchemeAssociationKind.FILL,ComponentState.DISABLED_SELECTED)
    footerSchemeBundle.registerColorScheme(disabledFooterScheme, ColorSchemeAssociationKind.FILL,ComponentState.DISABLED_UNSELECTED)

    // borders

    // borders
    val footerEnabledBorderScheme = schemes["Mariner Footer Enabled Border"]
    footerSchemeBundle.registerColorScheme(
        activeBorderScheme,
        ColorSchemeAssociationKind.BORDER, *ComponentState.activeStates
    )
    footerSchemeBundle.registerColorScheme(
        activeBorderScheme,
        ColorSchemeAssociationKind.BORDER, ComponentState.DISABLED_SELECTED
    )
    footerSchemeBundle.registerColorScheme(
        footerEnabledBorderScheme,
        ColorSchemeAssociationKind.BORDER, ComponentState.ENABLED
    )

    // marks

    // marks
    val footerEnabledMarkScheme = schemes["Mariner Footer Enabled Mark"]
    footerSchemeBundle.registerColorScheme(
        activeMarkScheme, ColorSchemeAssociationKind.MARK,
        *ComponentState.activeStates
    )
    footerSchemeBundle.registerColorScheme(
        footerEnabledMarkScheme,
        ColorSchemeAssociationKind.MARK, ComponentState.ENABLED
    )

    // separators

    // separators
    val footerSeparatorScheme = schemes["Mariner Footer Separator"]
    footerSchemeBundle.registerColorScheme(
        footerSeparatorScheme,
        ColorSchemeAssociationKind.SEPARATOR
    )

    val footerBackgroundColorScheme = schemes["Mariner Footer Background"]
    result.registerDecorationAreaSchemeBundle(
        footerSchemeBundle, footerBackgroundColorScheme,
        DecorationAreaType.FOOTER, DecorationAreaType.TOOLBAR, DecorationAreaType.CONTROL_PANE
    )
    return result
}
