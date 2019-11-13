import React, { Component } from "react";
import Home from "./components/Home";
import SignIn from "./components/SignIn";
import Axios from "axios";
const API = "https://slack.com/api/oauth.access?client_id=";
const CLIENT_ID = "788630701380.807629152245";
const CLIENT_SECRET = "4962b16d6ccdc9e73853ae97d02e7943";

class App extends Component {
  state = {
    isLoggedIn: false,
    code: null,
    fetchedDataUser: null,
    fetchedDataTeam: null,
    fetchedAccess_token: null
  };
  render() {
    return (
      <div style={{ margin: "10px" }}>
        {this.state.isLoggedIn ? (
          <Home
            user={this.state.fetchedDataUser}
            team={this.state.fetchedDataTeam}
            token={this.state.fetchedAccess_token}
            resetState={this.resetState}
          />
        ) : (
          <SignIn />
        )}
      </div>
    );
  }
  getCode = () => {
    let url_string = null;
    if (window.location.href.includes("loading")) {
      url_string = window.location.href;
      let url = new URL(url_string);
      let code = url.searchParams.get("code");
      this.setState({ code });
      return code;
    } else {
      return "False";
    }
  };

  async componentWillMount() {
    if (this.getCode() !== "False") {
      let url =
        API +
        CLIENT_ID +
        "&client_secret=" +
        CLIENT_SECRET +
        "&code=" +
        this.getCode();
      this.setState({ url });
      await Axios.get(url)
        .then(respone => respone.data)
        .then(data =>
          this.setState({
            fetchedDataUser: data["user"],
            fetchedDataTeam: data["team"],
            fetchedAccess_token: data["access_token"]
          })
        );
      if (this.state.fetchedDataTeam != null) {
        this.setState({ isLoggedIn: true });
      }
    }
  }

  resetState = () => {
    this.setState({
      isLoggedIn: false,
      fetchedAccess_token: null,
      fetchedDataUser: null,
      fetchedDataTeam: null
    });
  };
}

export default App;
