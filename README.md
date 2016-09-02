Please visit the official [blooprint.xyz](http://www.blooprint.xyz) website
------------------------------------------------------------------------

The Blooprint command line API is the world's most cost-effective content generation platform for a global web of handwriting.

Simply put, it gives the user a whiteboard design space with autosave and instantaneous editablility of hand-drawn work.

Run command line API inside web application:

    javac Blooprint.java
    java Blooprint args[0] args[1] args[2] .......
    java Blooprint [username] [current blooprint title] [input mode]

For updated args[] -> note updated changes in Bloopint-API/Blooprint.java

Image capture **input mode** = "bloop", "blip", "erase", "calibrate"

 - **bloop** = returns compiled blooprint image - input image contains user-drawn marker to be added
 - **blip** = returns textarea location info for dynamic DOM element addition
 - **erase** = returns compiled blooprint image - input image contains user-drawn eraser area
 - **calibrate** = to be run once after physical hardware is setup and stationary

________________________________
Creator:	DAVE DAGGETT - BLOOPRINT, LLC

Licence:	GPLv3
