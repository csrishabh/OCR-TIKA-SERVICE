/*package com.mrll.javelin.tikaparser.utils;

import com.abbyy.FREngine.Engine;
import com.abbyy.FREngine.IEngine;

public class AbbyyEngine {
	
	
	private static IEngine engine = null;
	
	private AbbyyEngine(){
		
	}
	
	public static IEngine getEngine() throws Exception{
		
		if(engine == null){
		loadEngine();
		}
		return engine;
	}
	
	private static void loadEngine() throws Exception {
		displayMessage("Initializing Engine...");
		engine = Engine.GetEngineObject(SamplesConfig.GetDllFolder(), SamplesConfig.GetDeveloperSN());
		engine.LoadPredefinedProfile("DocumentConversion_Accuracy");
	}
	
	private static void displayMessage(String message) {
		System.out.println(message);
	}
	
	public static void unloadEngine() throws Exception {
		displayMessage("Deinitializing Engine...");
		engine = null;
		Engine.DeinitializeEngine();
	}

}
*/