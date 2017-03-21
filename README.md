# Welcome to the Blooprint API **source** repository
### Are you the **non-techie** type?  Please **visit our** (incomplete) [**wiki**](https://github.com/blooprint/blooprint/wiki) page.
This API is **used by** the [**blooprint**](https://github.com/blooprint/blooprint) desktop application.

## An overview
### note: parts of this overview may be pertaining to branch: v0.0.x
for example, `calibrate` is a separate run command in `v0.0.x`, but `master` includes `calibrate` per `write`/`erase` commands

Simply **download the latest [blooprint.jar](https://github.com/blooprint/blooprint-api/releases) file** which already includes the external JAR dependencies you need to run with the main blooprint application.  Place it **[here](https://github.com/blooprint/blooprint/tree/master/api)**.

**OR**, using the most updated clone of the source code, [create a JAR file](http://docs.oracle.com/javase/tutorial/deployment/jar/build.html) (blooprint.jar) and place it **[here](https://github.com/blooprint/blooprint/tree/master/api)**. Don't forget to include a few required external JARs
- Google's [json-simple-1.1.1.jar](https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/json-simple/json-simple-1.1.1.jar)
- Apache's [commons-io-2.5.jar](http://www-eu.apache.org/dist//commons/io/binaries/commons-io-2.5-bin.zip)


#### Runtime requirements
```
"jdk": ">=1.8"
```

The [parent application](https://github.com/blooprint/blooprint) triggers this API a few different ways. A trigger always includes the timestamp of the input image to scrape pixel data from and a specific mode specifying what the API is to do with the image.

    java -jar blooprint.jar <timestamp> <branching image path> <bloop action mode> <write color>

**timestamp** = example -> `2017019234910931` - refers to 2017019234910931.jpg in `./sketches/` directory

**branching image path** = example -> `./blooprints/some_blooprint/2016010753850931.jpg`: image to be altered (branched from). Output image will be saved next to this input image under the name of its corresponding sketch, ./sketches/2017019234910931.jpg: `./blooprints/some_blooprint/2017019234910931.jpg`.

**write color** = example -> if RED, then `FF0000`

**bloop action** = `write`, `erase`, `calibrate`

- **write** = Returns compiled blooprint image - input image contains user-drawn marker to be added
- **erase** = Returns compiled blooprint image - input image contains user-drawn eraser area
- **calibrate** = Returns calibration info to be used for image processing. Run any time client hardware [(whiteboard, camera, projector)](https://github.com/blooprint/blooprint/wiki/Required-Hardware) is set up and stationary

#### calibrate
1. Once the [hardware](https://github.com/blooprint/blooprint/wiki/Required-Hardware) is set in place, the desktop application prompts the user through clicking the location of each corner, and then triggering **calibrate**.
2. Run
	`java -jar blooprint.jar <timestamp> null **calibrate** null`
    Returns `calibration.json` data object to [parent app directory](https://github.com/blooprint/blooprint/tree/master/api/calibration): `./calibration/` in which is automatically accessed for all proceeding write/erase bloop actions.

#### write or erase
1. 	User makes drawing revisions by adding marker and triggering **write**, or by drawing a single encapsulating area to be erased and hitting **erase**.
2. Run
	`java -jar blooprint.jar <timestamp> <branching image path> write <color>`
	or
	`java -jar blooprint.jar <timestamp> <branching image path> erase null`
    Returns output image to same directory containing input branching image.

### Created by Dave Daggett
### Licence:	**[GPL-3.0](https://github.com/blooprint/blooprint-api/blob/master/LICENSE)**
