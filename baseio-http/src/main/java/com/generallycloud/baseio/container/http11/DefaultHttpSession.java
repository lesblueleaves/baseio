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
package com.generallycloud.baseio.container.http11;

import java.io.IOException;

import com.generallycloud.baseio.collection.AttributesImpl;
import com.generallycloud.baseio.common.UUIDGenerator;
import com.generallycloud.baseio.component.SocketSession;
import com.generallycloud.baseio.protocol.ReadFuture;

public class DefaultHttpSession extends AttributesImpl implements HttpSession {

	private long			createTime	= System.currentTimeMillis();

	private SocketSession	ioSession;

	private long			lastAccessTime = createTime;

	private String			sessionID;

	private HttpContext		context;

	protected DefaultHttpSession(HttpContext context, SocketSession ioSession) {
		this.context = context;
		this.ioSession = ioSession;
		this.sessionID = UUIDGenerator.random();
	}

	protected DefaultHttpSession(HttpContext context, SocketSession ioSession, String sessionID) {
		this.context = context;
		this.ioSession = ioSession;
		this.sessionID = sessionID;
	}

	@Override
	public void active(SocketSession ioSession) {
		this.ioSession = ioSession;
		this.lastAccessTime = System.currentTimeMillis();
	}

	@Override
	public void flush(ReadFuture future) throws IOException {
		ioSession.flush(future);
	}

	@Override
	public long getCreateTime() {
		return createTime;
	}

	@Override
	public SocketSession getIoSession() {
		return ioSession;
	}

	@Override
	public long getLastAccessTime() {
		return lastAccessTime;
	}

	@Override
	public String getSessionID() {
		return sessionID;
	}

	@Override
	public boolean isValidate() {
		return System.currentTimeMillis() - lastAccessTime < 1000 * 60 * 30;
	}

	@Override
	public HttpContext getContext() {
		return context;
	}
}
