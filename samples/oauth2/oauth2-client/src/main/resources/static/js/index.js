function getToken() {
    fetch('/client/token')
        .then(res => res.text())
        .then(text => document.querySelector("#access-token").textContent = text)
}

function getResourceInfo() {
    fetch('/client/resource/info', {
        headers: {
            Authorization: `Bearer ${document.querySelector("#access-token").textContent}`
        },
        credentials: "omit"
    }).then(res => res.text())
    .then(text => document.querySelector("#resource-info").textContent = text)
}