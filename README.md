non-technical page [**here**](https://github.com/blooprint/whiteSocket/wiki)

**whiteSocket** is an executable jar file which generates an image diff upon execution. A single diff's record consists of either added or erased pixels of displayed whiteboard content. The output image is the printed copy of the applied diff to the previous version.

### to start

whiteSocketing for the first time? get the **[whiteSocket.jar](https://github.com/blooprint/whiteSocket/releases)**, and **[download](https://www.dropbox.com/sh/372p9m1oxi8sxvd/AAD1lzsmTGpnBXTevenzgjrna?dl=0)** some pre-fab example/test images and place image contents in following manner and open $terminal.

	dir/
		whiteSocket.jar
		write.bmp
		erase.bmp

write test - execute: `$ java -jar whiteSocket.jar writeTest blank write 000000`

erase test - execute: `$ java -jar whiteSocket.jar eraseTest toerase erase 000000`

Note: output images overwrite the input
______________________________________________________________________

#### executable file

    $ java -jar whiteSocket.jar <timestamp> <branching image> <mode> <color>

## timestamp - output image id

`2017019234910931` - becomes whiteSocket output save image title

## branching image id

`2016015803141428` - current image version about to be archived by completion of this jar executable

## mode

`write` - user draws marker and executes **write**

`erase` - user draws encapsulating areas and executes **erase**

## color

hex: if red, then `FF0000`
______________________________________________________________________

### Copyright: Dave Daggett : [personal website](http://ddaaggeett.xyz)

### Licence:	**[GPL-3.0](https://github.com/blooprint/blooprint-api/blob/master/LICENSE)**
