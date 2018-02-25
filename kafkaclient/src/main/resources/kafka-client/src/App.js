import $ from 'jquery';
import io from 'socket.io-client';
import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            loginTopicRows: [],
            loginFailureTopicRows: []
        };

        this.onSubmitLogin = this.onSubmitLogin.bind(this);
    }

    componentDidMount() {
        this.socket = io();

        this.socket.on('new-login', message => {
            this.setState({ loginTopicRows: this.state.loginTopicRows.concat(message) });
        });

        this.socket.on('login-failure', message => {
            this.setState({ loginFailureTopicRows: this.state.loginFailureTopicRows.concat(message) });
        });

        $.get('/topics/login').done(data => {
            this.setState({ loginTopicRows: data.rows });
        });
    }

    onSubmitLogin(event) {
        $.ajax({
            method: 'POST',
            url: '/login',
            dataType: 'json',
            data: { user: this.user.value, status: this.status.value }
        }).done(() => {

        }).fail(() => {
            alert('error');
        });

        event.preventDefault();
    }

    render() {
        return (
            <div className="App">
                <header className="App-header">
                    <img src={logo} className="App-logo" alt="logo" />
                    <form onSubmit={this.onSubmitLogin}>
                        <input type="text" placeholder="user" ref={(user) => this.user = user} /><br/>
                        <select ref={(status) => this.status = status}>
                            <option value="success">Success</option>
                            <option value="fail">Fail</option>
                        </select>
                        <input type="submit" value="Login" />
                    </form>
                </header>
                <p className="App-intro">
                    <div id="loginTopicList" className="topic-list">
                        <h3>Login Topic</h3>
                        <ul>
                            {this.state.loginTopicRows.map(this.renderLoginTopicRows)}
                        </ul>
                    </div>
                    <div id="loginFailureTopicList" className="topic-list">
                        <h3>Login Failure Topic</h3>
                        <ul>
                            {this.state.loginFailureTopicRows.map(this.renderLoginFailureTopicRows)}
                        </ul>
                    </div>
                </p>
            </div>
        );
    }

    renderLoginTopicRows(row) {
        return <li>[{row.logTime}] user: {row.userID} ({row.IP}) {row.status}</li>;
    }

    renderLoginFailureTopicRows(row) {
        return <li>[{new Date().toDateString()}] user: {row.userID} - {row.message}</li>;
    }

}

export default App;
