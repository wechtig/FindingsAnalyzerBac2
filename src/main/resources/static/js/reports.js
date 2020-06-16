function sendReport() {
    var e = document.getElementById("reportProjects");
    var project = e.options[e.selectedIndex].value;
    var startDate = document.getElementById("start").value;
    var endDate = document.getElementById("end").value;
    var printFindings = document.getElementById("includeFindings").value;

    var findingsRequest = new XMLHttpRequest();
    var params = project+"/"+startDate+"/"+endDate;
    var requestUrl = "http://localhost:8084/findings/"+params;
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

function sendReportWithoutFindings(findings) {
    var chart1 = generateChart1(findings);
    fetch('http://localhost:8084/reports/chart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body:chart1,
    }).then(function (data) {
        console.log('Request success: ', data);
    }).catch(function (error) {
        console.log('Request failure: ', error);
    });
}

function sendReportWithFindings(findings) {

}

function generateChart1(findings) {
    var chBarClasses = document.getElementById("lineChart");
    var errorsD = [];
    findings.forEach(function(entry) {
        var findingError = entry["message"];
        errorsD.push(findingError);
    });

    var errors = errorsD.reduce(function(a,b){if(a.indexOf(b)<0)a.push(b);return a;},[]);
    var barArray = getBarArrayErrors(findings);

    barArray.sort(function(a,b) {
        return b[1] - a[1];
    });

    var chart1 = new Chart(chBarClasses, {
        type: 'horizontalBar',
        data: {
            labels: [barArray[0][0], barArray[1][0], barArray[2][0], barArray[3][0], barArray[4][0]],
            datasets: [{
                barPercentage: 1,
                barThickness: 6,
                maxBarThickness: 8,
                minBarLength: 2,
                data: [barArray[0][1], barArray[1][1], barArray[2][1], barArray[3][1], barArray[4][1]]
            }]
        },
        options: {
            legend: {
                display: false
            },
            scales: {
                xAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        }}
    );

    var image1 = document.getElementById("lineChart").toDataURL("image/jpg");
    return image1;
}
