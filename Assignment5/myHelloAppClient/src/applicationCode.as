package
{
	// Imports
	import flash.events.MouseEvent;
	import flash.events.NetStatusEvent;
	import flash.net.NetConnection;
	import flash.net.NetStream;
	import flash.net.Responder;
	import flash.net.SharedObject;
	import flash.display.Sprite;
	import flash.text.TextField;
	
//	import mx.core.UIComponent;	
//	import mx.controls.Alert;
//	import mx.events.FlexEvent;
		
	public class applicationCode extends Sprite
	{
		/**
		 * Communication Systems for Multimedia Applications Exercises (KMA2011)
		 *  							Exercise 5
		 * 							Summer Term 2011
		 * 
		 * Bashar Altakrouri | Ambient Computing Group | Institute of Telematics | University of Luebeck | www.itm.uni-luebeck.de | altakrouri@itm.uni-luebeck.de
		 * 
		 * OVERVIEW
		 * The aim of this exercise is to be familiar with the basic strcuture for red5 and flex applications.
		 * We have a full client/server example to illustrate the follow
		 * 		- Server method invokation from the client side
		 * 		- Client method invokation from the server side
		 * 		- The use of SharedObjects to send cross clients messages
		 * 		- The use of Responder objects
		 * 		- Flex application basic GUI controls i.e. TextArea, Button, TextInput
		 * 		- Simple GUI control listners
		 * 		- Network status Listners
		 * 		
		 * TODO
		 * 1. Follow the instructions bellow and complete the exercise
		 * 2. Complete and run both Flex and Red5 projects 
		 * 
		 * Hint:
		 * - Read the sections marked: !!! LAB EXERCISE !!!
		 * - Complete the sections marked: *** YOUR CODE HERE ***
		 * - Have fun!
		 * 
		 * Note: This project is intentionally incomplete and should be finished by the student.
		 * To help guide your work, this project includes a partial structure along with 
		 * embedded comments, hints and links. An accompanying in-class presentation
		 * will provide the background information and instructions necessary to 
		 * complete this exercise.
		 *
		 * REFERENCES
		 * 	- AS3 GUI controls: http://livedocs.adobe.com/flash/9.0/ActionScriptLangRefV3/fl/controls/package-detail.html
		 * 	- AS3 reference: http://livedocs.adobe.com/flash/9.0/ActionScriptLangRefV3/package-summary.html
		 */
		
		/**
		 ************************************* !!! LAB EXERCISE !!!  *************************************
		 * this exercise consists of three parts:
		 * 1. be familiar with red5 applications
		 * 		a. start/stop red5 server.
		 * 		b. deploy and run red5 applications
		 * 		c. debug red5 messages via the Console
		 * 		d. file structure for red5 applications
		 * 		e. basic configuration nad property files for red5 applications "WEB-INF" contents
		 * 		
		 * 2. be familiar with simple flex applications
		 *   	a. file structure for flex applications i.e. ".as" and ".mxml"
		 * 		b. deploy flex applications
		 * 
		 * 3. complete the exercise following the comments and hints to
		 *   	**(On the client-side/flex application - "myHelloAppClient")**
		 *  	3.1. complete "onCreationComplete" method - part 3.1.
		 *   	3.2. complete "onCreationComplete" method - part 3.2.
		 * 		3.3. enable message sharing between clients using SharedObjects by completing part 3.3 in "onCreationComplete" method
		 * 
		 * 4. Homework: Build a simple public chatting application where users can send messages to the chatting room
		 * 		- build a new red5 project called "simpleChat" and flex client application called "simpleChatClient"
		 * 		- the user can set his/her name
		 * 		- the user can send messages to all other connected users.
		 * 		- the user can reset his chatting room at anytime
		 * 		- the user can also connect and disconnect to the server at any time
		 * 		- make sure you have the right GUI "graphical user interface" for your client application
		 * 		- feel free to add any other important / cool feature		
		 */ 					

		// Public Variables
		private var connection:NetConnection;
		private var ro:Responder = new Responder(onResult,onError); // to respond to server replays, onResult: if successful results, OnError: if errors happened
		private var so:SharedObject;
		
		public function applicationCode()
		{
            var tf:TextField = new TextField();
            tf.text = "Constructor of applicationCode was called.";
            tf.width = 400;
            addChild(tf);
            		
			onCreationComplete();
		}
		
		/*
			called right after loading the flash container on the page. This is the first method to be called. 
			Here goes all the instantiations and configurations of your client
		*/
		public function onCreationComplete() : void {
            var tf:TextField = new TextField();
            tf.text = "Event onCreationComplete was called.";
            tf.width = 400;            
            tf.y = 10;
            addChild(tf);            		
			
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			*	TODO 3.1: 
			*		- add network event listener to listen to network status and alert if the conencted to the server was successful or not
			*		- review the "myHelloAppClient.mxml". You are provided with a number of GUI controls i.e. buttons but you can
			*		  have your own if you like.
			*		- complete "onConnectionNetStatus" method
			* 	Hints:
			*		- addEventListener(NetStatusEvent.NET_STATUS, onConnectionNetStatus);
			*
			*	TODO 3.2:
			*		- call/invoke a server side method called "addOne" which simply adds "1" to the passed value
			* 	Hints:
			* 		- review at the "addOne" method on the server side "myHelloApp -> Java Resources -> src -> org.red5.core -> Application.java"
			* 		- use the "connection" object "call" method to call any method on the server side i.e. connection.call("MethodName", YouResponderObject, AnyNumber);
			* 		- first review the "ro" object and then use it as responder
			* 		- complete "onResult" method to recive results back from the server and alert the user
			*		- review "onError" method
			*
			*	TODO 3.3: enable message sharing between clients using SharedObjects
			* 		- the user can write a message in the TextInput "InputTxt", this message is then sent to all connected clients on "sendBtn" button press.
			* 		- add a listener to the button "sendBtn"
			*		- complete "onClickSendBtn" method to call the server side method "broadcastMessageToClients"
			*		- complete "receiveBroadcastedMessages" method which is invoked from the server side to print the results to TextArea "outputTxtArea"
			* 	Hints:
			*		- review the "myHelloAppClient.mxml".
			*	   	- to add a listener to a button use YourButtonName.Listener(MouseEvent.CLICK, onClickSendBtn);
			*  	 	- Review at the "broadcastMessageToClients" method on the server side "myHelloApp -> Java Resources -> src -> org.red5.core -> Application.java"	
			* 	
			*/
			
			// establish the connection with the server-side application "myHelloApp" using the RTMP protocol
			connection = new NetConnection();
			connection.connect("rtmp://localhost/Assignment5");
			connection.client = this;
			
			// ===================================================================================
			// *** YOUR CODE HERE ***
			connection.addEventListener(NetStatusEvent.NET_STATUS, onConnectionNetStatus);
			
			connection.call("addOne", ro, 100);						
					
			// ===================================================================================
		}
		
		
		//callback method for network status events
		public function onConnectionNetStatus(event:NetStatusEvent) : void {
			
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			* TODO 3.1: if connection is successful then alter the user with "Successful Connection" 
			* if not then "Unsuccessful Connection"
			*	Hints:
			*		- use Alert.show("your message", "Information" or "Error" or "AnyTitle");
			*		- a successful connection will have the following code "NetConnection.Connect.Success"
			*		- network status code is returned in event.info.code object
			*/
			
			
			// ===================================================================================
			// *** YOUR CODE HERE ***
			var tf:TextField = new TextField();
            
            if (event.info.code == "NetConnection.Connect.Success")
            {
            	tf.text = "Successful connection.";
            }
            else
            {
            	tf.text = "Unsuccessful connection.";
            }
            
            tf.width = 400;
            tf.y = 20;
            addChild(tf);
			// ===================================================================================
		
			
		}
		
		// Deal with the server's results
		public function onResult(responder:String): void{
		
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			* TODO 3.2: Alert the user with the results
			*	Hints:
			*		- use Alert.show("your message");
			*/
						
			// ===================================================================================
			// *** YOUR CODE HERE ***
			var tf:TextField = new TextField();
			tf.text = "Result from server: " + responder;            
            tf.width = 400;
            tf.y = 30;
            addChild(tf);
			// ===================================================================================
		
		}
		
		// Deal with errors in the responder object
		public function onError(e:Object): void{
			var tf:TextField = new TextField();
			tf.text = "Got an error: " + e.description;           
            tf.width = 400;
            tf.y = 30;
            addChild(tf);
		}
		
		// call this method on button click
		private function onClickSendBtn(event:MouseEvent):void
		{
			
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			* TODO 3.3: call "broadcastMessageToClients" method on the server to send the message "inputTxt.text" 
			* to all connected clients
			*	Hints:
			*		- use connection.call("MethodName", null, text);  // without responder
			*		- try to implement the same call but with responder object if you can. (Optional), very similar to what you did 
			*		in TODO 3.2.
			*/
						
			// ===================================================================================
			// *** YOUR CODE HERE ***
			// ===================================================================================			
		}
		
		// this method is called from the server side to send cross messages between clients using the shared object
		public function receiveBroadcastedMessages(msg:String):void
		{
			
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			* TODO 3.3: add the results from the server into the TextArea "outputTxtArea" 
			* 	Hints:
			*		- use TEXTAREA.text = or outputTxtArea.text +=
			* 		- use "\n" for new line if needed
			*		- review at the "broadcastMessageToClients" method on the server side "myHelloApp -> Java Resources -> src -> org.red5.core -> Application.java"
			*/
			
			// ===================================================================================
			// *** YOUR CODE HERE ***	
			// ===================================================================================
		
		}
	}
}