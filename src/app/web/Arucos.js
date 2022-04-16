const aruco0 = require('../../../assets/aruco_0.svg')
const aruco1 = require('../../../assets/aruco_1.svg')
const aruco2 = require('../../../assets/aruco_2.svg')
const aruco3 = require('../../../assets/aruco_3.svg')
const { borderWidth } = require('../../../config')

export default () => {
    return (
        <div>
            <img src={aruco0} style={{...arucoStyle, left:borderWidth,top:borderWidth}} />
            <img src={aruco1} style={{...arucoStyle, right:borderWidth,top:borderWidth}} />
            <img src={aruco2} style={{...arucoStyle, right:borderWidth,bottom:borderWidth}} />
            <img src={aruco3} style={{...arucoStyle, left:borderWidth,bottom:borderWidth}} />
        </div>
    )
}

const arucoStyle = {
    position: 'fixed',
    width: 100,
    height: 100,
}
