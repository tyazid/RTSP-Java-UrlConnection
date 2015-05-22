#RTSP-Java-UrlConnection

Full java Implemetation of RTSP (RFC 2326) command protocol.

##What is this?:

Rtsp Java url connection is an extention of java/net framwork allowing to support of rtsp protocol

##How is it disigned:

The RtspURLStreamHandlerFactory class is set as an application's URLStreamHandlerFactory. So, it will be used to construct a stream protocol handler from a protocol name.

java.net.URL.setURLStreamHandlerFactory( new RtspURLStreamHandlerFactory());

####General scheme illustrating this RTSP Protocol handling implementation based on Java net framework.

![ScreenShot](/project/src/com/net/rtsp/rtsp.jpg)

####Runtime resolution of an rtsp url connection

![ScreenShot](/project/src/com/net/rtsp/rtsp2.jpg)

####Used in code:
```
  java.net.URL.setURLStreamHandlerFactory( new RtspURLStreamHandlerFactory());
  //...
   java.net.URL url = new URL("rtsp://...");
    RtspURLConnection urlc = (RtspURLConnection) url.openConnection();
		urlc.connect();
		//...
		urlc.play( );
		//...
		urlc.pause( );
		//...
		urlc.teardown();
```  

##Test:

build your test and run it using an rtsp url ( such as this public and free one rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov) as program parameter.
