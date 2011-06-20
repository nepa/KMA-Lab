package org.red5.core;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006-2008 by respective authors (see below). All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later 
 * version. 
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along 
 * with this library; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */

import java.util.List;

import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.service.ServiceUtils;
import org.red5.server.api.so.ISharedObject;

/**
 * Sample application that uses the client manager.
 * 
 * Overview: This is the server side of your code
 */
public class Application extends ApplicationAdapter {

	/*
	 * The scope object. A statefull object shared between a group of clients connected to the same context path. 
	 * Scopes are arranged in hierarchical way, so its possible for a scope to have a parent and children scopes. 
	 * If a client connects to a scope then they are also connected to its parent scope. The scope object is used 
	 * to access resources, shared object, streams, etc. That is, scope are general option for grouping things in 
	 * application. The following are all names for scopes: application, room, place, lobby.
	 */
	private IScope appScope;

	
	/** {@inheritDoc} */
    @Override
	public boolean connect(IConnection conn, IScope scope, Object[] params) {
    	
    	// init appScope
    	appScope = scope;
    	
    	// create a sharedobject on server and call it "sharedMessage" under the current scope.
		createSharedObject(appScope, "sharedMessage", false);
		
		return true;
	}

	/** {@inheritDoc} */
    @Override
	public void disconnect(IConnection conn, IScope scope) {
		super.disconnect(conn, scope);
	}
    
    /* Simple method to illustrate how simple is to access the methods on the server side from the client side.
     * if called from the client it adds "1" to the passed argument.
     */
    public double addOne(double a) {

    	return a + 1;
    }
    
    /* Simple method to illustrate how simple is to access the methods on the client side from the server side.
     * Also this uses the SharedObject to send a unified message to all connected clients
     */

    public void broadcastMessageToClients(List<String> params) {

    	ISharedObject so = getSharedObject(appScope, "sharedMessage");
    	
    	// call receiveMessage method on all connected clients
    	so.sendMessage("receiveBroadcastedMessages", params); // send the received parameter back to all connected clients by calling the "receiveBroadcastedMessages" method on the client side 

    }
    
}
