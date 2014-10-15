/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package com.googlecode.goclipse.tooling.gocode;

import java.util.List;
import java.util.regex.Pattern;

import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;

import com.googlecode.goclipse.tooling.env.GoEnvironment;


public abstract class GocodeCompletionOperation<EXC extends Exception> {
	
	public static final boolean USE_TCP = true;
	
	protected final GoEnvironment goEnvironment;
	protected final String gocodePath;
	
	public GocodeCompletionOperation(GoEnvironment goEnvironment, String gocodePath) {
		this.goEnvironment = goEnvironment;
		this.gocodePath = gocodePath;
	}
	
	public ExternalProcessResult execute(String filePath, String bufferText, int offset) throws CommonException, EXC {
		
		setLibPathForEnvironment();
		
		ArrayList2<String> arguments = new ArrayList2<String>();
		if (USE_TCP) {
			arguments.add("-sock=tcp");
		}
		arguments.add("-f=csv");
		arguments.add("autocomplete");
		arguments.add(filePath);
		arguments.add("c" + offset);
		
		ExternalProcessResult processResult = runGocode(arguments, bufferText);

		if(processResult.exitValue != 0) {
			throw new CommonException("Error, gocode returned non-zero status: " + processResult.exitValue);
		}
		
		return processResult;
	}
	
	protected void setLibPathForEnvironment() throws CommonException, EXC {
		ArrayList2<String> arguments = new ArrayList2<>();
		
		if (USE_TCP) {
			arguments.add("-sock=tcp");
		}
		arguments.add("set");
		arguments.add("lib-path");
		arguments.add(goEnvironment.getPackageObjectsPathString());
		
		runGocode(arguments, null);
	}
	
	protected abstract ExternalProcessResult runGocode(List<String> arguments, String input) throws EXC;
	
	public static final Pattern LINE_SPLITTER = Pattern.compile("\n|(\r\n)|\r");
	
}