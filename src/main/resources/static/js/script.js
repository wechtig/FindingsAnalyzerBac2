var recommendations = [];
window.onload = function (ev) {
    var projectData = document.getElementById("projects");
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
            projectData.appendChild(opt);
        });
            console.log(result);
    })

    fetch(urlRecommendations).then(function(response) {
        return response.json();
    }).then(function(json) {
        recommendations = json;
    })
}