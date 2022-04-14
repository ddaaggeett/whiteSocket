import cv2
import mask
import sys
import os
import json
config = json.loads(open('config.json').read())
imageDataDir = config['imageData']

diff = json.loads(sys.argv[1])
img = os.path.join(imageDataDir,diff['uri'])
prev = os.path.join(imageDataDir,diff['prev_uri'])
outputFile = os.path.join(imageDataDir,diff['result_uri'])
mode = diff['mode']
shape = diff['shape']

write = True if mode == 'write' else False
image = cv2.imread(img)
prevImage = cv2.imread(prev)
roi, roiCorners = mask.roi(image)
inputMask = None
if(write): inputMask = mask.ink(image, roi)
else: inputMask = mask.eraser(image, roi)
diffMask = mask.warp(inputMask, roiCorners, shape)
output = mask.applyDiffMask(diffMask, prevImage)
cv2.imwrite(outputFile, output)
