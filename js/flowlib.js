$(function () {
    var source   = $("#entry-template").html();
    var template = Handlebars.compile(source);
    var $results = $('.results'),
        $searchButton = $('#search button'),
        $searchQuery = $('#search input');

    var packagesResourceUrl = 'http://hackathon-mule-repository.herokuapp.com/api/packages';

    function displayPackages(data) {
        $results.html(template({items: data}));
    }

    function findPackages(query){
        var url = packagesResourceUrl;

        if (query) {
            url += '?q=' + encodeURIComponent(query);
        }

        return $.getJSON(url)
    }


    //findPackages().then(displayPackages);
    $searchQuery.keydown(function (evt) {
        if (evt.keyCode === 13) {
            $searchButton.click();
        }
    });
    $searchButton.click(function () {
        findPackages($searchQuery.val()).then(displayPackages);
    });
});



