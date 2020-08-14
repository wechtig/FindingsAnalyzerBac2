var currentFindingModal = -1;
var commentFindings = [];
function getProjectData() {
    var e = document.getElementById("projects");
    var project = e.options[e.selectedIndex].value;
    var startDate = document.getElementById("start").value;
    var endDate = document.getElementById("end").value;
    var packages = [];

    if(project == null || startDate == null || endDate == null) {
        return;
    }

    var findingsRequest = new XMLHttpRequest();
    var params = project+"/"+startDate+"/"+endDate;
    var requestUrl = "http://localhost:8084/findings/"+params;
    var findings = [];

    findingsRequest.open("GET",requestUrl);
    findingsRequest.onload = function () {
        if (findingsRequest.status >= 200 && findingsRequest.status < 300) {
            var json_data = JSON.parse(findingsRequest.response);

            var result = [];

            for(var i in json_data)
                result.push([i, json_data [i]]);

            result.forEach(function(entry) {
                var findingObj = entry[1];
                findings.push(findingObj);
                packages.push(findingObj["file"]);
            });

            var configRequest = new XMLHttpRequest();
            var requestUrl = "http://localhost:8084/config/projects";
            var configurations =   [];
            configRequest.open("GET",requestUrl);
            configRequest.onload = function () {
                if (configRequest.status >= 200 && configRequest.status < 300) {
                    var json_data = JSON.parse(configRequest.response);
                    configurations = json_data;
                    setTableData(findings, configurations);
                }
            };

            configRequest.send();

            //generateLineChar(findings);

            if(project == "alle") {
                document.getElementById("divProjcets").innerHTML = "<canvas id=\"pieChartProjects\"></canvas>";
                document.getElementById("divPackages").innerHTML = "<canvas style='margin: 0px'></canvas>";
                generatePieChartProjects(findings);
            } else {
                document.getElementById("pieChartProjects");
                document.getElementById("divProjcets").innerHTML = "<canvas style='margin: 0px'></canvas>";
                document.getElementById("divPackages").innerHTML = "<canvas id=\"pieChartPackages\"></canvas>";
            }

            generateBarChartClasses(findings);
            generateBarChartErrors(findings);
            generatePieChartPackages(getPackages(packages));
        } else {
            console.warn(findingsRequest.statusText, findingsRequest.responseText);
        }};

    findingsRequest.send();
}

function generateBarChartErrors(findings) {
    if(window.CommonErrorChart != undefined) {
        window.CommonErrorChart.destroy();
    }

    var chBarClasses = document.getElementById("barChartErrors");
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

    window.CommonErrorChart = new Chart(chBarClasses, {
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
}

function generatePieChartProjects(findings) {
    var projectsD = [];

    findings.forEach(function(entry) {
        var findingProject = entry["project"];
        projectsD.push(findingProject);
    });

    var projects = projectsD.reduce(function(a,b){if(a.indexOf(b)<0)a.push(b);return a;},[]);
    var pieData = getPieData(projects, findings);

    var randomColors = getRandomColors(pieData);

    var projectPieChart = new Chart(document.getElementById("pieChartProjects"), {
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


}

function generatePieChartPackages(packages) {
    if(window.PackagesChart != undefined) {
        window.PackagesChart.destroy();
    }

    var packagesRed = packages.reduce(function(a,b){if(a.indexOf(b)<0)a.push(b);return a;},[]);
    var pieData = getPieDataPackages(packagesRed, packages);
    var randomColors = getRandomColors(packagesRed);

    window.PackagesChart = new Chart(document.getElementById("pieChartPackages"), {
        type: 'pie',
        data: {
            labels: pieData[0],
            datasets: [{
                label: "packageserros",
                backgroundColor: randomColors,
                data: pieData[1]
            }]
        },
        options: {
            title: {
                display: true,
                text: 'Findings per Package'
            }
        }
    });


}

function generateBarChartClasses(findings) {
    if(window.ClassesChart != undefined) {
        window.ClassesChart.destroy();
    }

    var chBarClasses = document.getElementById("barChartClasses");
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

   // var chBarClasses = document.getElementById("barChartClasses");
}

function generateLineChar(findings) {
    var lineChart;
    if(lineChart) {
        lineChart.destroy();
    }

    var findingObj = findings[0];
    var id = findingObj["id"];
    var project = findingObj["project"];
    var file = findingObj["file"];
    var source = findingObj["source"];
    var message = findingObj["message"];
    var severity = findingObj["severity"];
    var line = findingObj["line"];
    var date = findingObj["date"];
    var size = findings.size;

    var chartData = {
        labels: ["S", "M", "T", "W", "T", "F", "S"],
        datasets: [{
            data: [589, 445, 483, 503, 689, 692, 634],
        }/*,
            {
                findingsAnalyzer.data: [639, 465, 493, 478, 589, 632, 674],
            }*/]
    };

    var chLine = document.getElementById("chLine");
    if (chLine) {
        lineChart = new Chart(chLine, {
            type: 'line',
            data: chartData,
            options: {
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero: false
                        }
                    }]
                },
                legend: {
                    display: false
                }
            }
        });
    }

    //https://mdbootstrap.com/docs/jquery/javascript/charts/
   //document.getElementById("chart1").value = chLine;
}

function setTableData(findings, configurations) {
    var htmlTable = "<table class='table table-striped' id='dataTable'><thead class='thead-dark'><tr>" +
        "<th scope='col'>File</th>" +
       "<th scope='col'>Project</th>" +
       "<th scope='col'>Message</th>" +
        "<th scope='col'>Severity</th>" +
       "<th scope='col'>Line</th>" +
        "<th scope='col'>Date</th>" +
        "<th scope='col'>Add comments</th>" +
        "<th scope='col'>View comments</th>" +
        "</tr></thead><tbody>";

    console.log("recommendations: "+recommendations);
    for (var i=0; i<findings.length; i++) {
        var pathFile = findings[i]["file"];
        var filename = pathFile.replace(/^.*[\\\/]/, '')

        htmlTable += "<tr><td>" + filename + "</td>";
        htmlTable += "<td>" + findings[i]["project"] + "</td>";
        htmlTable += getRecommendationForMessage(findings[i], recommendations);
        htmlTable += "<td>" + findings[i]["severity"] + "</td>";
        htmlTable += "<td>" + getLineFieldByProjectConfig(pathFile, configurations, findings[i]["project"], findings[i]["line"]) + "</td>";
        htmlTable += "<td>" + findings[i]["date"] + "</td>";
        htmlTable += "<td><button data-rowId='"+i+"' class='addComment'>Add</button></td>";
        htmlTable += "<td><button data-rowId='"+i+"' class='viewComment'>View</button></td></tr>";

    }
    $("#tableContainer").on("click",".addComment", function(e){
        currentFindingModal = $(this).data().rowid;
        addComment(findings[$(this).data().rowid]);
    });
    $("#tableContainer").on("click",".viewComment", function(e){
        viewComments(findings[$(this).data().rowid]);
    });
    //htmlTable += "</tr></table>";
    htmlTable += "</tbody></table>";

    document.getElementById("tableContainer").innerHTML = htmlTable;
    document.getElementById("exportButton").disabled = false;
    commentFindings = findings;
    $('#dataTable').DataTable();
}

function addComment(finding) {
    $('#addModal').modal('show');
}

function saveComment() {
    var messageAddRequest = new XMLHttpRequest();
    var requestUrl = "http://localhost:8084/findings/comment";
    var comment = document.getElementById("comment").value;

    messageAddRequest.open("POST",requestUrl);
    messageAddRequest.onreadystatechange = function() { // Call a function when the state changes.
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
        }
    }

    messageAddRequest.send(commentFindings[currentFindingModal]["project"] + "&&" +
        commentFindings[currentFindingModal]["date"] + "&&" +
        commentFindings[currentFindingModal]["file"] + "&&" +
        commentFindings[currentFindingModal]["line"] + "&&" +
        commentFindings[currentFindingModal]["message"] + "&&" +comment);
}

function viewComments(finding) {
    $('#locModal').modal('show');

    var configRequest = new XMLHttpRequest();
    var filename = finding["file"].replace(/^.*[\\\/]/, '')

    var requestUrl = "http://localhost:8084/findings/comment/"+finding["project"]+"/"+finding["date"]+"/"+filename+"/"+finding["line"]+"/"+finding["message"];
    var data = "";
    configRequest.open("GET",requestUrl);
    configRequest.onload = function () {
        if (configRequest.status >= 200 && configRequest.status < 300) {
            var json_data = JSON.parse(configRequest.response);
            json_data.forEach(function (entry) {
                var text = entry["text"];
                data += text;
            });
            $(".modal-body #daten").html("<p>"+data+"</p>");
        }
    };

    configRequest.send();

}

function getLineFieldByProjectConfig(pathFile, config, project, line) {
    var lineField = "";
    config.forEach(function (entry) {
        if(entry["name"] == project) {
            var githubPath = pathFile.split(project+"\\");
            var fullPath = "";
            for(var i = 1; i < githubPath.length; i++) {
                fullPath += githubPath[i];
            }

            var link = entry["vcsRepositoryLink"] + "/blob/master/"+fullPath+"#L"+line;
            lineField = "<a href='"+link+"' target='_blank'>"+line+"</a>";
        }
    });

    if(lineField != "") {
        return lineField;
    }

    return line;
}

function getRecommendationForMessage(finding, recommendations) {
    var messageField  ="";
    if(recommendations == null) {
        messageField = "<td>" + finding["message"] + "</td>";
    } else {
        for(var k = 0; k < recommendations.length; k++) {
            if(finding["message"].includes(recommendations[k].error)) {
                messageField = "<td><a target='_blank' href='"+recommendations[k].link+"'>" + finding['message'] + "</a></td>";
                break;
            } else {
                messageField =  "<td>" + finding["message"] + "</td>";
            }
        }
    }

    return messageField;
}

function exportToPdf() {
    console.log("send");
    var e = document.getElementById("projects");
    var project = e.options[e.selectedIndex].value;
    var startDate = document.getElementById("start").value;
    var endDate = document.getElementById("end").value;

    if(project == null || startDate == null || endDate == null) {
        return;
    }

    var params = project+"/"+startDate+"/"+endDate;
    fetch('http://localhost:8084/export/'+params, {
        method: 'POST',
        data: params
    }).then(function(response) {
         return response.blob();
      }).then(function (blobObj) {
        var objectUrl = URL.createObjectURL(blobObj);
        var a = document.createElement('a');
        a.href = objectUrl;
        a.download = project + "Report.pdf";
        document.body.appendChild(a);
        a.click();
        a.remove();
      }).catch(function (error) {
        console.log('Request failure: ', error);
    });
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

function getBarArrayErrors(findings) {
    findings.sort((a,b) => (a.message > b.message) ? 1 : ((b.message > a.message) ? -1 : 0));
    var a = [], b = [], prev;

    for ( var i = 0; i < findings.length; i++ ) {
        if ( findings[i]["message"] !== prev ) {
            var error = findings[i]["message"];
            a.push(error);
            b.push(1);
        } else {
            b[b.length-1]++;
        }
        prev = findings[i]["message"];
    }

    return getArray(a, b);
}


function getArray(table1, table2)
{
    var i, out = [];
    for(i=0;i<table1.length;i++)
    {
        out.push([table1[i],table2[i]]);
    }
    return out;
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

function saveRecommendation() {
    var recError = document.getElementById("recError").value;
    var recLink = document.getElementById("recLink").value;
    var data = recError+"&&"+recLink;
    data = data.replace(/\"/g, "");

    fetch('http://localhost:8084/recommendation', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body:data,
    }).then(function (data) {
        console.log('Request success: ', data);
    }).catch(function (error) {
            console.log('Request failure: ', error);
    });
}


function getPackages(filesD) {
    var packages = [];

    filesD.forEach(function(entry) {
        var filePath = entry.split("\\");
        packages.push(filePath[filePath.length-2])
    });

    return packages;
}
