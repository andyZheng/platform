/*
   Copyright 2010 Gregor Latuske

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
*/
package org.wso2.carbon.bpel.ui.bpel2svg.latest.internal.frontend.model.components.svg;

import org.wso2.carbon.bpel.ui.bpel2svg.latest.internal.frontend.model.SVGDataModel;
import org.wso2.carbon.bpel.ui.bpel2svg.latest.internal.svg.settings.Settings;

/**
 * This class is used to set the granularity of the SVG.
 * 
 * @author Gregor Latuske
 */
public class ProcessModelSlider
	extends Slider {

	/** The maximum range of the slider. */
	private int maxRange;

	/**
	 * Constructor of ProcessModelSlider.
	 * 
	 * @param dataModel The associated SVGDataModel.
	 */
	public ProcessModelSlider(SVGDataModel dataModel) {
		super(dataModel);
	}

	/**
	 * Sets the value of maxRange to maxRange.
	 * 
	 * @param maxRange The new value of maxRange.
	 */
	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange + Settings.PM_GRANULARITY_MIN;
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxRange() {
		return this.maxRange;
	}

}
