var recommendations = [];
window.onload = function (ev) {
    var projectData = document.getElementById("projects");
    var reportProjects = document.getElementById("reportProjects");
    var userProjects = document.getElementById("userProjects");
    fetch(urlProjects).then(function(response) {
        return response.json();
    }).then(function(json) {
        var result = json;

        result.forEach(function (entry) {
            var opt = document.createElement('option');
            var projectname = entry;
            opt.value = projectname;
            opt.id = projectname;
            opt.innerHTML = projectname;

            if(projectData != null) {
                projectData.appendChild(opt);
            }

            if(reportProjects != null) {
                reportProjects.appendChild(opt);
            }

            if(userProjects != null) {
                userProjects.appendChild(opt);
            }
        });
            console.log(result);
    })

    fetch(urlRecommendations).then(function(response) {
        return response.json();
    }).then(function(json) {
        recommendations = json;
    })
};