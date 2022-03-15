import cv2
import mask
import sys

# all corners read in following order - TL,TR,BR,BL
img = sys.argv[1]
outputFile = sys.argv[2]
mode = sys.argv[3]
mode = True if mode == 'write' else False
image = cv2.imread(img)
roi, roiCorners = mask.roi(image)
inputMask = None
if(mode): inputMask = mask.ink(image, roi)
else: inputMask = mask.eraser(image, roi)
mask_output = mask.warp(inputMask, roiCorners)
cv2.imwrite(outputFile, mask_output)
