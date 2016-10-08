Please visit the official [blooprint.xyz](http://www.blooprint.xyz) website and see our [wiki](https://github.com/ddaaggeett/blooprint.xyz/wiki).
------------------------------------------------------------------------

Blooprint is the means for the common person have a public access point to a distributed version control system.

The Blooprint API is the world's most cost-effective content generation platform for a global web of live handwriting.

Simply, it gives the user a whiteboard design space with autosave and instantaneous editablility of hand-drawn work.
________________________________
From [web app](www.blooprint.xyz), run the following:

    javac Blooprint.java
    java Blooprint <current blooprint title> <input mode>


Image capture **input mode** = pick one -> "bloop", "blip", "erase", "calibrate"

 - **bloop** = Returns compiled blooprint image - input image contains user-drawn marker to be added
 - **blip** = Returns textarea location (x,y,width,height)
 - **erase** = Returns compiled blooprint image - input image contains user-drawn eraser area
 - **calibrate** = Returns calibration info to be used for image processing. Run any time client hardware [(whiteboard, camera, projector)](https://github.com/ddaaggeett/blooprint.xyz/wiki/Required-Hardware) is set up and stationary

###DETAILS
The sole purpose of the Blooprint image-processing API is to enable a version control system in which archives revisions of freehand design.  This API is specifically built to edit the physical drawn state of the image.  There are only two functions to call on the blooprint image 1.ADD(BLOOP) 2.ERASE

Blooprint image state maintenance (BLOOP+ERASE) can only be done by users with a [hardware station](https://github.com/ddaaggeett/blooprint.xyz/wiki/Required-Hardware).  Text additions (BLIP) on the other hand are able to be maintained by anyone who has open access to the website.  Blips are the way in which the [bloprint.xyz](www.blooprint.xyz) web app can tag blooprint image state details in a tagging manner, creating a user-defined layer of understanding pertaining to the blooprint image.  Think of the text as supplementary discourse about the drawings - a layer of transposing.  Blips are what give the version control system an open and maintainable argument platform.  Where the image state is the official object of interest.

The Blooprint API returns DOM element data for dynamic web application development.  Web application is to run full screen (F11) in the client browser and consists of only a background image and textareas.

All whiteboard design must take place within lit projector bounds, including the 4 calibration corner marks.


###Web App Incorporation Overview

####calibrate
1. Once hardware is set in place, the user draws 4 corner marks as accurately as possible in the projection area out to the corner bounds. -> user then hits **calibrate**
2. Run
	`java Blooprint <title> calibrate`
3. Image capture is displayed full screen in web app where the user then clicks 4 points just outside of user drawn corner points.  ie - these 4 click points are to be located on the whiteboard in the image, but outside the lit projection area.  The image processing is then able to only act upon pixels within the desired preojection area.

####bloop/erase
1. 	User makes drawing revisions by adding marker and hitting **bloop**, or by drawing a single encapsulating area to be erased and hitting **erase**.
2. Run
	`java Blooprint <title> bloop`
	or
	`java Blooprint <title> erase`

	Blooprint image updates

####blip
1. User draws a box area and hits **blip**.  Textarea unit values are returned for textarea addition to the DOM where the text is maintained by users within the web app.
2. Run
	`java Blooprint <title> blip`
Blips textareas can also be created by user in web app by click and drag rectangle

________________________________
Creator:	DAVE DAGGETT - BLOOPRINT, LLC

Licence:	[GPLv3](https://github.com/ddaaggeett/blooprint.xyz/blob/master/LICENSE)
