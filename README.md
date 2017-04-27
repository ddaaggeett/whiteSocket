non-technical page **[here](https://github.com/blooprint/whiteSocket/wiki)**

**whiteSocket** is an executable jar file which generates an image [diff](https://en.wikipedia.org/wiki/Diff_utility) upon execution. A single diff's record consists of either added or erased pixels of physical whiteboard image content. The whiteSocket output image is the printed copy of the applied diff to the previous version. Hopefully it's obvious, but as a precaution - the whiteSocket input image is an arbitrary camera capture of a well-lit whiteboard surface.

### to start

whiteSocketing for the first time? get the **[whiteSocket.jar](https://github.com/blooprint/whiteSocket/releases)**, and **[download](https://www.dropbox.com/sh/372p9m1oxi8sxvd/AAD1lzsmTGpnBXTevenzgjrna?dl=0)** some pre-fab example/test images and place image contents in following manner and open directory `whiteSocket/` in $terminal.

	whiteSocket/
		input/
		whiteSocket.jar
		output/

write test - execute: `$ java -jar whiteSocket.jar writeTest white write 000000`

erase test - execute: `$ java -jar whiteSocket.jar eraseTest black erase 000000`
______________________________________________________________________

#### executable file

    $ java -jar whiteSocket.jar <timestamp> <branching image> <mode> <color>

## timestamp - incoming diff image id

ie - `2017019234910931` - becomes whiteSocket output save image title

## branching image id

ie - `2016015803141428` - current image version being updated

## mode: write|erase

`write` - user draws marker and executes **write**

`erase` - user draws encapsulating areas and executes **erase**

## color

hexadecimal: ie - if red, then `FF0000`
______________________________________________________________________

### Further

@blooprint has initiated the development of its standardized application of the whiteSocket module here: **[blooprint/output](https://github.com/blooprint/output)**.

We welcome new attempts to build better applications of the `blooprint/whiteSocket` module that surpasses our default **`blooprint/output`** app.

So please, join the @blooprint team and crack open an awesome opportunity!
______________________________________________________________________

### Copyright: Dave Daggett : [personal website](http://ddaaggeett.xyz)

### Licence:	**[GPL-3.0](https://github.com/blooprint/whiteSocket/blob/master/LICENSE)**
