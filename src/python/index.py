import warp
# TODO: opencv aruco marker implementation for input corner image

inputImg = './img/test.png'
# a = []
# b = []
# c = []
# d = []
# inputCorners = [a,b,c,d]

# zigzag point placement - TL,TR,BL,BR
inputCorners = [[63, 184],[440, 95],[62, 403],[456, 443]]

warp.warp(inputImg,inputCorners)
