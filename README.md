non-technical page [**here**](https://github.com/blooprint/whiteSocket/wiki)

**whiteSocket** is an executable jar file which generates an image diff upon execution. A single diff's record consists of either added or erased pixels of displayed whiteboard content. The output image is the printed copy of the applied diff to the previous version.

### to start

whiteSocketing for the first time? get the **[whiteSocket.jar](https://github.com/blooprint/whiteSocket/releases)**, and **[download](https://www.dropbox.com/sh/372p9m1oxi8sxvd/AAD1lzsmTGpnBXTevenzgjrna?dl=0)** some pre-fab example/test images and place image contents in following manner.

open $terminal in directory containing the following structure:

	dir/
		whiteSocket.jar
		blooprints/
		write image
		erase image

write test - execute: `$ java -jar whiteSocket.jar write blank write 000000`

erase test - execute: `$ java -jar whiteSocket.jar erase toerase erase 000000`

______________________________________________________________________

#### executable file

    $ java -jar whiteSocket.jar <timestamp> <branching image> <mode> <color>

Output image here: `./blooprints/<timestamp>`

## timestamp - output image id

`2017019234910931`

## branching image id

`2016015803141428` image file to process (2016015803141428.bmp or jpg). This image is the current version about to be archived by completion of this jar executable.

## mode

`write`

User adds marker and executes **write**.

`erase`

User draws encapsulating areas and executes **erase**.

## color
hex: if red, then `FF0000`
______________________________________________________________________

### Copyright: Dave Daggett : [personal website](http://ddaaggeett.xyz)

### Licence:	**[GPL-3.0](https://github.com/blooprint/blooprint-api/blob/master/LICENSE)**
