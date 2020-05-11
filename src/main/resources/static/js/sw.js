var urlProjects = "http://localhost:8084/projects";
var urlRecommendations = "http://localhost:8084/recommendations";

function getProjects() {
    fetch(urlProjects)
        .then(function(data) {
            console.log(data);
        }).catch(function(error) {
            console.log(error);
    })
}

function createRecommendationsRequest() {
    return fetch(urlRecommendations).then(function(response) {
        return response.json();
    }).then(function(json) {
        return json;
    });
}

function getRecommendations() {
    createRecommendationsRequest().then(function(result) {
        return result;
    });
}