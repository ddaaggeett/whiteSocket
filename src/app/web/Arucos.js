const aruco0 = require('../../../assets/aruco_0.svg')
const aruco1 = require('../../../assets/aruco_1.svg')
const aruco2 = require('../../../assets/aruco_2.svg')
const aruco3 = require('../../../assets/aruco_3.svg')

export default () => {
    return (
        <div>
            <img src={aruco0} style={{...arucoStyle, left:0,top:0}} />
            <img src={aruco1} style={{...arucoStyle, right:0,top:0}} />
            <img src={aruco2} style={{...arucoStyle, right:0,bottom:0}} />
            <img src={aruco3} style={{...arucoStyle, left:0,bottom:0}} />
        </div>
    )
}

const arucoStyle = {
    position: 'fixed',
    width: 100,
    height: 100,
}
