/**
 * Copyright (C) 2016 Transwarp Technology(Shanghai ) Co., Ltd.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.transwarp.midas.renderer;

import com.rapidminer.gui.renderer.AbstractRenderer;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.report.Reportable;
import io.transwarp.midas.model.CurveCollection;
import io.transwarp.midas.ui.CurveViewer;

import java.awt.*;

public class CurveCollectionRenderer extends AbstractRenderer {
    @Override
    public String getName() {
        return "performance curve";
    }

    @Override
    public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {
        return new CurveViewer((CurveCollection) renderable);
    }

    @Override
    public Reportable createReportable(Object renderable, IOContainer ioContainer, int desiredWidth, int desiredHeight) {
        return new CurveViewer((CurveCollection) renderable);
    }
}
