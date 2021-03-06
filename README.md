# Blast

[Product website](http://blastcse.github.io/) </br>
[Developer website](http://mkhuat.github.io/blast-dev/)

![alt tag](https://github.com/mkhuat/blast-dev/blob/gh-pages/app.PNG)

##Final Release
[SRS Postmortem](https://drive.google.com/a/uw.edu/file/d/0B3PwQkCDyLnwT05zZndVNHJEd28/view) document.</br>
[Final Presentation](https://drive.google.com/a/uw.edu/file/d/0B3PwQkCDyLnwRGFYcnZpaS1HQmM/view) slides.</br>
[SRS](https://drive.google.com/a/uw.edu/file/d/0B3PwQkCDyLnwd1N3cUdFLUhSemM/view) and [SDS](https://drive.google.com/a/uw.edu/file/d/0B3PwQkCDyLnweGVhTm1COGN4c28/view) updates.</br>
Final [Class Diagram](https://drive.google.com/a/uw.edu/file/d/0B3PwQkCDyLnwUmtrY3R2REFyREE/view).</br>

The only noticeable change from the Release Candidate is a small addition to the toolbar of the main screen. Now at the top, a user will see a toggle that looks like a Google Maps pin. Clicking this switches the events displayed to be sorted by distance, with events closest to the user’s current location appearing first. The toggle icon will then change to a clock, allowing the user to go back to sorting by time, with events happening soon appearing first. Sorting by time is still what the app defaults to upon opening. Otherwise, refer to the information and instructions in the Release Candidate Updates section.</br>

Notes:</br>
- We developed Blast for the Nexus 6. Thus, we guarantee functionality on older phones, but we do not guarantee the same look and feel, as screen size differences may throw off aspects of the UI.
- The app is no longer in development mode, but users can still use the test account `oliver_efugadr_queen@tfbnw.net` with password `blast123` to sign in if they wish.
- Users need Google Apps installed on their device for location services. See the Installing Google Apps on Genymotion subsection if using the Genymotion emulator to run our app.
- For thorough testing, extra flags and test variables were added. To avoid cluttering our master branch and release code, our most current tests are located in the Test branch. See more instructions under the Unit Testing section.

##Release Candidate Updates
[SRS](https://drive.google.com/a/uw.edu/file/d/0B3PwQkCDyLnwZk8yU3hPYVdqems/view) and [SDS](https://drive.google.com/a/uw.edu/file/d/0B3PwQkCDyLnwLXBwNW54REpmRVk/view) updates. </br>

All of our functionality is complete. Upon logging in with Facebook, a first-time user will be greeted with tutorials on each screen. From the main screen, they can see all events happening around them that are occurring within the next 24 hours, with events happening soon appearing first. Events now contain a category, which supplies a preset picture to bring more life to the main screen. From the main screen, users can manage the events they are attending or have created via the left drawer. Clicking on an event in the main screen (or the left drawer) takes the user to the detail page. This screen lists all the relevant information about the event, including Facebook profile pictures of current attendees and a map showing the pinned location of the event. A user has the option to join or leave an event (if they had already joined) from this screen. If the user created the event, they will have the option to modify the event, upon which they can change information or delete the event. Going back to the main screen, a user can create an event by tapping on the plus icon. Input fields are robust in preventing invalid or too-long of input. The location field provides suggestions of places so that we can extract a real address, but the user is allowed to enter their own location if they desire (we will not show a map in the detail view in this situation). Thus, a user can do everything that we envisioned they would be able to in our app. The flow is simple and intuitive. </br>
- Note: events that have "expired" will stop appearing in the main view. Additionally, users are allowed to create events that will happen further than 24 hours in the future, but these events will not be displayed in the main view until they are happening within 24 hours.
- Because the app is still in development, users must use the test account `oliver_efugadr_queen@tfbnw.net` with password `blast123` to sign in. </br>

The previous dependency on an IP address for location services has been removed. However, users now need to have Google Apps installed on their device. For the Genymotion emulator, these are the instructions to install Google Apps:</br>
###Installing Google Apps on Genymotion
 1. Upgrade Genymotion and VirtualBox to the latest version</br>
 2. Make sure your computer has the `adb` command</br>
 3. Download two zip files: [ARM Translation Installer v1.1](http://filetrip.net/dl?4SUOrdcMRv) and [Google Apps for Android version 5.0](https://www.androidfilehost.com/?fid=95784891001614559) or later</br>
 4. Install ARM Translation
- Open Genymotion emulator and drag/drop the Genymotion-ARM-Translation_v1.1.zip file over the emulator on the home screen
- A dialog will appear and show as file transfer in prgress
- Wait for another dialog to appear and click OK
- Wait for the third dialog that shows as the file has been successfully transfered
- Reboot the virtual device through command line, with `adb reboot`
- Be patient and wait around 10-20 minutes until it's done rebooting (you can tell it's done from the command line by prompting into a new line)</br>

 5. Install gapps</br>
- Stay on the home screen on Genymotion. Drag and drop the gapps-lp-20141109-signed.zip file onto the screen
- Repeat the same substeps from step 4 regarding dialogs and rebooting
- Be even more patient! This time you will have to wait for 30-40 minutes</br>

 6. Update all the Google Maps through Google Play Store</br>
- After the virtual device is rebooted up, you will see a dialog saying that “Unfortunately, Google Play service has stopped.” Click OK and continue on
- If you don't see any dialog popping up saying that "Google Play Services has stopped," close your virtual machine and restart your genymotion device. Then you will see another dialog saying that you need to update your application
- Open your Google Play Store and update all your installed apps. Remember to update Google Play Services (Most important step!)</br>

 7. You're done!</br>

##Feature-Complete Updates
[SRS](https://drive.google.com/a/uw.edu/file/d/0B3PwQkCDyLnwNWpsT1R6UHRMUVE/view) and [SDS](https://drive.google.com/a/uw.edu/file/d/0B3PwQkCDyLnwa2ZFdG41X0Q5djQ/view) updates. </br>

As of the feature-complete release, a user is able to login with Facebook, view events happening within the upcoming 24 hours, attend and leave an event, create an event, and modify or delete an event they have created. </br>
- Note: events that have "expired" will stop appearing in the main view. Additionally, users are allowed to create events that will happen further than 24 hours in the future, but these events will not be displayed in the main view until they are happening within 24 hours.</br>

When a user is creating an event, the location field will give suggestions for real locations that match the typed string. This allows us to extract latitude and longitude coordinates. We also get the latitude and longitude of the user’s device, but currently this is hardcoded to Seattle for convenience. We have refined our UI and added tutorials for each different screen a user will see that appears the first time they login.
 </br>
- Because the app is still in development, users must use the test account `oliver_efugadr_queen@tfbnw.net` with password `blast123` to sign in. </br>
- Because the app is still in development, to activate location services on their device, users must go to the [Blast Developer Console](https://console.developers.google.com/apis/credentials/key/0?project=vivid-art-123423) in Incognito mode (right click on the link and open in incognito window). Sign in using the account `blastcse@gmail.com` with password `blast123`. At the bottom of the page, enter your local IP Addresss and click save. Note: access may take up to 5 minutes to be granted and direct link won't work if you switch Google accounts.</br>

Test coverage tool: scroll down to the Unit Testing section.

##Beta Updates
[SRS](https://drive.google.com/file/d/0B3PwQkCDyLnwbWJ4SnRZYzhYVzg/view) and [SDS](https://drive.google.com/file/d/0B3PwQkCDyLnwYTZEZTVPOUV4UWs/view) documents have been updated! </br>

We have two use cases functioning at this time: logging in with Facebook and creating an event. After logging in, the user is able to view all the events currently in the database. They can click on an event to view details, but they are not yet able to "attend" an event. They are able to create their own events though, which are sent to the database and thus displayed in their view. </br>
- Because the app is still in development, users must use the test account `oliver_efugadr_queen@tfbnw.net` with password `blast123` to sign in. </br>

Scroll down to the Design Patterns section to view the update to our Developer Documentation.

###Developer Documentation
Install Android Studio and run the following from the desired directory</br>
- To work on the latest stable release</br>
```
git clone https://github.com/sheend/blast.git
```
- To work on the current release</br>
```
git clone https://github.com/sheend/blast.git
git checkout develop
```
All project files are now ready to be opened, compiled, built and run in Android Studio </br>
The project can run on the Android built-in emulators or third party emulators such as [Genymotion](https://www.genymotion.com/)

##Directory Breakdown
Inside `app/src/main`
- `/res` contains all resources including drawables, menus, layouts, and values
- `/java/cse403/blast` contains all Activities
- `/java/cse403/blast/Model` contains all Java objects for the project
- `/java/cse403/blast/Support` contains all extra Java classes

##Development Style
[Java style guidelines](https://google.github.io/styleguide/javaguide.html) </br>
[Android style guidelines](http://developer.android.com/design/index.html) </br>
- Activities names based on the general function i.e. `CreateEventActivity.java`
- Connected layout files have the same basic name i.e. `activity_create_event.xml` and `content_create_event.xml`
- JavaDocs style comments for method headers, in-line comments for novel additions
- Data structures must have representation invariants that can be tested

##Design Patterns
With	any	graphical	user	interface,	it	becomes	important	to	separate	out	different	parts	of	the application	logic,	making	the	code	more	understandable	and	easier	to	update.	The	most popular	pattern	used	in	this	type	of	software	design	is	Model-View-Controller. While	MVC	is	a	more	commonly	used	pattern,	it	is	a	little	pedantic	and	forces	a	one-size-fits-all	philosophy	on	the	software. Because	Activities in Android	are	not unambiguously	just	one	of	the	three	parts in MVC, Blast uses	the	**Model-View-Presenter**	pattern.	Following	is	a	small	description	of MVP,	where	the	Activities	can	be	better	described	as	a	middle	man.
- Model: is	a	data	access	layer	with	necessary	information	storage	and	database	requests
- View: is	a	layer	that	displays	data	and	reacts	to	user	actions
- Presenter: is	a	layer	that	provides	the	View	with	data	from	the	Model	and	handles	any	background	tasks

Another design pattern part of this project is the **Singleton** pattern. For the FacebookManager file, we will be using this pattern because it allows for centralized management of resources and as well as a global point of access to itself. This is especially important because of the authentication details that Facebook provides need to be accesible at all points of use and in all Activity files in order for the user to not be logged out every time they switch pages. 

##Bug Tracking
Compiled list of bugs, along with open tickets to work on: [Github Issue Tracker](https://github.com/sheend/blast/issues) </br>
Once a ticket has been completed, any team member can close the issue </br>

##Unit Testing
To isolate our tests, the most recent tests now exist solely in the Test branch. Switch to that branch.</br>
Tests are located in `app/src/androidTest/java/cse403/blast`</br>
To run a unit test in Android Studio:
- Click on Build Variants on the bottom left corner
- Select `Android Instrumentation Tests` or `Unit Tests` as the Test Artifact: we have both kinds of tests
- Navigate to a test file i.e. `EventTest.java`
- Right click on the class name and click 'Run ExampleUnitTest'

To run our test coverage tool, select all of our tests, right click, and choose "Run with Coverage."
- As of the Feature-Complete release, we have 10% line coverage. This is much lower than we expected due to the fact that the coverage tool does not recognize our activity tests, which contain the bulk of our code. This is because the coverage tool only considers JUnit tests, while our activity tests are Instrumentation tests.

##Automated Testing
We have set up Jenkins to build the project daily: [Jenkins dashboard](http://54.191.110.63:8080/)
- It's automated to run every day at 12:00am and email the blast group if a build fails
- To see build history, click the build history tab on the left side bar
- To execute a build, go to the `Blast_App` job and click build now on the left side bar
- To configure Jenkins's settings, from the dashboard go to Manage Jenkins -> Configure System and find `Gradle version 2.11-rc-3` and `JDK version 7u80`

##New Release
For each milestone release, we will merge all branches to the master and then build that for the user. We will also tag the master branch with the following tags so all versions can be reverted to, if necessary. These tags are the primary method of documenting releases. After every release, we will make a new `APK` that can be downloaded by a user for the latest stable version of the app. The new version will be visible to the world on the master branch, and we will double check by building the repository in a new directory to make sure everything work fine. </br>
- Zero Feature: `v0.0`
- Beta: `v1.0`
- Feature Complete: `v1.5`
- Final: `v2.0` </br>

In order to do a testing release, push to the Git `develop` branch. At the end of each day, we will run and test the code currently on `develop`, and then accordingly assign tickets and issue bug reports. This way the software can be tested without potentially breaking the publically released version.</br></br>


#####Welcome to the team :punch:
