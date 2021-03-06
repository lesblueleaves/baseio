/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.generallycloud.baseio.log;

/**
 * @author wangkai
 *
 */
public class SysLoggerPrinter implements LoggerPrinter {

	private static SysLoggerPrinter printer = new SysLoggerPrinter();

	public static SysLoggerPrinter get() {
		return printer;
	}

	@Override
	public void println(String msg) {
		System.out.println(msg);
	}

	@Override
	public void printThrowable(Throwable t) {
		t.printStackTrace(System.out);
	}

	@Override
	public void errPrintln(String msg) {
		System.err.println(msg);
	}

	@Override
	public void errPrintThrowable(Throwable t) {
		t.printStackTrace(System.err);
	}

}
