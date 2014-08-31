NoTauLabsNag
============

## Dev Setup

set/export ANDROID_HOME environmental variable, point to Android SDK folder


## (NoTauLabsNag) OpenPilot GCS
|
-> *Original* code based on DroidPlanner v1 - https://github.com/arthurbenemann/droidplanner
   https://lh6.ggpht.com/sveD_QnMVCcbY90uEmIOO3LhAWSa2QuN69FFe-TkjavvWSL0gTW-7L_OdHUHL1ZI-w=h900

This only the Makefile and Android GCS directory. You need to place this inside an OpenPilot source tree

Shout out to http://www.technotalkative.com/android-gridview-example/ for the Custom Gridview.

Orignal code by "James Cotton of OpenPilot" before it was changed to ownership of "James Cotton of TauLabs"

Although "Tau Labs prides itself on being open and transparent" and claims to "compassionately support OP 
releases historically", it has become a point of contention in the community that they have chosen to add a 
"Nag Screen" to the "OpenPilot Android GCS" located in the Play Store here: 
https://play.google.com/store/apps/details?id=org.openpilot.androidgcs

This version has removed the nag message from the OpenPilot Android GCS.

Unfortunately although several changes have been made by James Cotton of TauLabs, the source code has not 
been shared back in an "open" fashion as per the Tau Labs mission statement. Details of this posturing can 
be located here: 
http://forums.openpilot.org/topic/38842-getting-the-legacy-android-gcs-up-on-newer-versions-like-130604

The breakdown in honoring their own open source commitments can be captured on their own Google forums: 
https://groups.google.com/d/msg/phoenixpilot/UP5MsLtgSlA/dXX2DEpoap0J
"I compiled androidgcs from next and it didn't worked and looked a lot older than the version on the store, why?" 
- metRo_

The app has historically been kept out of date with little effort made to keep it in sync with current developments: 
https://groups.google.com/forum/#!msg/phoenixpilot/UP5MsLtgSlA/LDeYps20NHkJ
"It supports lots of historical versions (and even OP releases compassionately) but obviously not firmwares after 
the apk was released."

Finding the current repo that matches the Google Play code is difficult at best. For example I have been unable to 
locate the code that contains the current "Nag Screen" as shown below: 
$ grep "Development" . -r
./res/layout/gcs_home.xml: _TextView android:textAppearance="?android:textAppearanceSmall" android:id="@id/textView1" 
android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Development on OpenPilot app 
has moved to Tau Labs. To follow future developement check out forums.taulabs.org. Use the button below to get the 
latest app." /_

These differences can be noted here in video form: https://www.youtube.com/watch?v=YJuyTkoO71M
Immediately after the split from OpenPilot these copyright changes were made to the code: 
https://github.com/TauLabs/TauLabs/commit/f7dce5af863105f13ad1e5db79ccd60ab0cf3865
After the following commit the most recent changes have been privately held by James Cotton: 
http://git.openpilot.org/changelog/OpenPilot?showid=8b94f101073b69f9f097c164a978cf73f2ecfae0&view=fe

A series of *unofficial* statements from TauLabs members has been collected below:
"why should peabody release anything to the scum that slandered him so viciously. So you can take credit for bringing 
Android Legacy back to OP?" - Alpackin (TauLabs)

(response) "you all are transparant and open right?" - Friends of OpenPilot that dislike Nag Screens 

"f**k you" - Alpackin (TauLabs)

"You sure the code you are looking isn't on branch next?
EDIT: ok you are right it isn't, I think peabody124 will have no problem in pushing it, or you can wait for the OP 
android GCS, I think I read on their forum that it is almost ready." - guilhermito (TauLabs)

"The code for the legacy OP versions is separate and has been deleted from their(OpenPilot) repository (although worth 
noting it was entirely written by me). I made a modification that added the message in the legacy app letting people 
know where to find updates and didn't bother publishing that change." - Peabody124 (TauLabs)

"As stated before, OpenPilot support for Android GCS is not a Tau Labs product. We can't control the openness of something 
that is not under the control of the Tau Labs governing body. Everything that falls under control of the Tau Labs governing 
body is perfectly in sync with our stated mission and motto." - Buzz Carlson (TauLabs)

"You say that peabody124 controls that app and I have no reason to doubt you. But you saying that peabody124 controls that 
app, and peabody124 is a member of Tau Labs, therefore Tau Labs controls that app is a logical fallacy." - Buzz Carlson (TauLabs)


