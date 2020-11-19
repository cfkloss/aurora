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
package org.pushingpixels.mosaic.painter.overlay

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.pushingpixels.mosaic.DecorationAreaType
import org.pushingpixels.mosaic.MosaicTrait
import org.pushingpixels.mosaic.colorscheme.MosaicSkinColors

/**
 * Overlay painter interface.
 *
 * @author Kirill Grouchnikov
 */
interface MosaicOverlayPainter : MosaicTrait {
    /**
     * Paints the overlay.
     *
     * @param drawScope Draw scope.
     * @param decorationAreaType Decoration area type.
     * @param width Width.
     * @param height Height.
     * @param colors Colors for painting the overlay.
     */
    fun paintOverlay(
        drawScope: DrawScope,
        decorationAreaType: DecorationAreaType,
        width: Float,
        height: Float,
        colors: MosaicSkinColors
    )
}