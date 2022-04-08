import cv2
import mask
import sys

# all corners read in following order - TL,TR,BR,BL
img = sys.argv[1]
prev = sys.argv[2]
outputFile = sys.argv[3]
mode = sys.argv[4]
outputShape = sys.argv[5]
write = True if mode == 'write' else False
image = cv2.imread(img)
prevImage = cv2.imread(prev)
roi, roiCorners = mask.roi(image)
inputMask = None
if(write): inputMask = mask.ink(image, roi)
else: inputMask = mask.eraser(image, roi)
diffMask = mask.warp(inputMask, roiCorners)
output = mask.applyDiffMask(diffMask, prevImage)
cv2.imwrite(outputFile, output)
