import cv2
import warp
import mask
import sys

# # all corners read in following order - TL,TR,BR,BL
img = sys.argv[1]
outputFile = sys.argv[2]
image = cv2.imread(img)
warped = warp.warp(image,inputCorners)
ink = mask.inkOnly(warped)
output = ink
cv2.imwrite(outputFile, output)
roi, roiCorners = mask.roi(image)
ink = mask.ink(image, roi)
eraser = mask.eraser(image, roi)

