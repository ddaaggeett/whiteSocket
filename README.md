#Welcome to the Blooprint API **source** repository
###Are you the **non-techie** type?  Please **visit our** (incomplete) [**wiki**](https://github.com/ddaaggeett/blooprint-api/wiki) page.
This API is **used by** the [**blooprint.xyz**](http://www.blooprint.xyz) web application.

##An overview for the **web developer**
From [parent module](https://github.com/ddaaggeett/blooprint.xyz), run:

    javac Blooprint.java
    java Blooprint <current blooprint title> <input mode>


Image capture **input mode** = pick one -> "bloop", "blip", "erase", "calibrate"

 - **bloop** = Returns compiled blooprint image - input image contains user-drawn marker to be added
 - **blip** = Returns textarea location (x,y,width,height)
 - **erase** = Returns compiled blooprint image - input image contains user-drawn eraser area
 - **calibrate** = Returns calibration info to be used for image processing. Run any time client hardware [(whiteboard, camera, projector)](https://github.com/ddaaggeett/blooprint.xyz/wiki/Required-Hardware) is set up and stationary

####**calibrate**
1. Once hardware is set in place, the user draws 4 corner marks as accurately as possible in the projection area out to the corner bounds. -> user then hits **calibrate**
2. Run
	`java Blooprint <title> calibrate`
3. Image capture is displayed full screen in web app where the user then clicks 4 points just outside of user drawn corner points.  ie - these 4 click points are to be located on the whiteboard in the image, but outside the lit projection area.  The image processing is then able to only act upon pixels within the desired preojection area.

####**bloop**/**erase**
1. 	User makes drawing revisions by adding marker and hitting **bloop**, or by drawing a single encapsulating area to be erased and hitting **erase**.
2. Run
	`java Blooprint <title> bloop`
	or
	`java Blooprint <title> erase`

	Blooprint image updates

####**blip**
1. User draws a box area and hits **blip**.  Textarea unit values are returned for textarea addition to the DOM where the text is maintained by users within the web app.
2. Run
	`java Blooprint <title> blip`
Blips textareas can also be created by user in web app by click and drag rectangle

###Created by Dave Daggett
###Licence:	[**GPL-3.0**](https://github.com/ddaaggeett/blooprint.xyz/blob/master/LICENSE)
