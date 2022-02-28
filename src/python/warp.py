import cv2
import numpy

outputWidth = 500

def warp(img,inputCorners):
    # zigzag point placement - TL,TR,BL,BR
    input = cv2.imread(img)
    inpoints = numpy.float32(inputCorners)
    outpoints = numpy.float32([[0,0],[outputWidth,0],[0,outputWidth],[outputWidth,outputWidth]])
    matrix = cv2.getPerspectiveTransform(inpoints,outpoints)
    output = cv2.warpPerspective(input,matrix,(outputWidth,outputWidth))
    cv2.imshow('output',output)
    cv2.imwrite('./img/output.png', output)
    cv2.waitKey(0)
