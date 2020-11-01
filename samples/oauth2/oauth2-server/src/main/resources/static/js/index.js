const redirectUri = 'http://localhost:90/index.html'

window.onload = function () {
    grantByAuthorizationCode();
    grantByImplicit();
}

function toAuthorizePage(responseType) {
    location.href = `http://localhost:90/oauth/authorize?client_id=test1&client_secret=123456&response_type=${responseType}&redirect_uri=${redirectUri}`;
}

function grantByAuthorizationCode() {
    let code = location.search.replace(/.*code=(\w+).*/, "$1");
    if (code.length < 1) {
        return
    }
    tokenReq({
        grant_type: 'authorization_code',
        redirect_uri: redirectUri,
        code
    })
}

function grantByImplicit() {
    let accessToken = location.hash.replace(/.*access_token=([^&\s]+).*/, "$1");
    if (accessToken.length < 1) {
        return
    }
    document.querySelector("#access-token").textContent = accessToken
    document.querySelector("#refresh-token").textContent = '无'
    document.querySelector("#decrypted-jwt").textContent = BASE64.urlsafe_decode(accessToken.split(".")[1])
}

function grantByPassword() {
    tokenReq({
        username: 'admin',
        password: '123456',
        grant_type: 'password'
    })
}

function grantByRefreshToken() {
    tokenReq({
        grant_type: 'refresh_token',
        refresh_token: document.querySelector("#refresh-token").textContent
    })
}

function grantByClientCredentials() {
    tokenReq({
        grant_type: 'client_credentials',
        client_id: 'test1',
        client_secret: '123456',
    })
}

function grantBySmsCode() {
    tokenReq({
        grant_type: 'sms_code',
        phone: '18533334444',
        code: '1234',
    })
}

function tokenReq(data) {
    document.querySelector("#login-info").textContent = ''
    let formData = new FormData();
    Object.keys(data).map(k => formData.append(k, data[k]))

    fetch('/oauth/token', {
        headers: {
            Authorization: 'Basic dGVzdDE6MTIzNDU2'
        },
        method: 'POST',
        body: formData
    }).then(res => res.json()).then(json => {
        if (json.access_token) {
            document.querySelector("#access-token").textContent = json.access_token
            document.querySelector("#refresh-token").textContent = json.refresh_token || '无'
            document.querySelector("#decrypted-jwt").textContent = BASE64.urlsafe_decode(json.access_token.split(".")[1])
        } else {
            document.querySelector("#decrypted-jwt").textContent = JSON.stringify(json)
        }
    })
}

function getLoginInfo() {
    fetch('/user/loginInfo', {
        headers: {
            Authorization: `Bearer ${document.querySelector("#access-token").textContent}`
        }
    }).then(res => res.text()).then(text => {
        document.querySelector("#login-info").textContent = text
    })
}
