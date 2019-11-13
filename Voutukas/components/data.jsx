import React, { Component } from "react";

let access_token = null;
let UserName = null;
let UserId = null;
let TeamName = null;
let TeamId = null;
let TeamDomain = null;

class Data extends Component {
  state = {};
  constructor(props) {
    super(props);
    access_token = this.props.token;
    UserName = this.props.user["name"];
    UserId = this.props.user["id"];
    TeamName = this.props.team["name"];
    TeamId = this.props.team["id"];
    TeamDomain = this.props.team["domain"];
  }
  render() {
    return (
      <div>
        <ul>
          <li>Access token: {access_token}</li>
          <li>User's name: {UserName}</li>
          <li>User's ID: {UserId}</li>
          <li>Team's name: {TeamName}</li>
          <li>Team's ID: {TeamId}</li>
          <li>Team's domain: {TeamDomain}</li>
        </ul>
      </div>
    );
  }
}

export default Data;
