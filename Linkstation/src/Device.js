import React from 'react';

/*
* Device-component is responsible for a single device.
* To look for a best linkstation. State is currently kept empty,
* but something more stateful could be created.
*/
class Device extends React.Component {
  constructor(props) {
    super(props);

    //binding the functions
    this.bestStation = this.bestStation.bind(this);
    this.calcPower = this.calcPower.bind(this);
    this.calcDistance = this.calcDistance.bind(this);
  }

  /*
  * Seeking for the best possible station (power-wise) to connect this device on.
  * @returns {Object} contains most powerful station and it's power
  */
  bestStation() {
    //no candidates unless otherwise proven
    let candidateStation = null,
        //proto-power, safe to assume it's 0 at least.
        //Even though in real life situation it could obviously be negative value as well (dBm)
        candidatePower = 0;

    //iterates through all the stations to search for the best.
    for (let station of this.props.stations) {
      let distance = this.calcDistance(station.x, this.props.x, station.y, this.props.y);
      //to check if distance between the station and device is smaller or at least equal to station's reach.
      if (distance <= station.reach) {
        //if distance is OK, we can calculate the power.
        let power = this.calcPower(station.reach, distance);
        //if power is greater than in the old candidate, we shall update.
        if (power > candidatePower) {
          candidatePower = power;
          candidateStation = station;
        }
      }
    }

    return {"station": candidateStation, "power": candidatePower};
  }

  /*
  * Simply calculates the power based on two parameters:
  * @param reach: reach of a linkstation.
  * @param distance: distance between two points (linkstation and device)
  * @returns {Float} returns power
  */
  calcPower(reach, distance) {
    return Math.pow(reach - distance, 2);
  }

  /*
  * Simply calculates the distance between two points:
  * @param x1 x-coordinate of point 1
  * @param x2 x-coordinate of point 2
  * @param y1 y-coordinate of point 1
  * @param y2 y-coordinate of point 2
  * @returns {Float} returns distance
  */
  calcDistance(x1,x2,y1,y2) {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  render() {
    //asking for the best station
    let stationAndPower = this.bestStation();
    let x1 = this.props.x,
        y1 = this.props.y,
        result = null;

    //if at least one suitable station is found
    if (stationAndPower.station !== null) {
      let x2 = stationAndPower.station.x,
          y2 = stationAndPower.station.y;

      result = `Best link station for point ${x1},${y1} is ${x2},${y2} with power ${stationAndPower.power.toFixed(2)}`;
    } else {
      result = `No link station within reach for point ${x1},${y1}`;
    }
    return(<p>{result}</p>);
  }
}

export default Device;
