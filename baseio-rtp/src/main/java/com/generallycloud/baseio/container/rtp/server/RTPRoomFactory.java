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
package com.generallycloud.baseio.container.rtp.server;

import com.generallycloud.baseio.collection.IntObjectHashMap;

public class RTPRoomFactory {

	private IntObjectHashMap<RTPRoom> rooms = new IntObjectHashMap<RTPRoom>();

	public synchronized RTPRoom getRTPRoom(int roomID) {
		return rooms.get(roomID);
	}

	public synchronized void removeRTPRoom(int roomID) {
		rooms.remove(roomID);
	}

	public synchronized void putRTPRoom(RTPRoom room) {
		rooms.put(room.getRoomID(), room);
	}
}
