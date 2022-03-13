import cv2
import warp
import mask
import aruco
import sys

# # all corners read in following order - TL,TR,BR,BL
img = sys.argv[1]
outputFile = sys.argv[2]
image = cv2.imread(img)
inputCorners, image = aruco.getInputCorners(image)
warped = warp.warp(image,inputCorners)
ink = mask.inkOnly(warped)
output = ink
cv2.imwrite(outputFile, output)
