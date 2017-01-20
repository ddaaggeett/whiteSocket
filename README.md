#Welcome to the Blooprint API **source** repository
###Are you the **non-techie** type?  Please **visit our** (incomplete) [**wiki**](https://github.com/blooprint/blooprint/wiki) page.
This API is **used by** the [**blooprint**](https://github.com/blooprint/blooprint) desktop application.

##An overview - first [create a JAR file](http://docs.oracle.com/javase/tutorial/deployment/jar/build.html) (blooprint.jar) and place in [here](https://github.com/blooprint/blooprint/tree/master/api). Don't forget to include the external JAR - [json-simple-1.1.1.jar](https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/json-simple/json-simple-1.1.1.jar)

The [parent module](https://github.com/blooprint/blooprint) triggers this API a few different ways. A trigger always includes the filename of the image to scrape pixel data from and a specific mode specifying what the API is to do with the image.

    java -jar blooprint.jar <sketch title> <input mode> <write color>

**sketch title** = example -> `2017019234910931` - refers to 2017019234910931.jpg in `./sketches/` directory
**write color** = pick one -> `red, green, blue, orange, purple, gray, brown, black, null`
**input mode** = pick one -> `write, erase, calibrate`

 - **write** = Returns compiled blooprint image - input image contains user-drawn marker to be added
 - **erase** = Returns compiled blooprint image - input image contains user-drawn eraser area
 - **calibrate** = Returns calibration info to be used for image processing. Run any time client hardware [(whiteboard, camera, projector)](https://github.com/blooprint/blooprint/wiki/Required-Hardware) is set up and stationary
 - **blip** = Returns textarea location (x,y,width,height) - **DEPRECATED** - blips are handled in main parent application

####**calibrate**
1. Once [hardware](https://github.com/blooprint/blooprint/wiki/Required-Hardware) is set in place, the user draws 4 corner marks as accurately as possible in the projection area out to the corner bounds. -> user then hits **calibrate**
2. Run
	`java -jar blooprint.jar <title> calibrate null`
3. Image capture is displayed full screen in web app where the user then clicks 4 points just outside of user drawn corner points.  ie - these 4 click points are to be located on the whiteboard in the image, but outside the lit projection area.  The image processing is then able to only act upon pixels within the desired projection area.

####**write**/**erase**
1. 	User makes drawing revisions by adding marker and hitting **write**, or by drawing a single encapsulating area to be erased and hitting **erase**.
2. Run
	`java -jar blooprint.jar <title> write <color>`
	or
	`java -jar blooprint.jar <title> erase null`

	Blooprint image returns to containing directory and renders to DOM

####**blip** - **DEPRECATED**
1. User draws a box area and hits **blip**.  Textarea unit values are returned for textarea addition to the DOM where the text is maintained by users within the web app.
2. Run
	`java -jar blooprint.jar <title> blip null`
Blips textareas can also be created by user in web app by click and drag rectangle - NOTE: blips are deprecated in the API because they are created and maintained by the parent module as part of the data state structure of the app with Redux.

###Created by Dave Daggett
###Licence:	[**GPL-3.0**](https://github.com/blooprint/blooprint/blob/master/LICENSE)
