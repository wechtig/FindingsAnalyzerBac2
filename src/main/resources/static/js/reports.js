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
    generateChart1(findings);
    generateChart2(findings);
    generateChart3(findings);
    var e = document.getElementById("reportProjects");
    var project = e.options[e.selectedIndex].value;
    var startDate = document.getElementById("start").value;
    var endDate = document.getElementById("end").value;
    var printFindings = document.getElementById("includeFindings").value;
    setTimeout(function(){
        var chart1 = document.getElementById("lineChart").toDataURL("image/jpg");
        var chart2 = document.getElementById("chartClasses").toDataURL("image/jpg");
        var chart3 = document.getElementById("pieChart").toDataURL("image/jpg");

        var chartData = project+ "&&" +startDate+ "&&" +endDate + "&&" + chart1 + "&&" +  chart2 + "&&" + chart3;

        fetch('http://localhost:8084/reports/chart/findings/false/', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body:chartData,
        }).then(function (data) {
            console.log('Request success: ', data);
        }).catch(function (error) {
            console.log('Request failure: ', error);
        });

    },1000);

}

function sendReportWithFindings(findings) {
    var chart1 = generateChart1(findings);
    var chart2 = generateChart2(findings);
    var chart3 = generateChart3(findings);
    var e = document.getElementById("reportProjects");
    var project = e.options[e.selectedIndex].value;
    var startDate = document.getElementById("start").value;
    var endDate = document.getElementById("end").value;
    var printFindings = document.getElementById("includeFindings").value;

    var chartData = project+ "&&" +startDate+ "&&" +endDate + "&&" + chart1 + "&&" +  chart2 + "&&" + chart3;

    fetch('http://localhost:8084/reports/chart/findings/true', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body:chartData,
    }).then(function (data) {
        console.log('Request success: ', data);
    }).catch(function (error) {
        console.log('Request failure: ', error);
    });
}

function generateChart2(findings) {
    var chBarClasses = document.getElementById("chartClasses");
    var classesD = [];

    findings.forEach(function(entry) {
        var findingClass = entry["file"];
        classesD.push(findingClass);
    });

    var classes = classesD.reduce(function(a,b){if(a.indexOf(b)<0)a.push(b);return a;},[]);
    var barArray = getBarArray(findings);

    barArray.sort(function(a,b) {
        return b[1] - a[1];
    });

    window.ClassesChart = new Chart(chBarClasses, {
        type: 'horizontalBar',
        data: {
            labels: [barArray[0][0], barArray[1][0], barArray[2][0], barArray[3][0], barArray[4][0]],
            datasets: [{
                barPercentage: 1,
                barThickness: 6,
                maxBarThickness: 8,
                minBarLength: 2,
                // findingsAnalyzer.data: [10, 20, 30, 40, 50, 60, 70]
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


    var image2 = document.getElementById("chartClasses").toDataURL("image/jpg");
    return image2;
}

function generateChart3(findings) {
    var projectsD = [];

    findings.forEach(function(entry) {
        var findingProject = entry["project"];
        projectsD.push(findingProject);
    });

    var projects = projectsD.reduce(function(a,b){if(a.indexOf(b)<0)a.push(b);return a;},[]);
    var pieData = getPieData(projects, findings);

    var randomColors = getRandomColors(pieData);

    var projectPieChart = new Chart(document.getElementById("pieChart"), {
        type: 'pie',
        data: {
            labels: pieData[0],
            datasets: [{
                label: "findings",
                backgroundColor: randomColors,
                data: pieData[1]
            }]
        },
        options: {
            title: {
                display: true,
                text: 'Findings per project'
            }
        }
    });

    var image3 = document.getElementById("pieChart").toDataURL("image/jpg");
    return image3;

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
    console.log(image1);
    return image1;
}

function getBarArray(findings) {
    findings.sort((a,b) => (a.file > b.file) ? 1 : ((b.file > a.file) ? -1 : 0));

    var a = [], b = [], prev;

    for ( var i = 0; i < findings.length; i++ ) {
        if ( findings[i]["file"] !== prev ) {
            var pathFile = findings[i]["file"]+" ("+findings[i]["project"]+")";
            var filename = pathFile.replace(/^.*[\\\/]/, '')
            a.push(filename);
            b.push(1);
        } else {
            b[b.length-1]++;
        }
        prev = findings[i]["file"];
    }

    return getArray(a, b);
}

function getRandomColors(pieData) {
    var randomColors = [];

    for ( var j = 0; j < pieData[0].length; j++ ) {
        var letters = '0123456789ABCDEF'.split('');
        var color = '#';
        for (var i = 0; i < 6; i++ ) {
            color += letters[Math.floor(Math.random() * 16)];
        }

        randomColors.push(color);
    }

    return randomColors;
}

function getPieData(projects, findings) {
    var a = [], b = [];

    for ( var i = 0; i < projects.length; i++ ) {
        var project = projects[i];
        a.push(project);
        b.push(getErrorsPerProject(project, findings));
    }

    return [a, b];
}

function getPieDataPackages(packagesNames, packagesFindings) {
    var a = [], b = [];

    for ( var i = 0; i < packagesNames.length; i++ ) {
        var project = packagesNames[i];
        a.push(project);
        b.push(getPackagesFindingsPerProject(project, packagesFindings));
    }

    return [a, b];
}

function getPackagesFindingsPerProject(packageName, packagesFindings) {
    var packageFindingAnz = 0;

    for ( var i = 0; i < packagesFindings.length; i++ ) {
        if(packagesFindings[i] == packageName) {
            packageFindingAnz++;
        }
    }

    return packageFindingAnz;
}

function getErrorsPerProject(project, findings) {
    var errors = 0;

    for ( var i = 0; i < findings.length; i++ ) {
        var finding = findings[i];
        if(finding["project"] == project) {
            errors++;
        }
    }

    return errors;
}