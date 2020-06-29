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

            if(!printFindings) {
                sendReportWithoutFindings(findings);
            } else if(printFindings) {
                sendReportWithFindings(findings);
            }

        }
    };

    findingsRequest.send();


}