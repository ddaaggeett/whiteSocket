import cv2
import numpy
import json

def applyDiffMask(diffMask, prevImage, shape):
    color = create_blank(shape['width'],shape['height'],(255,255,255))
    diff = cv2.bitwise_and(color, color, mask=diffMask)
    output = cv2.bitwise_xor(prevImage, diff, mask=None)
    return output

def create_blank(width, height, rgb_color):
    image = numpy.zeros((height, width, 3), numpy.uint8)
    color = tuple(reversed(rgb_color))
    image[:] = color
    return image

def ink(image, roi):
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    th, allInk = cv2.threshold(gray, 80, 255, cv2.THRESH_BINARY_INV)
    ink = cv2.bitwise_and(roi, allInk, mask=None)
    return(ink)

def eraser(image, roi):
    # TODO: edges need to be on the inside edge of the enk, not exterior
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    eraseAreas = cv2.Canny(gray, 30, 200)
    contours, hierarchy = cv2.findContours(eraseAreas, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
    cv2.fillPoly(eraseAreas, contours, color=(255,255,255))
    eraser = cv2.bitwise_and(roi, eraseAreas, mask=None)
    return(eraser)

def roi(image):
    # all corners read in following order - TL,TR,BR,BL
    mask = numpy.zeros(image.shape[:2], dtype="uint8")
    mask_arucos = numpy.zeros(image.shape[:2], dtype="uint8")
    roiCorners = numpy.zeros((4,2)) # outer-most corner of each arcuro
    arucoDict = cv2.aruco.Dictionary_get(cv2.aruco.DICT_4X4_50)
    arucoParams = cv2.aruco.DetectorParameters_create()
    corners, ids, rejected = cv2.aruco.detectMarkers(image, arucoDict, parameters=arucoParams)
    if len(ids) == 4:
        ids = ids.flatten()
        for (markerCorner, markerID) in zip(corners, ids):
            corners = markerCorner.reshape((4, 2))
            (tl, tr, br, bl) = corners
            tl = [int(tl[0]), int(tl[1])]
            tr = [int(tr[0]), int(tr[1])]
            br = [int(br[0]), int(br[1])]
            bl = [int(bl[0]), int(bl[1])]
            contours = numpy.array([tl,tr,br,bl])
            cv2.fillPoly(mask_arucos, pts = numpy.int32([contours]), color=(255,255,255))
            if markerID == 0: roiCorners[0] = tl
            elif markerID == 1: roiCorners[1] = tr
            elif markerID == 2: roiCorners[2] = br
            elif markerID == 3: roiCorners[3] = bl

    contours = numpy.array([roiCorners[0],roiCorners[1],roiCorners[2],roiCorners[3]])
    cv2.fillPoly(mask, pts = numpy.int32([contours]), color=(255,255,255))
    mask = cv2.bitwise_xor(mask, mask_arucos, mask = None)
    return(mask, roiCorners)

def warp(mask, roiCorners, shape):
    inpoints = numpy.float32(roiCorners)
    outpoints = numpy.float32([[0,0],[shape['width'],0],[shape['width'],shape['height']],[0,shape['height']]])
    matrix = cv2.getPerspectiveTransform(inpoints,outpoints)
    warped = cv2.warpPerspective(mask, matrix, (shape['width'],shape['height']))
    return(warped)
