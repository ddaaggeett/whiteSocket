non-technical page [**here**](https://github.com/blooprint/blooprint/wiki)

whiteSocket is an executable jar file which generates an image diff upon execution. A single diff's record consists of either added or erased pixels of displayed whiteboard content. The output image is the printed copy of the applied diff to the previous version.

    java -jar whiteSocket.jar <output/timestamp> <input image> <mode> <color>

## timestamp - output image title
`2017019234910931`

## input branching image title
`2016010753850931` image file to process (2016010753850931.bmp or jpg). This image is the current version about to be archived by completion on this jar executable. Output saves to same directory.

## mode

#### write

User adds marker and executes **write**.

	java -jar whiteSocket.jar <timestamp> <input> write <color>

Returns output image.

#### erase

User draws encapsulating areas and executes **erase**.

	java -jar whiteSocket.jar <timestamp> <input> erase null
	
Returns output image.
    
## color
if red, then `FF0000`

### Copyright: Dave Daggett : [personal website](http://ddaaggeett.xyz)

### Licence:	**[GPL-3.0](https://github.com/blooprint/blooprint-api/blob/master/LICENSE)**
