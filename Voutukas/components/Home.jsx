import React, { Component } from "react";
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link,
  Redirect
} from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import PollTable from "./PollTable";
import ResultsTable from "./ResultsTable";
import Data from "./data";

class Home extends Component {
  state = {};
  render() {
    return (
      <div>
        <Router>
          <div>
            <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
              <ul className="navbar-nav mr-auto">
                <li>
                  <Link to={"/Data"} className="nav-link">
                    Data
                  </Link>
                </li>
                <li>
                  <Link to={"/pollTable"} className="nav-link">
                    Poll table
                  </Link>
                </li>
                <li>
                  <Link to={"/resultsTable"} className="nav-link">
                    Results Table
                  </Link>
                </li>
              </ul>
              <ul className="navbar-nav ml-auto">
                <li style={{ color: "grey" }}>{this.props.user["name"]}</li>
                <li>
                  <Link to={"/"}>
                    <button
                      type="button"
                      className="btn btn-danger"
                      style={{ marginLeft: "10px" }}
                      onClick={this.props.resetState}
                    >
                      Log Out
                    </button>
                  </Link>
                </li>
              </ul>
            </nav>
            <Switch>
              <Redirect from="/loading" to="/Data" />
              <Route path="/Data">
                <Data
                  user={this.props.user}
                  team={this.props.team}
                  token={this.props.token}
                />
              </Route>
              <Route path="/pollTable" component={PollTable} />
              <Route path="/resultsTable" component={ResultsTable} />
            </Switch>
          </div>
        </Router>
      </div>
    );
  }
}

export default Home;
