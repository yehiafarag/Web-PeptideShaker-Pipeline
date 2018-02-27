/*
 * Copyright 2015 akquinet engineering GmbH
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.akquinet.engineering.vaadin.vaangular.demo.weather;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.JavaScript;
import com.vaadin.event.ConnectorEventListener;

import de.akquinet.engineering.vaadin.vaangular.angular.NgTemplatePlus;
import de.akquinet.engineering.vaadin.vaangular.angular.ServiceMethod;

@JavaScript({ "META-INF/resources/webjars/angularjs/1.3.15/angular.js",
		"META-INF/resources/webjars/angularjs/1.3.15/angular-sanitize.js",
		"litemol.js" })
public  class LiteMol extends NgTemplatePlus {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1857536974366619130L;

	private List<WeatherClickListener> listeners = new ArrayList<LiteMol.WeatherClickListener>();

	private int[] times;
	private String[] entries;

	public LiteMol() throws IOException, URISyntaxException {
		super(LiteMol.class.getPackage(), "weatherModule");
		addService("button", new Object() {
			@ServiceMethod
			public void click() {
				int index = getSliderPos();
				System.out.println("Button from w/in angular - value: " + index);
				for (WeatherClickListener listener : listeners) {
					listener.click(times[index], entries[index]);
				}
			}
		});
		setButtonCaption("senden");
                this.setSizeFull();
	}

	public void setButtonCaption(String caption) {
		setUserState("buttonCaption", "update");
	}

	public int getSliderPos() {
            
		return  Integer.parseInt(getVariables().get("sliderPos").toString());
	}
        public void invoke(){
        
        }

	public void setDaten(int[] times, String[] entries) {
		validateParameters(times, entries);
		this.times = times;
		this.entries = entries;
		setUserState("times", times);
		setUserState("entries", entries);
                setUserState("invoke", "invoke()");
		markAsDirty();
	}

	static void validateParameters(int[] times, String[] entries) {
		if (times.length != entries.length) {
			throw new IllegalArgumentException("#times does not match #entries");
		}
		if (times.length < 2) {
			throw new IllegalArgumentException(
					"#times/#entries needs to be >=2");
		}
		int step = calcStep(times);
		for (int i = 1; i < times.length; i++) {
			if (!(times[i - 1] < times[i])) {
				throw new IllegalArgumentException("Times must be in order");
			}
			if ((times[i] - times[i - 1]) != step) {
				throw new IllegalArgumentException(
						"Times must have same delta between one another");
			}
		}
	}

	private static int calcStep(int[] times) {
		int step = times[1] - times[0];
		return step;
	}

	public void addClickListener(WeatherClickListener listener) {
		listeners.add(listener);
	}

	public void removeClickListener(WeatherClickListener listener) {
		listeners.remove(listener);
	}

	public interface WeatherClickListener extends ConnectorEventListener {

		public void click(int time, String entry);
	}
        
        public void updateProteins(){
         com.vaadin.ui.JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.clickedBtn();");
        }

}
