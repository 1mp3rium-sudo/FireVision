package com.example.textrecognition;

public class testParser {
	private String ogString;
	
	
	public testParser() {
		ogString = "";
	}
	
	public testParser(String e) {
		ogString = e;
	}
	
	public int parseStr() {
		int output = 0;
		
		String holder[] = ogString.toLowerCase().split("\\n");
		
		//test for keyword 'set' 
		
		if(holder[0].charAt(0)=='s') {
			if(holder[0].indexOf("set ")==0) {
				output = setType(holder[0]);
				return output;
			}else if(holder[0].indexOf("say ")==0) {
				return 3;
			}
		}else if(holder[0].indexOf(" set ")!=-1) {
			output = setType(holder[0]);
			return output;
		}
		
		//test for reminder
		
		if(holder[0].indexOf("remind")!=-1 || holder[0].indexOf("remember")!=-1)
			return 1;
		
		
		
		
		
		
		
		
		
		
		return output;
	}
	
	public int setType(String e) {
		int output = 0;
		
		if(e.indexOf("reminder")!=-1) {
			output = 1;
		}else if(e.indexOf("alarm")!=-1){
			output = 2;
		}
		
		return output;
	}
}
