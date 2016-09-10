Please visit the official [blooprint.xyz](http://www.blooprint.xyz) website and see our [wiki](https://github.com/ddaaggeett/blooprint.xyz/wiki).
------------------------------------------------------------------------

The Blooprint API is the world's most cost-effective content generation platform for a global web of live handwriting.

Simply, it gives the user a whiteboard design space with autosave and instantaneous editablility of hand-drawn work.
________________________________
Run command line API inside web application:

    javac Blooprint.java
    java Blooprint args[0] args[1] .......
    java Blooprint [current blooprint title] [input mode]

Note any potential updates to accepted arguments by API in the [file](https://github.com/ddaaggeett/blooprint.xyz/blob/master/Blooprint-API/Blooprint.java).

Image capture **input mode** = pick one -> "bloop", "blip", "erase", "calibrate"

 - **bloop** = Returns compiled blooprint image - input image contains user-drawn marker to be added
 - **blip** = Returns textarea location (x,y,width,height)
 - **erase** = Returns compiled blooprint image - input image contains user-drawn eraser area
 - **calibrate** = Returns calibration info to be used for image processing. Run any time client hardware [(whiteboard, camera, projector)](https://github.com/ddaaggeett/blooprint.xyz/wiki/Required-Hardware) is set up and stationary

The Blooprint API returns DOM element data for dynamic web application development.  Web appplication is to run full screen (F11) in client browser.

________________________________
Creator:	DAVE DAGGETT - BLOOPRINT, LLC

Licence:	[GPLv3](https://github.com/ddaaggeett/blooprint.xyz/blob/master/LICENSE)
