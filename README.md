It is an open source project under GNU General Public License.

For developers:
To build this on your system, you need Android Studio, Java JDK.
Presently, the application creates a proxy server that runs as a service on the device where all the outgoing url requests are intercepted. We are in a state of transitioning the logic by using AndroidVpnService.


The device traffic is directed to a proxy server that is locally hosted on the device. The
URL requests throughout the device, via Browser or apps can be tracked here with vital information
like host, referrer, cookies atc. For now we are just maintaining the frequency count of every unique
URL request.
The code architecture has three major parts:
1. The first section is responsible for user interaction. It has simple functions like turning the
proxy server on or off and viewing the URLs tracked, their count and status
(BLOCKED/UNBLOCKED).
2. Android Service Class: This is responsible for running the server and intercept traffic in the
background, even after closing of the app (by navigating back)
3. Database Handlers: As soon as the service is stopped by user, the tracked info gets lost if not
saved, the handlers make sure that URL data gets stored into database and is easily
accessible when needed.Privacy Badger for Android is a project that aims at extablishing online privacy by blocking third party trackers on Android device. 
This is an Android Application that aims at intercepting the traffic from android device and detect URLs that appear to track the user while browsing.

Code documentation coming soon.
