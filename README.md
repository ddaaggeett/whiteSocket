non-technical page [**here**](https://github.com/blooprint/blooprint/wiki)

whiteSocket is an executable .jar, unique to [**blooprint**](https://github.com/blooprint/blooprint)

    `java -jar whiteSocket.jar <timestamp> <input image> <mode> <color>`

## timestamp
`2017019234910931`

## input branching image
`./2016010753850931.jpg` image file to process. Output saved to same directory.

## color
if red, then `FF0000`

## mode

#### write

User adds marker and executes **write**.

	`java -jar whiteSocket.jar <timestamp> <input> write <color>`

Returns output image to same directory containing input branching image.

#### erase

User draws encapsulating areas and executes **erase**.

	`java -jar whiteSocket.jar <timestamp> <input> erase null`
	
Returns output image to same directory containing input branching image.
    
### Created by Dave Daggett : @ddaaggeett

### Licence:	**[GPL-3.0](https://github.com/blooprint/blooprint-api/blob/master/LICENSE)**
