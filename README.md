non-technical page [**here**](https://github.com/blooprint/whiteSocket/wiki)

whiteSocket is an executable jar file which generates an image diff upon execution. A single diff's record consists of either added or erased pixels of displayed whiteboard content. The output image is the printed copy of the applied diff to the previous version.

whiteSocketing for the first time? see [here](https://github.com/blooprint/whiteSocket/tree/master/tests) to download some pre-fab test images.
write test - execute: - `java -jar whiteSocket.jar write blank write 000000`
erase test - execute: - `java -jar whiteSocket.jar erase toerase erase null`
______________________________________________________________________
#### executable file

    java -jar whiteSocket.jar <timestamp> <branching image> <mode> <color>

## timestamp - output image id
`2017019234910931`

## branching image id
`2016015803141428` image file to process (2016015803141428.bmp or jpg). This image is the current version about to be archived by completion on this jar executable. Output saves to same directory.

## mode

`write`

User adds marker and executes **write**. Returns output image.

`erase`

User draws encapsulating areas and executes **erase**. Returns output image.

## color
hex: if red, then `FF0000`
______________________________________________________________________

### Copyright: Dave Daggett : [personal website](http://ddaaggeett.xyz)

### Licence:	**[GPL-3.0](https://github.com/blooprint/blooprint-api/blob/master/LICENSE)**
