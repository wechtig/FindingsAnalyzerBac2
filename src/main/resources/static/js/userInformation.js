var urlUserFindings = "http://localhost:8084/config/projects";

function loadUserInformation() {
    var e = document.getElementById("userProjects");
    var project = e.options[e.selectedIndex].value;
    var startDate = document.getElementById("startdate").value;
    var endDate = document.getElementById("enddate").value;
    var findingsRequest = new XMLHttpRequest();
    var params = project+"/"+startDate+"/"+endDate;
    var requestUrl = "http://localhost:8084/findings/user/"+params;
    var findings = [];

    findingsRequest.open("GET",requestUrl);
    findingsRequest.onload = function () {
        if (findingsRequest.status >= 200 && findingsRequest.status < 300) {
            var json_data = JSON.parse(findingsRequest.response);
            var result = [];

            for (var i in json_data)
                result.push([i, json_data [i]]);

            result.forEach(function (entry) {
                var findingObj = entry[1];
                findings.push(findingObj);
            });
            setTableDataUser(findings);

           /* var configRequest = new XMLHttpRequest();
            var requestUrl = "http://localhost:8084/config/projects";
            var configurations =   [];
            configRequest.open("GET",requestUrl);
            configRequest.onload = function () {
                if (configRequest.status >= 200 && configRequest.status < 300) {
                    var json_data = JSON.parse(configRequest.response);
                    configurations = json_data;
                }
            };*/
        }
    };

    findingsRequest.send();
}


function setTableDataUser(findings) {
    var htmlTable = "<table class='table table-striped' id='userDataTable'><thead class='thead-dark'><tr>" +
        "<th scope='col'>File</th>" +
        "<th scope='col'>Project</th>" +
        "<th scope='col'>Message</th>" +
        "<th scope='col'>Severity</th>" +
        "<th scope='col'>Line</th>" +
        "<th scope='col'>Date</th>" +
        "</tr></thead><tbody>";

    for (var i=0; i<findings.length; i++) {
        var pathFile = findings[i]["file"];
        var filename = pathFile.replace(/^.*[\\\/]/, '')

        htmlTable += "<tr><td>" + filename + "</td>";
        htmlTable += "<td>" + findings[i]["project"] + "</td>";
        htmlTable += getRecommendationForMessage(findings[i]);
        htmlTable += "<td>" + findings[i]["severity"] + "</td>";
        htmlTable += "<td>" + getLineFieldByProjectConfig(pathFile, findings[i]["project"], findings[i]["line"]) + "</td>";
        htmlTable += "<td>" + findings[i]["date"] + "</td></tr>";

        /*var next = i+1;
        if (next%perrow==0 && next!=findings.length) {
            htmlTable += "</tr><tr>";
        }*/
    }
    //htmlTable += "</tr></table>";
    htmlTable += "</tbody></table>";

    document.getElementById("userContainer").innerHTML = htmlTable;
    $('#userDataTable').DataTable();
}

function getLineFieldByProjectConfig(pathFile, project, line) {
    return line;
}

function getRecommendationForMessage(finding) {
    return "<td>" + finding["message"] + "</td>";
}
