/*package com.mrll.javelin.tikaparser.utils;

// © 2013 ABBYY Production LLC
// SAMPLES code is property of ABBYY, exclusive rights are reserved. 
//
// DEVELOPER is allowed to incorporate SAMPLES into his own APPLICATION and modify it under 
// the  terms of  License Agreement between  ABBYY and DEVELOPER.


// Auto-generated config-file for FineReader Engine Java samples

public class SamplesConfig {
	// Folder with FRE dll
	public static String GetDllFolder() {
		if( is64BitJVMArchitecture() ) {
			return "C:\\Program Files\\ABBYY SDK\\11\\FineReader Engine\\Bin64";
		} else {
			return "C:\\Program Files\\ABBYY SDK\\11\\FineReader Engine\\Bin";
		}
	}

	// Return developer serial number for FRE
	public static String GetDeveloperSN() {
		return "SWAT-1101-0005-8586-6587-7625";
	}

	// Return full path to Samples directory
	public static String GetSamplesFolder() {
		return "C:\\Users\\rishabh.jain1\\Downloads\\LargeExcelFiles\\Abbyy";
	}

	// Determines whether the JVM architecture is a 64-bit architecture
	private static boolean is64BitJVMArchitecture()
	{
		String jvmKeys [] = {
			"sun.arch.data.model", 
			"com.ibm.vm.bitmode", 
			"os.arch"
		};
		for( String key : jvmKeys ) {
			String property = System.getProperty( key );
			if( property != null ) {
				if( property.indexOf( "64" ) >= 0 ) {
					return true;
				} else if( property.indexOf( "32" ) >= 0 ) {
					return false;
				} else if( property.indexOf( "86" ) >= 0 ) {
					return false;
				}
			}
		}
		return false;
	}
}
*/