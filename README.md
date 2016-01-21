This is an Android Application that aims at intercepting the traffic from android device and detect URLs that appear to track the user while browsing.

Basic Logic: The device traffic is directed to a proxy server that is locally hosted on the device. The URL requests throughout the device, via Browser or apps can be tracked here with vital information like host, referrer, cookies atc.
For now we are just maintaing the frequency count of every unique URL request.

The code architecture has three major parts:

1. The first section is responsible for user interaction. It has simple functions like turning the proxy server on or off and viewing the URLs tracked, their count and status (BLOCKED/UNBLOCKED).
2. Android Service Class: This is responsible for running the server and intercept traffic even after closing of the app.
3. Database Handlers: As soon as the service is stopped by user, the tracked info gets lost if not saved, the handlers make sure that URL data gets stored and is easily accesible when needed.

