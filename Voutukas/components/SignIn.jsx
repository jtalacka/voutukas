import React, { Component } from "react";
import "./Styles/SignIn.css";
import logo from "./logo.jpg";

class SignIn extends Component {
  state = {};
  render() {
    return (
      <div id="signIn">
        <img id="logo" src={logo} alt="logo" />
        <div id="SlackButton">
          <a
            id="SlackButton"
            href="https://slack.com/oauth/authorize?scope=identity.basic,identity.team&client_id=788630701380.807629152245"
          >
            <img
              alt="SlackButton"
              Sign
              in
              with
              Slack
              height="40"
              width="172"
              src="https://platform.slack-edge.com/img/sign_in_with_slack.png"
              srcSet="https://platform.slack-edge.com/img/sign_in_with_slack.png 1x, https://platform.slack-edge.com/img/sign_in_with_slack@2x.png 2x"
            />
          </a>
        </div>
      </div>
    );
  }
}

export default SignIn;
