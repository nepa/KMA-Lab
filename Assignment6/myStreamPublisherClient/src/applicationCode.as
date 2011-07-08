package
{
	// Imports
	import flash.events.MouseEvent;
	import flash.events.NetStatusEvent;
	import flash.media.Video;
	import flash.net.NetConnection;
	import flash.net.NetStream;
	import flash.display.*;
	
	// Own imports
	import flash.display.Sprite;
	import flash.text.TextField;
	
	// import mx.controls.Alert;
	// import mx.core.UIComponent;
	// import mx.events.FlexEvent;
	
	public class applicationCode extends Sprite
	{	
		/**
		 * Communication Systems for Multimedia Applications Exercises (KMA2011)
		 *  							Exercise 6
		 * 							Summer Term 2011
		 * 
		 * Bashar Altakrouri | Ambient Computing Group | Institute of Telematics | University of Luebeck | www.itm.uni-luebeck.de | altakrouri@itm.uni-luebeck.de
		 * 
		 * OVERVIEW
		 * The aim of this exercise is to be familiar with the basic streaming for recorded videos via flex and red5 applications. 
		 * This is a full client/server example to illustrate the follow
		 * 		- Server side streaming of locally stored video files on the server. 
		 * 		- Client side to play streams broadcasted by the server.
		 * 		- Network and playback events and listeners
		 * 		- Flex GUI controls
		 * 		
		 * TODO
		 * 1. Follow the instructions below and complete the exercise
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
		 *  - AS3 reference: http://livedocs.adobe.com/flash/9.0/ActionScriptLangRefV3/package-summary.html
		 */
		
		/**
		 ************************************* !!! LAB EXERCISE !!!  *************************************
		 * This exercise consists of three parts:
		 * 1. be familiar with red5 applications
		 * 		1.1. start/stop red5 server.
		 * 		1.2. deploy and run red5 applications
		 * 		1.3. debug red5 messages via the Console
		 * 		1.4. file structure for red5 applications
		 * 		1.5. basic configuration and property files for red5 applications "WEB-INF" contents
		 * 		
		 * 2. be familiar with simple flex applications
		 *   	2.1. file structure for flex applications i.e. ".as" and ".mxml"
		 * 		2.2. deploy flex applications
		 * 
		 * 3. complete the exercise following the comments and hints to
		 * 
		 * 		**(On the server-side/red5 application - "myStreamPublisher")**
		 * 		3.1. review the "myStreamPublisher" application basic file and class strcture.
		 * 		3.2. create a folder called "streams" under "WebContent" and add the sample ".flv" video files to the "streams" folder.
		 * 			- Hints:
		 * 				- The name of the video files will assemble the names of the streams broadcasted by the server. 
		 *  
		 *  	**(On the client-side/flex application - "myStreamPublisherClient")**
		 * 		3.3. complete "onCreationComplete" method.
		 * 		3.4. complete all call back methods as follows:
		 * 			- complete the "onConnectionNetStatus" method to alert the user with network status 
		 * 			- complete the "onClickPlayBtn", "onClickPauseBtn", "onClickResumeBtn", and "onClickTogglePauseBtn"  methods
		 * 		3.5. complete the "setupVideo" method to setup the stage to play the incomming streams from the server
		 * 		3.6. complete the "ns_onMetaData' method
		 * 	
		 * 4. Homework: Create your interactive TV streaming channel by using your gained knowledge from exercises "5" and "6", as follow:
		 *  	- your application is a simple platform to view video streams and share live comments about them.
		 *  	- build a new red5 project called "shareMyVideoView" and flex client called "shareMyVideoViewClient"
		 *  	- the user can connect and disconnect to the streaming server at any time
		 * 		- allow the user to choose and play any of 3 video streams
		 * 		- Allow basic user functionality i.e. clearing the comment section, play, pause and close video stream
		 * 		- the user can send share live comments about the played stream with other viewers
		 *  	- Messages are seen by all connected users.
		 * 		- categorize the comments based on the stream using color-coding. 
		 * 		- the user can set his/her name
		 *		- present the viewer information about the played stream i.e. duration, framerate, hight, with, video codec, and video data rate.
		 * 		- make sure you have the right GUI "graphical user interface" for your client application
		 * 		- feel free to add any important / cool feature
		 * 					
		 * */
		
		
		
		// network properties
		private var connection: NetConnection;
		private var inStream: NetStream;
		private var videoURL: String = "KMA_sample_video1.flv"; // Video name without file extension
		
		// device properties
		
		// video properties
		private var inVideo: Video;
		
		// wrapper flex components
		// you cannot add video directly to flex ui you have to have a wrapper
		// private var inVideoWrapper: UIComponent;
		
		public function applicationCode()
		{
            var tf:TextField = new TextField();
            tf.text = "Constructor of applicationCode was called.";
            tf.width = 400;
            addChild(tf);
            		
			onCreationComplete();
			
//			// ===================================================================================
//			// *** YOUR CODE HERE ***
//			var tf:TextField = new TextField();
//			tf.text = "Event onCreationComplete was called.";
//			tf.width = 400;
//			tf.y = 10;
//			addChild(tf);
//			
//			// Establish the connection with the server-side application (using the RTMP protocol)
//			connection = new NetConnection();
//			connection.connect("rtmp://localhost/Assignment6");
//			//connection.client = this;
//			
//			connection.addEventListener(NetStatusEvent.NET_STATUS, onConnectionNetStatus);					  
//			// ===================================================================================			
//		
//			connection.client = this;
		}
		
		/*
			Called right after loading the flash container on the page. This is the first method to be called. 
			Here goes all the instantiations and configurations of your client
		*/
		public function onCreationComplete() : void {
			
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			*	TODO 3.3: 
			*		- establish the connection with the server-side application "myStreamPublisher" using the RTMP protocol
			*		- add an event listner to listen to all NetStatusEvents 
			*		- review the "myStreamPublisherClient.mxml". You are provided with a number of play control buttons but you can
			*		  have your own if you like. 
			*		- add event listners to your play control buttons (play, pause, resume, and togglePause)
			* 	Hints:
			*		- to add event listener use "addEventListener" method. You will need "MouseEvent.CLICK" and "NetStatusEvent.NET_STATUS"
			*		- you have been provided with 4 button callback methods called onClickPlayBtn, onClickPauseBtn, onClickResumeBtn, 
			*		  and onClickTogglePauseBtn
			*/
		
			// ===================================================================================
			// *** YOUR CODE HERE ***
			var tf:TextField = new TextField();
			tf.text = "Event onCreationComplete was called.";
			tf.width = 400;
			tf.y = 10;
			addChild(tf);
			
			// Establish the connection with the server-side application (using the RTMP protocol)
			connection = new NetConnection();
			connection.connect("rtmp://localhost/Assignment6");
			connection.client = this;
			
			connection.addEventListener(NetStatusEvent.NET_STATUS, onConnectionNetStatus);
			
			// Create user interface
			drawPlayButton();
			drawPauseButton();					  
			// ===================================================================================								
		}
		
		
		//callback method for network status events
		public function onConnectionNetStatus(event:NetStatusEvent) : void {
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			*	TODO 3.4: complete "onConnectionNetStatus" method 
			*		- Alert the user on successful conection or unsuccessful connection
			* 		- on successfull connection call the "setupVideo()" method.
			*		- add a streaming status label to your interface to show the current playing status of the stream 
			*		(video playing, paused, resumed, and stream not found)
			* 		- add a general network status label to show the latest network event status to the user at all times.
			*	Hints:
			*		- use Alert.show("your message", "Information");
			*		- a successful connection will have the following code "NetConnection.Connect.Success"
			*		- network status code is returned in event.info.code object
			*		- for playing and pauseing status review the "code property", see the following reference
			*		- reference: http://www.adobe.com/livedocs/flash/9.0/ActionScriptLangRefV3/flash/events/NetStatusEvent.html#info
			*/
		
			// ===================================================================================
			// *** YOUR CODE HERE ***	
			
			// Connection status
			var conStatusLabel:TextField = new TextField();
			conStatusLabel.width = 400;
			conStatusLabel.y = 20;
			addChild(conStatusLabel);
			
			if (event.info.code == "NetConnection.Connect.Success")
			{
				conStatusLabel.text = "Successful connection.";
				
				setupVideo();
			}
			else if (event.info.code == "NetConnection.Connect.Failed")
			{
				conStatusLabel.text = "Unsuccessful connection.";
			}
						
			// Streaming status
			var strStatusLabel:TextField = new TextField();
			
			if (event.info.code == "NetStream.Play.Start")
			{
				strStatusLabel.text = "Playback has started.";
			}
			else if (event.info.code == "NetStream.Play.Stop")
			{
				strStatusLabel.text = "Playback has stopped.";
			}
			else if (event.info.code == "NetStream.Play.Failed")
			{
				strStatusLabel.text = "Playback failed.";
			}
			else if (event.info.code == "NetStream.Play.StreamNotFound")
			{
				strStatusLabel.text = "Stream could not be found.";
			}
			else if (event.info.code == "NetStream.Pause.Notify")
			{
				strStatusLabel.text = "Stream is paused.";			
			}
			else if (event.info.code == "NetStream.Unpause.Notify")
			{
				strStatusLabel.text = "Stream is resumed.";
			}
			
			strStatusLabel.width = 400;
			strStatusLabel.y = 30;
			addChild(strStatusLabel);
			
			// General network connection status
			var genStatusLabel:TextField = new TextField();
			
			if (event.info.code == "NetConnection.Connect.Success")
			{
				genStatusLabel.text = "Connection opened."							
			}
			else if (event.info.code == "NetConnection.Connect.Failed")
			{
				genStatusLabel.text = "Connection failed.";
			}
			else if (event.info.code == "NetConnection.Connect.Rejected")
			{
				genStatusLabel.text = "Connection rejected.";
			}
			else if (event.info.code == "NetConnection.Connect.Closed")
			{
				genStatusLabel.text = "Connection closed.";
			}
			
			genStatusLabel.width = 400;
			genStatusLabel.y = 40;
			addChild(genStatusLabel);
			// ===================================================================================			
		}
		
		// "setupVideo" method to setup the stage to play the incomming streams from the server
		
		private function setupVideo():void{
			
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			*	TODO 3.5: complete "setupVideo" method 
			*		- setup the input stream "inStream" using NetStream(connection)
			*		- add an event listener to the "inStream"
			*		- setup the incommping video "inVideo" using Video()
			*		- attach the stream to the video using the "attachNetStream" method
			*		- add the video as a child to the UIComponent "inVideoWrapper" by initialising 
			*		  the "inVideoWrapper" using "UIComponent()"
			*		then attach the video as a child, then add the wapper to the interface stage using 
			*		"this.addElement()" method
			*/
			
			// ===================================================================================
			// *** YOUR CODE HERE ***
			var tf:TextField = new TextField();
			tf.text = "Method setupVideo was called.";
			tf.width = 400;
			tf.y = 80;
			addChild(tf);
			
			inStream = new NetStream(connection);			
			inStream.addEventListener(NetStatusEvent.NET_STATUS, onConnectionNetStatus);
			// inStream.addEventListener(NetStatusEvent.NET_STATUS, ns_onMetaData);
			
			inVideo = new Video();			
			inVideo.attachNetStream(inStream);
			
			this.addChild(inVideo);
			
			// inStream.bufferTime = 30;
			
			// inStream.play(videoURL);
			
			// inVideoWrapper = new UIComponent();			
			/// inVideoWrapper.addChild(inVideo);
			
//			addChild(inVideo);
//			
//			inStream.bufferTime = 30; // set stream buffer to 5 seconds
//			
//			inStream.play(videoURL);
//			
//			inVideo.width = 100;
//			inVideo.height = 100;
//			
//			inVideo.x = 0;
//			inVideo.y = 0;
			
			// ===================================================================================						
		}
		
		// UI controls listeners
		
		// call this method on Play button click
		private function onClickPlayBtn(event:MouseEvent):void
		{
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			*	TODO 3.4: complete "onClickPlayBtn" method 
			* 		- set "inStream" buffer time
			*		- play the sample stream in the "streams" folder in your servier-side application
			*	Hints:
			*		- when you play a live or recorded stream, you must set a buffer time for the stream to play correctly.
			*		  The buffer time must be at least .1 seconds, but it can be higher. Add the following line to your code 
			*		  (ns is the name of the NetStream object)
			*	  	- review closely the bufferTime property, mainly the differences between the recorded and live streaming buffers. 
			* 		- "bufferTime" property specifies how long to buffer messages before starting to display the stream. 0.1 is the 
			*		  default value. To play a server-side playlist, set bufferTime to at least 1 second. If you experience playback
			*		  issues, increase the length of bufferTime. Recorded content To avoid distortion when streaming pre-recorded 
			*		  (not live) content, do not set the value of Netstream.bufferTime to 0.1  	
			*		- to play video files on Flash Media Server, specify the name of the stream without a file extension 
			*		  (for example, bolero). To play MP3 files, use mp3: before the stream name. To play the ID3 tags of MP3 
			*		  files, use id3: before the stream name. 
			* 		- review play(), close(), pause(), and resume() "NetStream" methods 
			*		- to play and resume video smoothly on red5, you might need to test with different buffer time to 
			*		  optain best results. Try (0, 0.1, 5, and 30) and find out what is best?
			*/
			
			// ===================================================================================
			// *** YOUR CODE HERE ***	
			inStream.bufferTime = 30; // set stream buffer to 30 seconds
			
			inStream.play(videoURL);
			
			inStream.client = {};
			inStream.client.onMetaData = ns_onMetaData;	
			// ===================================================================================		
		}
		
		
		// call this method on Pause button click
		private function onClickPauseBtn(event:MouseEvent):void
		{
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			*	TODO 3.4: complete "onClickPauseBtn" method 
			* 		- pause the incomming stream 
			*	Hints:
			*		- Calling the pausing method does nothing if the video is already paused.
			*/
			
			// ===================================================================================
			// *** YOUR CODE HERE ***	
			inStream.pause();
			// ===================================================================================				
		}
		
		
		// call this method on Resume button click
		private function onClickResumeBtn(event:MouseEvent):void
		{
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			*	TODO 3.4: complete "onClickResumeBtn" method 
			* 		- resume the incomming stream 
			*	Hints:
			*		- Calling the resume method if the video is already playing, calling this method does nothing.
			*/
			
			// ===================================================================================
			// *** YOUR CODE HERE ***
			inStream.resume();	
			// ===================================================================================								
		}
		
		// call this method on TogglePause button click
		private function onClickTogglePauseBtn(event:MouseEvent):void
		{
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			*	TODO 3.4: complete "onClickTogglePauseBtn" method 
			* 		- resume/pause the incomming stream 
			*	Hints:
			*		- Use "togglePause" method
			*		- The first time you call this method, it pauses play; the next time, it resumes play. 
			*		  You could use this method to let users pause or resume playback by pressing a single button. 
			*/
			
			// ===================================================================================
			// *** YOUR CODE HERE ***
			inStream.togglePause();	
			// ===================================================================================		
		}
		
		
		/*Establishes a listener to respond when Flash Player receives descriptive information embedded in the video being played. 
		For information about video file formats supported by Flash Media Server, see the www.adobe.com/go/learn_fms_fileformats_en.
		
		onMetaData is actually a property of the NetStream.client object. The property is listed in the Events section because it
		responds to a data event, either when streaming media using Flash Media Server or during FLV file playback. For more
		information, see the NetStream class description and the NetStream.client property. You cannot use the addEventListener()
		method, or any other EventDispatcher methods, to listen for or process onMetaData as an event. Define a single callback
		function and attach it to one of the following objects:
		
		The object that the client property of a NetStream instance references.
		An instance of a NetStream subclass. NetStream is a sealed class, which means that properties or methods cannot be added
		to a NetStream object at runtime. You can create a subclass of NetStream and define your event handler in the subclass.
		You can also make the subclass dynamic and add the event handler function to an instance of the subclass.
		*/
		private function ns_onMetaData(item:Object):void {
			/* -----------------------------------------------------------------------------------
			* !!! LAB EXERCISE !!!
			* ------------------------------------------------------------------------------------
			*	TODO 3.6: complete "ns_onMetaData" method.  
			* 		- resize the video control based on the stream video size
			*		- center the video instance of the the stage.
			*		- create info panel to display all relevent information about the played stream to the user 
			*		   i.e. duration, framerate, hight, with, video codec, and video data rate. 
			*	Hints:	
			* 		- review http://livedocs.adobe.com/flex/3/html/help.html?content=Working_with_Video_17.html
			*		- you will need to use the "stage" specially both properties "stageWidth" and "stageHeight"
			*/
			
			// ===================================================================================
			// *** YOUR CODE HERE ***
			
			// Resize video to fit metadata from info object (item)
			inVideo.width = item["width"];
			inVideo.height = item["height"];
			
			// Center video control on user interface
			inVideo.x = this.x / 2;
			inVideo.y = this.y / 2;
			
			// Build string out of metadata
			var metaDataString:String;
			var key:String;
			for (key in item)
			{
				metaDataString += key + ": " + item[key] + ", ";
			}
			
			var tf:TextField = new TextField();
			tf.text = metaDataString;
			tf.width = 400;
			tf.y = 270;
			addChild(tf);	
			// ===================================================================================		
		}
		
		private function drawPlayButton():void {
			var textLabel:TextField = new TextField();
		    var button:Sprite = new Sprite();
			button.graphics.clear();
			button.graphics.beginFill(0xD4D4D4); // grey color
			button.graphics.drawRoundRect(0, 300, 80, 25, 10, 10); // x, y, width, height, ellipseW, ellipseH
			button.graphics.endFill();
			textLabel.text = "Play";
			textLabel.x = 10;
			textLabel.y = 300;
			textLabel.selectable = false;
			button.addChild(textLabel);
			addChild(button);
			button.addEventListener(MouseEvent.MOUSE_DOWN, onClickPlayBtn);
		}
		
		private function drawPauseButton():void {
			var textLabel:TextField = new TextField();
		    var button:Sprite = new Sprite();
			button.graphics.clear();
			button.graphics.beginFill(0xD4D4D4); // grey color
			button.graphics.drawRoundRect(100, 300, 80, 25, 10, 10); // x, y, width, height, ellipseW, ellipseH
			button.graphics.endFill();
			textLabel.text = "Pause";
			textLabel.x = 110;
			textLabel.y = 300;
			textLabel.selectable = false;
			button.addChild(textLabel);
			addChild(button);
			button.addEventListener(MouseEvent.MOUSE_DOWN, onClickPauseBtn);
		}		
	}
}
