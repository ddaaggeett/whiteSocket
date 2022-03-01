import cv2
import numpy

def getInputCorners(img='./img/aruco_corners.png'):
	inputCorners = numpy.zeros((4,2))
	image = cv2.imread(img)
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
			if markerID == 0: inputCorners[0] = tl
			if markerID == 1: inputCorners[1] = tr
			if markerID == 2: inputCorners[2] = br
			if markerID == 3: inputCorners[3] = bl

	return inputCorners
