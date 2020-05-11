function ignoreMessage() {
    var ignoreMessage = document.getElementById('newMessage').value;
    saveMessageInDatabase(ignoreMessage);
}

function saveMessageInDatabase(ignoreMessage) {
    var messageAddRequest = new XMLHttpRequest();
    var requestUrl = "http://localhost:8084/addignored";

    messageAddRequest.open("POST",requestUrl);
    messageAddRequest.onreadystatechange = function() { // Call a function when the state changes.
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            loadIgnoreTable();
        }
    }

    messageAddRequest.send(ignoreMessage);
}

function loadIgnoreTable() {
    var messages = [];

    var messageRequest = new XMLHttpRequest();
    var requestUrl = "http://localhost:8084/ignored";

    messageRequest.open("GET",requestUrl);
    messageRequest.onload = function () {
        if (messageRequest.status >= 200 && messageRequest.status < 300) {
            var json_data = JSON.parse(messageRequest.response);

            var result = [];

            for(var i in json_data)
                result.push([i, json_data [i]]);

            result.forEach(function(entry) {
                var findingObj = entry[1];
                messages.push(findingObj);
            });

            setIgnoreTableDate(messages);
        } else {
            console.warn(messageRequest.statusText, messageRequest.responseText);
        }};

    messageRequest.send();
}

function setIgnoreTableDate(messages) {
    var htmlTable = "<table class='table' id='ignoreTable'><thead class='thead-dark'><tr>" +
        "<th scope='col'>Message</th>" +
        "<th scope='col'>Action</th>" +
        "</tr></thead><tbody></tbody></table>";

    document.getElementById("ignoreTableContainer").innerHTML = htmlTable;
    setRowData(messages);
}

function setRowData(messages) {
    var tableRef = document.getElementById('ignoreTable').getElementsByTagName('tbody')[0];
    for(var i = 0; i < messages.length; i++) {
        var ignoreMessage = messages[i];
        var newRow   = tableRef.insertRow();
        var messageCell  = newRow.insertCell(0);
        var buttonCell  = newRow.insertCell(1);
        var newText  = document.createTextNode(ignoreMessage);
        var btn = document.createElement('input');
        btn.type = "button";
        btn.className = "btn";
        btn.value = "Remove";
        btn.onclick = (function(ignoreMessage) {return function() {removeMessageFromIgnoreTable(ignoreMessage);}})(ignoreMessage);

        messageCell.appendChild(newText);
        buttonCell.appendChild(btn);
    }
}

function removeMessageFromIgnoreTable(message) {
    var messageRemRequest = new XMLHttpRequest();
    var requestUrl = "http://localhost:8084/removeignored";

    messageRemRequest.open("POST",requestUrl);
    messageRemRequest.onreadystatechange = function() { // Call a function when the state changes.
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            console.log("request sended");
            loadIgnoreTable();
        }
    }

    messageRemRequest.send(message);
}