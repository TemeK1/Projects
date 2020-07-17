import React from 'react';
import Device from './Device.js';
import './App.css';
import {linkStations} from './stations.js';
import {devices} from './devices.js';

/*
* Linkstations react-app.
* App component is responsible for fetching the necessary data,
* linkStations and devices, and for parenting of the Device-components.
* Extension points are left for future reference, in case something more interactive
* will be needed, e.g. state is not currently being set again after initiliazing phase.
*
* Use npm run build to build a production version, and then place
* build-folder to the web-server.
* Absolute homepage is set in package.json, so take care to check that.
*/
class App extends React.Component {
  constructor(props) {
    super(props);
    let clonedStations = [],
        clonedDevices = [];

    //making full clones of the objects, to be sure that nothing funny and weird happens due to references to original objects,
    //"break and bake", because these will be set into the state of the component.
    for (let s of linkStations) {
      clonedStations.push(JSON.parse(JSON.stringify(s)));
    }

    //look above commentary.
    for (let d of devices) {
      clonedDevices.push(JSON.parse(JSON.stringify(d)));
    }

    this.state = {
      "stations": clonedStations,
      "devices": clonedDevices
    }
  }

  render() {

    let aDevices = [];
    //iterating through all the devices, and creating Device child-elements.
    //and setting necessary props
    for (let d of this.state.devices) {
      aDevices.push(<Device key={d.x + d.y} x={d.x} y={d.y} stations={this.state.stations} />);
    }

    return (
      <div className="App">
        {aDevices}
      </div>
    );
  }
}

export default App;
