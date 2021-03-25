function getToken() {
    fetch('token')
        .then(res => res.text())
        .then(text => document.querySelector("#access-token").textContent = text)
}

function getResourceInfo() {
    fetch('resource/info', {
        headers: {
            Authorization: `Bearer ${document.querySelector("#access-token").textContent}`
        },
        credentials: "omit"
    }).then(res => res.text())
    .then(text => document.querySelector("#resource-info").textContent = text)
}