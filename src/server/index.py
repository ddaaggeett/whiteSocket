import cv2
import mask
import sys

# # all corners read in following order - TL,TR,BR,BL
img = sys.argv[1]
outputFile = sys.argv[2]
image = cv2.imread(img)
output = ink
cv2.imwrite(outputFile, output)
roi, roiCorners = mask.roi(image)

mask_ink = mask.ink(image, roi)
mask_eraser = mask.eraser(image, roi)

mask_output = mask.warp(mask_ink, roiCorners)
mask_output = mask.warp(mask_eraser, roiCorners)

